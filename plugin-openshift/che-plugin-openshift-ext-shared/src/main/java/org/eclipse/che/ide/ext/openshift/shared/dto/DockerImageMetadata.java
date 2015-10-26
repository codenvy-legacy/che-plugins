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
import org.eclipse.che.dto.shared.JsonFieldName;

@DTO
public interface DockerImageMetadata {
    String getKind();

    void setKind(String kind);

    DockerImageMetadata withKind(String kind);

    String getApiVersion();

    void setApiVersion(String apiVersion);

    DockerImageMetadata withApiVersion(String apiVersion);

    @JsonFieldName("Id")
    String getId();

    void setId(String id);

    DockerImageMetadata withId(String id);

    @JsonFieldName("Parent")
    String getParent();

    void setParent(String parent);

    DockerImageMetadata withParent(String parent);

    @JsonFieldName("Created")
    String getCreated();

    void setCreated(String created);

    DockerImageMetadata withCreated(String created);

    @JsonFieldName("Container")
    String getContainer();

    void setContainer(String container);

    DockerImageMetadata withContainer(String container);

    @JsonFieldName("DockerVersion")
    String getDockerVersion();

    void setDockerVersion(String dockerVersion);

    DockerImageMetadata withDockerVersion(String dockerVersion);

    @JsonFieldName("Architecture")
    String getArchitecture();

    void setArchitecture(String architecture);

    DockerImageMetadata withArchitecture(String architecture);

    @JsonFieldName("Config")
    Config getConfig();

    void setConfig(Config config);

    DockerImageMetadata withConfig(Config config);

    @JsonFieldName("ContainerConfig")
    Config getContainerConfig();

    void setContainerConfig(Config containerConfig);

    DockerImageMetadata withContainerConfig(Config containerConfig);
}
