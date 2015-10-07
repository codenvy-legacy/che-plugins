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
package org.eclipse.che.ide.ext.openshift.shared.dto;

import org.eclipse.che.dto.shared.DTO;

@DTO
public interface SELinuxOptions {
    String getRole();

    void setRole(String role);

    SELinuxOptions withRole(String role);

    String getLevel();

    void setLevel(String level);

    SELinuxOptions withLevel(String level);

    String getType();

    void setType(String type);

    SELinuxOptions withType(String type);

    String getUser();

    void setUser(String user);

    SELinuxOptions withUser(String user);

}
