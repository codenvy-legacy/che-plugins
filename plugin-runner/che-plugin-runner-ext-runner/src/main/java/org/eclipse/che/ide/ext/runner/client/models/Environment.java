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

import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.eclipse.che.commons.annotation.Nullable;
import java.util.Map;

/**
 * The class contains all needed information about environment.
 *
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 */
public interface Environment extends Comparable<Environment> {

    /** @return name of current environment */
    @NotNull
    String getName();

    /** @return id of current environment */
    @NotNull
    String getId();

    /** @return description of current environment */
    @Nullable
    String getDescription();

    /** @return scope of current environment */
    @NotNull
    Scope getScope();

    /** @return path to current environment */
    @NotNull
    String getPath();

    /** @return value of ram for current environment */
    @Min(value=0)
    int getRam();

    /**
     * Sets ram value for current environment.
     *
     * @param ram
     *         ram which need set
     */
    void setRam(@Min(value=0) int ram);

    /** @return type of current environment */
    @NotNull
    String getType();

    /** @return map which contains options for current environment */
    @NotNull
    Map<String, String> getOptions();

}