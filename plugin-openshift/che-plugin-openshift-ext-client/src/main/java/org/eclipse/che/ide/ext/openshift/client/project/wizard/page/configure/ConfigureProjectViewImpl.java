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
package org.eclipse.che.ide.ext.openshift.client.project.wizard.page.configure;

import elemental.dom.Element;
import elemental.html.SpanElement;
import elemental.html.TableElement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftResources;
import org.eclipse.che.ide.ext.openshift.shared.dto.Project;
import org.eclipse.che.ide.ui.Tooltip;
import org.eclipse.che.ide.ui.list.SimpleList;
import org.eclipse.che.ide.ui.menu.PositionController;
import org.eclipse.che.ide.ui.smartTree.DelayedTask;
import org.eclipse.che.ide.util.dom.Elements;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of view {@link ConfigureProjectView}.
 *
 * @author Vlad Zhukovskiy
 */
@Singleton
public class ConfigureProjectViewImpl implements ConfigureProjectView {

    private static ConfigureProjectViewImplUiBinder uiBinder = GWT.create(ConfigureProjectViewImplUiBinder.class);

    interface ConfigureProjectViewImplUiBinder extends UiBinder<DockPanel, ConfigureProjectViewImpl> {
    }

    @UiField
    TextBox osProjectNameInput;

    @UiField
    TextBox osProjectDisplayNameInput;

    @UiField
    TextArea osProjectDescriptionInput;

    @UiField
    RadioButton osNewProjectButton;

    @UiField
    RadioButton osExistProjectButton;

    @UiField
    TextBox cdProjectNameInput;

    @UiField
    TextArea cdProjectDescriptionInput;

    @UiField
    RadioButton cdPrivateProject;

    @UiField
    RadioButton cdPublicProject;

    @UiField
    ScrollPanel osExistProjectListPanel;

    private SimpleList<Project> projectsList;

    private ActionDelegate delegate;
    private DockPanel      widget;

    private Tooltip osInvalidNameTooltip;
    private Tooltip cdInvalidNameTooltip;

    @Inject
    public ConfigureProjectViewImpl(Resources resources, OpenshiftResources openshiftResources) {
        widget = uiBinder.createAndBindUi(this);

        TableElement breakPointsElement = Elements.createTableElement();
        breakPointsElement.setAttribute("style", "width: 100%");

        projectsList = SimpleList.create((SimpleList.View)breakPointsElement, resources.defaultSimpleListCss(),
                                         new SimpleList.ListItemRenderer<Project>() {
                                             @Override
                                             public void render(Element listItemBase, Project itemData) {
                                                 //TODO rework this method to proper display each project name
                                                 SpanElement container = Elements.createSpanElement();
                                                 container.setInnerText(itemData.getMetadata().getName());

                                                 listItemBase.appendChild(container);
                                             }
                                         },
                                         new SimpleList.ListEventDelegate<Project>() {
                                             @Override
                                             public void onListItemClicked(Element listItemBase, Project itemData) {
                                                 if (osExistProjectButton.getValue()) {
                                                     projectsList.getSelectionModel().setSelectedItem(itemData);
                                                     delegate.onExistProjectSelected();
                                                 }
                                             }

                                             @Override
                                             public void onListItemDoubleClicked(Element listItemBase, Project itemData) {

                                             }
                                         });
        projectsList.asWidget().getElement().setClassName(openshiftResources.css().templateList());

        osExistProjectListPanel.add(projectsList);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public Widget asWidget() {
        return widget;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isNewOpenShiftProjectSelected() {
        return osNewProjectButton.getValue();
    }

    @UiHandler({"osNewProjectButton", "osExistProjectButton"})
    public void onRadioButtonClicked(ClickEvent event) {
        final boolean enabled = osNewProjectButton.getValue();

        osProjectNameInput.setEnabled(enabled);
        osProjectDisplayNameInput.setEnabled(enabled);
        osProjectDescriptionInput.setEnabled(enabled);

        if (enabled) {
            projectsList.getSelectionModel().clearSelection();
            delegate.onExistProjectSelected();
        }
    }

    @UiHandler({"cdPublicProject", "cdPrivateProject"})
    public void onCodenvyPrivacyRadioButtonClicked(ClickEvent event) {
        delegate.onCodenvyProjectPrivacyChanged();
    }

    @UiHandler({"osProjectNameInput"})
    public void onOpenShiftProjectNameChanged(KeyUpEvent event) {
        if (osProjectNameInput.getValue().startsWith(cdProjectNameInput.getValue())) {
            cdProjectNameInput.setValue(osProjectNameInput.getValue(), true);
        }

        if (event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE && cdProjectNameInput.getValue().startsWith(osProjectNameInput.getValue())) {
            cdProjectNameInput.setValue(cdProjectNameInput.getValue().substring(0, cdProjectNameInput.getValue().length() - 1), true);
        }

        delegate.onOpenShiftNewProjectNameChanged();
        delegate.onCodenvyNewProjectNameChanged();
    }

    @Override
    public void showOpenShiftNewProjectNameInvalidValueMessage(boolean show) {
        if (osInvalidNameTooltip == null) {
            osInvalidNameTooltip = Tooltip.create((Element)osProjectNameInput.getElement(),
                                                  PositionController.VerticalAlign.MIDDLE,
                                                  PositionController.HorizontalAlign.LEFT,
                                                  "Project name must not be empty and be a DNS label (at most 63 characters, matching regex [a-z0-9]([-a-z0-9]*[a-z0-9])?)");
            osInvalidNameTooltip.setTitle("Wrong value");
        }

        if (show) {
            osInvalidNameTooltip.show();
        } else {
            osInvalidNameTooltip.forceHide();
        }
    }

    @UiHandler({"cdProjectNameInput"})
    public void onCodenvyProjectNameChanged(KeyUpEvent event) {
        delegate.onCodenvyNewProjectNameChanged();
    }

    @Override
    public void showCodenvyNewProjectNameInvalidValueMessage(boolean show) {
        if (cdInvalidNameTooltip == null) {
            cdInvalidNameTooltip = Tooltip.create((Element)cdProjectNameInput.getElement(),
                                                  PositionController.VerticalAlign.MIDDLE,
                                                  PositionController.HorizontalAlign.LEFT,
                                                  "Project name must not be empty and contains only letters, digits and '-', '_', '.' chars.");
            cdInvalidNameTooltip.setTitle("Wrong value");
        }

        if (show) {
            cdInvalidNameTooltip.show();
        } else {
            cdInvalidNameTooltip.forceHide();
        }
    }

    @UiHandler({"osProjectDescriptionInput"})
    public void onOpenShiftProjectDescriptionChanged(KeyUpEvent event) {
        if (osProjectDescriptionInput.getValue().startsWith(cdProjectDescriptionInput.getValue())) {
            cdProjectDescriptionInput.setValue(osProjectDescriptionInput.getValue(), true);
        }

        if (event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE && cdProjectDescriptionInput.getValue().startsWith(osProjectDescriptionInput.getValue())) {
            cdProjectDescriptionInput.setValue(cdProjectDescriptionInput.getValue().substring(0, cdProjectDescriptionInput.getValue().length() - 1), true);
        }
        delegate.onOpenShiftDescriptionChanged();
    }

    @UiHandler({"cdProjectDescriptionInput"})
    public void onCodenvyProjectDescriptionChanged(KeyUpEvent event) {
        delegate.onCodenvyDescriptionChanged();
    }

    @UiHandler({"osProjectDisplayNameInput"})
    public void onOpenShiftProjectDisplayNameChanged(KeyUpEvent event) {
        delegate.onOpenShiftDisplayNameChanged();
    }

    /** {@inheritDoc} */
    @Override
    public Project getExistedSelectedProject() {
        return projectsList.getSelectionModel().getSelectedItem();
    }

    /** {@inheritDoc} */
    @Override
    public void resetControls() {
        osProjectNameInput.setValue("", true);
        osProjectDisplayNameInput.setValue("", true);
        osProjectDescriptionInput.setValue("", true);
        osNewProjectButton.setValue(Boolean.TRUE, true);

        cdProjectNameInput.setValue("", true);
        cdProjectDescriptionInput.setValue("", true);
        cdPublicProject.setValue(Boolean.TRUE, true);

        projectsList.render(Collections.<Project>emptyList());
    }

    /** {@inheritDoc} */
    @Override
    public void setExistOpenShiftProjects(List<Project> projects) {
        projectsList.render(projects);
    }

    /** {@inheritDoc} */
    @Override
    public String getOpenShiftNewProjectName() {
        return osProjectNameInput.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public String getCodenvyNewProjectName() {
        return cdProjectNameInput.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public String getOpenShiftProjectDescription() {
        return osProjectDescriptionInput.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public String getCodenvyProjectDescription() {
        return cdProjectDescriptionInput.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public String getOpenShiftProjectDisplayName() {
        return osProjectDisplayNameInput.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCodenvyPublicProject() {
        return cdPublicProject.getValue();
    }
}
