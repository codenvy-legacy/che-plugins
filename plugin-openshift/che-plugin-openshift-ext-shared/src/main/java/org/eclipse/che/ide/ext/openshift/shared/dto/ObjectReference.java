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
public interface ObjectReference {
    String getUid();

    void setUid(String uid);

    ObjectReference withUid(String uid);

    String getApiVersion();

    void setApiVersion(String apiVersion);

    ObjectReference withApiVersion(String apiVersion);

    String getKind();

    void setKind(String kind);

    ObjectReference withKind(String kind);

    String getResourceVersion();

    void setResourceVersion(String resourceVersion);

    ObjectReference withResourceVersion(String resourceVersion);

    String getNamespace();

    void setNamespace(String namespace);

    ObjectReference withNamespace(String namespace);

    String getName();

    void setName(String name);

    ObjectReference withName(String name);

    String getFieldPath();

    void setFieldPath(String fieldPath);

    ObjectReference withFieldPath(String fieldPath);

}
