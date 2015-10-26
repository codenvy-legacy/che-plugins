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
public interface ImageStreamTag {
    ObjectMeta getMetadata();

    void setMetadata(ObjectMeta metadata);

    ImageStreamTag withMetadata(ObjectMeta metadata);

    DockerImageMetadata getDockerImageMetadata();

    void setDockerImageMetadata(DockerImageMetadata dockerImageMetadata);

    ImageStreamTag withDockerImageMetadata(DockerImageMetadata dockerImageMetadata);

    String getApiVersion();

    void setApiVersion(String apiVersion);

    ImageStreamTag withApiVersion(String apiVersion);

    String getImageName();

    void setImageName(String imageName);

    ImageStreamTag withImageName(String imageName);

    String getKind();

    void setKind(String kind);

    ImageStreamTag withKind(String kind);

    String getDockerImageReference();

    void setDockerImageReference(String dockerImageReference);

    ImageStreamTag withDockerImageReference(String dockerImageReference);

    String getDockerImageMetadataVersion();

    void setDockerImageMetadataVersion(String dockerImageMetadataVersion);

    ImageStreamTag withDockerImageMetadataVersion(String dockerImageMetadataVersion);

    String getDockerImageManifest();

    void setDockerImageManifest(String dockerImageManifest);

    ImageStreamTag withDockerImageManifest(String dockerImageManifest);

}
