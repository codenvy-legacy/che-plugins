package org.eclipse.che.jdt.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.corext.CorextMessages;
import org.eclipse.jdt.internal.corext.ValidateEditException;
import org.eclipse.text.edits.TextEdit;

/**
 * @author Evgen Vidolob
 */
public class JavaModelUtil {
    /**
     * The name of the package-info.java file.
     *
     * @since 3.8
     */
    public static final String PACKAGE_INFO_JAVA = "package-info.java"; //$NON-NLS-1$

    /**
     * The name of the package-info.class file.
     *
     * @since 3.9
     */
    public static final String PACKAGE_INFO_CLASS = "package-info.class"; //$NON-NLS-1$

    /**
     * The name of the package.html file.
     *
     * @since 3.9
     */
    public static final String PACKAGE_HTML = "package.html"; //$NON-NLS-1$

    /**
     * @param type
     *         the type to test
     * @return <code>true</code> iff the type is an interface or an annotation
     * @throws org.eclipse.jdt.core.JavaModelException
     *         thrown when the field can not be accessed
     */
    public static boolean isInterfaceOrAnnotation(IType type) throws JavaModelException {
        return type.isInterface();
    }

    /**
     * @param version1
     *         the first version
     * @param version2
     *         the second version
     * @return <code>true</code> iff version1 is less than version2
     */
    public static boolean isVersionLessThan(String version1, String version2) {
        if (JavaCore.VERSION_CLDC_1_1.equals(version1)) {
            version1 = JavaCore.VERSION_1_1 + 'a';
        }
        if (JavaCore.VERSION_CLDC_1_1.equals(version2)) {
            version2 = JavaCore.VERSION_1_1 + 'a';
        }
        return version1.compareTo(version2) < 0;
    }

    /**
     * Resolves a type name in the context of the declaring type.
     *
     * @param refTypeSig
     *         the type name in signature notation (for example 'QVector') this can also be an array type, but dimensions will be ignored.
     * @param declaringType
     *         the context for resolving (type where the reference was made in)
     * @return returns the fully qualified type name or build-in-type name. if a unresolved type couldn't be resolved null is returned
     * @throws JavaModelException
     *         thrown when the type can not be accessed
     */
    public static String getResolvedTypeName(String refTypeSig, IType declaringType) throws JavaModelException {
        int arrayCount = Signature.getArrayCount(refTypeSig);
        char type = refTypeSig.charAt(arrayCount);
        if (type == Signature.C_UNRESOLVED) {
            String name = ""; //$NON-NLS-1$
            int bracket = refTypeSig.indexOf(Signature.C_GENERIC_START, arrayCount + 1);
            if (bracket > 0)
                name = refTypeSig.substring(arrayCount + 1, bracket);
            else {
                int semi = refTypeSig.indexOf(Signature.C_SEMICOLON, arrayCount + 1);
                if (semi == -1) {
                    throw new IllegalArgumentException();
                }
                name = refTypeSig.substring(arrayCount + 1, semi);
            }
            String[][] resolvedNames = declaringType.resolveType(name);
            if (resolvedNames != null && resolvedNames.length > 0) {
                return JavaModelUtil.concatenateName(resolvedNames[0][0], resolvedNames[0][1]);
            }
            return null;
        } else {
            return Signature.toString(refTypeSig.substring(arrayCount));
        }
    }

    /**
     * Concatenates two names. Uses a dot for separation.
     * Both strings can be empty or <code>null</code>.
     *
     * @param name1
     *         the first name
     * @param name2
     *         the second name
     * @return the concatenated name
     */
    public static String concatenateName(String name1, String name2) {
        StringBuffer buf = new StringBuffer();
        if (name1 != null && name1.length() > 0) {
            buf.append(name1);
        }
        if (name2 != null && name2.length() > 0) {
            if (buf.length() > 0) {
                buf.append('.');
            }
            buf.append(name2);
        }
        return buf.toString();
    }

    /**
     * Concatenates two names. Uses a dot for separation.
     * Both strings can be empty or <code>null</code>.
     * @param name1 the first string
     * @param name2 the second string
     * @return the concatenated string
     */
    public static String concatenateName(char[] name1, char[] name2) {
        StringBuffer buf= new StringBuffer();
        if (name1 != null && name1.length > 0) {
            buf.append(name1);
        }
        if (name2 != null && name2.length > 0) {
            if (buf.length() > 0) {
                buf.append('.');
            }
            buf.append(name2);
        }
        return buf.toString();
    }

    /**
     * Returns the package fragment root of <code>IJavaElement</code>. If the given
     * element is already a package fragment root, the element itself is returned.
     *
     * @param element
     *         the element
     * @return the package fragment root of the element or <code>null</code>
     */
    public static IPackageFragmentRoot getPackageFragmentRoot(IJavaElement element) {
        return (IPackageFragmentRoot)element.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
    }

    public static boolean isPolymorphicSignature(IMethod method) {
        return method.getAnnotation("java.lang.invoke.MethodHandle$PolymorphicSignature").exists(); //$NON-NLS-1$
    }

    public static boolean is18OrHigher(String compliance) {
        return !isVersionLessThan(compliance, JavaCore.VERSION_1_8);
    }

    /**
     * Checks if the given project or workspace has source compliance 1.8 or greater.
     *
     * @param project
     *         the project to test or <code>null</code> to test the workspace settings
     * @return <code>true</code> if the given project or workspace has source compliance 1.8 or
     * greater.
     */
    public static boolean is18OrHigher(IJavaProject project) {
        return is18OrHigher(getSourceCompliance(project));
    }

    private static String getSourceCompliance(IJavaProject project) {
        return project != null ? project.getOption(JavaCore.COMPILER_SOURCE, true) : JavaCore.getOption(JavaCore.COMPILER_SOURCE);
    }

    /**
     * Finds a type container by container name. The returned element will be of type
     * <code>IType</code> or a <code>IPackageFragment</code>. <code>null</code> is returned if the
     * type container could not be found.
     *
     * @param jproject
     *         The Java project defining the context to search
     * @param typeContainerName
     *         A dot separated name of the type container
     * @return returns the container
     * @throws JavaModelException
     *         thrown when the project can not be accessed
     * @see #getTypeContainerName(IType)
     */
    public static IJavaElement findTypeContainer(IJavaProject jproject, String typeContainerName) throws JavaModelException {
        // try to find it as type
        IJavaElement result = jproject.findType(typeContainerName);
        if (result == null) {
            // find it as package
            IPath path = new Path(typeContainerName.replace('.', '/'));
            result = jproject.findElement(path);
            if (!(result instanceof IPackageFragment)) {
                result = null;
            }

        }
        return result;
    }

    public static boolean is50OrHigher(String compliance) {
        return !isVersionLessThan(compliance, JavaCore.VERSION_1_5);
    }

    /**
     * Checks if the given project or workspace has source compliance 1.5 or greater.
     *
     * @param project the project to test or <code>null</code> to test the workspace settings
     * @return <code>true</code> if the given project or workspace has source compliance 1.5 or greater.
     */
    public static boolean is50OrHigher(IJavaProject project) {
        return is50OrHigher(getSourceCompliance(project));
    }

    /**
     * Applies an text edit to a compilation unit. Filed bug 117694 against jdt.core.
     * 	@param cu the compilation unit to apply the edit to
     * 	@param edit the edit to apply
     * @param save is set, save the CU after the edit has been applied
     * @param monitor the progress monitor to use
     * @throws CoreException Thrown when the access to the CU failed
     * @throws ValidateEditException if validate edit fails
     */
    public static void applyEdit(ICompilationUnit cu, TextEdit edit, boolean save, IProgressMonitor monitor) throws CoreException,
                                                                                                                    ValidateEditException {
        IFile file= (IFile) cu.getResource();
        if (!save || !file.exists()) {
            cu.applyTextEdit(edit, monitor);
        } else {
            if (monitor == null) {
                monitor= new NullProgressMonitor();
            }
            monitor.beginTask(CorextMessages.JavaModelUtil_applyedit_operation, 2);
            try {
//                IStatus status= Resources.makeCommittable(file, null);
//                if (!status.isOK()) {
//                    throw new ValidateEditException(status);
//                }

                cu.applyTextEdit(edit, new SubProgressMonitor(monitor, 1));

                cu.save(new SubProgressMonitor(monitor, 1), true);
            } finally {
                monitor.done();
            }
        }
    }

    public static boolean isImplicitImport(String qualifier, ICompilationUnit cu) {
        if ("java.lang".equals(qualifier)) {  //$NON-NLS-1$
            return true;
        }
        String packageName= cu.getParent().getElementName();
        if (qualifier.equals(packageName)) {
            return true;
        }
        String typeName= JavaCore.removeJavaLikeExtension(cu.getElementName());
        String mainTypeName= JavaModelUtil.concatenateName(packageName, typeName);
        return qualifier.equals(mainTypeName);
    }

    /**
     * Checks whether the given type has a valid main method or not.
     * @param type the type to test
     * @return returns <code>true</code> if the type has a main method
     * @throws JavaModelException thrown when the type can not be accessed
     */
    public static boolean hasMainMethod(IType type) throws JavaModelException {
        IMethod[] methods= type.getMethods();
        for (int i= 0; i < methods.length; i++) {
            if (methods[i].isMainMethod()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds a method in a type.
     * This searches for a method with the same name and signature. Parameter types are only
     * compared by the simple name, no resolving for the fully qualified type name is done.
     * Constructors are only compared by parameters, not the name.
     * @param name The name of the method to find
     * @param paramTypes The type signatures of the parameters e.g. <code>{"QString;","I"}</code>
     * @param isConstructor If the method is a constructor
     * @param type the type
     * @return The first found method or <code>null</code>, if nothing foun
     * @throws JavaModelException thrown when the type can not be accessed
     */
    public static IMethod findMethod(String name, String[] paramTypes, boolean isConstructor, IType type) throws JavaModelException {
        IMethod[] methods= type.getMethods();
        for (int i= 0; i < methods.length; i++) {
            if (isSameMethodSignature(name, paramTypes, isConstructor, methods[i])) {
                return methods[i];
            }
        }
        return null;
    }


    /**
     * Tests if a method equals to the given signature.
     * Parameter types are only compared by the simple name, no resolving for
     * the fully qualified type name is done. Constructors are only compared by
     * parameters, not the name.
     * @param name Name of the method
     * @param paramTypes The type signatures of the parameters e.g. <code>{"QString;","I"}</code>
     * @param isConstructor Specifies if the method is a constructor
     * @param curr the method
     * @return Returns <code>true</code> if the method has the given name and parameter types and constructor state.
     * @throws JavaModelException thrown when the method can not be accessed
     */
    public static boolean isSameMethodSignature(String name, String[] paramTypes, boolean isConstructor, IMethod curr) throws JavaModelException {
        if (isConstructor || name.equals(curr.getElementName())) {
            if (isConstructor == curr.isConstructor()) {
                String[] currParamTypes= curr.getParameterTypes();
                if (paramTypes.length == currParamTypes.length) {
                    for (int i= 0; i < paramTypes.length; i++) {
                        String t1= Signature.getSimpleName(Signature.toString(paramTypes[i]));
                        String t2= Signature.getSimpleName(Signature.toString(currParamTypes[i]));
                        if (!t1.equals(t2)) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the field is boolean.
     * @param field the field
     * @return returns <code>true</code> if the field returns a boolean
     * @throws JavaModelException thrown when the field can not be accessed
     */
    public static boolean isBoolean(IField field) throws JavaModelException{
        return field.getTypeSignature().equals(Signature.SIG_BOOLEAN);
    }

    /**
     * Returns the classpath entry of the given package fragment root. This is the raw entry, except
     * if the root is a referenced library, in which case it's the resolved entry.
     *
     * @param root a package fragment root
     * @return the corresponding classpath entry
     * @throws JavaModelException if accessing the entry failed
     * @since 3.6
     */
    public static IClasspathEntry getClasspathEntry(IPackageFragmentRoot root) throws JavaModelException {
        IClasspathEntry rawEntry= root.getRawClasspathEntry();
        int rawEntryKind= rawEntry.getEntryKind();
        switch (rawEntryKind) {
            case IClasspathEntry.CPE_LIBRARY:
            case IClasspathEntry.CPE_VARIABLE:
            case IClasspathEntry.CPE_CONTAINER: // should not happen, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=305037
                if (root.isArchive() && root.getKind() == IPackageFragmentRoot.K_BINARY) {
                    IClasspathEntry resolvedEntry= root.getResolvedClasspathEntry();
                    if (resolvedEntry.getReferencingEntry() != null)
                        return resolvedEntry;
                    else
                        return rawEntry;
                }
        }
        return rawEntry;
    }


    /**
     * Tells whether the given CU is the package-info.java.
     *
     * @param cu the compilation unit to test
     * @return <code>true</code> if the given CU is the package-info.java
     * @since 3.4
     */
    public static boolean isPackageInfo(ICompilationUnit cu) {
        return PACKAGE_INFO_JAVA.equals(cu.getElementName());
    }
}
