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

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.api.project.shared.dto.RunnerEnvironment;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.RAM;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import org.eclipse.che.ide.ext.runner.client.util.GetEnvironmentsUtil;
import org.eclipse.che.ide.rest.RestContext;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.eclipse.che.commons.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

import static com.google.gwt.user.client.Window.Location.getHost;
import static com.google.gwt.user.client.Window.Location.getProtocol;
import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.SYSTEM;

/**
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
public class EnvironmentImpl implements Environment {

    public static final String ROOT_FOLDER = "/.codenvy/runners/environments/";

    private final RunnerEnvironment   runnerEnvironment;
    private final String              path;
    private final Map<String, String> options;
    private final String              id;
    private final String              name;
    private final Scope               scope;
    private final String              type;

    private int ram;

    @Inject
    public EnvironmentImpl(@RestContext String restContext,
                           AppContext appContext,
                           GetEnvironmentsUtil util,
                           @Assisted @NotNull RunnerEnvironment runnerEnvironment,
                           @Assisted @NotNull Scope scope) {
        this.runnerEnvironment = runnerEnvironment;
        this.scope = scope;
        this.ram = RAM.DEFAULT.getValue();
        this.id = runnerEnvironment.getId();

        int index = id.lastIndexOf('/') + 1;
        String lastIdPart = id.substring(index);

        String displayName = runnerEnvironment.getDisplayName();
        if (displayName == null || displayName.isEmpty()) {
            this.name = lastIdPart;
        } else {
            this.name = displayName;
        }

        CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            throw new IllegalStateException("Application context doesn't have current project. " +
                                            "This code can't work without this param.");
        }

        ProjectDescriptor descriptor = currentProject.getProjectDescription();

        boolean isScopeSystem = SYSTEM.equals(scope);

        if (isScopeSystem) {
            String wsId = descriptor.getWorkspaceId();
            path = getProtocol() + "//" + getHost() + restContext + "/runner/" + wsId + "/recipe?id=" + id;
        } else {
            path = descriptor.getPath() + ROOT_FOLDER + lastIdPart;
        }

        options = Collections.unmodifiableMap(runnerEnvironment.getOptions());

        if (isScopeSystem) {
            this.type = util.getCorrectCategoryName(id);
        } else {
            this.type = util.getType();
        }
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getId() {
        return id;
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public String getDescription() {
        return runnerEnvironment.getDescription();
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public Scope getScope() {
        return scope;
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getPath() {
        return path;
    }

    /** {@inheritDoc} */
    @Override
    public int getRam() {
        return ram;
    }

    /** {@inheritDoc} */
    @Override
    public void setRam(@Min(value=0) int ram) {
        this.ram = ram;
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getType() {
        return type;
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public Map<String, String> getOptions() {
        return options;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(@NotNull Environment otherEnvironment) {
        return name.toLowerCase().compareTo(otherEnvironment.getName().toLowerCase());
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Environment)) return false;
        Environment other = (Environment)obj;
        return (this.id.equals(other.getId()));
    }
}