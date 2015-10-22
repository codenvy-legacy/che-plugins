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
public interface User {
    ObjectMeta getMetadata();

    void setMetadata(ObjectMeta metadata);

    User withMetadata(ObjectMeta metadata);

    String getApiVersion();

    void setApiVersion(String apiVersion);

    User withApiVersion(String apiVersion);

    List<String> getIdentities();

    void setIdentities(List<String> identities);

    User withIdentities(List<String> identities);

    String getKind();

    void setKind(String kind);

    User withKind(String kind);

    String getFullName();

    void setFullName(String fullName);

    User withFullName(String fullName);

    List<String> getGroups();

    void setGroups(List<String> groups);

    User withGroups(List<String> groups);

}
