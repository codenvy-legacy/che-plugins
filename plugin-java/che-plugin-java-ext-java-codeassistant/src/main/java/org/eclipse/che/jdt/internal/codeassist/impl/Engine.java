/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.jdt.internal.codeassist.impl;

import org.eclipse.che.jdt.internal.compiler.parser.SourceTypeConverter;
import org.eclipse.che.jdt.internal.core.NameLookup;
import org.eclipse.che.jdt.internal.core.SearchableEnvironment;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.codeassist.impl.AssistOptions;
import org.eclipse.jdt.internal.codeassist.impl.AssistParser;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.ISourceType;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.ITypeRequestor;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.ExtraCompilerModifiers;
import org.eclipse.jdt.internal.compiler.lookup.ImportBinding;
import org.eclipse.jdt.internal.compiler.lookup.ImportConflictBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;

import java.util.Map;

@SuppressWarnings("rawtypes")
public abstract class Engine implements ITypeRequestor {

	public LookupEnvironment lookupEnvironment;

	protected CompilationUnitScope  unitScope;
	public    SearchableEnvironment nameEnvironment;

	public AssistOptions   options;
	public CompilerOptions compilerOptions;
	public boolean         forbiddenReferenceIsError;
	public boolean         discouragedReferenceIsError;

	public boolean importCachesInitialized = false;
	public char[][][]      importsCache;
	public ImportBinding[] onDemandImportsCache;
	public int    importCacheCount         = 0;
	public int    onDemandImportCacheCount = 0;
	public char[] currentPackageName       = null;

	public Engine(Map settings) {
		this.options = new AssistOptions(settings);
		this.compilerOptions = new CompilerOptions(settings);
		this.forbiddenReferenceIsError =
				(this.compilerOptions.getSeverity(CompilerOptions.ForbiddenReference) & ProblemSeverities.Error) != 0;
		this.discouragedReferenceIsError =
				(this.compilerOptions.getSeverity(CompilerOptions.DiscouragedReference) & ProblemSeverities.Error) != 0;
	}

	/**
	 * Add an additional binary type
	 */
	public void accept(IBinaryType binaryType, PackageBinding packageBinding, AccessRestriction accessRestriction) {
		this.lookupEnvironment.createBinaryTypeFrom(binaryType, packageBinding, accessRestriction);
	}

	/**
	 * Add an additional compilation unit.
	 */
	public void accept(ICompilationUnit sourceUnit, AccessRestriction accessRestriction) {
		CompilationResult result = new CompilationResult(sourceUnit, 1, 1, this.compilerOptions.maxProblemsPerUnit);

		AssistParser assistParser = getParser();
		Object parserState = assistParser.becomeSimpleParser();

		CompilationUnitDeclaration parsedUnit =
				assistParser.dietParse(sourceUnit, result);

		assistParser.restoreAssistParser(parserState);

		this.lookupEnvironment.buildTypeBindings(parsedUnit, accessRestriction);
		this.lookupEnvironment.completeTypeBindings(parsedUnit, true);
	}

	/**
	 * Add additional source types (the first one is the requested type, the rest is formed by the
	 * secondary types defined in the same compilation unit).
	 */
	public void accept(ISourceType[] sourceTypes, PackageBinding packageBinding, AccessRestriction accessRestriction) {
		CompilationResult result =
				new CompilationResult(sourceTypes[0].getFileName(), 1, 1, this.compilerOptions.maxProblemsPerUnit);
		CompilationUnitDeclaration unit =
				SourceTypeConverter.buildCompilationUnit(
                        sourceTypes,//sourceTypes[0] is always toplevel here
                        SourceTypeConverter.FIELD_AND_METHOD // need field and methods
                        | SourceTypeConverter.MEMBER_TYPE, // need member types
                        // no need for field initialization
                        this.lookupEnvironment.problemReporter,
                        result);

		if (unit != null) {
			this.lookupEnvironment.buildTypeBindings(unit, accessRestriction);
			this.lookupEnvironment.completeTypeBindings(unit, true);
		}
	}

	public abstract AssistParser getParser();

	public void initializeImportCaches() {
		if (this.currentPackageName == null) {
			initializePackageCache();
		}

		ImportBinding[] importBindings = this.unitScope.imports;
		int length = importBindings == null ? 0 : importBindings.length;

		for (int i = 0; i < length; i++) {
			ImportBinding importBinding = importBindings[i];
			if(importBinding.onDemand) {
				if(this.onDemandImportsCache == null) {
					this.onDemandImportsCache = new ImportBinding[length - i];
				}
				this.onDemandImportsCache[this.onDemandImportCacheCount++] =
					importBinding;
			} else {
				if(!(importBinding.resolvedImport instanceof MethodBinding) ||
						importBinding instanceof ImportConflictBinding) {
					if(this.importsCache == null) {
						this.importsCache = new char[length - i][][];
					}
					this.importsCache[this.importCacheCount++] = new char[][]{
							importBinding.compoundName[importBinding.compoundName.length - 1],
							CharOperation.concatWith(importBinding.compoundName, '.')
						};
				}
			}
		}

		this.importCachesInitialized = true;
	}
	
	public void initializePackageCache() {
		if (this.unitScope.fPackage != null) {
			this.currentPackageName = CharOperation.concatWith(this.unitScope.fPackage.compoundName, '.');
		} else if (this.unitScope.referenceContext != null &&
				this.unitScope.referenceContext.currentPackage != null) {
			this.currentPackageName = CharOperation.concatWith(this.unitScope.referenceContext.currentPackage.tokens, '.');
		} else {
			this.currentPackageName = CharOperation.NO_CHAR;
		}
	}

	protected boolean mustQualifyType(
		char[] packageName,
		char[] typeName,
		char[] enclosingTypeNames,
		int modifiers) {

		// If there are no types defined into the current CU yet.
		if (this.unitScope == null)
			return true;

		if(!this.importCachesInitialized) {
			initializeImportCaches();
		}

		for (int i = 0; i < this.importCacheCount; i++) {
			char[][] importName = this.importsCache[i];
			if(CharOperation.equals(typeName, importName[0])) {
				char[] fullyQualifiedTypeName =
					enclosingTypeNames == null || enclosingTypeNames.length == 0
							? CharOperation.concat(
							packageName,
							typeName,
							'.')
							: CharOperation.concat(
									CharOperation.concat(
											packageName,
											enclosingTypeNames,
											'.'),
									typeName,
									'.');
				return !CharOperation.equals(fullyQualifiedTypeName, importName[1]);
			}
		}

		if ((enclosingTypeNames == null || enclosingTypeNames.length == 0 ) && CharOperation.equals(this.currentPackageName, packageName))
			return false;

		char[] fullyQualifiedEnclosingTypeName = null;

		for (int i = 0; i < this.onDemandImportCacheCount; i++) {
			ImportBinding importBinding = this.onDemandImportsCache[i];
			Binding resolvedImport = importBinding.resolvedImport;

			char[][] importName = importBinding.compoundName;
			char[] importFlatName = CharOperation.concatWith(importName, '.');

			boolean isFound = false;
			// resolvedImport is a ReferenceBindng or a PackageBinding
			if(resolvedImport instanceof ReferenceBinding) {
				if(enclosingTypeNames != null && enclosingTypeNames.length != 0) {
					if(fullyQualifiedEnclosingTypeName == null) {
						fullyQualifiedEnclosingTypeName =
							CharOperation.concat(
									packageName,
									enclosingTypeNames,
									'.');
					}
					if(CharOperation.equals(fullyQualifiedEnclosingTypeName, importFlatName)) {
						if(importBinding.isStatic()) {
							isFound = (modifiers & ClassFileConstants.AccStatic) != 0;
						} else {
							isFound = true;
						}
					}
				}
			} else {
				if(enclosingTypeNames == null || enclosingTypeNames.length == 0) {
					if(CharOperation.equals(packageName, importFlatName)) {
						if(importBinding.isStatic()) {
							isFound = (modifiers & ClassFileConstants.AccStatic) != 0;
						} else {
							isFound = true;
						}
					}
				}
			}

			// find potential conflict with another import
			if(isFound) {
				for (int j = 0; j < this.onDemandImportCacheCount; j++) {
					if(i != j) {
						ImportBinding conflictingImportBinding = this.onDemandImportsCache[j];
						if(conflictingImportBinding.resolvedImport instanceof ReferenceBinding) {
							ReferenceBinding refBinding =
								(ReferenceBinding) conflictingImportBinding.resolvedImport;
							if (refBinding.getMemberType(typeName) != null) {
								return true;
							}
						} else {
							char[] conflictingImportName =
								CharOperation.concatWith(conflictingImportBinding.compoundName, '.');

							if (this.nameEnvironment.nameLookup.findType(
									String.valueOf(typeName),
									String.valueOf(conflictingImportName),
									false,
									NameLookup.ACCEPT_ALL,
									false/*don't check restrictions*/) != null) {
								return true;
							}
						}
					}
				}
				return false;
			}
		}
		return true;
	}

	/*
	 * Find the node (a field, a method or an initializer) at the given position
	 * and parse its block statements if it is a method or an initializer.
	 * Returns the node or null if not found
	 */
	protected ASTNode parseBlockStatements(CompilationUnitDeclaration unit, int position) {
		int length = unit.types.length;
		for (int i = 0; i < length; i++) {
			TypeDeclaration type = unit.types[i];
			if (type.declarationSourceStart < position
				&& type.declarationSourceEnd >= position) {
				getParser().scanner.setSource(unit.compilationResult);
				return parseBlockStatements(type, unit, position);
			}
		}
		return null;
	}

	private ASTNode parseBlockStatements(
		TypeDeclaration type,
		CompilationUnitDeclaration unit,
		int position) {
		//members
		TypeDeclaration[] memberTypes = type.memberTypes;
		if (memberTypes != null) {
			int length = memberTypes.length;
			for (int i = 0; i < length; i++) {
				TypeDeclaration memberType = memberTypes[i];
				if (memberType.bodyStart > position)
					continue;
				if (memberType.declarationSourceEnd >= position) {
					return parseBlockStatements(memberType, unit, position);
				}
			}
		}
		//methods
		AbstractMethodDeclaration[] methods = type.methods;
		if (methods != null) {
			int length = methods.length;
			for (int i = 0; i < length; i++) {
				AbstractMethodDeclaration method = methods[i];
				if (method.bodyStart > position + 1)
					continue;

				if(method.isDefaultConstructor())
					continue;

				if (method.declarationSourceEnd >= position) {

					getParser().parseBlockStatements(method, unit);
					return method;
				}
			}
		}
		//initializers
		FieldDeclaration[] fields = type.fields;
		if (fields != null) {
			int length = fields.length;
			for (int i = 0; i < length; i++) {
				FieldDeclaration field = fields[i];
				if (field.sourceStart > position)
					continue;
				if (field.declarationSourceEnd >= position) {
					if (field instanceof Initializer) {
						getParser().parseBlockStatements((Initializer)field, type, unit);
					}
					return field;
				}
			}
		}
		return null;
	}

	protected void reset(boolean resetLookupEnvironment) {
		if (resetLookupEnvironment) this.lookupEnvironment.reset();
	}

	public static char[] getTypeSignature(TypeBinding typeBinding) {
		char[] result = typeBinding.signature();
		if (result != null) {
			result = CharOperation.replaceOnCopy(result, '/', '.');
		}
		return result;
	}

	public static char[] getSignature(MethodBinding methodBinding) {
		char[] result = null;

		int oldMod = methodBinding.modifiers;
		//TODO remove the next line when method from binary type will be able to generate generic signature
		methodBinding.modifiers |= ExtraCompilerModifiers.AccGenericSignature;
		result = methodBinding.genericSignature();
		if(result == null) {
			result = methodBinding.signature();
		}
		methodBinding.modifiers = oldMod;

		if (result != null) {
			result = CharOperation.replaceOnCopy(result, '/', '.');
		}
		return result;
	}

	public static char[] getSignature(TypeBinding typeBinding) {
		char[] result = null;

		result = typeBinding.genericTypeSignature();

		if (result != null) {
			result = CharOperation.replaceOnCopy(result, '/', '.');
		}
		return result;
	}
}
