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
public interface DeploymentTriggerImageChangeParams {
    String getLastTriggeredImage();

    void setLastTriggeredImage(String lastTriggeredImage);

    DeploymentTriggerImageChangeParams withLastTriggeredImage(String lastTriggeredImage);

    boolean getAutomatic();

    void setAutomatic(boolean automatic);

    DeploymentTriggerImageChangeParams withAutomatic(boolean automatic);

    ObjectReference getFrom();

    void setFrom(ObjectReference from);

    DeploymentTriggerImageChangeParams withFrom(ObjectReference from);

    List<String> getContainerNames();

    void setContainerNames(List<String> containerNames);

    DeploymentTriggerImageChangeParams withContainerNames(List<String> containerNames);

}
