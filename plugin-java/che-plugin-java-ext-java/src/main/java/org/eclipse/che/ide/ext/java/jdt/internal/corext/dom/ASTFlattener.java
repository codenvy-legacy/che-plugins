/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.java.jdt.internal.corext.dom;

import org.eclipse.che.ide.ext.java.jdt.core.dom.AST;
import org.eclipse.che.ide.ext.java.jdt.core.dom.ASTNode;
import org.eclipse.che.ide.ext.java.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.che.ide.ext.java.jdt.core.dom.Annotation;
import org.eclipse.che.ide.ext.java.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.che.ide.ext.java.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.che.ide.ext.java.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.che.ide.ext.java.jdt.core.dom.ArrayAccess;
import org.eclipse.che.ide.ext.java.jdt.core.dom.ArrayCreation;
import org.eclipse.che.ide.ext.java.jdt.core.dom.ArrayInitializer;
import org.eclipse.che.ide.ext.java.jdt.core.dom.ArrayType;
import org.eclipse.che.ide.ext.java.jdt.core.dom.AssertStatement;
import org.eclipse.che.ide.ext.java.jdt.core.dom.Assignment;
import org.eclipse.che.ide.ext.java.jdt.core.dom.Block;
import org.eclipse.che.ide.ext.java.jdt.core.dom.BlockComment;
import org.eclipse.che.ide.ext.java.jdt.core.dom.BodyDeclaration;
import org.eclipse.che.ide.ext.java.jdt.core.dom.BooleanLiteral;
import org.eclipse.che.ide.ext.java.jdt.core.dom.BreakStatement;
import org.eclipse.che.ide.ext.java.jdt.core.dom.CastExpression;
import org.eclipse.che.ide.ext.java.jdt.core.dom.CatchClause;
import org.eclipse.che.ide.ext.java.jdt.core.dom.CharacterLiteral;
import org.eclipse.che.ide.ext.java.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.che.ide.ext.java.jdt.core.dom.CompilationUnit;
import org.eclipse.che.ide.ext.java.jdt.core.dom.ConditionalExpression;
import org.eclipse.che.ide.ext.java.jdt.core.dom.ConstructorInvocation;
import org.eclipse.che.ide.ext.java.jdt.core.dom.ContinueStatement;
import org.eclipse.che.ide.ext.java.jdt.core.dom.DoStatement;
import org.eclipse.che.ide.ext.java.jdt.core.dom.EmptyStatement;
import org.eclipse.che.ide.ext.java.jdt.core.dom.EnhancedForStatement;
import org.eclipse.che.ide.ext.java.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.che.ide.ext.java.jdt.core.dom.EnumDeclaration;
import org.eclipse.che.ide.ext.java.jdt.core.dom.Expression;
import org.eclipse.che.ide.ext.java.jdt.core.dom.ExpressionStatement;
import org.eclipse.che.ide.ext.java.jdt.core.dom.FieldAccess;
import org.eclipse.che.ide.ext.java.jdt.core.dom.FieldDeclaration;
import org.eclipse.che.ide.ext.java.jdt.core.dom.ForStatement;
import org.eclipse.che.ide.ext.java.jdt.core.dom.IExtendedModifier;
import org.eclipse.che.ide.ext.java.jdt.core.dom.IfStatement;
import org.eclipse.che.ide.ext.java.jdt.core.dom.ImportDeclaration;
import org.eclipse.che.ide.ext.java.jdt.core.dom.InfixExpression;
import org.eclipse.che.ide.ext.java.jdt.core.dom.Initializer;
import org.eclipse.che.ide.ext.java.jdt.core.dom.InstanceofExpression;
import org.eclipse.che.ide.ext.java.jdt.core.dom.Javadoc;
import org.eclipse.che.ide.ext.java.jdt.core.dom.LabeledStatement;
import org.eclipse.che.ide.ext.java.jdt.core.dom.LineComment;
import org.eclipse.che.ide.ext.java.jdt.core.dom.MarkerAnnotation;
import org.eclipse.che.ide.ext.java.jdt.core.dom.MemberRef;
import org.eclipse.che.ide.ext.java.jdt.core.dom.MemberValuePair;
import org.eclipse.che.ide.ext.java.jdt.core.dom.MethodDeclaration;
import org.eclipse.che.ide.ext.java.jdt.core.dom.MethodInvocation;
import org.eclipse.che.ide.ext.java.jdt.core.dom.MethodRef;
import org.eclipse.che.ide.ext.java.jdt.core.dom.MethodRefParameter;
import org.eclipse.che.ide.ext.java.jdt.core.dom.Modifier;
import org.eclipse.che.ide.ext.java.jdt.core.dom.Name;
import org.eclipse.che.ide.ext.java.jdt.core.dom.NormalAnnotation;
import org.eclipse.che.ide.ext.java.jdt.core.dom.NullLiteral;
import org.eclipse.che.ide.ext.java.jdt.core.dom.NumberLiteral;
import org.eclipse.che.ide.ext.java.jdt.core.dom.PackageDeclaration;
import org.eclipse.che.ide.ext.java.jdt.core.dom.ParameterizedType;
import org.eclipse.che.ide.ext.java.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.che.ide.ext.java.jdt.core.dom.PostfixExpression;
import org.eclipse.che.ide.ext.java.jdt.core.dom.PrefixExpression;
import org.eclipse.che.ide.ext.java.jdt.core.dom.PrimitiveType;
import org.eclipse.che.ide.ext.java.jdt.core.dom.QualifiedName;
import org.eclipse.che.ide.ext.java.jdt.core.dom.QualifiedType;
import org.eclipse.che.ide.ext.java.jdt.core.dom.ReturnStatement;
import org.eclipse.che.ide.ext.java.jdt.core.dom.SimpleName;
import org.eclipse.che.ide.ext.java.jdt.core.dom.SimpleType;
import org.eclipse.che.ide.ext.java.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.che.ide.ext.java.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.che.ide.ext.java.jdt.core.dom.Statement;
import org.eclipse.che.ide.ext.java.jdt.core.dom.StringLiteral;
import org.eclipse.che.ide.ext.java.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.che.ide.ext.java.jdt.core.dom.SuperFieldAccess;
import org.eclipse.che.ide.ext.java.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.che.ide.ext.java.jdt.core.dom.SwitchCase;
import org.eclipse.che.ide.ext.java.jdt.core.dom.SwitchStatement;
import org.eclipse.che.ide.ext.java.jdt.core.dom.SynchronizedStatement;
import org.eclipse.che.ide.ext.java.jdt.core.dom.TagElement;
import org.eclipse.che.ide.ext.java.jdt.core.dom.TextElement;
import org.eclipse.che.ide.ext.java.jdt.core.dom.ThisExpression;
import org.eclipse.che.ide.ext.java.jdt.core.dom.ThrowStatement;
import org.eclipse.che.ide.ext.java.jdt.core.dom.TryStatement;
import org.eclipse.che.ide.ext.java.jdt.core.dom.Type;
import org.eclipse.che.ide.ext.java.jdt.core.dom.TypeDeclaration;
import org.eclipse.che.ide.ext.java.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.che.ide.ext.java.jdt.core.dom.TypeLiteral;
import org.eclipse.che.ide.ext.java.jdt.core.dom.TypeParameter;
import org.eclipse.che.ide.ext.java.jdt.core.dom.UnionType;
import org.eclipse.che.ide.ext.java.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.che.ide.ext.java.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.che.ide.ext.java.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.che.ide.ext.java.jdt.core.dom.WhileStatement;
import org.eclipse.che.ide.ext.java.jdt.core.dom.WildcardType;
import org.eclipse.che.ide.runtime.Assert;

import java.util.Iterator;
import java.util.List;

//import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;

public class ASTFlattener extends GenericVisitor {

    /**
     * The string buffer into which the serialized representation of the AST is
     * written.
     */
    protected StringBuffer fBuffer;

    /** Creates a new AST printer. */
    public ASTFlattener() {
        this.fBuffer = new StringBuffer();
    }

    /**
     * Returns the string accumulated in the visit.
     *
     * @return the serialized
     */
    public String getResult() {
        return this.fBuffer.toString();
    }

    /** Resets this printer so that it can be used again. */
    public void reset() {
        this.fBuffer.setLength(0);
    }

    public static String asString(ASTNode node) {
        //      Assert.isTrue(node.getAST().apiLevel() == ASTProvider.SHARED_AST_LEVEL);

        ASTFlattener flattener = new ASTFlattener();
        node.accept(flattener);
        return flattener.getResult();
    }

    @Override
    protected boolean visitNode(ASTNode node) {
        Assert.isTrue(false, "No implementation to flatten node: " + node.toString()); //$NON-NLS-1$
        return false;
    }

    /**
     * Appends the text representation of the given modifier flags, followed by a single space.
     * Used for 3.0 modifiers and annotations.
     *
     * @param ext
     *         the list of modifier and annotation nodes
     *         (element type: <code>IExtendedModifier</code>)
     */
    private void printModifiers(List<IExtendedModifier> ext) {
        for (Iterator<IExtendedModifier> it = ext.iterator(); it.hasNext(); ) {
            ASTNode p = (ASTNode)it.next();
            p.accept(this);
            this.fBuffer.append(" ");//$NON-NLS-1$
        }
    }

    /*
     * @see ASTVisitor#visit(AnnotationTypeDeclaration)
     * @since 3.0
     */
    @Override
    public boolean visit(AnnotationTypeDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        printModifiers(node.modifiers());
        this.fBuffer.append("@interface ");//$NON-NLS-1$
        node.getName().accept(this);
        this.fBuffer.append(" {");//$NON-NLS-1$
        for (Iterator<BodyDeclaration> it = node.bodyDeclarations().iterator(); it.hasNext(); ) {
            BodyDeclaration d = it.next();
            d.accept(this);
        }
        this.fBuffer.append("}");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(AnnotationTypeMemberDeclaration)
     * @since 3.0
     */
    @Override
    public boolean visit(AnnotationTypeMemberDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        printModifiers(node.modifiers());
        node.getType().accept(this);
        this.fBuffer.append(" ");//$NON-NLS-1$
        node.getName().accept(this);
        this.fBuffer.append("()");//$NON-NLS-1$
        if (node.getDefault() != null) {
            this.fBuffer.append(" default ");//$NON-NLS-1$
            node.getDefault().accept(this);
        }
        this.fBuffer.append(";");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(AnonymousClassDeclaration)
     */
    @Override
    public boolean visit(AnonymousClassDeclaration node) {
        this.fBuffer.append("{");//$NON-NLS-1$
        List<BodyDeclaration> bodyDeclarations = node.bodyDeclarations();
        for (Iterator<BodyDeclaration> it = bodyDeclarations.iterator(); it.hasNext(); ) {
            BodyDeclaration b = it.next();
            b.accept(this);
        }
        this.fBuffer.append("}");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(ArrayAccess)
     */
    @Override
    public boolean visit(ArrayAccess node) {
        node.getArray().accept(this);
        this.fBuffer.append("[");//$NON-NLS-1$
        node.getIndex().accept(this);
        this.fBuffer.append("]");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(ArrayCreation)
     */
    @Override
    public boolean visit(ArrayCreation node) {
        this.fBuffer.append("new ");//$NON-NLS-1$
        ArrayType at = node.getType();
        int dims = at.getDimensions();
        Type elementType = at.getElementType();
        elementType.accept(this);
        for (Iterator<Expression> it = node.dimensions().iterator(); it.hasNext(); ) {
            this.fBuffer.append("[");//$NON-NLS-1$
            Expression e = it.next();
            e.accept(this);
            this.fBuffer.append("]");//$NON-NLS-1$
            dims--;
        }
        // add empty "[]" for each extra array dimension
        for (int i = 0; i < dims; i++) {
            this.fBuffer.append("[]");//$NON-NLS-1$
        }
        if (node.getInitializer() != null) {
            node.getInitializer().accept(this);
        }
        return false;
    }

    /*
     * @see ASTVisitor#visit(ArrayInitializer)
     */
    @Override
    public boolean visit(ArrayInitializer node) {
        this.fBuffer.append("{");//$NON-NLS-1$
        for (Iterator<Expression> it = node.expressions().iterator(); it.hasNext(); ) {
            Expression e = it.next();
            e.accept(this);
            if (it.hasNext()) {
                this.fBuffer.append(",");//$NON-NLS-1$
            }
        }
        this.fBuffer.append("}");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(ArrayType)
     */
    @Override
    public boolean visit(ArrayType node) {
        node.getComponentType().accept(this);
        this.fBuffer.append("[]");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(AssertStatement)
     */
    @Override
    public boolean visit(AssertStatement node) {
        this.fBuffer.append("assert ");//$NON-NLS-1$
        node.getExpression().accept(this);
        if (node.getMessage() != null) {
            this.fBuffer.append(" : ");//$NON-NLS-1$
            node.getMessage().accept(this);
        }
        this.fBuffer.append(";");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(Assignment)
     */
    @Override
    public boolean visit(Assignment node) {
        node.getLeftHandSide().accept(this);
        this.fBuffer.append(node.getOperator().toString());
        node.getRightHandSide().accept(this);
        return false;
    }

    /*
     * @see ASTVisitor#visit(Block)
     */
    @Override
    public boolean visit(Block node) {
        this.fBuffer.append("{");//$NON-NLS-1$
        for (Iterator<Statement> it = node.statements().iterator(); it.hasNext(); ) {
            Statement s = it.next();
            s.accept(this);
        }
        this.fBuffer.append("}");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(BlockComment)
     * @since 3.0
     */
    @Override
    public boolean visit(BlockComment node) {
        this.fBuffer.append("/* */");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(BooleanLiteral)
     */
    @Override
    public boolean visit(BooleanLiteral node) {
        if (node.booleanValue() == true) {
            this.fBuffer.append("true");//$NON-NLS-1$
        } else {
            this.fBuffer.append("false");//$NON-NLS-1$
        }
        return false;
    }

    /*
     * @see ASTVisitor#visit(BreakStatement)
     */
    @Override
    public boolean visit(BreakStatement node) {
        this.fBuffer.append("break");//$NON-NLS-1$
        if (node.getLabel() != null) {
            this.fBuffer.append(" ");//$NON-NLS-1$
            node.getLabel().accept(this);
        }
        this.fBuffer.append(";");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(CastExpression)
     */
    @Override
    public boolean visit(CastExpression node) {
        this.fBuffer.append("(");//$NON-NLS-1$
        node.getType().accept(this);
        this.fBuffer.append(")");//$NON-NLS-1$
        node.getExpression().accept(this);
        return false;
    }

    /*
     * @see ASTVisitor#visit(CatchClause)
     */
    @Override
    public boolean visit(CatchClause node) {
        this.fBuffer.append("catch (");//$NON-NLS-1$
        node.getException().accept(this);
        this.fBuffer.append(") ");//$NON-NLS-1$
        node.getBody().accept(this);
        return false;
    }

    /*
     * @see ASTVisitor#visit(CharacterLiteral)
     */
    @Override
    public boolean visit(CharacterLiteral node) {
        this.fBuffer.append(node.getEscapedValue());
        return false;
    }

    /*
     * @see ASTVisitor#visit(ClassInstanceCreation)
     */
    @Override
    public boolean visit(ClassInstanceCreation node) {
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
            this.fBuffer.append(".");//$NON-NLS-1$
        }
        this.fBuffer.append("new ");//$NON-NLS-1$
        if (node.getAST().apiLevel() >= AST.JLS3) {
            if (!node.typeArguments().isEmpty()) {
                this.fBuffer.append("<");//$NON-NLS-1$
                for (Iterator<Type> it = node.typeArguments().iterator(); it.hasNext(); ) {
                    Type t = it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        this.fBuffer.append(",");//$NON-NLS-1$
                    }
                }
                this.fBuffer.append(">");//$NON-NLS-1$
            }
            node.getType().accept(this);
        }
        this.fBuffer.append("(");//$NON-NLS-1$
        for (Iterator<Expression> it = node.arguments().iterator(); it.hasNext(); ) {
            Expression e = it.next();
            e.accept(this);
            if (it.hasNext()) {
                this.fBuffer.append(",");//$NON-NLS-1$
            }
        }
        this.fBuffer.append(")");//$NON-NLS-1$
        if (node.getAnonymousClassDeclaration() != null) {
            node.getAnonymousClassDeclaration().accept(this);
        }
        return false;
    }

    /*
     * @see ASTVisitor#visit(CompilationUnit)
     */
    @Override
    public boolean visit(CompilationUnit node) {
        if (node.getPackage() != null) {
            node.getPackage().accept(this);
        }
        for (Iterator<ImportDeclaration> it = node.imports().iterator(); it.hasNext(); ) {
            ImportDeclaration d = it.next();
            d.accept(this);
        }
        for (Iterator<AbstractTypeDeclaration> it = node.types().iterator(); it.hasNext(); ) {
            AbstractTypeDeclaration d = it.next();
            d.accept(this);
        }
        return false;
    }

    /*
     * @see ASTVisitor#visit(ConditionalExpression)
     */
    @Override
    public boolean visit(ConditionalExpression node) {
        node.getExpression().accept(this);
        this.fBuffer.append("?");//$NON-NLS-1$
        node.getThenExpression().accept(this);
        this.fBuffer.append(":");//$NON-NLS-1$
        node.getElseExpression().accept(this);
        return false;
    }

    /*
     * @see ASTVisitor#visit(ConstructorInvocation)
     */
    @Override
    public boolean visit(ConstructorInvocation node) {
        if (node.getAST().apiLevel() >= AST.JLS3) {
            if (!node.typeArguments().isEmpty()) {
                this.fBuffer.append("<");//$NON-NLS-1$
                for (Iterator<Type> it = node.typeArguments().iterator(); it.hasNext(); ) {
                    Type t = it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        this.fBuffer.append(",");//$NON-NLS-1$
                    }
                }
                this.fBuffer.append(">");//$NON-NLS-1$
            }
        }
        this.fBuffer.append("this(");//$NON-NLS-1$
        for (Iterator<Expression> it = node.arguments().iterator(); it.hasNext(); ) {
            Expression e = it.next();
            e.accept(this);
            if (it.hasNext()) {
                this.fBuffer.append(",");//$NON-NLS-1$
            }
        }
        this.fBuffer.append(");");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(ContinueStatement)
     */
    @Override
    public boolean visit(ContinueStatement node) {
        this.fBuffer.append("continue");//$NON-NLS-1$
        if (node.getLabel() != null) {
            this.fBuffer.append(" ");//$NON-NLS-1$
            node.getLabel().accept(this);
        }
        this.fBuffer.append(";");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(DoStatement)
     */
    @Override
    public boolean visit(DoStatement node) {
        this.fBuffer.append("do ");//$NON-NLS-1$
        node.getBody().accept(this);
        this.fBuffer.append(" while (");//$NON-NLS-1$
        node.getExpression().accept(this);
        this.fBuffer.append(");");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(EmptyStatement)
     */
    @Override
    public boolean visit(EmptyStatement node) {
        this.fBuffer.append(";");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(EnhancedForStatement)
     * @since 3.0
     */
    @Override
    public boolean visit(EnhancedForStatement node) {
        this.fBuffer.append("for (");//$NON-NLS-1$
        node.getParameter().accept(this);
        this.fBuffer.append(" : ");//$NON-NLS-1$
        node.getExpression().accept(this);
        this.fBuffer.append(") ");//$NON-NLS-1$
        node.getBody().accept(this);
        return false;
    }

    /*
     * @see ASTVisitor#visit(EnumConstantDeclaration)
     * @since 3.0
     */
    @Override
    public boolean visit(EnumConstantDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        printModifiers(node.modifiers());
        node.getName().accept(this);
        if (!node.arguments().isEmpty()) {
            this.fBuffer.append("(");//$NON-NLS-1$
            for (Iterator<Expression> it = node.arguments().iterator(); it.hasNext(); ) {
                Expression e = it.next();
                e.accept(this);
                if (it.hasNext()) {
                    this.fBuffer.append(",");//$NON-NLS-1$
                }
            }
            this.fBuffer.append(")");//$NON-NLS-1$
        }
        if (node.getAnonymousClassDeclaration() != null) {
            node.getAnonymousClassDeclaration().accept(this);
        }
        return false;
    }

    /*
     * @see ASTVisitor#visit(EnumDeclaration)
     * @since 3.0
     */
    @Override
    public boolean visit(EnumDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        printModifiers(node.modifiers());
        this.fBuffer.append("enum ");//$NON-NLS-1$
        node.getName().accept(this);
        this.fBuffer.append(" ");//$NON-NLS-1$
        if (!node.superInterfaceTypes().isEmpty()) {
            this.fBuffer.append("implements ");//$NON-NLS-1$
            for (Iterator<Type> it = node.superInterfaceTypes().iterator(); it.hasNext(); ) {
                Type t = it.next();
                t.accept(this);
                if (it.hasNext()) {
                    this.fBuffer.append(", ");//$NON-NLS-1$
                }
            }
            this.fBuffer.append(" ");//$NON-NLS-1$
        }
        this.fBuffer.append("{");//$NON-NLS-1$
        for (Iterator<EnumConstantDeclaration> it = node.enumConstants().iterator(); it.hasNext(); ) {
            EnumConstantDeclaration d = it.next();
            d.accept(this);
            // enum constant declarations do not include punctuation
            if (it.hasNext()) {
                // enum constant declarations are separated by commas
                this.fBuffer.append(", ");//$NON-NLS-1$
            }
        }
        if (!node.bodyDeclarations().isEmpty()) {
            this.fBuffer.append("; ");//$NON-NLS-1$
            for (Iterator<BodyDeclaration> it = node.bodyDeclarations().iterator(); it.hasNext(); ) {
                BodyDeclaration d = it.next();
                d.accept(this);
                // other body declarations include trailing punctuation
            }
        }
        this.fBuffer.append("}");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(ExpressionStatement)
     */
    @Override
    public boolean visit(ExpressionStatement node) {
        node.getExpression().accept(this);
        this.fBuffer.append(";");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(FieldAccess)
     */
    @Override
    public boolean visit(FieldAccess node) {
        node.getExpression().accept(this);
        this.fBuffer.append(".");//$NON-NLS-1$
        node.getName().accept(this);
        return false;
    }

    /*
     * @see ASTVisitor#visit(FieldDeclaration)
     */
    @Override
    public boolean visit(FieldDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        if (node.getAST().apiLevel() >= AST.JLS3) {
            printModifiers(node.modifiers());
        }
        node.getType().accept(this);
        this.fBuffer.append(" ");//$NON-NLS-1$
        for (Iterator<VariableDeclarationFragment> it = node.fragments().iterator(); it.hasNext(); ) {
            VariableDeclarationFragment f = it.next();
            f.accept(this);
            if (it.hasNext()) {
                this.fBuffer.append(", ");//$NON-NLS-1$
            }
        }
        this.fBuffer.append(";");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(ForStatement)
     */
    @Override
    public boolean visit(ForStatement node) {
        this.fBuffer.append("for (");//$NON-NLS-1$
        for (Iterator<Expression> it = node.initializers().iterator(); it.hasNext(); ) {
            Expression e = it.next();
            e.accept(this);
        }
        this.fBuffer.append("; ");//$NON-NLS-1$
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
        }
        this.fBuffer.append("; ");//$NON-NLS-1$
        for (Iterator<Expression> it = node.updaters().iterator(); it.hasNext(); ) {
            Expression e = it.next();
            e.accept(this);
        }
        this.fBuffer.append(") ");//$NON-NLS-1$
        node.getBody().accept(this);
        return false;
    }

    /*
     * @see ASTVisitor#visit(IfStatement)
     */
    @Override
    public boolean visit(IfStatement node) {
        this.fBuffer.append("if (");//$NON-NLS-1$
        node.getExpression().accept(this);
        this.fBuffer.append(") ");//$NON-NLS-1$
        node.getThenStatement().accept(this);
        if (node.getElseStatement() != null) {
            this.fBuffer.append(" else ");//$NON-NLS-1$
            node.getElseStatement().accept(this);
        }
        return false;
    }

    /*
     * @see ASTVisitor#visit(ImportDeclaration)
     */
    @Override
    public boolean visit(ImportDeclaration node) {
        this.fBuffer.append("import ");//$NON-NLS-1$
        if (node.getAST().apiLevel() >= AST.JLS3) {
            if (node.isStatic()) {
                this.fBuffer.append("static ");//$NON-NLS-1$
            }
        }
        node.getName().accept(this);
        if (node.isOnDemand()) {
            this.fBuffer.append(".*");//$NON-NLS-1$
        }
        this.fBuffer.append(";");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(InfixExpression)
     */
    @Override
    public boolean visit(InfixExpression node) {
        node.getLeftOperand().accept(this);
        this.fBuffer.append(' '); // for cases like x= i - -1; or x= i++ + ++i;
        this.fBuffer.append(node.getOperator().toString());
        this.fBuffer.append(' ');
        node.getRightOperand().accept(this);
        final List<Expression> extendedOperands = node.extendedOperands();
        if (extendedOperands.size() != 0) {
            this.fBuffer.append(' ');
            for (Iterator<Expression> it = extendedOperands.iterator(); it.hasNext(); ) {
                this.fBuffer.append(node.getOperator().toString()).append(' ');
                Expression e = it.next();
                e.accept(this);
            }
        }
        return false;
    }

    /*
     * @see ASTVisitor#visit(InstanceofExpression)
     */
    @Override
    public boolean visit(InstanceofExpression node) {
        node.getLeftOperand().accept(this);
        this.fBuffer.append(" instanceof ");//$NON-NLS-1$
        node.getRightOperand().accept(this);
        return false;
    }

    /*
     * @see ASTVisitor#visit(Initializer)
     */
    @Override
    public boolean visit(Initializer node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        if (node.getAST().apiLevel() >= AST.JLS3) {
            printModifiers(node.modifiers());
        }
        node.getBody().accept(this);
        return false;
    }

    /*
     * @see ASTVisitor#visit(Javadoc)
     */
    @Override
    public boolean visit(Javadoc node) {
        this.fBuffer.append("/** ");//$NON-NLS-1$
        for (Iterator<TagElement> it = node.tags().iterator(); it.hasNext(); ) {
            ASTNode e = it.next();
            e.accept(this);
        }
        this.fBuffer.append("\n */");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(LabeledStatement)
     */
    @Override
    public boolean visit(LabeledStatement node) {
        node.getLabel().accept(this);
        this.fBuffer.append(": ");//$NON-NLS-1$
        node.getBody().accept(this);
        return false;
    }

    /*
     * @see ASTVisitor#visit(LineComment)
     * @since 3.0
     */
    @Override
    public boolean visit(LineComment node) {
        this.fBuffer.append("//\n");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(MarkerAnnotation)
     * @since 3.0
     */
    @Override
    public boolean visit(MarkerAnnotation node) {
        this.fBuffer.append("@");//$NON-NLS-1$
        node.getTypeName().accept(this);
        return false;
    }

    /*
     * @see ASTVisitor#visit(MemberRef)
     * @since 3.0
     */
    @Override
    public boolean visit(MemberRef node) {
        if (node.getQualifier() != null) {
            node.getQualifier().accept(this);
        }
        this.fBuffer.append("#");//$NON-NLS-1$
        node.getName().accept(this);
        return false;
    }

    /*
     * @see ASTVisitor#visit(MemberValuePair)
     * @since 3.0
     */
    @Override
    public boolean visit(MemberValuePair node) {
        node.getName().accept(this);
        this.fBuffer.append("=");//$NON-NLS-1$
        node.getValue().accept(this);
        return false;
    }

    /*
     * @see ASTVisitor#visit(MethodRef)
     * @since 3.0
     */
    @Override
    public boolean visit(MethodRef node) {
        if (node.getQualifier() != null) {
            node.getQualifier().accept(this);
        }
        this.fBuffer.append("#");//$NON-NLS-1$
        node.getName().accept(this);
        this.fBuffer.append("(");//$NON-NLS-1$
        for (Iterator<MethodRefParameter> it = node.parameters().iterator(); it.hasNext(); ) {
            MethodRefParameter e = it.next();
            e.accept(this);
            if (it.hasNext()) {
                this.fBuffer.append(",");//$NON-NLS-1$
            }
        }
        this.fBuffer.append(")");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(MethodRefParameter)
     * @since 3.0
     */
    @Override
    public boolean visit(MethodRefParameter node) {
        node.getType().accept(this);
        if (node.getAST().apiLevel() >= AST.JLS3) {
            if (node.isVarargs()) {
                this.fBuffer.append("...");//$NON-NLS-1$
            }
        }
        if (node.getName() != null) {
            this.fBuffer.append(" ");//$NON-NLS-1$
            node.getName().accept(this);
        }
        return false;
    }

    /*
     * @see ASTVisitor#visit(MethodDeclaration)
     */
    @Override
    public boolean visit(MethodDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        if (node.getAST().apiLevel() >= AST.JLS3) {
            printModifiers(node.modifiers());
            if (!node.typeParameters().isEmpty()) {
                this.fBuffer.append("<");//$NON-NLS-1$
                for (Iterator<TypeParameter> it = node.typeParameters().iterator(); it.hasNext(); ) {
                    TypeParameter t = it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        this.fBuffer.append(", ");//$NON-NLS-1$
                    }
                }
                this.fBuffer.append("> ");//$NON-NLS-1$
            }
        }
        if (!node.isConstructor()) {
            if (node.getReturnType2() != null) {
                node.getReturnType2().accept(this);
            } else {
                // methods really ought to have a return type
                this.fBuffer.append("void");//$NON-NLS-1$
            }
            this.fBuffer.append(" ");//$NON-NLS-1$
        }
        node.getName().accept(this);
        this.fBuffer.append("(");//$NON-NLS-1$
        for (Iterator<SingleVariableDeclaration> it = node.parameters().iterator(); it.hasNext(); ) {
            SingleVariableDeclaration v = it.next();
            v.accept(this);
            if (it.hasNext()) {
                this.fBuffer.append(", ");//$NON-NLS-1$
            }
        }
        this.fBuffer.append(")");//$NON-NLS-1$
        for (int i = 0; i < node.getExtraDimensions(); i++) {
            this.fBuffer.append("[]"); //$NON-NLS-1$
        }
        if (!node.thrownExceptions().isEmpty()) {
            this.fBuffer.append(" throws ");//$NON-NLS-1$
            for (Iterator<Name> it = node.thrownExceptions().iterator(); it.hasNext(); ) {
                Name n = it.next();
                n.accept(this);
                if (it.hasNext()) {
                    this.fBuffer.append(", ");//$NON-NLS-1$
                }
            }
            this.fBuffer.append(" ");//$NON-NLS-1$
        }
        if (node.getBody() == null) {
            this.fBuffer.append(";");//$NON-NLS-1$
        } else {
            node.getBody().accept(this);
        }
        return false;
    }

    /*
     * @see ASTVisitor#visit(MethodInvocation)
     */
    @Override
    public boolean visit(MethodInvocation node) {
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
            this.fBuffer.append(".");//$NON-NLS-1$
        }
        if (node.getAST().apiLevel() >= AST.JLS3) {
            if (!node.typeArguments().isEmpty()) {
                this.fBuffer.append("<");//$NON-NLS-1$
                for (Iterator<Type> it = node.typeArguments().iterator(); it.hasNext(); ) {
                    Type t = it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        this.fBuffer.append(",");//$NON-NLS-1$
                    }
                }
                this.fBuffer.append(">");//$NON-NLS-1$
            }
        }
        node.getName().accept(this);
        this.fBuffer.append("(");//$NON-NLS-1$
        for (Iterator<Expression> it = node.arguments().iterator(); it.hasNext(); ) {
            Expression e = it.next();
            e.accept(this);
            if (it.hasNext()) {
                this.fBuffer.append(",");//$NON-NLS-1$
            }
        }
        this.fBuffer.append(")");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(Modifier)
     * @since 3.0
     */
    @Override
    public boolean visit(Modifier node) {
        this.fBuffer.append(node.getKeyword().toString());
        return false;
    }

    /*
     * @see ASTVisitor#visit(NormalAnnotation)
     * @since 3.0
     */
    @Override
    public boolean visit(NormalAnnotation node) {
        this.fBuffer.append("@");//$NON-NLS-1$
        node.getTypeName().accept(this);
        this.fBuffer.append("(");//$NON-NLS-1$
        for (Iterator<MemberValuePair> it = node.values().iterator(); it.hasNext(); ) {
            MemberValuePair p = it.next();
            p.accept(this);
            if (it.hasNext()) {
                this.fBuffer.append(",");//$NON-NLS-1$
            }
        }
        this.fBuffer.append(")");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(NullLiteral)
     */
    @Override
    public boolean visit(NullLiteral node) {
        this.fBuffer.append("null");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(NumberLiteral)
     */
    @Override
    public boolean visit(NumberLiteral node) {
        this.fBuffer.append(node.getToken());
        return false;
    }

    /*
     * @see ASTVisitor#visit(PackageDeclaration)
     */
    @Override
    public boolean visit(PackageDeclaration node) {
        if (node.getAST().apiLevel() >= AST.JLS3) {
            if (node.getJavadoc() != null) {
                node.getJavadoc().accept(this);
            }
            for (Iterator<Annotation> it = node.annotations().iterator(); it.hasNext(); ) {
                Annotation p = it.next();
                p.accept(this);
                this.fBuffer.append(" ");//$NON-NLS-1$
            }
        }
        this.fBuffer.append("package ");//$NON-NLS-1$
        node.getName().accept(this);
        this.fBuffer.append(";");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(ParameterizedType)
     * @since 3.0
     */
    @Override
    public boolean visit(ParameterizedType node) {
        node.getType().accept(this);
        this.fBuffer.append("<");//$NON-NLS-1$
        for (Iterator<Type> it = node.typeArguments().iterator(); it.hasNext(); ) {
            Type t = it.next();
            t.accept(this);
            if (it.hasNext()) {
                this.fBuffer.append(",");//$NON-NLS-1$
            }
        }
        this.fBuffer.append(">");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(ParenthesizedExpression)
     */
    @Override
    public boolean visit(ParenthesizedExpression node) {
        this.fBuffer.append("(");//$NON-NLS-1$
        node.getExpression().accept(this);
        this.fBuffer.append(")");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(PostfixExpression)
     */
    @Override
    public boolean visit(PostfixExpression node) {
        node.getOperand().accept(this);
        this.fBuffer.append(node.getOperator().toString());
        return false;
    }

    /*
     * @see ASTVisitor#visit(PrefixExpression)
     */
    @Override
    public boolean visit(PrefixExpression node) {
        this.fBuffer.append(node.getOperator().toString());
        node.getOperand().accept(this);
        return false;
    }

    /*
     * @see ASTVisitor#visit(PrimitiveType)
     */
    @Override
    public boolean visit(PrimitiveType node) {
        this.fBuffer.append(node.getPrimitiveTypeCode().toString());
        return false;
    }

    /*
     * @see ASTVisitor#visit(QualifiedName)
     */
    @Override
    public boolean visit(QualifiedName node) {
        node.getQualifier().accept(this);
        this.fBuffer.append(".");//$NON-NLS-1$
        node.getName().accept(this);
        return false;
    }

    /*
     * @see ASTVisitor#visit(QualifiedType)
     * @since 3.0
     */
    @Override
    public boolean visit(QualifiedType node) {
        node.getQualifier().accept(this);
        this.fBuffer.append(".");//$NON-NLS-1$
        node.getName().accept(this);
        return false;
    }

    /*
     * @see ASTVisitor#visit(ReturnStatement)
     */
    @Override
    public boolean visit(ReturnStatement node) {
        this.fBuffer.append("return");//$NON-NLS-1$
        if (node.getExpression() != null) {
            this.fBuffer.append(" ");//$NON-NLS-1$
            node.getExpression().accept(this);
        }
        this.fBuffer.append(";");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(SimpleName)
     */
    @Override
    public boolean visit(SimpleName node) {
        this.fBuffer.append(node.getIdentifier());
        return false;
    }

    /*
     * @see ASTVisitor#visit(SimpleType)
     */
    @Override
    public boolean visit(SimpleType node) {
        return true;
    }

    /*
     * @see ASTVisitor#visit(SingleMemberAnnotation)
     * @since 3.0
     */
    @Override
    public boolean visit(SingleMemberAnnotation node) {
        this.fBuffer.append("@");//$NON-NLS-1$
        node.getTypeName().accept(this);
        this.fBuffer.append("(");//$NON-NLS-1$
        node.getValue().accept(this);
        this.fBuffer.append(")");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(SingleVariableDeclaration)
     */
    @Override
    public boolean visit(SingleVariableDeclaration node) {
        if (node.getAST().apiLevel() >= AST.JLS3) {
            printModifiers(node.modifiers());
        }
        node.getType().accept(this);
        if (node.getAST().apiLevel() >= AST.JLS3) {
            if (node.isVarargs()) {
                this.fBuffer.append("...");//$NON-NLS-1$
            }
        }
        this.fBuffer.append(" ");//$NON-NLS-1$
        node.getName().accept(this);
        for (int i = 0; i < node.getExtraDimensions(); i++) {
            this.fBuffer.append("[]"); //$NON-NLS-1$
        }
        if (node.getInitializer() != null) {
            this.fBuffer.append("=");//$NON-NLS-1$
            node.getInitializer().accept(this);
        }
        return false;
    }

    /*
     * @see ASTVisitor#visit(StringLiteral)
     */
    @Override
    public boolean visit(StringLiteral node) {
        this.fBuffer.append(node.getEscapedValue());
        return false;
    }

    /*
     * @see ASTVisitor#visit(SuperConstructorInvocation)
     */
    @Override
    public boolean visit(SuperConstructorInvocation node) {
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
            this.fBuffer.append(".");//$NON-NLS-1$
        }
        if (node.getAST().apiLevel() >= AST.JLS3) {
            if (!node.typeArguments().isEmpty()) {
                this.fBuffer.append("<");//$NON-NLS-1$
                for (Iterator<Type> it = node.typeArguments().iterator(); it.hasNext(); ) {
                    Type t = it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        this.fBuffer.append(",");//$NON-NLS-1$
                    }
                }
                this.fBuffer.append(">");//$NON-NLS-1$
            }
        }
        this.fBuffer.append("super(");//$NON-NLS-1$
        for (Iterator<Expression> it = node.arguments().iterator(); it.hasNext(); ) {
            Expression e = it.next();
            e.accept(this);
            if (it.hasNext()) {
                this.fBuffer.append(",");//$NON-NLS-1$
            }
        }
        this.fBuffer.append(");");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(SuperFieldAccess)
     */
    @Override
    public boolean visit(SuperFieldAccess node) {
        if (node.getQualifier() != null) {
            node.getQualifier().accept(this);
            this.fBuffer.append(".");//$NON-NLS-1$
        }
        this.fBuffer.append("super.");//$NON-NLS-1$
        node.getName().accept(this);
        return false;
    }

    /*
     * @see ASTVisitor#visit(SuperMethodInvocation)
     */
    @Override
    public boolean visit(SuperMethodInvocation node) {
        if (node.getQualifier() != null) {
            node.getQualifier().accept(this);
            this.fBuffer.append(".");//$NON-NLS-1$
        }
        this.fBuffer.append("super.");//$NON-NLS-1$
        if (node.getAST().apiLevel() >= AST.JLS3) {
            if (!node.typeArguments().isEmpty()) {
                this.fBuffer.append("<");//$NON-NLS-1$
                for (Iterator<Type> it = node.typeArguments().iterator(); it.hasNext(); ) {
                    Type t = it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        this.fBuffer.append(",");//$NON-NLS-1$
                    }
                }
                this.fBuffer.append(">");//$NON-NLS-1$
            }
        }
        node.getName().accept(this);
        this.fBuffer.append("(");//$NON-NLS-1$
        for (Iterator<Expression> it = node.arguments().iterator(); it.hasNext(); ) {
            Expression e = it.next();
            e.accept(this);
            if (it.hasNext()) {
                this.fBuffer.append(",");//$NON-NLS-1$
            }
        }
        this.fBuffer.append(")");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(SwitchCase)
     */
    @Override
    public boolean visit(SwitchCase node) {
        if (node.isDefault()) {
            this.fBuffer.append("default :");//$NON-NLS-1$
        } else {
            this.fBuffer.append("case ");//$NON-NLS-1$
            node.getExpression().accept(this);
            this.fBuffer.append(":");//$NON-NLS-1$
        }
        return false;
    }

    /*
     * @see ASTVisitor#visit(SwitchStatement)
     */
    @Override
    public boolean visit(SwitchStatement node) {
        this.fBuffer.append("switch (");//$NON-NLS-1$
        node.getExpression().accept(this);
        this.fBuffer.append(") ");//$NON-NLS-1$
        this.fBuffer.append("{");//$NON-NLS-1$
        for (Iterator<Statement> it = node.statements().iterator(); it.hasNext(); ) {
            Statement s = it.next();
            s.accept(this);
        }
        this.fBuffer.append("}");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(SynchronizedStatement)
     */
    @Override
    public boolean visit(SynchronizedStatement node) {
        this.fBuffer.append("synchronized (");//$NON-NLS-1$
        node.getExpression().accept(this);
        this.fBuffer.append(") ");//$NON-NLS-1$
        node.getBody().accept(this);
        return false;
    }

    /*
     * @see ASTVisitor#visit(TagElement)
     * @since 3.0
     */
    @Override
    public boolean visit(TagElement node) {
        if (node.isNested()) {
            // nested tags are always enclosed in braces
            this.fBuffer.append("{");//$NON-NLS-1$
        } else {
            // top-level tags always begin on a new line
            this.fBuffer.append("\n * ");//$NON-NLS-1$
        }
        boolean previousRequiresWhiteSpace = false;
        if (node.getTagName() != null) {
            this.fBuffer.append(node.getTagName());
            previousRequiresWhiteSpace = true;
        }
        boolean previousRequiresNewLine = false;
        for (Iterator<? extends ASTNode> it = node.fragments().iterator(); it.hasNext(); ) {
            ASTNode e = it.next();
            // assume text elements include necessary leading and trailing whitespace
            // but Name, MemberRef, MethodRef, and nested TagElement do not include white space
            boolean currentIncludesWhiteSpace = (e instanceof TextElement);
            if (previousRequiresNewLine && currentIncludesWhiteSpace) {
                this.fBuffer.append("\n * ");//$NON-NLS-1$
            }
            previousRequiresNewLine = currentIncludesWhiteSpace;
            // add space if required to separate
            if (previousRequiresWhiteSpace && !currentIncludesWhiteSpace) {
                this.fBuffer.append(" "); //$NON-NLS-1$
            }
            e.accept(this);
            previousRequiresWhiteSpace = !currentIncludesWhiteSpace && !(e instanceof TagElement);
        }
        if (node.isNested()) {
            this.fBuffer.append("}");//$NON-NLS-1$
        }
        return false;
    }

    /*
     * @see ASTVisitor#visit(TextElement)
     * @since 3.0
     */
    @Override
    public boolean visit(TextElement node) {
        this.fBuffer.append(node.getText());
        return false;
    }

    /*
     * @see ASTVisitor#visit(ThisExpression)
     */
    @Override
    public boolean visit(ThisExpression node) {
        if (node.getQualifier() != null) {
            node.getQualifier().accept(this);
            this.fBuffer.append(".");//$NON-NLS-1$
        }
        this.fBuffer.append("this");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(ThrowStatement)
     */
    @Override
    public boolean visit(ThrowStatement node) {
        this.fBuffer.append("throw ");//$NON-NLS-1$
        node.getExpression().accept(this);
        this.fBuffer.append(";");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(TryStatement)
     */
    @Override
    public boolean visit(TryStatement node) {
        this.fBuffer.append("try ");//$NON-NLS-1$
        if (node.getAST().apiLevel() >= AST.JLS4) {
            if (!node.resources().isEmpty()) {
                this.fBuffer.append("(");//$NON-NLS-1$
                for (Iterator<VariableDeclarationExpression> it = node.resources().iterator(); it.hasNext(); ) {
                    VariableDeclarationExpression var = it.next();
                    var.accept(this);
                    if (it.hasNext()) {
                        this.fBuffer.append(",");//$NON-NLS-1$
                    }
                }
                this.fBuffer.append(") ");//$NON-NLS-1$
            }
        }
        node.getBody().accept(this);
        this.fBuffer.append(" ");//$NON-NLS-1$
        for (Iterator<CatchClause> it = node.catchClauses().iterator(); it.hasNext(); ) {
            CatchClause cc = it.next();
            cc.accept(this);
        }
        if (node.getFinally() != null) {
            this.fBuffer.append("finally ");//$NON-NLS-1$
            node.getFinally().accept(this);
        }
        return false;
    }

    /*
     * @see ASTVisitor#visit(TypeDeclaration)
     */
    @Override
    public boolean visit(TypeDeclaration node) {
        if (node.getJavadoc() != null) {
            node.getJavadoc().accept(this);
        }
        if (node.getAST().apiLevel() >= AST.JLS3) {
            printModifiers(node.modifiers());
        }
        this.fBuffer.append(node.isInterface() ? "interface " : "class ");//$NON-NLS-2$//$NON-NLS-1$
        node.getName().accept(this);
        if (node.getAST().apiLevel() >= AST.JLS3) {
            if (!node.typeParameters().isEmpty()) {
                this.fBuffer.append("<");//$NON-NLS-1$
                for (Iterator<TypeParameter> it = node.typeParameters().iterator(); it.hasNext(); ) {
                    TypeParameter t = it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        this.fBuffer.append(",");//$NON-NLS-1$
                    }
                }
                this.fBuffer.append(">");//$NON-NLS-1$
            }
        }
        this.fBuffer.append(" ");//$NON-NLS-1$
        if (node.getAST().apiLevel() >= AST.JLS3) {
            if (node.getSuperclassType() != null) {
                this.fBuffer.append("extends ");//$NON-NLS-1$
                node.getSuperclassType().accept(this);
                this.fBuffer.append(" ");//$NON-NLS-1$
            }
            if (!node.superInterfaceTypes().isEmpty()) {
                this.fBuffer.append(node.isInterface() ? "extends " : "implements ");//$NON-NLS-2$//$NON-NLS-1$
                for (Iterator<Type> it = node.superInterfaceTypes().iterator(); it.hasNext(); ) {
                    Type t = it.next();
                    t.accept(this);
                    if (it.hasNext()) {
                        this.fBuffer.append(", ");//$NON-NLS-1$
                    }
                }
                this.fBuffer.append(" ");//$NON-NLS-1$
            }
        }
        this.fBuffer.append("{");//$NON-NLS-1$
        BodyDeclaration prev = null;
        for (Iterator<BodyDeclaration> it = node.bodyDeclarations().iterator(); it.hasNext(); ) {
            BodyDeclaration d = it.next();
            if (prev instanceof EnumConstantDeclaration) {
                // enum constant declarations do not include punctuation
                if (d instanceof EnumConstantDeclaration) {
                    // enum constant declarations are separated by commas
                    this.fBuffer.append(", ");//$NON-NLS-1$
                } else {
                    // semicolon separates last enum constant declaration from
                    // first class body declarations
                    this.fBuffer.append("; ");//$NON-NLS-1$
                }
            }
            d.accept(this);
            prev = d;
        }
        this.fBuffer.append("}");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(TypeDeclarationStatement)
     */
    @Override
    public boolean visit(TypeDeclarationStatement node) {
        if (node.getAST().apiLevel() >= AST.JLS3) {
            node.getDeclaration().accept(this);
        }
        return false;
    }

    /*
     * @see ASTVisitor#visit(TypeLiteral)
     */
    @Override
    public boolean visit(TypeLiteral node) {
        node.getType().accept(this);
        this.fBuffer.append(".class");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(TypeParameter)
     * @since 3.0
     */
    @Override
    public boolean visit(TypeParameter node) {
        node.getName().accept(this);
        if (!node.typeBounds().isEmpty()) {
            this.fBuffer.append(" extends ");//$NON-NLS-1$
            for (Iterator<Type> it = node.typeBounds().iterator(); it.hasNext(); ) {
                Type t = it.next();
                t.accept(this);
                if (it.hasNext()) {
                    this.fBuffer.append(" & ");//$NON-NLS-1$
                }
            }
        }
        return false;
    }

    /*
     * @see ASTVisitor#visit(UnionType)
     */
    @Override
    public boolean visit(UnionType node) {
        for (Iterator<Type> it = node.types().iterator(); it.hasNext(); ) {
            Type t = it.next();
            t.accept(this);
            if (it.hasNext()) {
                this.fBuffer.append("|");//$NON-NLS-1$
            }
        }
        return false;
    }

    /*
     * @see ASTVisitor#visit(VariableDeclarationExpression)
     */
    @Override
    public boolean visit(VariableDeclarationExpression node) {
        if (node.getAST().apiLevel() >= AST.JLS3) {
            printModifiers(node.modifiers());
        }
        node.getType().accept(this);
        this.fBuffer.append(" ");//$NON-NLS-1$
        for (Iterator<VariableDeclarationFragment> it = node.fragments().iterator(); it.hasNext(); ) {
            VariableDeclarationFragment f = it.next();
            f.accept(this);
            if (it.hasNext()) {
                this.fBuffer.append(", ");//$NON-NLS-1$
            }
        }
        return false;
    }

    /*
     * @see ASTVisitor#visit(VariableDeclarationFragment)
     */
    @Override
    public boolean visit(VariableDeclarationFragment node) {
        node.getName().accept(this);
        for (int i = 0; i < node.getExtraDimensions(); i++) {
            this.fBuffer.append("[]");//$NON-NLS-1$
        }
        if (node.getInitializer() != null) {
            this.fBuffer.append("=");//$NON-NLS-1$
            node.getInitializer().accept(this);
        }
        return false;
    }

    /*
     * @see ASTVisitor#visit(VariableDeclarationStatement)
     */
    @Override
    public boolean visit(VariableDeclarationStatement node) {
        if (node.getAST().apiLevel() >= AST.JLS3) {
            printModifiers(node.modifiers());
        }
        node.getType().accept(this);
        this.fBuffer.append(" ");//$NON-NLS-1$
        for (Iterator<VariableDeclarationFragment> it = node.fragments().iterator(); it.hasNext(); ) {
            VariableDeclarationFragment f = it.next();
            f.accept(this);
            if (it.hasNext()) {
                this.fBuffer.append(", ");//$NON-NLS-1$
            }
        }
        this.fBuffer.append(";");//$NON-NLS-1$
        return false;
    }

    /*
     * @see ASTVisitor#visit(WildcardType)
     * @since 3.0
     */
    @Override
    public boolean visit(WildcardType node) {
        this.fBuffer.append("?");//$NON-NLS-1$
        Type bound = node.getBound();
        if (bound != null) {
            if (node.isUpperBound()) {
                this.fBuffer.append(" extends ");//$NON-NLS-1$
            } else {
                this.fBuffer.append(" super ");//$NON-NLS-1$
            }
            bound.accept(this);
        }
        return false;
    }

    /*
     * @see ASTVisitor#visit(WhileStatement)
     */
    @Override
    public boolean visit(WhileStatement node) {
        this.fBuffer.append("while (");//$NON-NLS-1$
        node.getExpression().accept(this);
        this.fBuffer.append(") ");//$NON-NLS-1$
        node.getBody().accept(this);
        return false;
    }

}
