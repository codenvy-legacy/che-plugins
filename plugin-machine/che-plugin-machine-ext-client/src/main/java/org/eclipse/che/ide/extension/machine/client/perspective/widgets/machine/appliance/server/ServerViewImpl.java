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
package org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.appliance.server;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;

import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.TableResources;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * The class displays server's information for current machine.
 *
 * @author Dmitry Shnurenko
 */
public class ServerViewImpl extends Composite implements ServerView {
    interface ServerWidgetImplUiBinder extends UiBinder<Widget, ServerViewImpl> {
    }

    private final static ServerWidgetImplUiBinder UI_BINDER = GWT.create(ServerWidgetImplUiBinder.class);

    @UiField(provided = true)
    final MachineLocalizationConstant locale;
    @UiField(provided = true)
    final CellTable<Server>           servers;

    @Inject
    public ServerViewImpl(MachineLocalizationConstant locale, TableResources tableResources) {
        this.locale = locale;
        this.servers = createTable(tableResources);

        initWidget(UI_BINDER.createAndBindUi(this));
    }

    @Nonnull
    private CellTable<Server> createTable(@Nonnull TableResources tableResources) {
        CellTable<Server> table = new CellTable<>(0, tableResources);
        table.setLoadingIndicator(null);

        TextColumn<Server> name = new TextColumn<Server>() {
            @Override
            public String getValue(Server server) {
                return server.getName();
            }
        };

        TextColumn<Server> value = new TextColumn<Server>() {
            @Override
            public String getValue(Server server) {
                return server.getAddress();
            }
        };

        table.addColumn(name, locale.infoServerName());
        table.addColumn(value, locale.infoServerAddress());

        final SingleSelectionModel<Server> selectionModel = new SingleSelectionModel<>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
            }
        });
        table.setSelectionModel(selectionModel);

        return table;
    }

    /** {@inheritDoc} */
    @Override
    public void setServers(@Nonnull List<Server> servers) {
        this.servers.setRowData(servers);
    }
}