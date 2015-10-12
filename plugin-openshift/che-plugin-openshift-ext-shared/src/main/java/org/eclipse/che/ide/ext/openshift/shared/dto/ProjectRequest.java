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
public interface ProjectRequest {
    ObjectMeta getMetadata();

    void setMetadata(ObjectMeta metadata);

    ProjectRequest withMetadata(ObjectMeta metadata);

    String getApiVersion();

    void setApiVersion(String apiVersion);

    ProjectRequest withApiVersion(String apiVersion);

    String getKind();

    void setKind(String kind);

    ProjectRequest withKind(String kind);

    String getDisplayName();

    void setDisplayName(String displayName);

    ProjectRequest withDisplayName(String displayName);

    String getDescription();

    void setDescription(String description);

    ProjectRequest withDescription(String description);

}
