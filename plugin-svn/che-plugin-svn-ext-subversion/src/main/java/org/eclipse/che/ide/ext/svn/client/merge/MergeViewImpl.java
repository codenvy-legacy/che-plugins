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
package org.eclipse.che.ide.ext.svn.client.merge;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.eclipse.che.ide.api.project.tree.AbstractTreeNode;
import org.eclipse.che.ide.api.project.tree.TreeNode;
import org.eclipse.che.ide.ext.svn.client.SubversionExtensionLocalizationConstants;
import org.eclipse.che.ide.ext.svn.client.SubversionExtensionResources;
import org.eclipse.che.ide.ui.tree.*;
import org.eclipse.che.ide.ui.window.Window;

import javax.annotation.Nonnull;

/**
 * An implementation of MergeView, represented as popup modal dialog.
 */
@Singleton
public class MergeViewImpl extends Window implements MergeView {

    interface CopyViewImplUiBinder extends UiBinder<Widget, MergeViewImpl> {
    }

    private static CopyViewImplUiBinder uiBinder = GWT.create(CopyViewImplUiBinder.class);

    @UiField(provided = true)
    SubversionExtensionLocalizationConstants constants;

    @UiField(provided = true)
    SubversionExtensionResources resources;

    /* Delegate to perform actions */
    private ActionDelegate delegate;


    @UiField
    CheckBox targetCheckBox;

    @UiField
    Label targetTypeLabel;

    @UiField
    TextBox targetTextBox;


    @UiField
    CheckBox sourceURLCheckBox;

    private AbstractTreeNode<?> rootNode;

    private Tree<TreeNode<?>> tree;



    /* Merge button */
    private Button mergeButton;

    /* Cancel button */
    private Button cancelButton;

    private static final String PLACEHOLDER       = "placeholder";
    private static final String PLACEHOLDER_DUMMY = "https://subversion.site.com/svn/sht_site/trunk";

    /* Default constructor creating an instance of this MergeViewImpl */
    @Inject
    public MergeViewImpl(SubversionExtensionLocalizationConstants constants,
                         SubversionExtensionResources resources) {
        this.constants = constants;
        this.resources = resources;

        ensureDebugId("plugin-svn merge-dialog");
        setWidget(uiBinder.createAndBindUi(this));

        mergeButton = createButton(constants.buttonMerge(), "plugin-svn-merge-dialog-merge-button", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.mergeClicked();
            }
        });
        mergeButton.addStyleName(super.resources.centerPanelCss().blueButton());
        getFooter().add(mergeButton);

        cancelButton = createButton(constants.buttonCancel(), "plugin-svn-merge-dialog-cancel-button", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.cancelClicked();
            }
        });
        getFooter().add(cancelButton);

        targetTextBox.getElement().setAttribute(PLACEHOLDER, PLACEHOLDER_DUMMY);

        rootNode = new AbstractTreeNode<Void>(null, null, null, null) {
            /** {@inheritDoc} */
            @Nonnull
            @Override
            public String getId() {
                return "ROOT";
            }

            /** {@inheritDoc} */
            @Nonnull
            @Override
            public String getDisplayName() {
                return "ROOT";
            }

            /** {@inheritDoc} */
            @Override
            public boolean isLeaf() {
                return false;
            }

            /** {@inheritDoc} */
            @Override
            public void refreshChildren(AsyncCallback<TreeNode<?>> callback) {
            }
        };

    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void enableTargetTextBox(boolean enabled) {
        targetTextBox.setEnabled(enabled);
    }

    @Override
    public HasValue<String> targetTextBox() {
        return targetTextBox;
    }

    @Override
    public HasValue<Boolean> targetCheckBox() {
        return targetCheckBox;
    }

    @Override
    public void setTargetIsURL(boolean targetIsURL) {
        if (targetIsURL) {
            targetTypeLabel.setText("URL:");
        } else {
            targetTypeLabel.setText("Path:");
        }
    }

    @UiHandler("targetCheckBox")
    @SuppressWarnings("unused")
    public void onSourceUrlCheckBoxActivated(ClickEvent event) {
        targetTypeLabel.setText(targetCheckBox.getValue() ? "URL:" : "Path:");
        delegate.onSourceCheckBoxChanged();
    }

}
