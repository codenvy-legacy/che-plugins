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
package org.eclipse.che.ide.ext.openshift.client.deploy._new;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftLocalizationConstant;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftResources;
import org.eclipse.che.ide.ui.window.Window;

import java.util.Collections;
import java.util.List;

/**
 * @author Vlad Zhukovskiy
 */
@Singleton
public class NewApplicationViewImpl extends Window implements NewApplicationView {

    interface NewApplicationViewImplUiBinder extends UiBinder<DockLayoutPanel, NewApplicationViewImpl> {
    }

    private static NewApplicationViewImplUiBinder uiBinder = GWT.create(NewApplicationViewImplUiBinder.class);

    @UiField
    TextBox projectName;

    @UiField
    TextBox displayName;

    @UiField
    TextArea description;

    @UiField
    TextBox applicationName;

    @UiField
    ListBox projectsList;

    @UiField
    RadioButton createNewProject;

    @UiField
    RadioButton choseExistProject;

    @UiField
    ListBox buildImages;

    private Button cancelBtn;

    private Button deployBtn;

    @UiField(provided = true)
    OpenshiftResources resources;

    @UiField(provided = true)
    OpenshiftLocalizationConstant locale;

    private ActionDelegate delegate;

    @Inject
    public NewApplicationViewImpl(OpenshiftResources resources,
                                  OpenshiftLocalizationConstant locale,
                                  CoreLocalizationConstant constants) {
        this.resources = resources;
        this.locale = locale;

        ensureDebugId("deployCodenvyApplication");
        setTitle(locale.linkProjectWithExistingApplicationViewTitle());

        setWidget(uiBinder.createAndBindUi(this));

        deployBtn = createPrimaryButton("Deploy",
                                        "deployCodenvyApplication-deploy-button", new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        delegate.onDeployClicked();
                    }
                });
        addButtonToFooter(deployBtn);

        cancelBtn = createButton(constants.cancel(), "deployCodenvyApplication-cancel-button", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        addButtonToFooter(cancelBtn);

        createNewProject.setValue(true);
        projectName.setEnabled(true);
        displayName.setEnabled(true);
        description.setEnabled(true);
        projectsList.setEnabled(false);
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getProjectName() {
        return projectName.getValue();
    }

    @Override
    public String getDisplayName() {
        return displayName.getValue();
    }

    @Override
    public String getDescriptionName() {
        return description.getValue();
    }

    @Override
    public String getApplicationName() {
        return applicationName.getValue();
    }

    @Override
    public void setApplicationName(String name) {
        applicationName.setValue(name, true);
    }

    @Override
    public void setProjectList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        projectsList.clear();
        for (String project : list) {
            projectsList.addItem(project, project);
        }
    }

    @Override
    public void setBuildImageList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        Collections.sort(list);
        buildImages.clear();
        for (String image : list) {
            buildImages.addItem(image, image);
        }
    }

    @UiHandler({"createNewProject", "choseExistProject"})
    public void onCreateModeChanged(ValueChangeEvent<Boolean> event) {
        projectName.setEnabled(createNewProject.getValue());
        displayName.setEnabled(createNewProject.getValue());
        description.setEnabled(createNewProject.getValue());
        projectsList.setEnabled(!createNewProject.getValue());
    }
}
