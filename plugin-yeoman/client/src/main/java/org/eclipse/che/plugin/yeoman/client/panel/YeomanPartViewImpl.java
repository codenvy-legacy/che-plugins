/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.yeoman.client.panel;

import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.parts.base.BaseView;
import org.eclipse.che.ide.notification.NotificationResources;
import org.eclipse.che.plugin.yeoman.client.YeomanResources;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.vectomatic.dom.svg.ui.SVGImage;

/**
 * @author Florent Benoit
 */
public class YeomanPartViewImpl extends BaseView<YeomanPartView.ActionDelegate> implements YeomanPartView {

    interface YeomanPartViewImplUiBinder extends UiBinder<Widget, YeomanPartViewImpl> {
    }

    /**
     * CSS resource.
     */
    @UiField(provided = true)
    protected final YeomanResources uiResources;

    /**
     * CSS resource.
     */
    protected final NotificationResources notificationResources;


    @UiField
    TextBox   resourceName;
    @UiField
    ListBox   resourceType;
    @UiField
    Button    addButton;
    @UiField
    Button    generateButton;
    @UiField
    FlowPanel resultZone;

    SimplePanel iconField;

    private SimplePanel container;

    private SVGImage progressIcon;

    @Inject
    public YeomanPartViewImpl(YeomanResources yeomanResources,
                              PartStackUIResources resources,
                              NotificationResources notificationResources,
                              YeomanPartViewImplUiBinder uiBinder) {
        super(resources);
        this.uiResources = yeomanResources;
        this.notificationResources = notificationResources;

        container = new SimplePanel();
        setContentWidget(container);
        container.add(uiBinder.createAndBindUi(this));

        // add types
        resourceType.addItem("Controller");
        resourceType.addItem("Constant");
        resourceType.addItem("Directive");
        resourceType.addItem("Decorator");
        resourceType.addItem("Factory");
        resourceType.addItem("Filter");
        resourceType.addItem("Provider");
        resourceType.addItem("Route");
        resourceType.addItem("Service");
        resourceType.addItem("Value");
        resourceType.addItem("View");

        minimizeButton.ensureDebugId("outline-minimizeBut");

        // add iconPanel inside the button
        iconField = new SimplePanel();
        iconField.setStyleName(yeomanResources.uiCss().yeomanWizardGenerateButtonIcon());
        generateButton.getElement().appendChild(iconField.getElement());

        // create icon
        progressIcon = new SVGImage(notificationResources.progress());
        progressIcon.getElement().setAttribute("class", notificationResources.notificationCss().progress());

        disableGenerateButton();

    }

    @UiHandler("generateButton")
    public void clickOnGenerateButton(final ClickEvent event) {
        delegate.generate();

    }

    @UiHandler("addButton")
    public void clickOnAddButton(final ClickEvent event) {
        String type = resourceType.getValue(resourceType.getSelectedIndex());
        YeomanGeneratorType selectedType = null;
        for (YeomanGeneratorType yeomanGeneratorType : YeomanGeneratorType.values()) {
            if (yeomanGeneratorType.getName().equalsIgnoreCase(type)) {
                selectedType = yeomanGeneratorType;
                break;
            }
        }

        delegate.addItem(resourceName.getText(), selectedType);


    }

    public void clear() {
        resultZone.clear();
    }

    @Override
    public void removeItem(YeomanGeneratorType type, String name, GeneratedItemView itemView) {
        delegate.removeItem(type, name, itemView);
    }

    public void addFoldingPanel(FoldingPanel foldingPanel) {
        resultZone.add(foldingPanel);
    }

    @Override
    public void removeFoldingPanel(FoldingPanel foldingPanel) {
        resultZone.remove(foldingPanel);
    }

    /**
     * Enable the button
     */
    public void enableGenerateButton() {
        generateButton.setEnabled(true);
    }

    /**
     * Disable the generate button
     */
    public void disableGenerateButton() {
        generateButton.setEnabled(false);
    }

    @Override
    public void disableProgressOnGenerateButton() {
        iconField.remove(progressIcon);
    }

    @Override
    public void enableProgressOnGenerateButton() {
        iconField.setWidget(progressIcon);
    }

}
