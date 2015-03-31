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

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.eclipse.che.api.user.shared.dto.ProfileDescriptor;
import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.Notification.Status;
import org.eclipse.che.ide.api.notification.Notification.Type;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.ext.datasource.client.common.interaction.ConfirmCallback;
import org.eclipse.che.ide.ext.datasource.client.common.interaction.DialogFactory;
import org.eclipse.che.ide.ext.datasource.client.common.interaction.confirm.ConfirmWindow;
import org.eclipse.che.ide.ext.datasource.client.common.interaction.message.MessageWindow;
import org.eclipse.che.ide.ext.datasource.client.editdatasource.celllist.DatasourceKeyProvider;
import org.eclipse.che.ide.ext.datasource.client.editdatasource.wizard.EditDatasourceLauncher;
import org.eclipse.che.ide.ext.datasource.client.events.DatasourceListChangeEvent;
import org.eclipse.che.ide.ext.datasource.client.events.DatasourceListChangeHandler;
import org.eclipse.che.ide.ext.datasource.client.newdatasource.InitializableWizardDialog;
import org.eclipse.che.ide.ext.datasource.client.newdatasource.NewDatasourceWizardAction;
import org.eclipse.che.ide.ext.datasource.client.store.DatabaseInfoStore;
import org.eclipse.che.ide.ext.datasource.client.store.DatasourceManager;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseConfigurationDTO;
import org.eclipse.che.ide.util.loging.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * The presenter for the datasource edit/delete datasources dialog.
 * 
 * @author "Mickaël Leduque"
 */
public class EditDatasourcesPresenter implements EditDatasourcesView.ActionDelegate, DatasourceListChangeHandler,
                                     SelectionChangeEvent.Handler, InitializableWizardDialog<DatabaseConfigurationDTO> {

    /** The view component. */
    private final EditDatasourcesView                           view;

    /** The component that stores datasources. */
    private final DatasourceManager                             datasourceManager;

    /** The datasource list model component. */
    private final ListDataProvider<DatabaseConfigurationDTO>    dataProvider        = new ListDataProvider<>();
    /** The selection model for the datasource list widget. */
    private final MultiSelectionModel<DatabaseConfigurationDTO> selectionModel;

    /** The i18n messages instance. */
    private final EditDatasourceMessages                        messages;

    private final NotificationManager                           notificationManager;

    /** the event bus, used to send event "datasources list modified". */
    private final EventBus                                      eventBus;

    /** A factory that will provide modification wizards. */
    private final EditDatasourceLauncher                        editDatasourceLauncher;

    /** A reference to remove handler from eventbus. */
    private HandlerRegistration[]                               handlerRegistration = new HandlerRegistration[2];

    /** The action object to create new datasources. */
    private final NewDatasourceWizardAction                     newDatasourceAction;

    /** Factory for confirmation and message windows. */
    private final DialogFactory                                 dialogFactory;

    /** Metadata cache. */
    private final DatabaseInfoStore                             databaseInfoStore;

    private DatabaseConfigurationDTO                            configuration;


    @Inject
    public EditDatasourcesPresenter(final @NotNull EditDatasourcesView view,
                                    final @NotNull DatasourceManager datasourceManager,
                                    final @NotNull @Named(DatasourceKeyProvider.NAME) DatasourceKeyProvider keyProvider,
                                    final @NotNull EditDatasourceMessages messages,
                                    final @NotNull NotificationManager notificationManager,
                                    final @NotNull EventBus eventBus,
                                    final @NotNull EditDatasourceLauncher editDatasourceLauncher,
                                    final @NotNull NewDatasourceWizardAction newDatasourceAction,
                                    final @NotNull DialogFactory dialogFactory,
                                    final @NotNull DatabaseInfoStore databaseInfoStore) {
        this.view = view;
        this.datasourceManager = datasourceManager;
        this.messages = messages;
        this.notificationManager = notificationManager;
        this.eventBus = eventBus;
        this.editDatasourceLauncher = editDatasourceLauncher;
        this.view.bindDatasourceModel(dataProvider);
        this.view.setDelegate(this);

        this.selectionModel = new MultiSelectionModel<>(keyProvider);
        this.view.bindSelectionModel(this.selectionModel);
        updateButtonsState();

        this.newDatasourceAction = newDatasourceAction;
        this.dialogFactory = dialogFactory;
        this.databaseInfoStore = databaseInfoStore;
    }

    /** Show dialog. */
    public void showDialog() {
        setupDatasourceList();
        this.handlerRegistration[0] = this.eventBus.addHandler(DatasourceListChangeEvent.getType(), this);
        this.handlerRegistration[1] = this.selectionModel.addSelectionChangeHandler(this);
        this.view.showDialog();

        if (configuration != null) {
            this.selectionModel.setSelected(configuration, true);
            onSelectionChange(null);
        }
    }

    @Override
    public void closeDialog() {
        this.view.closeDialog();
        for (final HandlerRegistration handlerReg : this.handlerRegistration) {
            handlerReg.removeHandler();
        }
        this.handlerRegistration = null;
    }

    /** Sets the content of the datasource widget. */
    private void setupDatasourceList() {
        this.dataProvider.getList().clear();
        for (DatabaseConfigurationDTO datasource : this.datasourceManager) {
            this.dataProvider.getList().add(datasource);
        }
        this.dataProvider.refresh();

        // selection must be redone
        final Set<DatabaseConfigurationDTO> oldSelection = this.selectionModel.getSelectedSet();
        fixSelection(oldSelection);
    }

    @Override
    public void deleteSelectedDatasources() {
        final Set<DatabaseConfigurationDTO> selection = this.selectionModel.getSelectedSet();
        if (selection.isEmpty()) {
            final String warnMessage = this.messages.deleteNoSelectionMessage();
            final String warnTitle = this.messages.editOrDeleteNoSelectionTitle();
            final MessageWindow messageWindow = this.dialogFactory.createMessageWindow(warnTitle, warnMessage, null);
            messageWindow.inform();
            return;
        }
        final String confirmMessage = messages.confirmDeleteDatasources(selection.size());
        final String confirmTitle = messages.confirmDeleteDatasourcesTitle();
        final ConfirmCallback callback = new ConfirmCallback() {
            @Override
            public void accepted() {
                doDeleteSelection(selection);
            }
        };

        // no cancel callback
        final ConfirmWindow confirmWindow = this.dialogFactory.createConfirmWindow(confirmTitle, confirmMessage,
                                                                                   callback, null);
        confirmWindow.confirm();

    }

    private void doDeleteSelection(final Set<DatabaseConfigurationDTO> selection) {
        for (final DatabaseConfigurationDTO datasource : selection) {
            this.dataProvider.getList().remove(datasource);
            this.datasourceManager.remove(datasource);
            // also clear metadata cache
            this.databaseInfoStore.clearDatabaseInfo(datasource.getDatasourceId());
        }
        final Notification persistNotification = new Notification("Saving datasources definitions", Status.PROGRESS);
        this.notificationManager.showNotification(persistNotification);
        try {
            this.datasourceManager.persist(new AsyncCallback<ProfileDescriptor>() {

                @Override
                public void onSuccess(final ProfileDescriptor result) {
                    Log.debug(EditDatasourcesPresenter.class, "Datasource definitions saved.");
                    persistNotification.setMessage("Datasource definitions saved");
                    persistNotification.setStatus(Notification.Status.FINISHED);

                }

                @Override
                public void onFailure(final Throwable e) {
                    Log.error(EditDatasourcesPresenter.class, "Exception when persisting datasources: " + e.getMessage());
                    GWT.log("Full exception :", e);
                    notificationManager.showNotification(new Notification("Failed to persist datasources", Type.ERROR));

                }
            });
        } catch (final Exception e) {
            Log.error(EditDatasourcesPresenter.class, "Exception when persisting datasources: " + e.getMessage());
            notificationManager.showNotification(new Notification("Failed to persist datasources", Type.ERROR));
        }

        // reset datasource model
        setupDatasourceList();
        // inform the world about the datasource list modification
        this.eventBus.fireEvent(new DatasourceListChangeEvent());
    }

    @Override
    public void editSelectedDatasource() {
        final Set<DatabaseConfigurationDTO> selection = this.selectionModel.getSelectedSet();
        if (selection.isEmpty()) {
            final String warnMessage = this.messages.editNoSelectionMessage();
            final String warnTitle = this.messages.editOrDeleteNoSelectionTitle();
            final MessageWindow messageWindow = this.dialogFactory.createMessageWindow(warnTitle, warnMessage, null);
            messageWindow.inform();
            return;
        }
        if (selection.size() > 1) {
            final String warnMessage = this.messages.editMultipleSelectionMessage();
            final String warnTitle = this.messages.editMultipleSelectionTitle();
            final MessageWindow messageWindow = this.dialogFactory.createMessageWindow(warnTitle, warnMessage, null);
            messageWindow.inform();
            return;
        }

        for (DatabaseConfigurationDTO datasource : selection) { // there is only one !
            // clear the metadata cache
            this.databaseInfoStore.clearDatabaseInfo(datasource.getDatasourceId());
            // show edit dialog
            this.editDatasourceLauncher.launch(datasource);
        }
    }

    @Override
    public void onDatasourceListChange(final DatasourceListChangeEvent event) {
        // reset datasource model
        setupDatasourceList();
    }

    /**
     * Repair the selection after the datasource list has been modified. The element with the same id as those which where selected before
     * the datasource model was changed are selected.
     * 
     * @param oldSelection the selection before model change
     */
    private void fixSelection(final Set<DatabaseConfigurationDTO> oldSelection) {
        this.selectionModel.clear();

        final List<DatabaseConfigurationDTO> items = this.dataProvider.getList();
        for (final DatabaseConfigurationDTO oldItem : oldSelection) { // if no selection, no list traversal
            for (DatabaseConfigurationDTO item : items) {
                if (this.selectionModel.getKey(item).equals(this.selectionModel.getKey(oldItem))) {
                    this.selectionModel.setSelected(item, true);
                }
            }
        }
    }

    @Override
    public void createDatasource() {
        this.newDatasourceAction.actionPerformed();
    }

    @Override
    public void onSelectionChange(final SelectionChangeEvent event) {
        Log.debug(EditDatasourcesPresenter.class, "Datasource selection changed, updating buttons state");
        updateButtonsState();
    }

    private void updateButtonsState() {
        final Set<DatabaseConfigurationDTO> selected = this.selectionModel.getSelectedSet();
        if (selected == null || selected.size() == 0) {
            this.view.setEditEnabled(false);
            this.view.setDeleteEnabled(false);
        } else {
            this.view.setDeleteEnabled(true);
            if (selected.size() == 1) {
                this.view.setEditEnabled(true);
            } else {
                this.view.setEditEnabled(false);
            }
        }
    }

    @Override
    public void initData(DatabaseConfigurationDTO configuration) {
        this.configuration = configuration;
    }
}
