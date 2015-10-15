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
package org.eclipse.che.ide.ext.openshift.client.project.wizard;


import com.google.inject.Inject;

import org.eclipse.che.api.project.shared.dto.ImportProject;
import org.eclipse.che.api.project.shared.dto.ImportSourceDescriptor;
import org.eclipse.che.api.project.shared.dto.NewProject;
import org.eclipse.che.api.project.shared.dto.Source;
import org.eclipse.che.ide.api.wizard.Wizard;
import org.eclipse.che.ide.api.wizard.WizardPage;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.openshift.client.project.wizard.page.configure.ConfigureProjectPresenter;
import org.eclipse.che.ide.ext.openshift.client.project.wizard.page.template.SelectTemplatePresenter;
import org.eclipse.che.ide.ext.openshift.client.dto.NewApplicationRequest;

import javax.validation.constraints.NotNull;

/**
 * Presenter for new application request.
 *
 * @author Vlad Zhukovskiy
 */
public class CreateProjectPresenter implements Wizard.UpdateDelegate, CreateProjectView.ActionDelegate {

    private       CreateProjectWizard        wizard;
    private final CreateProjectView          view;
    private final CreateProjectWizardFactory wizardFactory;
    private final ConfigureProjectPresenter configProjectPage;
    private final SelectTemplatePresenter   selectTemplatePage;
    private final DtoFactory                dtoFactory;

    private WizardPage currentPage;

    @Inject
    public CreateProjectPresenter(CreateProjectView view,
                                  CreateProjectWizardFactory wizardFactory,
                                  ConfigureProjectPresenter configProjectPage,
                                  SelectTemplatePresenter selectTemplatePage,
                                  DtoFactory dtoFactory) {
        this.view = view;
        this.wizardFactory = wizardFactory;
        this.configProjectPage = configProjectPage;
        this.selectTemplatePage = selectTemplatePage;
        this.dtoFactory = dtoFactory;
        view.setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public void onNextClicked() {
        final WizardPage nextPage = wizard.navigateToNext();
        if (nextPage != null) {
            showWizardPage(nextPage);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onPreviousClicked() {
        final WizardPage prevPage = wizard.navigateToPrevious();
        if (prevPage != null) {
            showWizardPage(prevPage);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCreateClicked() {
        wizard.complete(new Wizard.CompleteCallback() {
            @Override
            public void onCompleted() {
                view.closeWizard();
            }

            @Override
            public void onFailure(Throwable e) {
                //show some error to user
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void updateControls() {
        view.setPreviousButtonEnabled(wizard.hasPrevious());
        view.setNextButtonEnabled(wizard.hasNext() && currentPage.isCompleted());
        view.setCreateButtonEnabled(wizard.canComplete());
    }

    private void showWizardPage(@NotNull WizardPage wizardPage) {
        currentPage = wizardPage;
        updateControls();
        view.showPage(currentPage);
    }

    public void createWizardAndShow() {
        wizard = createDefaultWizard();
        final WizardPage<NewApplicationRequest> firstPage = wizard.navigateToFirst();
        if (firstPage != null) {
            showWizardPage(firstPage);
            view.showWizard();
        }
    }

    private CreateProjectWizard createDefaultWizard() {
        NewApplicationRequest newApplicationRequest = dtoFactory.createDto(NewApplicationRequest.class);

        ImportProject dataObject = dtoFactory.createDto(ImportProject.class)
                                             .withProject(dtoFactory.createDto(NewProject.class))
                                             .withSource(dtoFactory.createDto(Source.class)
                                                                   .withProject(dtoFactory.createDto(ImportSourceDescriptor.class)
                                                                                          .withLocation("")));

        newApplicationRequest.setImportProject(dataObject);

        CreateProjectWizard createProjectWizard = wizardFactory.newWizard(newApplicationRequest);
        createProjectWizard.setUpdateDelegate(this);
        createProjectWizard.addPage(configProjectPage);
        createProjectWizard.addPage(selectTemplatePage);
        return createProjectWizard;
    }
}
