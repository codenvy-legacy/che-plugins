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
package org.eclipse.che.ide.ext.datasource.client.editdatasource;

import javax.validation.constraints.NotNull;

import org.eclipse.che.ide.ext.datasource.client.common.pager.ShowMorePagerPanel;
import org.eclipse.che.ide.ext.datasource.client.editdatasource.celllist.DatasourceCell;
import org.eclipse.che.ide.ext.datasource.client.editdatasource.celllist.DatasourceCellListResources;
import org.eclipse.che.ide.ext.datasource.client.editdatasource.celllist.DatasourceKeyProvider;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseConfigurationDTO;
import org.eclipse.che.ide.ui.window.Window;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * View implementation for the edit/delete datasource dialog.
 * 
 * @author "Mickaël Leduque"
 */
public class EditDatasourcesViewImpl extends Window implements EditDatasourcesView {

    /** Number of datasources that are visible in the datasource list. */
    private static final int                         DATASOURCES_LIST_PAGE_SIZE = 20;
    /** Number of datasources that are added in the datasource list when the bottom of the list is reached. */
    private static final int                         DATASOURCES_LIST_INCREMENT = 10;

    private final CellList<DatabaseConfigurationDTO> datasourceList;

    @UiField
    ShowMorePagerPanel                               pagerPanel;

    @UiField
    Button                                           createButton;

    @UiField
    Button                                           deleteButton;

    @UiField
    Button                                           editButton;

    @UiField(provided = true)
    EditDatasourceMessages                           messages;

    /** The widget used as window footer. */
    private final EditWindowFooter                   footer;

    /** The delegate/control component. */
    private ActionDelegate                           delegate;

    @Inject
    public EditDatasourcesViewImpl(final EditDatadourceViewImplUiBinder uiBinder,
                                   final EditDatasourceMessages messages,
                                   final @Named(DatasourceKeyProvider.NAME) DatasourceKeyProvider keyProvider,
                                   final DatasourceCellListResources dsListResources,
                                   final DatasourceCell datasourceCell,
                                   final @NotNull EditWindowFooter editWindowFooter) {
        this.messages = messages;
        Widget widget = uiBinder.createAndBindUi(this);
        setWidget(widget);

        this.datasourceList = new CellList<>(datasourceCell, dsListResources, keyProvider);
        this.pagerPanel.setIncrementSize(DATASOURCES_LIST_INCREMENT);
        this.pagerPanel.setDisplay(this.datasourceList);

        this.footer = editWindowFooter;

        this.setTitle(messages.editDatasourcesDialogText());
        this.getFooter().add(editWindowFooter);

        this.datasourceList.setEmptyListWidget(new Label(messages.emptyDatasourceList()));
        this.datasourceList.setPageSize(DATASOURCES_LIST_PAGE_SIZE);
        this.datasourceList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
        this.datasourceList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
    }

    @Override
    public void showDialog() {
        this.show();
    }

    @Override
    public void closeDialog() {
        this.hide();
    }

    @Override
    public void setDelegate(final ActionDelegate delegate) {
        this.delegate = delegate;
        this.footer.setDelegate(delegate);
    }

    @Override
    public void bindDatasourceModel(final AbstractDataProvider<DatabaseConfigurationDTO> provider) {
        provider.addDataDisplay(this.datasourceList);
    }

    @Override
    public void bindSelectionModel(SelectionModel<DatabaseConfigurationDTO> selectionModel) {
        this.datasourceList.setSelectionModel(selectionModel);
    }

    @UiHandler("createButton")
    public void handleCreateButton(final ClickEvent clickEvent) {
        this.delegate.createDatasource();
    }

    @UiHandler("editButton")
    public void handleEditButton(final ClickEvent clickEvent) {
        this.delegate.editSelectedDatasource();
    }

    @UiHandler("deleteButton")
    public void handleDeleteButton(final ClickEvent clickEvent) {
        this.delegate.deleteSelectedDatasources();
    }

    @Override
    protected void onClose() {
    }

    @Override
    public void setEditEnabled(final boolean enabled) {
        this.editButton.setEnabled(enabled);
    }

    @Override
    public void setDeleteEnabled(final boolean enabled) {
        this.deleteButton.setEnabled(enabled);
    }

    /**
     * UiBinder interface for this view.
     * 
     * @author "Mickaël Leduque"
     */
    interface EditDatadourceViewImplUiBinder extends UiBinder<Widget, EditDatasourcesViewImpl> {
    }

}
