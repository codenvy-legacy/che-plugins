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

package org.eclipse.che.jdt.refactoring;

import org.eclipse.che.ide.ext.java.BaseTest;
import org.eclipse.che.ide.ext.java.server.dto.DtoServerImpls;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.ChangeCreationResult;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.MoveSettings;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.RefactoringStatus;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.ReorgDestination;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.internal.ui.refactoring.AbstractChangeNode;
import org.eclipse.ltk.internal.ui.refactoring.PreviewNode;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Evgen Vidolob
 */
public class MoveRefactoringSessionTest extends BaseTest {


    private RefactoringManager manager;

    @Before
    public void createManager(){
        manager = new RefactoringManager();
    }


    @Test
    public void testCreateMoveSession() throws Exception {
        IType type = project.findType("com.codenvy.test.TestClass");
        ICompilationUnit unit = type.getCompilationUnit();
        String sessionId = manager.createMoveRefactoringSession(new IJavaElement[]{unit});
        assertThat(sessionId).isNotNull().isNotEmpty();
    }

    @Test
    public void testSetMoveDestination() throws Exception {
        IType type = project.findType("com.codenvy.test.TestClass");
        ICompilationUnit unit = type.getCompilationUnit();
        String sessionId = manager.createMoveRefactoringSession(new IJavaElement[]{unit});
        ReorgDestination destination = new DtoServerImpls.ReorgDestinationImpl();
        destination.setSessionId(sessionId);
        destination.setProjectPath("/test");
        destination.setDestination("/test/src/main/java/p1");
        destination.setType(ReorgDestination.DestinationType.PACKAGE);
        RefactoringStatus status = manager.setRefactoringDestination(destination);
        assertThat(status).isNotNull();
        assertThat(status.getSeverity()).isEqualTo(RefactoringStatus.OK);
    }

    @Test
    public void testCtrateMoveChanges() throws Exception {
        IType type = project.findType("com.codenvy.test.TestClass");
        ICompilationUnit unit = type.getCompilationUnit();
        String sessionId = manager.createMoveRefactoringSession(new IJavaElement[]{unit});
        ReorgDestination destination = new DtoServerImpls.ReorgDestinationImpl();
        destination.setSessionId(sessionId);
        destination.setProjectPath("/test");
        destination.setDestination("/test/src/main/java/p1");
        destination.setType(ReorgDestination.DestinationType.PACKAGE);
        manager.setRefactoringDestination(destination);
        MoveSettings settings = new DtoServerImpls.MoveSettingsImpl();
        settings.setUpdateReferences(true);
        settings.setSessionId(sessionId);
        manager.setMoveSettings(settings);
        ChangeCreationResult change = manager.createChange(sessionId);
        assertThat(change).isNotNull();
        assertThat(change.isCanShowPreviewPage()).isTrue();
        assertThat(change.getStatus().getSeverity()).isEqualTo(RefactoringStatus.OK);
    }

    @Test
    public void testGetMoveChanges() throws Exception {
        IType type = project.findType("com.codenvy.test.TestClass");
        ICompilationUnit unit = type.getCompilationUnit();
        String sessionId = manager.createMoveRefactoringSession(new IJavaElement[]{unit});
        ReorgDestination destination = new DtoServerImpls.ReorgDestinationImpl();
        destination.setSessionId(sessionId);
        destination.setProjectPath("/test");
        destination.setDestination("/test/src/main/java/p1");
        destination.setType(ReorgDestination.DestinationType.PACKAGE);
        manager.setRefactoringDestination(destination);
        MoveSettings settings = new DtoServerImpls.MoveSettingsImpl();
        settings.setUpdateReferences(true);
        settings.setSessionId(sessionId);
        manager.setMoveSettings(settings);
        manager.createChange(sessionId);
        Change change = manager.getRefactoringChanges(sessionId);

        CompositeChange fTreeViewerInputChange;
        if (change instanceof CompositeChange) {
            fTreeViewerInputChange= (CompositeChange)change;
        } else {
            fTreeViewerInputChange= new CompositeChange("Dummy Change"); //$NON-NLS-1$
            fTreeViewerInputChange.add(change);
        }
        PreviewNode node = AbstractChangeNode.createNode(null, fTreeViewerInputChange);
        printChanges(node);

    }

    private void printChanges(PreviewNode node){
        System.out.println(node.getText());
        for (PreviewNode previewNode : node.getChildren()) {
            printChanges(previewNode);
        }
    }

}
