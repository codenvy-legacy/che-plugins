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

import org.eclipse.che.api.machine.shared.dto.ServerDto;
import org.eclipse.che.ide.extension.machine.client.inject.factories.EntityFactory;
import org.eclipse.che.ide.extension.machine.client.machine.Machine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class ServerPresenterTest {

    private static final String NAME_1    = "name1";
    private static final String NAME_2    = "name2";
    private static final String ADDRESS_1 = "address1";
    private static final String ADDRESS_2 = "address2";

    //constructor mocks
    @Mock
    private ServerView    view;
    @Mock
    private EntityFactory entityFactory;

    //additional mocks
    @Mock
    private Machine          machine;
    @Mock
    private AcceptsOneWidget container;
    @Mock
    private ServerDto        descriptor1;
    @Mock
    private ServerDto descriptor2;

    @Captor
    private ArgumentCaptor<List<Server>> serverListCaptor;

    @InjectMocks
    private ServerPresenter presenter;

    @Before
    public void setUp() {
        Map<String, ServerDto> servers = new HashMap<>();
        servers.put(NAME_1, descriptor1);
        servers.put(NAME_2, descriptor2);

        when(descriptor1.getAddress()).thenReturn(ADDRESS_1);
        when(descriptor2.getAddress()).thenReturn(ADDRESS_2);

        when(machine.getServers()).thenReturn(servers);
    }

    @Test
    public void serverShouldBeUpdated() {
        presenter.updateInfo(machine);

        verify(entityFactory).createServer(NAME_1, descriptor1);
        verify(entityFactory).createServer(NAME_2, descriptor2);

        verify(view).setServers(serverListCaptor.capture());

        List<Server> servers = serverListCaptor.getValue();

        assertThat(servers.size(), equalTo(2));
    }

    @Test
    public void terminalShouldBeDisplayed() {
        presenter.go(container);

        verify(container).setWidget(view);
    }

    @Test
    public void terminalVisibilityShouldBeChanged() {
        presenter.setVisible(true);

        verify(view).setVisible(true);
    }
}