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
package org.eclipse.che.ide.ext.runner.client.constants;

import javax.validation.constraints.NotNull;

/**
 * The class contains ids of runner components.
 *
 * @author Valeriy Svydenko
 */
public enum ActionId {
    RUN_APP_ID("runApp"),
    CHOOSE_RUNNER_ID("chooseRunner"),
    RUN_WITH("runWith"),
    CREATE_CUSTOM_RUNNER("createCustomRunner");

    private final String id;

    ActionId(@NotNull String id) {
        this.id = id;
    }

    /** @return id of the runner component. */
    @NotNull
    public String getId() {
        return id;
    }
}
