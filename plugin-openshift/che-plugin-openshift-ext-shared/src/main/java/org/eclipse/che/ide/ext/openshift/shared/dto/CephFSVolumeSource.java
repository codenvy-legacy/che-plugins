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

import java.util.List;

@DTO
public interface CephFSVolumeSource {
    LocalObjectReference getSecretRef();

    void setSecretRef(LocalObjectReference secretRef);

    CephFSVolumeSource withSecretRef(LocalObjectReference secretRef);

    String getSecretFile();

    void setSecretFile(String secretFile);

    CephFSVolumeSource withSecretFile(String secretFile);

    boolean getReadOnly();

    void setReadOnly(boolean readOnly);

    CephFSVolumeSource withReadOnly(boolean readOnly);

    String getUser();

    void setUser(String user);

    CephFSVolumeSource withUser(String user);

    List<String> getMonitors();

    void setMonitors(List<String> monitors);

    CephFSVolumeSource withMonitors(List<String> monitors);

}
