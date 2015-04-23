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
package org.eclipse.che.jdt.core;

import org.eclipse.che.jdt.internal.core.ClasspathEntry;
import org.eclipse.che.jdt.internal.core.JavaModel;
import org.eclipse.che.jdt.internal.core.JavaModelManager;
import org.eclipse.che.jdt.internal.core.JavaProject;
import org.eclipse.che.jdt.internal.core.SetContainerOperation;
import org.eclipse.che.jdt.internal.core.util.Util;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.core.ClasspathAccessRule;
import org.eclipse.jdt.internal.core.ClasspathAttribute;
import org.eclipse.jdt.internal.core.DefaultWorkingCopyOwner;
import org.eclipse.jdt.internal.core.util.MementoTokenizer;

import java.util.Hashtable;
import java.util.Map;

/**
 * @author Evgen Vidolob
 */
public class JavaCore {
    public static  String         COMPILER_TASK_TAGS = org.eclipse.jdt.core.JavaCore.COMPILER_TASK_TAGS;

    /**
     * The plug-in identifier of the Java core support
     * (value <code>"org.eclipse.jdt.core"</code>).
     */
    public static final String PLUGIN_ID = "org.eclipse.jdt.core" ; //$NON-NLS-1$
    /**
     * Compiler option ID: Setting Source Compatibility Mode.
     * <p>Specify whether which source level compatibility is used. From 1.4 on, <code>'assert'</code> is a keyword
     *    reserved for assertion support. Also note, than when toggling to 1.4 mode, the target VM
     *    level should be set to <code>"1.4"</code> and the compliance mode should be <code>"1.4"</code>.</p>
     * <p>Source level 1.5 is necessary to enable generics, autoboxing, covariance, annotations, enumerations
     *    enhanced for loop, static imports and varargs. Once toggled, the target VM level should be set to <code>"1.5"</code>
     *    and the compliance mode should be <code>"1.5"</code>.</p>
     * <p>Source level 1.6 is necessary to enable the computation of stack map tables. Once toggled, the target
     *    VM level should be set to <code>"1.6"</code> and the compliance mode should be <code>"1.6"</code>.</p>
     * <p>Once the source level 1.7 is toggled, the target VM level should be set to <code>"1.7"</code> and the compliance mode
     *    should be <code>"1.7"</code>.</p>
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.source"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "1.3", "1.4", "1.5", "1.6", "1.7" }</code></dd>
     * <dt>Default:</dt><dd><code>"1.3"</code></dd>
     * </dl>
     * @since 2.0
     * @category CompilerOptionID
     */
    public static final String COMPILER_SOURCE = org.eclipse.jdt.core.JavaCore.COMPILER_SOURCE;


    /**
     * Compiler option ID: Setting Compliance Level.
     * <p>Select the compliance level for the compiler. In <code>"1.3"</code> mode, source and target settings
     *    should not go beyond <code>"1.3"</code> level.</p>
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.compliance"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "1.3", "1.4", "1.5", "1.6", "1.7" }</code></dd>
     * <dt>Default:</dt><dd><code>"1.4"</code></dd>
     * </dl>
     * @since 2.0
     * @category CompilerOptionID
     */
    public static final String COMPILER_COMPLIANCE = org.eclipse.jdt.core.JavaCore.COMPILER_COMPLIANCE;

    /**
     * Configurable option value: {@value}.
     * @since 2.0
     * @category OptionValue
     */
    public static final String SPACE = org.eclipse.jdt.core.JavaCore.SPACE;

    /**
     * Compiler option ID: Name of Annotation Type for Non-Null Types.
     * <p>This option defines a fully qualified Java type name that the compiler may use
     *    to perform special null analysis.</p>
     * <p>If the annotation specified by this option is applied to a type in a method
     *    signature or variable declaration, this will be interpreted as a specification
     *    that <code>null</code> is <b>not</b> a legal value in that position. Currently
     *    supported positions are: method parameters, method return type, fields and local variables.</p>
     * <p>For values declared with this annotation, the compiler will never trigger a null
     *    reference diagnostic (as controlled by {@link #COMPILER_PB_POTENTIAL_NULL_REFERENCE}
     *    and {@link #COMPILER_PB_NULL_REFERENCE}), because the assumption is made that null
     *    will never occur at runtime in these positions.</p>
     * <p>The compiler may furthermore check adherence to the null specification as further
     *    controlled by {@link #COMPILER_PB_NULL_SPECIFICATION_VIOLATION},
     *    {@link #COMPILER_PB_NULL_ANNOTATION_INFERENCE_CONFLICT} and
     *    {@link #COMPILER_PB_NULL_UNCHECKED_CONVERSION}.</p>
     * <p>This option only has an effect if the option {@link #COMPILER_ANNOTATION_NULL_ANALYSIS} is enabled.</p>
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.annotation.nonnull"</code></dd>
     * <dt>Possible values:</dt><dd>any legal, fully qualified Java type name; must resolve to an annotation type.</dd>
     * <dt>Default:</dt><dd><code>"org.eclipse.jdt.annotation.NonNull"</code></dd>
     * </dl>
     * @since 3.8
     * @category CompilerOptionID
     */
    public static final String COMPILER_NONNULL_ANNOTATION_NAME = org.eclipse.jdt.core.JavaCore.COMPILER_NONNULL_ANNOTATION_NAME;
    /**
     * Configurable option value: {@value}.
     * @since 2.0
     * @category OptionValue
     */
    public static final String INSERT = org.eclipse.jdt.core.JavaCore.INSERT;

    /**
     * Compiler option ID: Name of Annotation Type for Nullable Types.
     * <p>This option defines a fully qualified Java type name that the compiler may use
     *    to perform special null analysis.</p>
     * <p>If the annotation specified by this option is applied to a type in a method
     *    signature or variable declaration, this will be interpreted as a specification
     *    that <code>null</code> is a legal value in that position. Currently supported
     *    positions are: method parameters, method return type, fields and local variables.</p>
     * <p>If a value whose type
     *    is annotated with this annotation is dereferenced without checking for null,
     *    the compiler will trigger a diagnostic as further controlled by
     *    {@link #COMPILER_PB_POTENTIAL_NULL_REFERENCE}.</p>
     * <p>The compiler may furthermore check adherence to the null specification as
     *    further controlled by {@link #COMPILER_PB_NULL_SPECIFICATION_VIOLATION},
     *    {@link #COMPILER_PB_NULL_ANNOTATION_INFERENCE_CONFLICT} and
     *    {@link #COMPILER_PB_NULL_UNCHECKED_CONVERSION}.</p>
     * <p>This option only has an effect if the option {@link #COMPILER_ANNOTATION_NULL_ANALYSIS} is enabled.</p>
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.annotation.nullable"</code></dd>
     * <dt>Possible values:</dt><dd>any legal, fully qualified Java type name; must resolve to an annotation type.</dd>
     * <dt>Default:</dt><dd><code>"org.eclipse.jdt.annotation.Nullable"</code></dd>
     * </dl>
     * @since 3.8
     * @category CompilerOptionID
     */
    public static final String COMPILER_NULLABLE_ANNOTATION_NAME = org.eclipse.jdt.core.JavaCore.COMPILER_NULLABLE_ANNOTATION_NAME;

//    IClasspathContainer jreContainer = new JREContainer(new StandardVMType(), null, this);

    public static IClasspathContainer getClasspathContainer(IPath containerPath, JavaProject project) throws JavaModelException {
//        if (containerPath.toOSString().startsWith("org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType")) {
//            return new JREContainer(standardVMType, containerPath, project);
//        }
//        if(containerPath.toOSString().equals(MavenClasspathContainer.CONTAINER_ID)){
//            return new MavenClasspathContainer(project);
//        }
//        return null;
        JavaModelManager manager = JavaModelManager.getJavaModelManager();
        IClasspathContainer container = manager.getClasspathContainer(containerPath, project);
        return container;
    }

    /**
     * Creates and returns a new classpath entry of kind <code>CPE_CONTAINER</code>
     * for the given path. This method is fully equivalent to calling
     * {@link #newContainerEntry(IPath, org.eclipse.jdt.core.IAccessRule[], org.eclipse.jdt.core.IClasspathAttribute[], boolean)
     * newContainerEntry(containerPath, new IAccessRule[0], new IClasspathAttribute[0], false)}.
     * <p>
     * @param containerPath the path identifying the container, it must be formed of two
     * 	segments
     * @return a new container classpath entry
     *
     * @see JavaCore#getClasspathContainer(IPath, org.eclipse.jdt.core.IJavaProject)
     */
    public static IClasspathEntry newContainerEntry(IPath containerPath) {
        return newContainerEntry(
                containerPath,
                ClasspathEntry.NO_ACCESS_RULES,
                ClasspathEntry.NO_EXTRA_ATTRIBUTES,
                false/*not exported*/);
    }

    /**
     * Creates and returns a new classpath entry of kind <code>CPE_CONTAINER</code>
     * for the given path. This method is fully equivalent to calling
     * {@link #newContainerEntry(IPath, org.eclipse.jdt.core.IAccessRule[], org.eclipse.jdt.core.IClasspathAttribute[], boolean)
     * newContainerEntry(containerPath, new IAccessRule[0], new IClasspathAttribute[0], isExported)}.
     *
     * @param containerPath the path identifying the container, it must be formed of at least
     * 	one segment (ID+hints)
     * @param isExported a boolean indicating whether this entry is contributed to dependent
     *    projects in addition to the output location
     * @return a new container classpath entry
     *
     * @see JavaCore#getClasspathContainer(IPath, org.eclipse.jdt.core.IJavaProject)
     * @see JavaCore#setClasspathContainer(IPath, org.eclipse.jdt.core.IJavaProject[], org.eclipse.jdt.core.IClasspathContainer[], org.eclipse.core.runtime.IProgressMonitor)
     * @since 2.0
     */
    public static IClasspathEntry newContainerEntry(IPath containerPath, boolean isExported) {
        return newContainerEntry(
                containerPath,
                ClasspathEntry.NO_ACCESS_RULES,
                ClasspathEntry.NO_EXTRA_ATTRIBUTES,
                isExported);
    }

    /**
     * Creates and returns a new classpath entry of kind <code>CPE_CONTAINER</code>
     * for the given path. The path of the container will be used during resolution so as to map this
     * container entry to a set of other classpath entries the container is acting for.
     * <p>
     * A container entry allows to express indirect references to a set of libraries, projects and variable entries,
     * which can be interpreted differently for each Java project where it is used.
     * A classpath container entry can be resolved using <code>JavaCore.getResolvedClasspathContainer</code>,
     * and updated with <code>JavaCore.classpathContainerChanged</code>
     * <p>
     * A container is exclusively resolved by a <code>ClasspathContainerInitializer</code> registered onto the
     * extension point "org.eclipse.jdt.core.classpathContainerInitializer".
     * <p>
     * A container path must be formed of at least one segment, where: <ul>
     * <li> the first segment is a unique ID identifying the target container, there must be a container initializer registered
     * 	onto this ID through the extension point  "org.eclipse.jdt.core.classpathContainerInitializer". </li>
     * <li> the remaining segments will be passed onto the initializer, and can be used as additional
     * 	hints during the initialization phase. </li>
     * </ul>
     * <p>
     * Example of an ClasspathContainerInitializer for a classpath container denoting a default JDK container:
     * <pre>
     * containerEntry = JavaCore.newContainerEntry(new Path("MyProvidedJDK/default"));
     *
     * &lt;extension
     *    point="org.eclipse.jdt.core.classpathContainerInitializer"&gt;
     *    &lt;containerInitializer
     *       id="MyProvidedJDK"
     *       class="com.example.MyInitializer"/&gt;
     * </pre>
     * <p>
     * The access rules determine the set of accessible source and class files
     * in the container. If the list of access rules is empty, then all files
     * in this container are accessible.
     * See {@link org.eclipse.jdt.core.IAccessRule} for a detailed description of access
     * rules. Note that if an entry defined by the container defines access rules,
     * then these access rules are combined with the given access rules.
     * The given access rules are considered first, then the entry's access rules are
     * considered.
     * </p>
     * <p>
     * The <code>extraAttributes</code> list contains name/value pairs that must be persisted with
     * this entry. If no extra attributes are provided, an empty array must be passed in.<br>
     * Note that this list should not contain any duplicate name.
     * </p>
     * <p>
     * The <code>isExported</code> flag indicates whether this entry is contributed to dependent
     * projects. If not exported, dependent projects will not see any of the classes from this entry.
     * If exported, dependent projects will concatenate the accessible files patterns of this entry with the
     * accessible files patterns of the projects, and they will concatenate the non accessible files patterns of this entry
     * with the non accessible files patterns of the project.
     * </p>
     * <p>
     * Note that this operation does not attempt to validate classpath containers
     * or access the resources at the given paths.
     * </p>
     *
     * @param containerPath the path identifying the container, it must be formed of at least
     * 	one segment (ID+hints)
     * @param accessRules the possibly empty list of access rules for this entry
     * @param extraAttributes the possibly empty list of extra attributes to persist with this entry
     * @param isExported a boolean indicating whether this entry is contributed to dependent
     *    projects in addition to the output location
     * @return a new container classpath entry
     *
     * @see JavaCore#getClasspathContainer(IPath, org.eclipse.jdt.core.IJavaProject)
     * @see JavaCore#setClasspathContainer(IPath, org.eclipse.jdt.core.IJavaProject[], org.eclipse.jdt.core.IClasspathContainer[], org.eclipse.core.runtime.IProgressMonitor)
     * @see JavaCore#newContainerEntry(IPath, boolean)
     * @see JavaCore#newAccessRule(IPath, int)
     * @since 3.1
     */
    public static IClasspathEntry newContainerEntry(
            IPath containerPath,
            IAccessRule[] accessRules,
            IClasspathAttribute[] extraAttributes,
            boolean isExported) {

        if (containerPath == null) {
            throw new ClasspathEntry.AssertionFailedException("Container path cannot be null"); //$NON-NLS-1$
        } else if (containerPath.segmentCount() < 1) {
            throw new ClasspathEntry.AssertionFailedException("Illegal classpath container path: \'" + containerPath.makeRelative().toString() + "\', must have at least one segment (containerID+hints)"); //$NON-NLS-1$//$NON-NLS-2$
        }
        if (accessRules == null) {
            accessRules = ClasspathEntry.NO_ACCESS_RULES;
        }
        if (extraAttributes == null) {
            extraAttributes = ClasspathEntry.NO_EXTRA_ATTRIBUTES;
        }
        return new ClasspathEntry(
                IPackageFragmentRoot.K_SOURCE,
                IClasspathEntry.CPE_CONTAINER,
                containerPath,
                ClasspathEntry.INCLUDE_ALL, // inclusion patterns
                ClasspathEntry.EXCLUDE_NONE, // exclusion patterns
                null, // source attachment
                null, // source attachment root
                null, // specific output folder
                isExported,
                accessRules,
                true, // combine access rules
                extraAttributes);
    }


    /**
     * Creates and returns a new classpath entry of kind <code>CPE_SOURCE</code>
     * for all files in the project's source folder identified by the given
     * absolute workspace-relative path.
     * <p>
     * The convenience method is fully equivalent to:
     * <pre>
     * newSourceEntry(path, new IPath[] {}, new IPath[] {}, null);
     * </pre>
     * </p>
     *
     * @param path the absolute workspace-relative path of a source folder
     * @return a new source classpath entry
     * @see #newSourceEntry(IPath, IPath[], IPath[], IPath)
     */
    public static IClasspathEntry newSourceEntry(IPath path) {

        return newSourceEntry(path, ClasspathEntry.INCLUDE_ALL, ClasspathEntry.EXCLUDE_NONE, null /*output location*/);
    }

    /**
     * Creates and returns a new classpath entry of kind <code>CPE_SOURCE</code>
     * for the project's source folder identified by the given absolute
     * workspace-relative path but excluding all source files with paths
     * matching any of the given patterns.
     * <p>
     * The convenience method is fully equivalent to:
     * <pre>
     * newSourceEntry(path, new IPath[] {}, exclusionPatterns, null);
     * </pre>
     * </p>
     *
     * @param path the absolute workspace-relative path of a source folder
     * @param exclusionPatterns the possibly empty list of exclusion patterns
     *    represented as relative paths
     * @return a new source classpath entry
     * @see #newSourceEntry(IPath, IPath[], IPath[], IPath)
     * @since 2.1
     */
    public static IClasspathEntry newSourceEntry(IPath path, IPath[] exclusionPatterns) {

        return newSourceEntry(path, ClasspathEntry.INCLUDE_ALL, exclusionPatterns, null /*output location*/);
    }

    /**
     * Creates and returns a new classpath entry of kind <code>CPE_SOURCE</code>
     * for the project's source folder identified by the given absolute
     * workspace-relative path but excluding all source files with paths
     * matching any of the given patterns, and associated with a specific output location
     * (that is, ".class" files are not going to the project default output location).
     * <p>
     * The convenience method is fully equivalent to:
     * <pre>
     * newSourceEntry(path, new IPath[] {}, exclusionPatterns, specificOutputLocation);
     * </pre>
     * </p>
     *
     * @param path the absolute workspace-relative path of a source folder
     * @param exclusionPatterns the possibly empty list of exclusion patterns
     *    represented as relative paths
     * @param specificOutputLocation the specific output location for this source entry (<code>null</code> if using project default ouput location)
     * @return a new source classpath entry
     * @see #newSourceEntry(IPath, IPath[], IPath[], IPath)
     * @since 2.1
     */
    public static IClasspathEntry newSourceEntry(IPath path, IPath[] exclusionPatterns, IPath specificOutputLocation) {

        return newSourceEntry(path, ClasspathEntry.INCLUDE_ALL, exclusionPatterns, specificOutputLocation);
    }

    /**
     * Creates and returns a new classpath entry of kind <code>CPE_SOURCE</code>
     * for the project's source folder identified by the given absolute
     * workspace-relative path but excluding all source files with paths
     * matching any of the given patterns, and associated with a specific output location
     * (that is, ".class" files are not going to the project default output location).
     * <p>
     * The convenience method is fully equivalent to:
     * <pre>
     * newSourceEntry(path, new IPath[] {}, exclusionPatterns, specificOutputLocation, new IClasspathAttribute[] {});
     * </pre>
     * </p>
     *
     * @param path the absolute workspace-relative path of a source folder
     * @param inclusionPatterns the possibly empty list of inclusion patterns
     *    represented as relative paths
     * @param exclusionPatterns the possibly empty list of exclusion patterns
     *    represented as relative paths
     * @param specificOutputLocation the specific output location for this source entry (<code>null</code> if using project default ouput location)
     * @return a new source classpath entry
     * @see #newSourceEntry(IPath, IPath[], IPath[], IPath, IClasspathAttribute[])
     * @since 3.0
     */
    public static IClasspathEntry newSourceEntry(IPath path, IPath[] inclusionPatterns, IPath[] exclusionPatterns, IPath specificOutputLocation) {
        return newSourceEntry(path, inclusionPatterns, exclusionPatterns, specificOutputLocation, ClasspathEntry.NO_EXTRA_ATTRIBUTES);
    }

    /**
     * Creates and returns a new classpath entry of kind <code>CPE_SOURCE</code>
     * for the project's source folder identified by the given absolute
     * workspace-relative path using the given inclusion and exclusion patterns
     * to determine which source files are included, and the given output path
     * to control the output location of generated files.
     * <p>
     * The source folder is referred to using an absolute path relative to the
     * workspace root, e.g. <code>/Project/src</code>. A project's source
     * folders are located with that project. That is, a source classpath
     * entry specifying the path <code>/P1/src</code> is only usable for
     * project <code>P1</code>.
     * </p>
     * <p>
     * The inclusion patterns determines the initial set of source files that
     * are to be included; the exclusion patterns are then used to reduce this
     * set. When no inclusion patterns are specified, the initial file set
     * includes all relevent files in the resource tree rooted at the source
     * entry's path. On the other hand, specifying one or more inclusion
     * patterns means that all <b>and only</b> files matching at least one of
     * the specified patterns are to be included. If exclusion patterns are
     * specified, the initial set of files is then reduced by eliminating files
     * matched by at least one of the exclusion patterns. Inclusion and
     * exclusion patterns look like relative file paths with wildcards and are
     * interpreted relative to the source entry's path. File patterns are
     * case-sensitive can contain '**', '*' or '?' wildcards (see
     * {@link IClasspathEntry#getExclusionPatterns()} for the full description
     * of their syntax and semantics). The resulting set of files are included
     * in the corresponding package fragment root; all package fragments within
     * the root will have children of type <code>ICompilationUnit</code>.
     * </p>
     * <p>
     * For example, if the source folder path is
     * <code>/Project/src</code>, there are no inclusion filters, and the
     * exclusion pattern is
     * <code>com/xyz/tests/&#42;&#42;</code>, then source files
     * like <code>/Project/src/com/xyz/Foo.java</code>
     * and <code>/Project/src/com/xyz/utils/Bar.java</code> would be included,
     * whereas <code>/Project/src/com/xyz/tests/T1.java</code>
     * and <code>/Project/src/com/xyz/tests/quick/T2.java</code> would be
     * excluded.
     * </p>
     * <p>
     * Additionally, a source entry can be associated with a specific output location.
     * By doing so, the Java builder will ensure that the generated ".class" files will
     * be issued inside this output location, as opposed to be generated into the
     * project default output location (when output location is <code>null</code>).
     * Note that multiple source entries may target the same output location.
     * The output location is referred to using an absolute path relative to the
     * workspace root, e.g. <code>"/Project/bin"</code>, it must be located inside
     * the same project as the source folder.
     * </p>
     * <p>
     * Also note that all sources/binaries inside a project are contributed as
     * a whole through a project entry
     * (see <code>JavaCore.newProjectEntry</code>). Particular source entries
     * cannot be selectively exported.
     * </p>
     * <p>
     * The <code>extraAttributes</code> list contains name/value pairs that must be persisted with
     * this entry. If no extra attributes are provided, an empty array must be passed in.<br>
     * Note that this list should not contain any duplicate name.
     * </p>
     *
     * @param path the absolute workspace-relative path of a source folder
     * @param inclusionPatterns the possibly empty list of inclusion patterns
     *    represented as relative paths
     * @param exclusionPatterns the possibly empty list of exclusion patterns
     *    represented as relative paths
     * @param specificOutputLocation the specific output location for this source entry (<code>null</code> if using project default ouput location)
     * @param extraAttributes the possibly empty list of extra attributes to persist with this entry
     * @return a new source classpath entry with the given exclusion patterns
     * @see IClasspathEntry#getInclusionPatterns()
     * @see IClasspathEntry#getExclusionPatterns()
     * @see IClasspathEntry#getOutputLocation()
     * @since 3.1
     */
    public static IClasspathEntry newSourceEntry(IPath path, IPath[] inclusionPatterns, IPath[] exclusionPatterns, IPath specificOutputLocation, IClasspathAttribute[] extraAttributes) {

        if (path == null) throw new ClasspathEntry.AssertionFailedException("Source path cannot be null"); //$NON-NLS-1$
        if (!path.isAbsolute()) throw new ClasspathEntry.AssertionFailedException("Path for IClasspathEntry must be absolute"); //$NON-NLS-1$
        if (exclusionPatterns == null) {
            exclusionPatterns = ClasspathEntry.EXCLUDE_NONE;
        }
        if (inclusionPatterns == null) {
            inclusionPatterns = ClasspathEntry.INCLUDE_ALL;
        }
        if (extraAttributes == null) {
            extraAttributes = ClasspathEntry.NO_EXTRA_ATTRIBUTES;
        }
        return new ClasspathEntry(
                IPackageFragmentRoot.K_SOURCE,
                IClasspathEntry.CPE_SOURCE,
                path,
                inclusionPatterns,
                exclusionPatterns,
                null, // source attachment
                null, // source attachment root
                specificOutputLocation, // custom output location
                false,
                null,
                false, // no access rules to combine
                extraAttributes);
    }

    /**
     * Creates and returns a new non-exported classpath entry of kind <code>CPE_VARIABLE</code>
     * for the given path. This method is fully equivalent to calling
     * {@link #newVariableEntry(IPath, IPath, IPath, IAccessRule[], IClasspathAttribute[], boolean)
     * newVariableEntry(variablePath, variableSourceAttachmentPath, sourceAttachmentRootPath, new IAccessRule[0], new IClasspathAttribute[0], false)}.
     *
     * @param variablePath the path of the binary archive; first segment is the
     *   name of a classpath variable
     * @param variableSourceAttachmentPath the path of the corresponding source archive,
     *    or <code>null</code> if none; if present, the first segment is the
     *    name of a classpath variable (not necessarily the same variable
     *    as the one that begins <code>variablePath</code>)
     * @param sourceAttachmentRootPath the location of the root of the source files within the source archive
     *    or <code>null</code> if <code>variableSourceAttachmentPath</code> is also <code>null</code>
     * @return a new library classpath entry
     */
    public static IClasspathEntry newVariableEntry(
            IPath variablePath,
            IPath variableSourceAttachmentPath,
            IPath sourceAttachmentRootPath) {

        return newVariableEntry(variablePath, variableSourceAttachmentPath, sourceAttachmentRootPath, false);
    }

    /**
     * Creates and returns a new classpath entry of kind <code>CPE_VARIABLE</code>
     * for the given path. This method is fully equivalent to calling
     * {@link #newVariableEntry(IPath, IPath, IPath, IAccessRule[], IClasspathAttribute[], boolean)
     * newVariableEntry(variablePath, variableSourceAttachmentPath, sourceAttachmentRootPath, new IAccessRule[0], new IClasspathAttribute[0], isExported)}.
     *
     * @param variablePath the path of the binary archive; first segment is the
     *   name of a classpath variable
     * @param variableSourceAttachmentPath the path of the corresponding source archive,
     *    or <code>null</code> if none; if present, the first segment is the
     *    name of a classpath variable (not necessarily the same variable
     *    as the one that begins <code>variablePath</code>)
     * @param variableSourceAttachmentRootPath the location of the root of the source files within the source archive
     *    or <code>null</code> if <code>variableSourceAttachmentPath</code> is also <code>null</code>
     * @param isExported indicates whether this entry is contributed to dependent
     * 	  projects in addition to the output location
     * @return a new variable classpath entry
     * @since 2.0
     */
    public static IClasspathEntry newVariableEntry(
            IPath variablePath,
            IPath variableSourceAttachmentPath,
            IPath variableSourceAttachmentRootPath,
            boolean isExported) {

        return newVariableEntry(
                variablePath,
                variableSourceAttachmentPath,
                variableSourceAttachmentRootPath,
                ClasspathEntry.NO_ACCESS_RULES,
                ClasspathEntry.NO_EXTRA_ATTRIBUTES,
                isExported);
    }

    /**
     * Creates and returns a new classpath entry of kind <code>CPE_VARIABLE</code>
     * for the given path. The first segment of the path is the name of a classpath variable.
     * The trailing segments of the path will be appended to resolved variable path.
     * <p>
     * A variable entry allows to express indirect references on a classpath to other projects or libraries,
     * depending on what the classpath variable is referring.
     * </p>
     * <p>
     * It is possible to register an automatic initializer (<code>ClasspathVariableInitializer</code>),
     * which will be invoked through the extension point "org.eclipse.jdt.core.classpathVariableInitializer".
     * After resolution, a classpath variable entry may either correspond to a project or a library entry.
     * </p>
     * <p>
     * e.g. Here are some examples of variable path usage
     * </p>
     * <ul>
     * <li> "JDTCORE" where variable <code>JDTCORE</code> is
     *		bound to "c:/jars/jdtcore.jar". The resolved classpath entry is denoting the library "c:\jars\jdtcore.jar"</li>
     * <li> "JDTCORE" where variable <code>JDTCORE</code> is
     *		bound to "/Project_JDTCORE". The resolved classpath entry is denoting the project "/Project_JDTCORE"</li>
     * <li> "PLUGINS/com.example/example.jar" where variable <code>PLUGINS</code>
     *      is bound to "c:/eclipse/plugins". The resolved classpath entry is denoting the library "c:\eclipse\plugins\com.example\example.jar"</li>
     * </ul>
     * <p>
     * The access rules determine the set of accessible class files
     * in the project or library. If the list of access rules is empty then all files
     * in this project or library are accessible.
     * See {@link IAccessRule} for a detailed description of access rules.
     * </p>
     * <p>
     * The <code>extraAttributes</code> list contains name/value pairs that must be persisted with
     * this entry. If no extra attributes are provided, an empty array must be passed in.<br>
     * Note that this list should not contain any duplicate name.
     * </p>
     * <p>
     * The <code>isExported</code> flag indicates whether this entry is contributed to dependent
     * projects. If not exported, dependent projects will not see any of the classes from this entry.
     * If exported, dependent projects will concatenate the accessible files patterns of this entry with the
     * accessible files patterns of the projects, and they will concatenate the non accessible files patterns of this entry
     * with the non accessible files patterns of the project.
     * </p>
     * <p>
     * Note that this operation does not attempt to validate classpath variables
     * or access the resources at the given paths.
     * </p>
     *
     * @param variablePath the path of the binary archive; first segment is the
     *   name of a classpath variable
     * @param variableSourceAttachmentPath the path of the corresponding source archive,
     *    or <code>null</code> if none; if present, the first segment is the
     *    name of a classpath variable (not necessarily the same variable
     *    as the one that begins <code>variablePath</code>)
     * @param variableSourceAttachmentRootPath the location of the root of the source files within the source archive
     *    or <code>null</code> if <code>variableSourceAttachmentPath</code> is also <code>null</code>
     * @param accessRules the possibly empty list of access rules for this entry
     * @param extraAttributes the possibly empty list of extra attributes to persist with this entry
     * @param isExported indicates whether this entry is contributed to dependent
     * 	  projects in addition to the output location
     * @return a new variable classpath entry
     * @since 3.1
     */
    public static IClasspathEntry newVariableEntry(
            IPath variablePath,
            IPath variableSourceAttachmentPath,
            IPath variableSourceAttachmentRootPath,
            IAccessRule[] accessRules,
            IClasspathAttribute[] extraAttributes,
            boolean isExported) {

        if (variablePath == null) throw new ClasspathEntry.AssertionFailedException("Variable path cannot be null"); //$NON-NLS-1$
        if (variablePath.segmentCount() < 1) {
            throw new ClasspathEntry.AssertionFailedException("Illegal classpath variable path: \'" + variablePath.makeRelative().toString() + "\', must have at least one segment"); //$NON-NLS-1$//$NON-NLS-2$
        }
        if (accessRules == null) {
            accessRules = ClasspathEntry.NO_ACCESS_RULES;
        }
        if (extraAttributes == null) {
            extraAttributes = ClasspathEntry.NO_EXTRA_ATTRIBUTES;
        }

        return new ClasspathEntry(
                IPackageFragmentRoot.K_SOURCE,
                IClasspathEntry.CPE_VARIABLE,
                variablePath,
                ClasspathEntry.INCLUDE_ALL, // inclusion patterns
                ClasspathEntry.EXCLUDE_NONE, // exclusion patterns
                variableSourceAttachmentPath, // source attachment
                variableSourceAttachmentRootPath, // source attachment root
                null, // specific output folder
                isExported,
                accessRules,
                false, // no access rules to combine
                extraAttributes);
    }

    /**
     * Creates and returns a new non-exported classpath entry of kind <code>CPE_PROJECT</code>
     * for the project identified by the given absolute path.
     * <p>
     * This method is fully equivalent to calling
     * {@link #newProjectEntry(IPath, IAccessRule[], boolean, IClasspathAttribute[], boolean)
     * newProjectEntry(path, new IAccessRule[0], true, new IClasspathAttribute[0], false)}.
     * </p>
     *
     * @param path the absolute path of the binary archive
     * @return a new project classpath entry
     */
    public static IClasspathEntry newProjectEntry(IPath path) {
        return newProjectEntry(path, false);
    }

    /**
     * Creates and returns a new classpath entry of kind <code>CPE_PROJECT</code>
     * for the project identified by the given absolute path.
     * <p>
     * This method is fully equivalent to calling
     * {@link #newProjectEntry(IPath, IAccessRule[], boolean, IClasspathAttribute[], boolean)
     * newProjectEntry(path, new IAccessRule[0], true, new IClasspathAttribute[0], isExported)}.
     * </p>
     *
     * @param path the absolute path of the prerequisite project
     * @param isExported indicates whether this entry is contributed to dependent
     * 	  projects in addition to the output location
     * @return a new project classpath entry
     * @since 2.0
     */
    public static IClasspathEntry newProjectEntry(IPath path, boolean isExported) {

        if (!path.isAbsolute()) throw new ClasspathEntry.AssertionFailedException("Path for IClasspathEntry must be absolute"); //$NON-NLS-1$

        return newProjectEntry(
                path,
                ClasspathEntry.NO_ACCESS_RULES,
                true,
                ClasspathEntry.NO_EXTRA_ATTRIBUTES,
                isExported);
    }

    /**
     * Creates and returns a new classpath entry of kind <code>CPE_PROJECT</code>
     * for the project identified by the given absolute path.
     * <p>
     * A project entry is used to denote a prerequisite project on a classpath.
     * The referenced project will be contributed as a whole, either as sources (in the Java Model, it
     * contributes all its package fragment roots) or as binaries (when building, it contributes its
     * whole output location).
     * </p>
     * <p>
     * A project reference allows to indirect through another project, independently from its internal layout.
     * </p><p>
     * The prerequisite project is referred to using an absolute path relative to the workspace root.
     * </p>
     * <p>
     * The access rules determine the set of accessible class files
     * in the project. If the list of access rules is empty then all files
     * in this project are accessible.
     * See {@link IAccessRule} for a detailed description of access rules.
     * </p>
     * <p>
     * The <code>combineAccessRules</code> flag indicates whether access rules of one (or more)
     * exported entry of the project should be combined with the given access rules. If they should
     * be combined, the given access rules are considered first, then the entry's access rules are
     * considered.
     * </p>
     * <p>
     * The <code>extraAttributes</code> list contains name/value pairs that must be persisted with
     * this entry. If no extra attributes are provided, an empty array must be passed in.<br>
     * Note that this list should not contain any duplicate name.
     * </p>
     * <p>
     * The <code>isExported</code> flag indicates whether this entry is contributed to dependent
     * projects. If not exported, dependent projects will not see any of the classes from this entry.
     * If exported, dependent projects will concatenate the accessible files patterns of this entry with the
     * accessible files patterns of the projects, and they will concatenate the non accessible files patterns of this entry
     * with the non accessible files patterns of the project.
     * </p>
     *
     * @param path the absolute path of the prerequisite project
     * @param accessRules the possibly empty list of access rules for this entry
     * @param combineAccessRules whether the access rules of the project's exported entries should be combined with the given access rules
     * @param extraAttributes the possibly empty list of extra attributes to persist with this entry
     * @param isExported indicates whether this entry is contributed to dependent
     * 	  projects in addition to the output location
     * @return a new project classpath entry
     * @since 3.1
     */
    public static IClasspathEntry newProjectEntry(
            IPath path,
            IAccessRule[] accessRules,
            boolean combineAccessRules,
            IClasspathAttribute[] extraAttributes,
            boolean isExported) {

        if (!path.isAbsolute()) throw new ClasspathEntry.AssertionFailedException("Path for IClasspathEntry must be absolute"); //$NON-NLS-1$
        if (accessRules == null) {
            accessRules = ClasspathEntry.NO_ACCESS_RULES;
        }
        if (extraAttributes == null) {
            extraAttributes = ClasspathEntry.NO_EXTRA_ATTRIBUTES;
        }
        return new ClasspathEntry(
                IPackageFragmentRoot.K_SOURCE,
                IClasspathEntry.CPE_PROJECT,
                path,
                ClasspathEntry.INCLUDE_ALL, // inclusion patterns
                ClasspathEntry.EXCLUDE_NONE, // exclusion patterns
                null, // source attachment
                null, // source attachment root
                null, // specific output folder
                isExported,
                accessRules,
                combineAccessRules,
                extraAttributes);
    }

    /**
     * Creates and returns a new access rule with the given file pattern and kind.
     * <p>
     * The rule kind is one of {@link IAccessRule#K_ACCESSIBLE}, {@link IAccessRule#K_DISCOURAGED},
     * or {@link IAccessRule#K_NON_ACCESSIBLE}, optionally combined with {@link IAccessRule#IGNORE_IF_BETTER},
     * e.g. <code>IAccessRule.K_NON_ACCESSIBLE | IAccessRule.IGNORE_IF_BETTER</code>.
     * </p>
     *
     * @param filePattern the file pattern this access rule should match
     * @param kind one of {@link IAccessRule#K_ACCESSIBLE}, {@link IAccessRule#K_DISCOURAGED},
     *                     or {@link IAccessRule#K_NON_ACCESSIBLE}, optionally combined with
     *                     {@link IAccessRule#IGNORE_IF_BETTER}
     * @return a new access rule
     * @since 3.1
     *
     * @see IClasspathEntry#getExclusionPatterns()
     */
    public static IAccessRule newAccessRule(IPath filePattern, int kind) {
        return new ClasspathAccessRule(filePattern, kind);
    }

    /**
     * This is a helper method, which returns the resolved classpath entry denoted
     * by a given entry (if it is a variable entry). It is obtained by resolving the variable
     * reference in the first segment. Returns <code>null</code> if unable to resolve using
     * the following algorithm:
     * <ul>
     * <li> if variable segment cannot be resolved, returns <code>null</code></li>
     * <li> finds a project, JAR or binary folder in the workspace at the resolved path location</li>
     * <li> if none finds an external JAR file or folder outside the workspace at the resolved path location </li>
     * <li> if none returns <code>null</code></li>
     * </ul>
     * <p>
     * Variable source attachment path and root path are also resolved and recorded in the resulting classpath entry.
     * </p>
     * <p>
     * NOTE: This helper method does not handle classpath containers, for which should rather be used
     * <code>JavaCore#getClasspathContainer(IPath, IJavaProject)</code>.
     * </p>
     *
     * @param entry the given variable entry
     * @return the resolved library or project classpath entry, or <code>null</code>
     *   if the given variable entry could not be resolved to a valid classpath entry
     */
    public static IClasspathEntry getResolvedClasspathEntry(IClasspathEntry entry) {
        return JavaModelManager.getJavaModelManager().resolveVariableEntry(entry, false/*don't use previous session value*/);
    }

    /**
     * Creates and returns a new classpath attribute with the given name and the given value.
     *
     * @return a new classpath attribute
     */
    public static IClasspathAttribute newClasspathAttribute(String name, String value) {
        return new ClasspathAttribute(name, value);
    }

    /**
     * Creates and returns a new non-exported classpath entry of kind <code>CPE_LIBRARY</code> for the
     * JAR or folder identified by the given absolute path. This specifies that all package fragments
     * within the root will have children of type <code>IClassFile</code>.
     * This method is fully equivalent to calling
     * {@link #newLibraryEntry(IPath, IPath, IPath, IAccessRule[], IClasspathAttribute[], boolean)
     * newLibraryEntry(path, sourceAttachmentPath, sourceAttachmentRootPath, new IAccessRule[0], new IClasspathAttribute[0], false)}.
     *
     * @param path the path to the library
     * @param sourceAttachmentPath the absolute path of the corresponding source archive or folder,
     *    or <code>null</code> if none. Note, since 3.0, an empty path is allowed to denote no source attachment.
     *    Since 3.4, this path can also denote a path external to the workspace.
     *   and will be automatically converted to <code>null</code>.
     * @param sourceAttachmentRootPath the location of the root of the source files within the source archive or folder
     *    or <code>null</code> if this location should be automatically detected.
     * @return a new library classpath entry
     */
    public static IClasspathEntry newLibraryEntry(
            IPath path,
            IPath sourceAttachmentPath,
            IPath sourceAttachmentRootPath) {

        return newLibraryEntry(
                path,
                sourceAttachmentPath,
                sourceAttachmentRootPath,
                ClasspathEntry.NO_ACCESS_RULES,
                ClasspathEntry.NO_EXTRA_ATTRIBUTES,
                false/*not exported*/);
    }

    /**
     * Creates and returns a new classpath entry of kind <code>CPE_LIBRARY</code> for the JAR or folder
     * identified by the given absolute path. This specifies that all package fragments within the root
     * will have children of type <code>IClassFile</code>.
     * This method is fully equivalent to calling
     * {@link #newLibraryEntry(IPath, IPath, IPath, IAccessRule[], IClasspathAttribute[], boolean)
     * newLibraryEntry(path, sourceAttachmentPath, sourceAttachmentRootPath, new IAccessRule[0], new IClasspathAttribute[0], isExported)}.
     *
     * @param path the path to the library
     * @param sourceAttachmentPath the absolute path of the corresponding source archive or folder,
     *    or <code>null</code> if none. Note, since 3.0, an empty path is allowed to denote no source attachment.
     *   and will be automatically converted to <code>null</code>. Since 3.4, this path can also denote a path external
     *   to the workspace.
     * @param sourceAttachmentRootPath the location of the root of the source files within the source archive or folder
     *    or <code>null</code> if this location should be automatically detected.
     * @param isExported indicates whether this entry is contributed to dependent
     * 	  projects in addition to the output location
     * @return a new library classpath entry
     * @since 2.0
     */
    public static IClasspathEntry newLibraryEntry(
            IPath path,
            IPath sourceAttachmentPath,
            IPath sourceAttachmentRootPath,
            boolean isExported) {

        return newLibraryEntry(
                path,
                sourceAttachmentPath,
                sourceAttachmentRootPath,
                ClasspathEntry.NO_ACCESS_RULES,
                ClasspathEntry.NO_EXTRA_ATTRIBUTES,
                isExported);
    }

    /**
     * Creates and returns a new classpath entry of kind <code>CPE_LIBRARY</code> for the JAR or folder
     * identified by the given absolute path. This specifies that all package fragments within the root
     * will have children of type <code>IClassFile</code>.
     * <p>
     * A library entry is used to denote a prerequisite JAR or root folder containing binaries.
     * The target JAR can either be defined internally to the workspace (absolute path relative
     * to the workspace root), or externally to the workspace (absolute path in the file system).
     * The target root folder can also be defined internally to the workspace (absolute path relative
     * to the workspace root), or - since 3.4 - externally to the workspace (absolute path in the file system).
     * Since 3.5, the path to the library can also be relative to the project using ".." as the first segment.
     * <p>
     * e.g. Here are some examples of binary path usage<ul>
     *	<li><code> "c:\jdk1.2.2\jre\lib\rt.jar" </code> - reference to an external JAR on Windows</li>
     *	<li><code> "/Project/someLib.jar" </code> - reference to an internal JAR on Windows or Linux</li>
     *	<li><code> "/Project/classes/" </code> - reference to an internal binary folder on Windows or Linux</li>
     *	<li><code> "/home/usr/classes" </code> - reference to an external binary folder on Linux</li>
     *	<li><code> "../../lib/someLib.jar" </code> - reference to an external JAR that is a sibbling of the workspace on either platform</li>
     * </ul>
     * Note that on non-Windows platform, a path <code>"/some/lib.jar"</code> is ambiguous.
     * It can be a path to an external JAR (its file system path being <code>"/some/lib.jar"</code>)
     * or it can be a path to an internal JAR (<code>"some"</code> being a project in the workspace).
     * Such an ambiguity is solved when the classpath entry is used (e.g. in {@link org.eclipse.jdt.core.IJavaProject#getPackageFragmentRoots()}).
     * If the resource <code>"lib.jar"</code> exists in project <code>"some"</code>, then it is considered an
     * internal JAR. Otherwise it is an external JAR.
     * <p>Also note that this operation does not attempt to validate or access the
     * resources at the given paths.
     * </p><p>
     * The access rules determine the set of accessible class files
     * in the library. If the list of access rules is empty then all files
     * in this library are accessible.
     * See {@link IAccessRule} for a detailed description of access
     * rules.
     * </p>
     * <p>
     * The <code>extraAttributes</code> list contains name/value pairs that must be persisted with
     * this entry. If no extra attributes are provided, an empty array must be passed in.<br>
     * Note that this list should not contain any duplicate name.
     * </p>
     * <p>
     * The <code>isExported</code> flag indicates whether this entry is contributed to dependent
     * projects. If not exported, dependent projects will not see any of the classes from this entry.
     * If exported, dependent projects will concatenate the accessible files patterns of this entry with the
     * accessible files patterns of the projects, and they will concatenate the non accessible files patterns of this entry
     * with the non accessible files patterns of the project.
     * </p>
     * <p>
     * Since 3.5, if the libray is a ZIP archive, the "Class-Path" clause (if any) in the "META-INF/MANIFEST.MF" is read
     * and referenced ZIP archives are added to the {@link org.eclipse.jdt.core.IJavaProject#getResolvedClasspath(boolean) resolved classpath}.
     * </p>
     *
     * @param path the path to the library
     * @param sourceAttachmentPath the absolute path of the corresponding source archive or folder,
     *    or <code>null</code> if none. Note, since 3.0, an empty path is allowed to denote no source attachment.
     *   and will be automatically converted to <code>null</code>. Since 3.4, this path can also denote a path external
     *   to the workspace.
     * @param sourceAttachmentRootPath the location of the root of the source files within the source archive or folder
     *    or <code>null</code> if this location should be automatically detected.
     * @param accessRules the possibly empty list of access rules for this entry
     * @param extraAttributes the possibly empty list of extra attributes to persist with this entry
     * @param isExported indicates whether this entry is contributed to dependent
     * 	  projects in addition to the output location
     * @return a new library classpath entry
     * @since 3.1
     */
    public static IClasspathEntry newLibraryEntry(
            IPath path,
            IPath sourceAttachmentPath,
            IPath sourceAttachmentRootPath,
            IAccessRule[] accessRules,
            IClasspathAttribute[] extraAttributes,
            boolean isExported) {

        if (path == null) throw new ClasspathEntry.AssertionFailedException("Library path cannot be null"); //$NON-NLS-1$
        if (accessRules == null) {
            accessRules = ClasspathEntry.NO_ACCESS_RULES;
        }
        if (extraAttributes == null) {
            extraAttributes = ClasspathEntry.NO_EXTRA_ATTRIBUTES;
        }
        boolean hasDotDot = ClasspathEntry.hasDotDot(path);
        if (!hasDotDot && !path.isAbsolute()) throw new ClasspathEntry.AssertionFailedException("Path for IClasspathEntry must be absolute: " + path); //$NON-NLS-1$
        if (sourceAttachmentPath != null) {
            if (sourceAttachmentPath.isEmpty()) {
                sourceAttachmentPath = null; // treat empty path as none
            } else if (!sourceAttachmentPath.isAbsolute()) {
                throw new ClasspathEntry.AssertionFailedException("Source attachment path '" //$NON-NLS-1$
                                                                                                + sourceAttachmentPath
                                                                                                + "' for IClasspathEntry must be absolute"); //$NON-NLS-1$
            }
        }
        return new ClasspathEntry(
                IPackageFragmentRoot.K_BINARY,
                IClasspathEntry.CPE_LIBRARY,
//                hasDotDot ? path : JavaProject.canonicalizedPath(path),
                path,
                ClasspathEntry.INCLUDE_ALL, // inclusion patterns
                ClasspathEntry.EXCLUDE_NONE, // exclusion patterns
                sourceAttachmentPath,
                sourceAttachmentRootPath,
                null, // specific output folder
                isExported,
                accessRules,
                false, // no access rules to combine
                extraAttributes);
    }


    /**
     * Bind a container reference path to some actual containers (<code>IClasspathContainer</code>).
     * This API must be invoked whenever changes in container need to be reflected onto the JavaModel.
     * Containers can have distinct values in different projects, therefore this API considers a
     * set of projects with their respective containers.
     * <p>
     * <code>containerPath</code> is the path under which these values can be referenced through
     * container classpath entries (<code>IClasspathEntry#CPE_CONTAINER</code>). A container path
     * is formed by a first ID segment followed with extra segments, which can be used as additional hints
     * for the resolution. The container ID is used to identify a <code>ClasspathContainerInitializer</code>
     * registered on the extension point "org.eclipse.jdt.core.classpathContainerInitializer".
     * </p>
     * <p>
     * There is no assumption that each individual container value passed in argument
     * (<code>respectiveContainers</code>) must answer the exact same path when requested
     * <code>IClasspathContainer#getPath</code>.
     * Indeed, the containerPath is just an indication for resolving it to an actual container object. It can be
     * delegated to a <code>ClasspathContainerInitializer</code>, which can be activated through the extension
     * point "org.eclipse.jdt.core.ClasspathContainerInitializer").
     * </p>
     * <p>
     * In reaction to changing container values, the JavaModel will be updated to reflect the new
     * state of the updated container. A combined Java element delta will be notified to describe the corresponding
     * classpath changes resulting from the container update. This operation is batched, and automatically eliminates
     * unnecessary updates (new container is same as old one). This operation acquires a lock on the workspace's root.
     * </p>
     * <p>
     * This functionality cannot be used while the workspace is locked, since
     * it may create/remove some resource markers.
     * </p>
     * <p>
     * Classpath container values are persisted locally to the workspace, but
     * are not preserved from a session to another. It is thus highly recommended to register a
     * <code>ClasspathContainerInitializer</code> for each referenced container
     * (through the extension point "org.eclipse.jdt.core.ClasspathContainerInitializer").
     * </p>
     * <p>
     * Note: setting a container to <code>null</code> will cause it to be lazily resolved again whenever
     * its value is required. In particular, this will cause a registered initializer to be invoked
     * again.
     * </p>
     * @param containerPath - the name of the container reference, which is being updated
     * @param affectedProjects - the set of projects for which this container is being bound
     * @param respectiveContainers - the set of respective containers for the affected projects
     * @param monitor a monitor to report progress
     * @throws JavaModelException
     * @see ClasspathContainerInitializer
     * @see #getClasspathContainer(IPath, IJavaProject)
     * @see IClasspathContainer
     * @since 2.0
     */
    public static void setClasspathContainer(IPath containerPath, IJavaProject[] affectedProjects, IClasspathContainer[] respectiveContainers, IProgressMonitor monitor) throws JavaModelException {
        if (affectedProjects.length != respectiveContainers.length)
            throw new ClasspathEntry.AssertionFailedException("Projects and containers collections should have the same size"); //$NON-NLS-1$
        if (affectedProjects.length == 1) {
            IClasspathContainer container = respectiveContainers[0];
            if (container != null) {
                JavaModelManager manager = JavaModelManager.getJavaModelManager();
                IJavaProject project = affectedProjects[0];
                IClasspathContainer existingCointainer = manager.containerGet(project, containerPath);
                if (existingCointainer == null) {
                    manager.containerBeingInitializedPut(project, containerPath, container);
                    return;
                }
            }
        }
        SetContainerOperation operation = new SetContainerOperation(containerPath, affectedProjects, respectiveContainers);
        operation.runOperation(monitor);
    }
    /**
     * Returns the Java element corresponding to the given file, or
     * <code>null</code> if unable to associate the given file
     * with a Java element.
     *
     * <p>The file must be one of:</p>
     *	<ul>
     *	<li>a file with one of the {@link JavaCore#getJavaLikeExtensions()
     *      Java-like extensions} - the element returned is the corresponding <code>ICompilationUnit</code></li>
     *	<li>a <code>.class</code> file - the element returned is the corresponding <code>IClassFile</code></li>
     *	<li>a ZIP archive (e.g. a <code>.jar</code>, a <code>.zip</code> file, etc.) - the element returned is the corresponding <code>IPackageFragmentRoot</code></li>
     *	</ul>
     * <p>
     * Creating a Java element has the side effect of creating and opening all of the
     * element's parents if they are not yet open.
     * </p>
     *
     * @param file the given file
     * @return the Java element corresponding to the given file, or
     * <code>null</code> if unable to associate the given file
     * with a Java element
     */
    public static IJavaElement create(IFile file) {
        return JavaModelManager.create(file, null);
    }

    /**
     * Returns the package fragment or package fragment root corresponding to the given folder, or
     * <code>null</code> if unable to associate the given folder with a Java element.
     * <p>
     * Note that a package fragment root is returned rather than a default package.
     * </p>
     * <p>
     * Creating a Java element has the side effect of creating and opening all of the
     * element's parents if they are not yet open.
     * </p>
     *
     * @param folder the given folder
     * @return the package fragment or package fragment root corresponding to the given folder, or
     * <code>null</code> if unable to associate the given folder with a Java element
     */
    public static IJavaElement create(IFolder folder) {
        return JavaModelManager.create(folder, null/*unknown java project*/);
    }
    /**
     * Returns the Java project corresponding to the given project.
     * <p>
     * Creating a Java Project has the side effect of creating and opening all of the
     * project's parents if they are not yet open.
     * </p>
     * <p>
     * Note that no check is done at this time on the existence or the java nature of this project.
     * </p>
     *
     * @param project the given project
     * @return the Java project corresponding to the given project, null if the given project is null
     */
    public static IJavaProject create(IProject project) {
        if (project == null) {
            return null;
        }
        JavaModel javaModel = JavaModelManager.getJavaModelManager().getJavaModel();
        return javaModel.getJavaProject(project);
    }
    /**
     * Returns the Java element corresponding to the given resource, or
     * <code>null</code> if unable to associate the given resource
     * with a Java element.
     * <p>
     * The resource must be one of:
     * </p>
     *	<ul>
     *	<li>a project - the element returned is the corresponding <code>IJavaProject</code></li>
     *	<li>a file with one of the {@link JavaCore#getJavaLikeExtensions()
     *      Java-like extensions} - the element returned is the corresponding <code>ICompilationUnit</code></li>
     *	<li>a <code>.class</code> file - the element returned is the corresponding <code>IClassFile</code></li>
     *	<li>a ZIP archive (e.g. a <code>.jar</code>, a <code>.zip</code> file, etc.) - the element returned is the corresponding <code>IPackageFragmentRoot</code></li>
     *  <li>a folder - the element returned is the corresponding <code>IPackageFragmentRoot</code>
     *    	or <code>IPackageFragment</code></li>
     *  <li>the workspace root resource - the element returned is the <code>IJavaModel</code></li>
     *	</ul>
     * <p>
     * Creating a Java element has the side effect of creating and opening all of the
     * element's parents if they are not yet open.
     * </p>
     *
     * @param resource the given resource
     * @return the Java element corresponding to the given resource, or
     * <code>null</code> if unable to associate the given resource
     * with a Java element
     */
    public static IJavaElement create(IResource resource) {
        return JavaModelManager.create(resource, null/*unknown java project*/);
    }

    /**
     * Returns the Java model element corresponding to the given handle identifier
     * generated by <code>IJavaElement.getHandleIdentifier()</code>, or
     * <code>null</code> if unable to create the associated element.
     *
     * @param handleIdentifier the given handle identifier
     * @param project
     * @return the Java element corresponding to the handle identifier
     */
    public static IJavaElement create(String handleIdentifier, JavaProject project) {
        return create(handleIdentifier, DefaultWorkingCopyOwner.PRIMARY, project);
    }

    /**
     * Returns the Java model element corresponding to the given handle identifier
     * generated by <code>IJavaElement.getHandleIdentifier()</code>, or
     * <code>null</code> if unable to create the associated element.
     * If the returned Java element is an <code>ICompilationUnit</code> or an element
     * inside a compilation unit, the compilation unit's owner is the given owner if such a
     * working copy exists, otherwise the compilation unit is a primary compilation unit.
     *
     * @param handleIdentifier the given handle identifier
     * @param owner the owner of the returned compilation unit, ignored if the returned
     *   element is not a compilation unit, or an element inside a compilation unit
     * @return the Java element corresponding to the handle identifier
     * @since 3.0
     */
    public static IJavaElement create(String handleIdentifier, WorkingCopyOwner owner, JavaProject project) {
        if (handleIdentifier == null) {
            return null;
        }
        if (owner == null)
            owner = DefaultWorkingCopyOwner.PRIMARY;
        MementoTokenizer memento = new MementoTokenizer(handleIdentifier);

        return project.getHandleFromMemento(memento, owner);
    }

    /**
     * Returns the table of the current options. Initially, all options have their default values,
     * and this method returns a table that includes all known options.
     * <p>
     * Helper constants have been defined on JavaCore for each of the option IDs
     * (categorized in Code assist option ID, Compiler option ID and Core option ID)
     * and some of their acceptable values (categorized in Option value). Some
     * options accept open value sets beyond the documented constant values.
     * </p>
     * <p>
     * Note: each release may add new options.
     * </p>
     * <p>Returns a default set of options even if the platform is not running.</p>
     *
     * @return table of current settings of all options
     *   (key type: <code>String</code>; value type: <code>String</code>)
     * @see #getDefaultOptions()
     * @see JavaCorePreferenceInitializer for changing default settings
     */
    public static Hashtable<String, String> getOptions() {
        Map<String, String> defaultOptionsMap = new CompilerOptions().getMap(); // compiler defaults

        // Override some compiler defaults
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.COMPILER_LOCAL_VARIABLE_ATTR, org.eclipse.jdt.core.JavaCore.GENERATE);
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.COMPILER_CODEGEN_UNUSED_LOCAL, org.eclipse.jdt.core.JavaCore.PRESERVE);
        defaultOptionsMap.put(JavaCore.COMPILER_TASK_TAGS, org.eclipse.jdt.core.JavaCore.DEFAULT_TASK_TAGS);
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.COMPILER_TASK_PRIORITIES, org.eclipse.jdt.core.JavaCore.DEFAULT_TASK_PRIORITIES);
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.COMPILER_TASK_CASE_SENSITIVE, org.eclipse.jdt.core.JavaCore.ENABLED);
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.COMPILER_DOC_COMMENT_SUPPORT, org.eclipse.jdt.core.JavaCore.ENABLED);
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.COMPILER_PB_FORBIDDEN_REFERENCE, org.eclipse.jdt.core.JavaCore.ERROR);

        // Builder settings
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CORE_JAVA_BUILD_RESOURCE_COPY_FILTER, ""); //$NON-NLS-1$
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CORE_JAVA_BUILD_INVALID_CLASSPATH, org.eclipse.jdt.core.JavaCore.ABORT);
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CORE_JAVA_BUILD_DUPLICATE_RESOURCE, org.eclipse.jdt.core.JavaCore.WARNING);
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CORE_JAVA_BUILD_CLEAN_OUTPUT_FOLDER, org.eclipse.jdt.core.JavaCore.CLEAN);

        // JavaCore settings
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CORE_JAVA_BUILD_ORDER, org.eclipse.jdt.core.JavaCore.IGNORE);
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CORE_INCOMPLETE_CLASSPATH, org.eclipse.jdt.core.JavaCore.ERROR);
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CORE_CIRCULAR_CLASSPATH, org.eclipse.jdt.core.JavaCore.ERROR);
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CORE_INCOMPATIBLE_JDK_LEVEL, org.eclipse.jdt.core.JavaCore.IGNORE);
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CORE_OUTPUT_LOCATION_OVERLAPPING_ANOTHER_SOURCE, org.eclipse.jdt.core.JavaCore.ERROR);
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CORE_ENABLE_CLASSPATH_EXCLUSION_PATTERNS, org.eclipse.jdt.core.JavaCore.ENABLED);
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CORE_ENABLE_CLASSPATH_MULTIPLE_OUTPUT_LOCATIONS, org.eclipse.jdt.core.JavaCore.ENABLED);

        // Formatter settings
        defaultOptionsMap.putAll(DefaultCodeFormatterConstants.getEclipseDefaultSettings());

        // CodeAssist settings
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CODEASSIST_VISIBILITY_CHECK, org.eclipse.jdt.core.JavaCore.DISABLED);
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CODEASSIST_DEPRECATION_CHECK, org.eclipse.jdt.core.JavaCore.DISABLED);
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CODEASSIST_IMPLICIT_QUALIFICATION, org.eclipse.jdt.core.JavaCore.DISABLED);
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CODEASSIST_FIELD_PREFIXES, ""); //$NON-NLS-1$
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CODEASSIST_STATIC_FIELD_PREFIXES, ""); //$NON-NLS-1$
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CODEASSIST_STATIC_FINAL_FIELD_PREFIXES, ""); //$NON-NLS-1$
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CODEASSIST_LOCAL_PREFIXES, ""); //$NON-NLS-1$
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CODEASSIST_ARGUMENT_PREFIXES, ""); //$NON-NLS-1$
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CODEASSIST_FIELD_SUFFIXES, ""); //$NON-NLS-1$
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CODEASSIST_STATIC_FIELD_SUFFIXES, ""); //$NON-NLS-1$
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CODEASSIST_STATIC_FINAL_FIELD_SUFFIXES, ""); //$NON-NLS-1$
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CODEASSIST_LOCAL_SUFFIXES, ""); //$NON-NLS-1$
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CODEASSIST_ARGUMENT_SUFFIXES, ""); //$NON-NLS-1$
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CODEASSIST_FORBIDDEN_REFERENCE_CHECK, org.eclipse.jdt.core.JavaCore.ENABLED);
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CODEASSIST_DISCOURAGED_REFERENCE_CHECK, org.eclipse.jdt.core.JavaCore.DISABLED);
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CODEASSIST_CAMEL_CASE_MATCH, org.eclipse.jdt.core.JavaCore.ENABLED);
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.CODEASSIST_SUGGEST_STATIC_IMPORTS, org.eclipse.jdt.core.JavaCore.ENABLED);

        // Time out for parameter names
        defaultOptionsMap.put(org.eclipse.jdt.core.JavaCore.TIMEOUT_FOR_PARAMETER_NAME_FROM_ATTACHED_JAVADOC, "50"); //$NON-NLS-1$

        return new Hashtable<>(defaultOptionsMap);
    }

    /**
     * Helper method for returning one option value only. Equivalent to <code>(String)JavaCore.getOptions().get(optionName)</code>
     * Note that it may answer <code>null</code> if this option does not exist.
     * <p>
     * Helper constants have been defined on JavaCore for each of the option IDs
     * (categorized in Code assist option ID, Compiler option ID and Core option ID)
     * and some of their acceptable values (categorized in Option value). Some
     * options accept open value sets beyond the documented constant values.
     * </p>
     * <p>
     * Note: each release may add new options.
     * </p>
     *
     * @param optionName the name of an option
     * @return the String value of a given option
     * @see JavaCore#getDefaultOptions()
     * @see JavaCorePreferenceInitializer for changing default settings
     * @since 2.0
     */
    public static String getOption(String optionName) {
//        return JavaModelManager.getJavaModelManager().getOption(optionName);
        return getOptions().get(optionName);
    }

    /**
     * Removes the file extension from the given file name, if it has a Java-like file
     * extension. Otherwise the file name itself is returned.
     * Note this removes the dot ('.') before the extension as well.
     *
     * @param fileName the name of a file
     * @return the fileName without the Java-like extension
     * @since 3.2
     */
    public static String removeJavaLikeExtension(String fileName) {
        return Util.getNameWithoutJavaLikeExtension(fileName);
    }

    /**
     * Sets the default compiler options inside the given options map according
     * to the given compliance.
     *
     * <p>The given compliance must be one of those supported by the compiler,
     * that is one of the acceptable values for option {@link #COMPILER_COMPLIANCE}.</p>
     *
     * <p>The list of modified options is currently:</p>
     * <ul>
     * <li>{@link #COMPILER_COMPLIANCE}</li>
     * <li>{@link #COMPILER_SOURCE}</li>
     * <li>{@link #COMPILER_CODEGEN_TARGET_PLATFORM}</li>
     * <li>{@link #COMPILER_PB_ASSERT_IDENTIFIER}</li>
     * <li>{@link #COMPILER_PB_ENUM_IDENTIFIER}</li>
     * <li>{@link #COMPILER_CODEGEN_INLINE_JSR_BYTECODE} for compliance levels 1.5 and greater</li>
     * </ul>
     *
     * <p>If the given compliance is unknown, the given map is unmodified.</p>
     *
     * @param compliance the given compliance
     * @param options the given options map
     * @since 3.3
     */
    public static void setComplianceOptions(String compliance, Map options) {
        switch((int) (CompilerOptions.versionToJdkLevel(compliance) >>> 16)) {
            case ClassFileConstants.MAJOR_VERSION_1_3:
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_COMPLIANCE, org.eclipse.jdt.core.JavaCore.VERSION_1_3);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_SOURCE, org.eclipse.jdt.core.JavaCore.VERSION_1_3);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, org.eclipse.jdt.core.JavaCore.VERSION_1_1);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, org.eclipse.jdt.core.JavaCore.IGNORE);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_PB_ENUM_IDENTIFIER, org.eclipse.jdt.core.JavaCore.IGNORE);
                break;
            case ClassFileConstants.MAJOR_VERSION_1_4:
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_COMPLIANCE, org.eclipse.jdt.core.JavaCore.VERSION_1_4);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_SOURCE, org.eclipse.jdt.core.JavaCore.VERSION_1_3);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, org.eclipse.jdt.core.JavaCore.VERSION_1_2);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, org.eclipse.jdt.core.JavaCore.WARNING);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_PB_ENUM_IDENTIFIER, org.eclipse.jdt.core.JavaCore.WARNING);
                break;
            case ClassFileConstants.MAJOR_VERSION_1_5:
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_COMPLIANCE, org.eclipse.jdt.core.JavaCore.VERSION_1_5);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_SOURCE, org.eclipse.jdt.core.JavaCore.VERSION_1_5);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, org.eclipse.jdt.core.JavaCore.VERSION_1_5);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, org.eclipse.jdt.core.JavaCore.ERROR);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_PB_ENUM_IDENTIFIER, org.eclipse.jdt.core.JavaCore.ERROR);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_CODEGEN_INLINE_JSR_BYTECODE, org.eclipse.jdt.core.JavaCore.ENABLED);
                break;
            case ClassFileConstants.MAJOR_VERSION_1_6:
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_COMPLIANCE, org.eclipse.jdt.core.JavaCore.VERSION_1_6);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_SOURCE, org.eclipse.jdt.core.JavaCore.VERSION_1_6);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, org.eclipse.jdt.core.JavaCore.VERSION_1_6);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, org.eclipse.jdt.core.JavaCore.ERROR);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_PB_ENUM_IDENTIFIER, org.eclipse.jdt.core.JavaCore.ERROR);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_CODEGEN_INLINE_JSR_BYTECODE, org.eclipse.jdt.core.JavaCore.ENABLED);
                break;
            case ClassFileConstants.MAJOR_VERSION_1_7:
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_COMPLIANCE, org.eclipse.jdt.core.JavaCore.VERSION_1_7);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_SOURCE, org.eclipse.jdt.core.JavaCore.VERSION_1_7);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, org.eclipse.jdt.core.JavaCore.VERSION_1_7);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, org.eclipse.jdt.core.JavaCore.ERROR);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_PB_ENUM_IDENTIFIER, org.eclipse.jdt.core.JavaCore.ERROR);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_CODEGEN_INLINE_JSR_BYTECODE, org.eclipse.jdt.core.JavaCore.ENABLED);
                break;
            case ClassFileConstants.MAJOR_VERSION_1_8:
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_COMPLIANCE, org.eclipse.jdt.core.JavaCore.VERSION_1_8);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_SOURCE, org.eclipse.jdt.core.JavaCore.VERSION_1_8);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, org.eclipse.jdt.core.JavaCore.VERSION_1_8);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, org.eclipse.jdt.core.JavaCore.ERROR);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_PB_ENUM_IDENTIFIER, org.eclipse.jdt.core.JavaCore.ERROR);
                options.put(org.eclipse.jdt.core.JavaCore.COMPILER_CODEGEN_INLINE_JSR_BYTECODE, org.eclipse.jdt.core.JavaCore.ENABLED);
                break;
        }
    }
}
