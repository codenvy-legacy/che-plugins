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

import java.util.Map;

@DTO
public interface ResourceRequirements {
    Map<String, String> getRequests();

    void setRequests(Map<String, String> requests);

    ResourceRequirements withRequests(Map<String, String> requests);

    Map<String, String> getLimits();

    void setLimits(Map<String, String> limits);

    ResourceRequirements withLimits(Map<String, String> limits);

}
