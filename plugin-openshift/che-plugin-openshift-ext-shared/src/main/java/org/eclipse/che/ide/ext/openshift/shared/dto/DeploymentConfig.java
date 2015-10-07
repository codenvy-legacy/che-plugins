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
public interface DeploymentConfig {
    ObjectMeta getMetadata();

    void setMetadata(ObjectMeta metadata);

    DeploymentConfig withMetadata(ObjectMeta metadata);

    String getApiVersion();

    void setApiVersion(String apiVersion);

    DeploymentConfig withApiVersion(String apiVersion);

    String getKind();

    void setKind(String kind);

    DeploymentConfig withKind(String kind);

    DeploymentConfigSpec getSpec();

    void setSpec(DeploymentConfigSpec spec);

    DeploymentConfig withSpec(DeploymentConfigSpec spec);

    DeploymentConfigStatus getStatus();

    void setStatus(DeploymentConfigStatus status);

    DeploymentConfig withStatus(DeploymentConfigStatus status);

}
