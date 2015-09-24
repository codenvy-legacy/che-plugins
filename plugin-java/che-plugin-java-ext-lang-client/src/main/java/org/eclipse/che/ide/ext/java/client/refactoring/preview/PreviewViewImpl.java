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
package org.eclipse.che.ide.ext.java.client.refactoring.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.ide.ext.java.client.JavaLocalizationConstant;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.ChangePreview;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.RefactoringPreview;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.RefactoringStatus;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.RefactoringStatusEntry;
import org.eclipse.che.ide.orion.compare.Compare;
import org.eclipse.che.ide.orion.compare.CompareConfig;
import org.eclipse.che.ide.orion.compare.CompareFactory;
import org.eclipse.che.ide.orion.compare.FileOptions;
import org.eclipse.che.ide.ui.cellview.CellTreeResources;
import org.eclipse.che.ide.ui.window.Window;

import javax.validation.constraints.NotNull;
import java.util.Iterator;
import java.util.List;

/**
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
@Singleton
final class PreviewViewImpl extends Window implements PreviewView {
    interface PreviewViewImplUiBinder extends UiBinder<Widget, PreviewViewImpl> {
    }

    private static final PreviewViewImplUiBinder UI_BINDER = GWT.create(PreviewViewImplUiBinder.class);

    @UiField(provided = true)
    final JavaLocalizationConstant locale;
    @UiField
    SimplePanel diff;
    @UiField
    FlowPanel   diffPanelToHide;
    @UiField
    SimplePanel noPreviewToHide;
    @UiField
    ScrollPanel treePanel;
    @UiField
    Label       errorLabel;

    private ActionDelegate delegate;
    private FileOptions    newFile;
    private FileOptions    oldFile;
    private Compare        compare;
    private CellTree       tree;

    private final CellTreeResources cellTreeResources;
    private final CompareFactory    compareFactory;

    @Inject
    public PreviewViewImpl(JavaLocalizationConstant locale,
                           CellTreeResources cellTreeResources,
                           CompareFactory compareFactory) {
        this.locale = locale;
        this.cellTreeResources = cellTreeResources;
        this.compareFactory = compareFactory;

        setTitle(locale.moveDialogTitle());

        setWidget(UI_BINDER.createAndBindUi(this));

        Button backButton = createButton(locale.moveDialogButtonBack(), "preview-back-button", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onBackButtonClicked();
            }
        });

        Button cancelButton = createButton(locale.moveDialogButtonCancel(), "preview-cancel-button", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });

        Button acceptButton = createButton(locale.moveDialogButtonOk(), "preview-ok-button", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onAcceptButtonClicked();
            }
        });

        getFooter().add(acceptButton);
        getFooter().add(cancelButton);
        getFooter().add(backButton);

        diff.getElement().setId(Document.get().createUniqueId());
        showDiff(null);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setTreeOfChanges(final RefactoringPreview changes) {
        showDiffPanel(false);

        final SelectionModel<RefactoringPreview> selectionModel = new SingleSelectionModel<>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                RefactoringPreview selectedNode = (RefactoringPreview)((SingleSelectionModel)selectionModel).getSelectedObject();
                delegate.onSelectionChanged(selectedNode);
            }
        });

        tree = new CellTree(new PreviewChangesModel(changes, selectionModel, delegate), null, cellTreeResources);
        treePanel.clear();
        treePanel.add(tree);
        expandAll(tree.getRootTreeNode());
    }

    private void showDiffPanel(boolean isVisible) {
        diffPanelToHide.setVisible(isVisible);
        noPreviewToHide.setVisible(!isVisible);
    }

    private void expandAll(TreeNode node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            if (!node.isChildLeaf(i)) {
                expandAll(node.setChildOpen(i, true));
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void showErrorMessage(RefactoringStatus status) {
        errorLabel.getElement().getStyle().setColor("#C34d4d");

        showMessage(status);
    }

    /** {@inheritDoc} */
    @Override
    public void showDiff(@Nullable ChangePreview preview) {
        if (preview == null) {
            showDiffPanel(false);
        } else {
            showDiffPanel(true);
            if (compare == null) {
                prepareDiffEditor(preview);
                return;
            }
            refreshComperingFiles(preview);

            compare.refresh();
        }
    }

    private void refreshComperingFiles(@NotNull ChangePreview preview) {
        newFile.setContent(preview.getNewContent());
        newFile.setName(preview.getFileName());
        oldFile.setContent(preview.getOldContent());
        oldFile.setName(preview.getFileName());
    }

    private void prepareDiffEditor(@NotNull ChangePreview preview) {
        newFile = compareFactory.createFieOptions();
        newFile.setReadOnly(true);

        oldFile = compareFactory.createFieOptions();
        oldFile.setReadOnly(true);

        refreshComperingFiles(preview);

        CompareConfig compareConfig = compareFactory.createCompareConfig();
        compareConfig.setNewFile(newFile);
        compareConfig.setOldFile(oldFile);
        compareConfig.setShowTitle(true);
        compareConfig.setShowLineStatus(true);
        compareConfig.setElementId(diff.getElement().getId());

        Promise<Compare> comparePromise = compareFactory.createCompare(compareConfig);
        comparePromise.then(new Operation<Compare>() {
            @Override
            public void apply(Compare arg) throws OperationException {
                compare = arg;
            }
        });
    }

    private void showMessage(RefactoringStatus status) {
        RefactoringStatusEntry statusEntry = getEntryMatchingSeverity(status.getSeverity(), status.getEntries());
        if (statusEntry != null) {
            errorLabel.setText(statusEntry.getMessage());
        } else {
            errorLabel.setText("");
        }
    }

    /**
     * Returns the first entry which severity is equal or greater than the
     * given severity. If more than one entry exists that matches the
     * criteria the first one is returned. Returns <code>null</code> if no
     * entry matches.
     *
     * @param severity
     *         the severity to search for. Must be one of <code>FATAL
     *         </code>, <code>ERROR</code>, <code>WARNING</code> or <code>INFO</code>
     * @param entries
     * @return the entry that matches the search criteria
     */
    private RefactoringStatusEntry getEntryMatchingSeverity(int severity, List<RefactoringStatusEntry> entries) {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            RefactoringStatusEntry entry = (RefactoringStatusEntry)iter.next();
            if (entry.getSeverity() >= severity)
                return entry;
        }
        return null;
    }

}