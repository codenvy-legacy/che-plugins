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
public interface BuildConfig {
    ObjectMeta getMetadata();

    void setMetadata(ObjectMeta metadata);

    BuildConfig withMetadata(ObjectMeta metadata);

    String getApiVersion();

    void setApiVersion(String apiVersion);

    BuildConfig withApiVersion(String apiVersion);

    String getKind();

    void setKind(String kind);

    BuildConfig withKind(String kind);

    BuildConfigSpec getSpec();

    void setSpec(BuildConfigSpec spec);

    BuildConfig withSpec(BuildConfigSpec spec);

    BuildConfigStatus getStatus();

    void setStatus(BuildConfigStatus status);

    BuildConfig withStatus(BuildConfigStatus status);

}
