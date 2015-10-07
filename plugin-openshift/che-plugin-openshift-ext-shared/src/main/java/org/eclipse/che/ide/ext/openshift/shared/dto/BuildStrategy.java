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
public interface BuildStrategy {
    SourceBuildStrategy getSourceStrategy();

    void setSourceStrategy(SourceBuildStrategy sourceStrategy);

    BuildStrategy withSourceStrategy(SourceBuildStrategy sourceStrategy);

    DockerBuildStrategy getDockerStrategy();

    void setDockerStrategy(DockerBuildStrategy dockerStrategy);

    BuildStrategy withDockerStrategy(DockerBuildStrategy dockerStrategy);

    String getType();

    void setType(String type);

    BuildStrategy withType(String type);

    CustomBuildStrategy getCustomStrategy();

    void setCustomStrategy(CustomBuildStrategy customStrategy);

    BuildStrategy withCustomStrategy(CustomBuildStrategy customStrategy);

}
