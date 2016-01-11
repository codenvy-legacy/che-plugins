/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.extension.machine.client.command.valueproviders;

import com.google.gwt.user.client.Window;
import com.google.inject.Singleton;

/**
 * Provide current window host name.
 *
 * Need for IDEX-3924 as intermediate solution.
 *
 * @author Vlad Zhukovskiy
 */
@Singleton
public class MachineHostNameProvider implements CommandPropertyValueProvider {

    public static final String KEY = "${machine.hostname}";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        return Window.Location.getHostName();
    }
}
