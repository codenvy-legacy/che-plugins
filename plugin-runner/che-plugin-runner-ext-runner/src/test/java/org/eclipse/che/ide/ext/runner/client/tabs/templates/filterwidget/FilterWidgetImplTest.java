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
package org.eclipse.che.ide.ext.runner.client.tabs.templates.filterwidget;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import org.eclipse.che.ide.ext.runner.client.tabs.templates.filterwidget.FilterWidget.ActionDelegate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.ALL;
import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.PROJECT;
import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.SYSTEM;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class FilterWidgetImplTest {

    private static final String SOME_TEXT = "someText";
    private static final String TYPE_ALL  = "All";

    //constructor mocks
    @Mock
    private RunnerLocalizationConstant locale;
    @Mock
    private RunnerResources            resources;

    //additional mocks
    @Mock
    private ActionDelegate delegate;
    @Mock
    private ChangeEvent    event;

    private FilterWidgetImpl filter;

    @Before
    public void setUp() throws Exception {
        when(locale.configsTypeAll()).thenReturn(TYPE_ALL);

        filter = new FilterWidgetImpl(resources, locale);

        filter.setDelegate(delegate);
    }

    @Test
    public void constructorShouldBeVerified() throws Exception {
        verify(filter.scopes).addItem(PROJECT.toString());
        verify(filter.scopes).addItem(SYSTEM.toString());
        verify(filter.scopes).addItem(ALL.toString());
    }

    @Test
    public void allTypeShouldBeSelected() throws Exception {
        when(filter.types.getItemCount()).thenReturn(2);
        when(filter.types.getValue(0)).thenReturn(SOME_TEXT);
        when(filter.types.getValue(1)).thenReturn(TYPE_ALL);

        filter.addType(SOME_TEXT);

        filter.selectType(TYPE_ALL);

        verify(filter.types).setItemSelected(1, true);
    }

    @Test
    public void anyTypeShouldNotBeSelectedWhenTypesIsEmpty() throws Exception {
        when(filter.types.getItemCount()).thenReturn(0);

        filter.selectType(SOME_TEXT);

        verify(filter.types, never()).getValue(anyInt());
        verify(filter.types, never()).setItemSelected(anyInt(), anyBoolean());
    }

    @Test
    public void projectScopeShouldBeSelected() throws Exception {
        filter.selectScope(PROJECT);

        verify(filter.scopes).setItemSelected(0, true);
    }

    @Test
    public void systemScopeShouldBeSelected() throws Exception {
        filter.selectScope(SYSTEM);

        verify(filter.scopes).setItemSelected(1, true);
    }

    @Test
    public void allScopeShouldBeSelected() throws Exception {
        filter.selectScope(ALL);

        verify(filter.scopes).setItemSelected(2, true);
    }

    @Test
    public void typeShouldBeAdded() throws Exception {
        reset(filter.types);

        filter.addType(SOME_TEXT);

        verify(filter.types).clear();
        verify(filter.types).addItem('/' + SOME_TEXT);
        verify(filter.types).addItem(TYPE_ALL);
    }

    @Test
    public void theSecondScopeShouldBeReturned() throws Exception {
        when(filter.scopes.getSelectedIndex()).thenReturn(1);
        when(filter.scopes.getValue(1)).thenReturn("system");

        Scope scope = filter.getScope();

        assertThat(scope, equalTo(SYSTEM));

        verify(filter.scopes).getSelectedIndex();
        verify(filter.scopes).getValue(1);
    }

    @Test
    public void typeShouldBeReturnedWhenIndexIsMoreThenZero() throws Exception {
        when(filter.types.getSelectedIndex()).thenReturn(1);
        when(filter.types.getValue(1)).thenReturn(TYPE_ALL);

        String type = filter.getType();

        assertThat(type, equalTo(TYPE_ALL));

        verify(filter.types).getSelectedIndex();
        verify(filter.types).getValue(1);
    }

    @Test
    public void emptyStringShouldBeReturnedWhenIndexIsLessThenZero() throws Exception {
        when(filter.types.getSelectedIndex()).thenReturn(-1);

        String type = filter.getType();

        assertThat(type.isEmpty(), is(true));
    }

    @Test
    public void valueShouldBeChanged() throws Exception {
        filter.onValueChanged(event);

        verify(delegate).onValueChanged();
    }
}