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
import java.util.Map;

@DTO
public interface DeploymentConfigSpec {
    PodTemplateSpec getTemplate();

    void setTemplate(PodTemplateSpec template);

    DeploymentConfigSpec withTemplate(PodTemplateSpec template);

    Integer getReplicas();

    void setReplicas(Integer replicas);

    DeploymentConfigSpec withReplicas(Integer replicas);

    Map<String, String> getSelector();

    void setSelector(Map<String, String> selector);

    DeploymentConfigSpec withSelector(Map<String, String> selector);

    DeploymentStrategy getStrategy();

    void setStrategy(DeploymentStrategy strategy);

    DeploymentConfigSpec withStrategy(DeploymentStrategy strategy);

    List<DeploymentTriggerPolicy> getTriggers();

    void setTriggers(List<DeploymentTriggerPolicy> triggers);

    DeploymentConfigSpec withTriggers(List<DeploymentTriggerPolicy> triggers);

}
