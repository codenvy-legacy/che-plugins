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
package org.eclipse.che.ide.ext.tutorials.client.wizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.eclipse.che.ide.ext.tutorials.client.TutorialsLocalizationConstant;

/**
 * @author Evgen Vidolob
 */
public class ExtensionPageViewImpl implements ExtensionPageView {

    private static MavenPageViewImplUiBinder ourUiBinder = GWT.create(MavenPageViewImplUiBinder.class);
    private final DockLayoutPanel rootElement;
    private       ActionDelegate  delegate;

    @UiField
    Style   style;
    @UiField
    TextBox versionField;
    @UiField
    TextBox groupId;
    @UiField
    TextBox artifactId;
    @UiField
    Button  artifactIdTooltipButton;
    @UiField
    Button  groupIdTooltipButton;

    @Inject
    public ExtensionPageViewImpl(TutorialsLocalizationConstant localizedConstant) {
        rootElement = ourUiBinder.createAndBindUi(this);

        final Element artifactIdTooltip = DOM.createSpan();
        artifactIdTooltip.setInnerText(localizedConstant.extensionPageArtifactIdTooltip());

        artifactIdTooltipButton.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                final Element link = event.getRelativeElement();
                if (!link.isOrHasChild(artifactIdTooltip)) {
                    link.appendChild(artifactIdTooltip);
                }
            }
        });
        artifactIdTooltipButton.addStyleName(style.tooltip());

        final Element groupIdTooltip = DOM.createSpan();
        groupIdTooltip.setInnerText(localizedConstant.extensionPageGroupIdTooltip());

        groupIdTooltipButton.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                final Element link = event.getRelativeElement();
                if (!link.isOrHasChild(groupIdTooltip)) {
                    link.appendChild(groupIdTooltip);
                }
            }
        });
        groupIdTooltipButton.addStyleName(style.tooltip());

    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }

    @Override
    public String getArtifactId() {
        return artifactId.getText();
    }

    @Override
    public String getVersion() {
        return versionField.getText();
    }

    @Override
    public void setArtifactId(String artifactId) {
        this.artifactId.setText(artifactId);
    }

    @Override
    public void setGroupId(String group) {
        groupId.setText(group);
    }

    @Override
    public void setVersion(String value) {
        versionField.setText(value);
    }


    @Override
    public void reset() {
        artifactId.setText("");
        groupId.setText("");
        versionField.setText("1.0-SNAPSHOT");
    }

    @Override
    public String getGroupId() {
        return groupId.getText();
    }

    @UiHandler({"versionField", "groupId", "artifactId"})
    void onKeyUp(KeyUpEvent event) {
        delegate.onTextsChange();
    }

    @Override
    public void showArtifactIdMissingIndicator(boolean doShow) {
        if (doShow) {
            artifactId.addStyleName(style.inputError());
        } else {
            artifactId.removeStyleName(style.inputError());
        }
    }

    @Override
    public void showGroupIdMissingIndicator(boolean doShow) {
        if (doShow) {
            groupId.addStyleName(style.inputError());
        } else {
            groupId.removeStyleName(style.inputError());
        }
    }

    @Override
    public void showVersionMissingIndicator(boolean doShow) {
        if (doShow) {
            versionField.addStyleName(style.inputError());
        } else {
            versionField.removeStyleName(style.inputError());
        }
    }

    interface MavenPageViewImplUiBinder
            extends UiBinder<DockLayoutPanel, ExtensionPageViewImpl> {
    }

    interface Style extends CssResource {
        String inputError();
        String tooltip();
    }
}