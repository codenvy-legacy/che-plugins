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
package org.eclipse.che.ide.ext.git.client.patcher;

import org.eclipse.che.ide.util.Config;
import com.googlecode.gwt.test.patchers.PatchClass;
import com.googlecode.gwt.test.patchers.PatchMethod;

/**
 * Patcher for Utils class. Replace native method into Utils.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@PatchClass(Config.class)
public class ConfigPatcher {
    public static final String WORKSPACE_NAME = "workspaceName";

    /** Patch getWorkspaceName method. */
    @PatchMethod(override = true)
    public static String getWorkspaceName() {
        return WORKSPACE_NAME;
    }
}