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
public interface ContainerPort {
    String getProtocol();

    void setProtocol(String protocol);

    ContainerPort withProtocol(String protocol);

    String getHostIP();

    void setHostIP(String hostIP);

    ContainerPort withHostIP(String hostIP);

    String getName();

    void setName(String name);

    ContainerPort withName(String name);

    Integer getContainerPort();

    void setContainerPort(Integer containerPort);

    ContainerPort withContainerPort(Integer containerPort);

    Integer getHostPort();

    void setHostPort(Integer hostPort);

    ContainerPort withHostPort(Integer hostPort);

}
