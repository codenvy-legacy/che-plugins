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
public interface ReplicationControllerSpec {
    PodTemplateSpec getTemplate();

    void setTemplate(PodTemplateSpec template);

    ReplicationControllerSpec withTemplate(PodTemplateSpec template);

    Integer getReplicas();

    void setReplicas(Integer replicas);

    ReplicationControllerSpec withReplicas(Integer replicas);

    Map<String, String> getSelector();

    void setSelector(Map<String, String> selector);

    ReplicationControllerSpec withSelector(Map<String, String> selector);

}
