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
package org.eclipse.che.ide.ext.datasource.client.common.interaction.confirm;

/**
 * Interface to the confirm window component.
 * 
 * @author "Mickaël Leduque"
 */
public interface ConfirmWindow {

    /** Operate the confirmation window : show it and manage user actions. */
    void confirm();
}
