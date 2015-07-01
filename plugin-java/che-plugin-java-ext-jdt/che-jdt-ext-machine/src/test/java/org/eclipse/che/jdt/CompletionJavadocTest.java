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

package org.eclipse.che.jdt;

import org.eclipse.che.jdt.javaeditor.TextViewer;
import org.eclipse.che.jdt.quickfix.QuickFixTest;
import org.eclipse.che.jdt.rest.UrlContextProvider;
import org.eclipse.che.jdt.testplugin.Java18ProjectTestSetup;
import org.eclipse.che.jdt.testplugin.JavaProjectHelper;
import org.eclipse.che.jdt.testplugin.ProjectTestSetup;
import org.eclipse.che.jface.text.contentassist.ICompletionProposal;
import org.eclipse.che.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.core.internal.filebuffers.FileBuffersPlugin;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.DocumentAdapter;
import org.eclipse.jdt.internal.ui.text.java.JavaAllCompletionProposalComputer;
import org.eclipse.jdt.internal.ui.text.java.RelevanceSorter;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Point;
import org.everrest.guice.GuiceUriBuilderImpl;
import org.fest.assertions.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Evgen Vidolob
 */
public class CompletionJavadocTest {
    protected static final String wsPath = QuickFixTest.class.getResource("/projects").getFile();
//    protected static ResourcesPlugin   plugin            =
//            new ResourcesPlugin(wsPath + "/index", CompletionJavadocTest.class.getResource("/projects").getFile());
//    protected static JavaPlugin        javaPlugin        = new JavaPlugin(wsPath + "/set");
    protected static FileBuffersPlugin fileBuffersPlugin;
    private          ProjectTestSetup  setup;
    private          IPackageFragmentRoot
            fSourceFolder;
    private          IJavaProject fJProject1;

    static {
//        plugin.start();
//        javaPlugin.start();
//        UrlContextProvider.setUrlContext("http://test.com/");
//        DefaultWorkingCopyOwner.setPrimaryBufferProvider(new WorkingCopyOwner() {
//            @Override
//            public IBuffer createBuffer(ICompilationUnit workingCopy) {
//                return BufferManager.createBuffer(workingCopy);
//            }
//        });
//        if(FileBuffersPlugin.getDefault() == null) {
//            fileBuffersPlugin = new FileBuffersPlugin();
//        } else {
//            fileBuffersPlugin = FileBuffersPlugin.getDefault();
//        }
        UrlContextProvider.setUriBuilder(new GuiceUriBuilderImpl());
    }

    public CompletionJavadocTest() {
        setup = new Java18ProjectTestSetup();
    }

    @Before
    public void setUp() throws Exception {
        setup.setUp();
        fJProject1 = Java18ProjectTestSetup.getProject();
        fSourceFolder = JavaProjectHelper.addSourceContainer(fJProject1, "src");
    }

    @After
    public void tearDown() throws Exception {
        setup.tearDown();
        JavaProjectHelper.clear(fJProject1, ProjectTestSetup.getDefaultClasspath());
    }

    @Test
    public void testJavadoc() throws Exception {
        IPackageFragment pack1 = fSourceFolder.createPackageFragment("test1", false, null);
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    /**\n");
        buf.append("     * Test JavaDoc.\n");
        buf.append("     */\n");
        buf.append("    public void foo(int i) {\n");
        buf.append("        foo(10);");
        buf.append("    }\n");
        buf.append("}\n");

        ICompilationUnit cu = pack1.createCompilationUnit("E.java", buf.toString(), false, null);
        List<ICompletionProposal> proposals = computeProposals(cu, buf.indexOf("    foo") + "    foo".length());
        Assertions.assertThat(proposals).hasSize(1);
        ICompletionProposal proposal = proposals.get(0);
        String result;
        if (proposal instanceof ICompletionProposalExtension5) {
            result = ((ICompletionProposalExtension5)proposal).getAdditionalProposalInfo(null).toString();
        } else {
            result = proposal.getAdditionalProposalInfo();
        }
        Assertions.assertThat(result).contains("Test JavaDoc.");
    }

    @Test
    public void testInheredJavadoc() throws Exception {
        IPackageFragment pack1 = fSourceFolder.createPackageFragment("test1", false, null);
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    /**\n");
        buf.append("     * Test JavaDoc.\n");
        buf.append("     */\n");
        buf.append("    public void foo(int i) {\n");
        buf.append("    }\n");
        buf.append("}\n");

        pack1.createCompilationUnit("E.java", buf.toString(), false, null);

        StringBuffer buf2 = new StringBuffer();
        buf2.append("package test1;\n");
        buf2.append("public class B extends E {\n");
        buf2.append("    @Override\n");
        buf2.append("    public void foo(int i) {\n");
        buf2.append("        foo(10);\n");
        buf2.append("    }\n");
        buf2.append("}\n");

        ICompilationUnit cu2 = pack1.createCompilationUnit("B.java", buf2.toString(), false, null);

        List<ICompletionProposal> proposals = computeProposals(cu2, buf2.indexOf("  foo") + "  foo".length());
        Assertions.assertThat(proposals).hasSize(1);
        ICompletionProposal proposal = proposals.get(0);
        String result;
        if(proposal instanceof ICompletionProposalExtension5){
            result = ((ICompletionProposalExtension5)proposal).getAdditionalProposalInfo(null).toString();
        } else {
           result = proposal.getAdditionalProposalInfo();
        }
        Assertions.assertThat(result).contains("Test JavaDoc.");
    }

    private static List<ICompletionProposal> computeProposals(ICompilationUnit compilationUnit, int offset) throws JavaModelException {
        IBuffer buffer = compilationUnit.getBuffer();
        IDocument document;
        if (buffer instanceof org.eclipse.jdt.internal.ui.javaeditor.DocumentAdapter) {
            document = ((org.eclipse.jdt.internal.ui.javaeditor.DocumentAdapter)buffer).getDocument();
        } else {
            document = new DocumentAdapter(buffer);
        }
        TextViewer viewer = new TextViewer(document, new Point(offset, 0));
        JavaContentAssistInvocationContext context =
                new JavaContentAssistInvocationContext(viewer, offset, compilationUnit);

        List<ICompletionProposal> proposals = new ArrayList<>();
        proposals.addAll(new JavaAllCompletionProposalComputer().computeCompletionProposals(context, null));
//        proposals.addAll(new TemplateCompletionProposalComputer().computeCompletionProposals(context, null));

        Collections.sort(proposals, new RelevanceSorter());
        return proposals;
    }
}
