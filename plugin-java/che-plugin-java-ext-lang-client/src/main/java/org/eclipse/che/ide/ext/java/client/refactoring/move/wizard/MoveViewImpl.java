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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.project.tree.generic.StorableNode;
import org.eclipse.che.ide.ext.java.client.JavaLocalizationConstant;
import org.eclipse.che.ide.ext.java.client.refactoring.RefactorInfo;
import org.eclipse.che.ide.ext.java.client.refactoring.move.MoveType;
import org.eclipse.che.ide.ext.java.client.refactoring.move.MovedItemType;
import org.eclipse.che.ide.ui.window.Window;

import java.util.List;

import static org.eclipse.che.ide.ext.java.client.refactoring.move.MoveType.REFACTOR_MENU;
import static org.eclipse.che.ide.ext.java.client.refactoring.move.MovedItemType.COMPILATION_UNIT;

/**
 * @author Dmitry Shnurenko
 */
@Singleton
final class MoveViewImpl extends Window implements MoveView {
    interface MoveViewImplUiBinder extends UiBinder<Widget, MoveViewImpl> {
    }

    private static MoveViewImplUiBinder UI_BINDER = GWT.create(MoveViewImplUiBinder.class);

    private Button preview;
    private Button cancel;
    private Button accept;

    @UiField
    SimplePanel icon;
    @UiField
    TextBox     patternField;
    @UiField
    CheckBox    updateFullNames;
    @UiField
    Label       classNameUR;
    @UiField
    CheckBox    updateReferences;
    @UiField
    FlowPanel   treePanel;
    @UiField
    Label       className;
    @UiField
    FlowPanel   treePanelToHide;
    @UiField
    FlowPanel   patternsPanelToHide;

    @UiField(provided = true)
    final JavaLocalizationConstant locale;

    private ActionDelegate delegate;

    @Inject
    public MoveViewImpl(JavaLocalizationConstant locale) {
        this.locale = locale;

        setTitle(locale.moveDialogTitle());

        setWidget(UI_BINDER.createAndBindUi(this));

        createButtons(locale);
    }

    private void createButtons(JavaLocalizationConstant locale) {
        preview = createButton(locale.moveDialogButtonPreview(), "move-preview-button", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onPreviewButtonClicked();
            }
        });

        cancel = createButton(locale.moveDialogButtonCancel(), "move-cancel-button", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });

        accept = createButton(locale.moveDialogButtonOk(), "move-accept-button", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onAcceptButtonClicked();
            }
        });

        getFooter().add(accept);
        getFooter().add(cancel);
        getFooter().add(preview);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void show(RefactorInfo refactorInfo) {
        MoveType moveType = refactorInfo.getMoveType();
        MovedItemType movedItemType = refactorInfo.getMovedItemType();

        treePanelToHide.setVisible(REFACTOR_MENU.equals(moveType));
        patternsPanelToHide.setVisible(COMPILATION_UNIT.equals(movedItemType));

        List<?> selectedItems = refactorInfo.getSelectedItems();

        int selectionSize = selectedItems.size();

        boolean isMultiSelection = selectionSize > 1;

        StorableNode selectedItem = (StorableNode)selectedItems.get(0);

        classNameUR.setText(isMultiSelection ? locale.multiSelectionReferences(selectionSize) : selectedItem.getDisplayName());
        className.setText(isMultiSelection ? locale.multiSelectionDestination(selectionSize) : selectedItem.getDisplayName());

        show();
    }
}