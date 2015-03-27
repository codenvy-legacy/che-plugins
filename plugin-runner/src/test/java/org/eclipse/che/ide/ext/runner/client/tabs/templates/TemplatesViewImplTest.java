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
package org.eclipse.che.ide.ext.runner.client.tabs.templates;

import com.google.gwtmockito.GwtMockitoTestRunner;

import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.inject.factories.WidgetFactory;
import org.eclipse.che.ide.ext.runner.client.models.Environment;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import org.eclipse.che.ide.ext.runner.client.tabs.templates.environment.EnvironmentWidget;
import org.eclipse.che.ide.ext.runner.client.tabs.templates.filterwidget.FilterWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.PROJECT;
import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.SYSTEM;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class TemplatesViewImplTest {

    private Map<Scope, List<Environment>> environmentMap;

    //constructor mocks
    @Mock
    private RunnerLocalizationConstant locale;
    @Mock
    private RunnerResources            resources;
    @Mock
    private WidgetFactory              widgetFactory;

    //additional mocks
    @Mock
    private EnvironmentWidget widget;
    @Mock
    private Environment       systemEnvironment1;
    @Mock
    private Environment       systemEnvironment2;
    @Mock
    private Environment       projectEnvironment1;
    @Mock
    private Environment       projectEnvironment2;
    @Mock
    private FilterWidget      filterWidget;

    @InjectMocks
    private TemplatesViewImpl view;

    @Before
    public void setUp() throws Exception {
        environmentMap = new EnumMap<>(Scope.class);
        environmentMap.put(PROJECT, Arrays.asList(projectEnvironment1, projectEnvironment2));
        environmentMap.put(SYSTEM, Arrays.asList(systemEnvironment1, systemEnvironment2));

        when(widgetFactory.createEnvironment()).thenReturn(widget);
    }

    @Test
    public void environmentsShouldBeAdded() throws Exception {
        view.addEnvironment(environmentMap);

        verify(view.environmentsPanel).clear();
        verify(widget, times(2)).setScope(PROJECT);
        verify(widget, times(2)).setScope(SYSTEM);
        verify(widget).update(projectEnvironment1);
        verify(widget).update(projectEnvironment2);
        verify(widget).update(systemEnvironment1);
        verify(widget).update(systemEnvironment2);
        verify(widgetFactory, times(4)).createEnvironment();
        verify(view.environmentsPanel, times(4)).add(widget);
    }

    @Test
    public void widgetsShouldBeGotFromCache() throws Exception {
        view.addEnvironment(environmentMap);

        view.addEnvironment(environmentMap);

        verify(widget, times(4)).unSelect();
        verify(widgetFactory, times(4)).createEnvironment();
        verify(view.environmentsPanel, times(8)).add(widget);
    }

    @Test
    public void environmentShouldBeSelected() throws Exception {
        view.addEnvironment(environmentMap);

        view.selectEnvironment(projectEnvironment1);

        verify(widget, times(4)).unSelect();
        verify(widget).select();
    }

    @Test
    public void filterWidgetShouldBwSet() throws Exception {
        view.setFilterWidget(filterWidget);

        verify(view.filterPanel).setWidget(filterWidget);
    }

    @Test
    public void environmentsPanelShouldBeCleared() throws Exception {
        view.clearEnvironmentsPanel();

        verify(view.environmentsPanel).clear();
    }

}