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
public interface SecurityContext {
    boolean getPrivileged();

    void setPrivileged(boolean privileged);

    SecurityContext withPrivileged(boolean privileged);

    Integer getRunAsUser();

    void setRunAsUser(Integer runAsUser);

    SecurityContext withRunAsUser(Integer runAsUser);

    Capabilities getCapabilities();

    void setCapabilities(Capabilities capabilities);

    SecurityContext withCapabilities(Capabilities capabilities);

    SELinuxOptions getSeLinuxOptions();

    void setSeLinuxOptions(SELinuxOptions seLinuxOptions);

    SecurityContext withSeLinuxOptions(SELinuxOptions seLinuxOptions);

    boolean getRunAsNonRoot();

    void setRunAsNonRoot(boolean runAsNonRoot);

    SecurityContext withRunAsNonRoot(boolean runAsNonRoot);

}
