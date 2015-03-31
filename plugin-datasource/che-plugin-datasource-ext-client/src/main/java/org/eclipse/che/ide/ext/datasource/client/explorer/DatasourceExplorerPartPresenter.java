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
package org.eclipse.che.ide.ext.datasource.client.explorer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.eclipse.che.ide.api.parts.base.BasePresenter;
import org.eclipse.che.ide.api.selection.Selection;
import org.eclipse.che.ide.ext.datasource.client.events.DatasourceListChangeEvent;
import org.eclipse.che.ide.ext.datasource.client.events.DatasourceListChangeHandler;
import org.eclipse.che.ide.ext.datasource.client.events.SelectedDatasourceChangeEvent;
import org.eclipse.che.ide.ext.datasource.client.properties.DataEntityPropertiesPresenter;
import org.eclipse.che.ide.ext.datasource.client.selection.DatabaseEntitySelectionEvent;
import org.eclipse.che.ide.ext.datasource.client.selection.DatabaseInfoErrorEvent;
import org.eclipse.che.ide.ext.datasource.client.selection.DatabaseInfoErrorHandler;
import org.eclipse.che.ide.ext.datasource.client.selection.DatabaseInfoReceivedEvent;
import org.eclipse.che.ide.ext.datasource.client.selection.DatabaseInfoReceivedHandler;
import org.eclipse.che.ide.ext.datasource.client.service.FetchMetadataService;
import org.eclipse.che.ide.ext.datasource.client.store.DatabaseInfoStore;
import org.eclipse.che.ide.ext.datasource.client.store.DatasourceManager;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseConfigurationDTO;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseDTO;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseMetadataEntityDTO;
import org.eclipse.che.ide.ext.datasource.shared.ExploreTableType;
import org.eclipse.che.ide.util.loging.Log;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Project Explorer display Project Model in a dedicated Part (view).
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
public class DatasourceExplorerPartPresenter extends BasePresenter implements
                                                                  DatasourceExplorerView.ActionDelegate,
                                                                  DatasourceExplorerPart,
                                                                  DatasourceListChangeHandler,
                                                                  DatabaseInfoReceivedHandler,
                                                                  DatabaseInfoErrorHandler {
    private final DatasourceExplorerView        view;
    private final EventBus                      eventBus;
    private final FetchMetadataService          service;
    private final DatasourceManager             datasourceManager;
    private final DataEntityPropertiesPresenter propertiesPresenter;
    private final DatasourceExplorerConstants   constants;
    private final DatabaseInfoStore             databaseInfoStore;

    /** The currently selected datasource. */
    private String                              selectedDatasource;

    /** The currently selected table type. */
    private ExploreTableType                    selectedTableType;

    /**
     * Instantiates the ProjectExplorer Presenter
     */
    @Inject
    public DatasourceExplorerPartPresenter(@NotNull final DatasourceExplorerView view,
                                           @NotNull final EventBus eventBus,
                                           @NotNull final FetchMetadataService service,
                                           @NotNull final DatasourceManager datasourceManager,
                                           @NotNull final DataEntityPropertiesPresenter propertiesPresenter,
                                           @NotNull final DatasourceExplorerConstants constants,
                                           @NotNull final DatabaseInfoStore databaseInfoStore) {
        this.view = view;
        this.eventBus = eventBus;
        this.service = service;
        this.datasourceManager = datasourceManager;
        this.propertiesPresenter = propertiesPresenter;
        this.constants = constants;
        this.databaseInfoStore = databaseInfoStore;

        this.view.setTitle(constants.datasourceExplorerPartTitle());
        bind();

        // register for datasource creation events
        this.eventBus.addHandler(DatasourceListChangeEvent.getType(), this);
        // register for datasource metadata ready events
        this.eventBus.addHandler(DatabaseInfoReceivedEvent.getType(), this);
        // register for datasource metadata error events
        this.eventBus.addHandler(DatabaseInfoErrorEvent.getType(), this);

        setupTabletypesList();
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);

        // fill the datasources list (deferred until insertion)
        setupDatasourceList();
        propertiesPresenter.go(view.getPropertiesDisplayContainer());
    }

    /** Adds behavior to view components */
    protected void bind() {
        view.setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return constants.datasourceExplorerTabTitle();
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        return null;// resources.projectExplorer();
    }

    /** {@inheritDoc} */
    @Override
    public String getTitleToolTip() {
        return "";
    }

    @Override
    public void onDatabaseMetadataEntitySelected(@NotNull DatabaseMetadataEntityDTO dbMetadataEntity) {
        if (dbMetadataEntity != null) {
            Log.debug(DatasourceExplorerPartPresenter.class, "Database entity selected : "
                                                             + dbMetadataEntity.getLookupKey()
                                                             + " - "
                                                             + dbMetadataEntity.getName());
        } else {
            Log.debug(DatasourceExplorerPartPresenter.class, "Database entity selected : null");
        }
        setSelection(new Selection<DatabaseMetadataEntityDTO>(dbMetadataEntity)); // useless ?
        eventBus.fireEvent(new DatabaseEntitySelectionEvent(dbMetadataEntity));
    }

    @Override
    public void onDatabaseMetadataEntityAction(@NotNull DatabaseMetadataEntityDTO dbMetadataEntity) {
        // do nothing ATM
    }

    /** {@inheritDoc} */
    @Override
    public void onContextMenu(int mouseX, int mouseY) {
        // contextMenuPresenter.show(mouseX, mouseY);
    }

    @Override
    public void onClickExploreButton(final String datasourceId) {
        loadDatasource(datasourceId);
    }

    protected void loadDatasource(final String datasourceId) {
        if (datasourceId == null || datasourceId.isEmpty()) {
            view.setItems(null);
            return;
        }

        DatabaseConfigurationDTO datasourceObject = this.datasourceManager.getByName(datasourceId);
        service.fetchDatabaseInfo(datasourceObject, this.selectedTableType);
    }

    @Override
    public void onSelectedDatasourceChanged(final String datasourceId) {
        this.selectedDatasource = datasourceId;
        DatabaseDTO dsMeta = databaseInfoStore.getDatabaseInfo(datasourceId);
        if (dsMeta != null) {
            view.setItems(dsMeta);
            eventBus.fireEvent(new DatabaseEntitySelectionEvent(dsMeta));
            return;
        }
        loadDatasource(datasourceId);

        eventBus.fireEvent(new SelectedDatasourceChangeEvent(datasourceId));
    }

    private void setupTabletypesList() {
        final List<String> values = new ArrayList<String>();
        for (final ExploreTableType type : ExploreTableType.values()) {
            values.add(constants.tableCategories()[type.getIndex()]);
        }
        this.view.setTableTypesList(values);
        this.view.setTableTypes(ExploreTableType.STANDARD);
    }

    @Override
    public void onSelectedTableTypesChanged(final int selectedIndex) {
        final ExploreTableType newValue = ExploreTableType.fromIndex(selectedIndex);
        if (newValue != null) {
            this.selectedTableType = newValue;
        } else {
            this.view.setTableTypes(this.selectedTableType);
        }
    }

    @Override
    public void onDatasourceListChange(final DatasourceListChangeEvent event) {
        setupDatasourceList();
    }

    /**
     * Fills the datasource list widget with the known datasource ids.
     */
    private void setupDatasourceList() {
        Collection<String> datasourceIds = this.datasourceManager.getNames();
        this.view.setDatasourceList(datasourceIds);
    }

    @Override
    public void onDatabaseInfoReceived(final DatabaseInfoReceivedEvent event) {
        if (this.selectedDatasource != null && this.selectedDatasource.equals(event.getDatabaseId())) {
            final DatabaseDTO datasource = this.databaseInfoStore.getDatabaseInfo(this.selectedDatasource);
            this.view.setItems(datasource);
        }
    }

    @Override
    public void onDatabaseInfoError(final DatabaseInfoErrorEvent event) {
        if (this.selectedDatasource != null && this.selectedDatasource.equals(event.getDatabaseId())) {
            this.view.setItems(null);
        }

    }
}
