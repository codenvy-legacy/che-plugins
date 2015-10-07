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
public interface RBDVolumeSource {
    String getImage();

    void setImage(String image);

    RBDVolumeSource withImage(String image);

    String getPool();

    void setPool(String pool);

    RBDVolumeSource withPool(String pool);

    LocalObjectReference getSecretRef();

    void setSecretRef(LocalObjectReference secretRef);

    RBDVolumeSource withSecretRef(LocalObjectReference secretRef);

    boolean getReadOnly();

    void setReadOnly(boolean readOnly);

    RBDVolumeSource withReadOnly(boolean readOnly);

    String getFsType();

    void setFsType(String fsType);

    RBDVolumeSource withFsType(String fsType);

    String getUser();

    void setUser(String user);

    RBDVolumeSource withUser(String user);

    String getKeyring();

    void setKeyring(String keyring);

    RBDVolumeSource withKeyring(String keyring);

    List<String> getMonitors();

    void setMonitors(List<String> monitors);

    RBDVolumeSource withMonitors(List<String> monitors);

}
