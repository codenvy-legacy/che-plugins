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

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;

import org.eclipse.che.api.machine.shared.dto.ServerDescriptor;
import org.eclipse.che.ide.extension.machine.client.inject.factories.EntityFactory;
import org.eclipse.che.ide.extension.machine.client.machine.Machine;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.tab.content.TabPresenter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The class contains business logic which allows update server's information for current machine. The class is a tab presenter and
 * represent content of server tab.
 *
 * @author Dmitry Shnurenko
 */
public class ServerPresenter implements TabPresenter {

    private final ServerView    view;
    private final EntityFactory entityFactory;

    @Inject
    public ServerPresenter(ServerView view, EntityFactory entityFactory) {
        this.view = view;
        this.entityFactory = entityFactory;
    }

    /**
     * Calls special method on view which updates server's information for current machine.
     *
     * @param machine
     *         machine for which need update information
     */
    public void updateInfo(@NotNull Machine machine) {
        List<Server> serversList = new ArrayList<>();

        Map<String, ServerDescriptor> servers = machine.getServers();

        for (Map.Entry<String, ServerDescriptor> entry : servers.entrySet()) {
            String exposedPort = entry.getKey();
            ServerDescriptor descriptor = entry.getValue();

            Server server = entityFactory.createServer(exposedPort, descriptor);

            serversList.add(server);
        }

        view.setServers(serversList);
    }

    /** {@inheritDoc} */
    @Override
    public IsWidget getView() {
        return view;
    }

    /** {@inheritDoc} */
    @Override
    public void setVisible(boolean visible) {
        view.setVisible(visible);
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }
}
