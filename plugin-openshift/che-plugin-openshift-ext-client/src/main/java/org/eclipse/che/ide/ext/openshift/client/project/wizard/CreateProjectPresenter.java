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

import org.eclipse.che.api.workspace.shared.dto.ProjectConfigDto;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.wizard.Wizard;
import org.eclipse.che.ide.api.wizard.WizardPage;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftLocalizationConstant;
import org.eclipse.che.ide.ext.openshift.client.dto.NewApplicationRequest;
import org.eclipse.che.ide.ext.openshift.client.project.wizard.page.configure.ConfigureProjectPresenter;
import org.eclipse.che.ide.ext.openshift.client.project.wizard.page.template.SelectTemplatePresenter;

import javax.validation.constraints.NotNull;

/**
 * Presenter for new application request.
 *
 * @author Vlad Zhukovskiy
 */
public class CreateProjectPresenter implements Wizard.UpdateDelegate, CreateProjectView.ActionDelegate {

    private       CreateProjectWizard           wizard;
    private final CreateProjectView             view;
    private final CreateProjectWizardFactory    wizardFactory;
    private final ConfigureProjectPresenter     configProjectPage;
    private final SelectTemplatePresenter       selectTemplatePage;
    private final DtoFactory                    dtoFactory;
    private final NotificationManager           notificationManager;
    private final OpenshiftLocalizationConstant locale;

    private WizardPage currentPage;

    @Inject
    public CreateProjectPresenter(CreateProjectView view,
                                  CreateProjectWizardFactory wizardFactory,
                                  ConfigureProjectPresenter configProjectPage,
                                  SelectTemplatePresenter selectTemplatePage,
                                  NotificationManager notificationManager,
                                  OpenshiftLocalizationConstant locale,
                                  DtoFactory dtoFactory) {
        this.view = view;
        this.wizardFactory = wizardFactory;
        this.configProjectPage = configProjectPage;
        this.selectTemplatePage = selectTemplatePage;
        this.dtoFactory = dtoFactory;
        this.notificationManager = notificationManager;
        this.locale = locale;
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
                notificationManager.showInfo(locale.createFromTemplateSuccess());
                view.closeWizard();
            }

            @Override
            public void onFailure(Throwable e) {
                String message = e.getMessage() != null ? e.getMessage() : locale.createFromTemplateFailed();
                notificationManager.showError(message);
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

        newApplicationRequest.setProjectConfigDto(dtoFactory.createDto(ProjectConfigDto.class));

        CreateProjectWizard createProjectWizard = wizardFactory.newWizard(newApplicationRequest);
        createProjectWizard.setUpdateDelegate(this);
        createProjectWizard.addPage(configProjectPage);
        createProjectWizard.addPage(selectTemplatePage);
        return createProjectWizard;
    }
}
