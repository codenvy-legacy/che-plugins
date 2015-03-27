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
package org.eclipse.che.ide.ext.runner.client.models;

import com.google.gwtmockito.GwtMockitoTestRunner;

import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.api.project.shared.dto.ProjectTypeDefinition;
import org.eclipse.che.api.project.shared.dto.RunnerEnvironment;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.RAM;
import org.eclipse.che.ide.ext.runner.client.util.GetEnvironmentsUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.PROJECT;
import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.SYSTEM;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Andrienko
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class EnvironmentImplTest {

    private static final String FIRST_PART   = "someid/tomcat";
    private static final String LAST_PART    = "environment";
    private static final String TEXT         = FIRST_PART + '/' + LAST_PART;
    private static final String DISPLAY_NAME = "display name";

    @Mock(answer = RETURNS_DEEP_STUBS)
    private AppContext          appContext;
    @Mock
    private RunnerEnvironment   runnerEnvironment;
    @Mock
    private GetEnvironmentsUtil util;

    @Mock
    private CurrentProject        currentProject;
    @Mock
    private ProjectDescriptor     descriptor;
    @Mock
    private ProjectTypeDefinition definition;
    @Mock
    private Environment           otherEnvironment;

    private EnvironmentImpl environment;

    @Before
    public void setUp() {
        Map<String, String> options = new HashMap<>();
        options.put("someKey1", "someValue1");
        options.put("someKey2", "someValue2");

        when(appContext.getCurrentProject()).thenReturn(currentProject);
        when(currentProject.getProjectDescription()).thenReturn(descriptor);
        when(currentProject.getRunner()).thenReturn(TEXT);

        when(descriptor.getWorkspaceId()).thenReturn(TEXT);
        when(descriptor.getPath()).thenReturn(TEXT);

        when(runnerEnvironment.getId()).thenReturn(TEXT);
        when(runnerEnvironment.getOptions()).thenReturn(options);
        when(runnerEnvironment.getDescription()).thenReturn(TEXT);
        when(runnerEnvironment.getDisplayName()).thenReturn(DISPLAY_NAME);

        environment = new EnvironmentImpl(appContext, util, runnerEnvironment, SYSTEM);
    }

    @Test
    public void prepareActionShouldBePerformedWhenScopeIsSystem() {
        verify(runnerEnvironment).getId();
        verify(appContext).getCurrentProject();

        verify(currentProject).getProjectDescription();
        verify(descriptor).getWorkspaceId();

        verify(runnerEnvironment).getOptions();

        assertThat(environment.getScope(), is(SYSTEM));
        verifyInitializationConstructor();
    }

    private void verifyInitializationConstructor() {
        assertThat(environment.getRam(), is(RAM.DEFAULT.getValue()));
        assertThat(environment.getId(), is(TEXT));
        assertThat(environment.getOptions().keySet(), hasItems("someKey1", "someKey2"));
        assertThat(environment.getOptions().values(), hasItems("someValue1", "someValue2"));
    }

    @Test
    public void prepareActionShouldBePerformedWhenScopeIsProject() {
        environment = new EnvironmentImpl(appContext, util, runnerEnvironment, PROJECT);

        verify(runnerEnvironment, times(2)).getId();
        verify(appContext, times(2)).getCurrentProject();

        verify(currentProject, times(2)).getProjectDescription();
        verify(descriptor).getPath();

        verify(runnerEnvironment, times(2)).getOptions();

        assertThat(environment.getScope(), is(PROJECT));
        verifyInitializationConstructor();
        assertThat(environment.getName(), is(DISPLAY_NAME));
    }

    @Test
    public void prepareActionShouldBePerformedWhenDisplayNameIsNull() {
        when(runnerEnvironment.getDisplayName()).thenReturn(null);

        environment = new EnvironmentImpl(appContext, util, runnerEnvironment, PROJECT);

        verify(runnerEnvironment, times(2)).getId();
        verify(appContext, times(2)).getCurrentProject();

        verify(currentProject, times(2)).getProjectDescription();
        verify(descriptor).getPath();

        verify(runnerEnvironment, times(2)).getOptions();

        assertThat(environment.getScope(), is(PROJECT));
        verifyInitializationConstructor();
        assertThat(environment.getName(), is(LAST_PART));
    }

    @Test
    public void prepareActionShouldBePerformedWhenDisplayNameIsEmpty() {
        when(runnerEnvironment.getDisplayName()).thenReturn("");

        environment = new EnvironmentImpl(appContext, util, runnerEnvironment, PROJECT);

        verify(runnerEnvironment, times(2)).getId();
        verify(appContext, times(2)).getCurrentProject();

        verify(currentProject, times(2)).getProjectDescription();
        verify(descriptor).getPath();

        verify(runnerEnvironment, times(2)).getOptions();

        assertThat(environment.getScope(), is(PROJECT));
        verifyInitializationConstructor();
        assertThat(environment.getName(), is(LAST_PART));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldBeThrownExceptionInConstructorWhenCurrentProjectIsNull() {
        when(appContext.getCurrentProject()).thenReturn(null);

        environment = new EnvironmentImpl(appContext, util, runnerEnvironment, SYSTEM);
    }

    @Test
    public void nameShouldBeReturned() {
        assertThat(environment.getName(), is(DISPLAY_NAME));
    }

    @Test
    public void descriptionShouldBeReturned() {
        assertThat(environment.getDescription(), is(TEXT));

        verify(runnerEnvironment).getDescription();
    }

    @Test
    public void pathShouldBeReturned() {
        assertThat(environment.getPath(), is("///api/runner/someid/tomcat/environment/recipe?id=someid/tomcat/environment"));
    }

    @Test
    public void ramShouldBeChanged() {
        assertThat(environment.getRam(), is(RAM.DEFAULT.getValue()));

        environment.setRam(4096);

        assertThat(environment.getRam(), is(4096));
    }

    @Test
    public void environmentIdShouldBeEqualsDefaultRunnerCategoryWhenScopeIsProject() throws Exception {
        when(util.getType()).thenReturn(FIRST_PART);

        environment = new EnvironmentImpl(appContext, util, runnerEnvironment, PROJECT);

        verify(util).getType();

        assertThat(environment.getType(), equalTo(FIRST_PART));
    }

    @Test
    public void environmentIdShouldBeEqualsDefaultRunnerCategoryWhenScopeIsSystem() throws Exception {
        when(util.getCorrectCategoryName(TEXT)).thenReturn(FIRST_PART);
        when(runnerEnvironment.getId()).thenReturn(TEXT);

        environment = new EnvironmentImpl(appContext, util, runnerEnvironment, SYSTEM);

        verify(util, times(2)).getCorrectCategoryName(TEXT);

        assertThat(environment.getType(), equalTo(FIRST_PART));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void optionsShouldNotBeModified1() {
        environment.getOptions().put("someKey1", "new Value");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void optionsShouldNotBeModified2() {
        environment.getOptions().remove("someKey1");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void optionsShouldNotBeModified3() {
        environment.getOptions().clear();
    }

    @Test
    public void positiveValueShouldBeReturned() throws Exception {
        when(otherEnvironment.getName()).thenReturn("abc");

        int value = environment.compareTo(otherEnvironment);

        assertThat(value > 0, is(true));
    }

    @Test
    public void negativeValueShouldBeReturned() throws Exception {
        when(otherEnvironment.getName()).thenReturn("www");

        int value = environment.compareTo(otherEnvironment);

        assertThat(value < 0, is(true));
    }

    @Test
    public void zeroShouldBeReturned() throws Exception {
        when(otherEnvironment.getName()).thenReturn(DISPLAY_NAME);

        int value = environment.compareTo(otherEnvironment);

        assertThat(value == 0, is(true));
    }

    @Test
    public void nameShouldBeComparedWhenOtherNameIsUpper() throws Exception {
        when(otherEnvironment.getName()).thenReturn("DISPLAY NAME");

        int value = environment.compareTo(otherEnvironment);

        assertThat(value == 0, is(true));
    }

}