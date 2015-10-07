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
public interface GCEPersistentDiskVolumeSource {
    Integer getPartition();

    void setPartition(Integer partition);

    GCEPersistentDiskVolumeSource withPartition(Integer partition);

    boolean getReadOnly();

    void setReadOnly(boolean readOnly);

    GCEPersistentDiskVolumeSource withReadOnly(boolean readOnly);

    String getPdName();

    void setPdName(String pdName);

    GCEPersistentDiskVolumeSource withPdName(String pdName);

    String getFsType();

    void setFsType(String fsType);

    GCEPersistentDiskVolumeSource withFsType(String fsType);

}
