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
package org.eclipse.che.ide.ext.java.client.refactoring.move.wizard;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.project.tree.VirtualFile;
import org.eclipse.che.ide.api.project.tree.generic.StorableNode;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.java.client.navigation.JavaNavigationService;
import org.eclipse.che.ide.ext.java.client.projecttree.JavaSourceFolderUtil;
import org.eclipse.che.ide.ext.java.client.projecttree.nodes.PackageNode;
import org.eclipse.che.ide.ext.java.client.projecttree.nodes.SourceFileNode;
import org.eclipse.che.ide.ext.java.client.refactoring.RefactorInfo;
import org.eclipse.che.ide.ext.java.client.refactoring.preview.PreviewPresenter;
import org.eclipse.che.ide.ext.java.client.refactoring.service.RefactoringServiceClient;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.CreateMoveRefactoring;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.JavaElement;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.JavaProject;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.PackageFragmentRoot;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry Shnurenko
 */
@Singleton
public class MovePresenter implements MoveView.ActionDelegate {

    private final MoveView                 view;
    private final AppContext               appContext;
    private final PreviewPresenter         previewPresenter;
    private final DtoFactory               dtoFactory;
    private final RefactoringServiceClient refactorService;
    private final JavaNavigationService    navigationService;

    private RefactorInfo refactorInfo;
    private String       refactoringSessionId;

    @Inject
    public MovePresenter(MoveView view,
                         AppContext appContext,
                         PreviewPresenter previewPresenter,
                         RefactoringServiceClient refactorService,
                         JavaNavigationService navigationService,
                         DtoFactory dtoFactory) {
        this.view = view;
        this.view.setDelegate(this);

        this.appContext = appContext;
        this.previewPresenter = previewPresenter;
        this.refactorService = refactorService;
        this.navigationService = navigationService;
        this.dtoFactory = dtoFactory;
    }

    public void show(final RefactorInfo refactorInfo) {
        this.refactorInfo = refactorInfo;

        CreateMoveRefactoring moveRefactoring = createMoveDto();

        Promise<String> sessionIdPromise = refactorService.createMoveRefactoring(moveRefactoring);

        sessionIdPromise.then(new Operation<String>() {
            @Override
            public void apply(String sessionId) throws OperationException {
                MovePresenter.this.refactoringSessionId = sessionId;

                showProjectsAndPackages();
            }
        });
    }

    private CreateMoveRefactoring createMoveDto() {
        List<JavaElement> elements = new ArrayList<>();

        for (Object node : refactorInfo.getSelectedItems()) {
            StorableNode<?> storableNode = (StorableNode)node;

            JavaElement element = dtoFactory.createDto(JavaElement.class);

            if (storableNode instanceof PackageNode) {
                element.setPath(storableNode.getPath());
                element.setPack(true);
            }

            if (storableNode instanceof SourceFileNode) {
                element.setPath(JavaSourceFolderUtil.getFQNForFile((VirtualFile)storableNode));
                element.setPack(false);
            }

            elements.add(element);
        }

        String pathToProject = getPathToProject();

        CreateMoveRefactoring moveRefactoring = dtoFactory.createDto(CreateMoveRefactoring.class);

        moveRefactoring.setElements(elements);
        moveRefactoring.setProjectPath(pathToProject);

        return moveRefactoring;
    }

    private String getPathToProject() {
        CurrentProject currentProject = appContext.getCurrentProject();

        if (currentProject == null) {
            throw new IllegalArgumentException(getClass() + " Current project undefined...");
        }

        return currentProject.getProjectDescription().getPath();
    }

    private void showProjectsAndPackages() {
        Promise<List<JavaProject>> projectsPromise = navigationService.getProjectsAndPackages(true);

        projectsPromise.then(new Operation<List<JavaProject>>() {
            @Override
            public void apply(List<JavaProject> projects) throws OperationException {
                for (JavaProject project : projects) {
                    for (PackageFragmentRoot packageFragment : project.getPackageFragmentRoots()) {
                        //TODO need add tree to show destinations
                    }
                }

                view.show(refactorInfo);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onPreviewButtonClicked() {
        previewPresenter.show(refactoringSessionId, refactorInfo);

        view.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void onAcceptButtonClicked() {

    }
}
