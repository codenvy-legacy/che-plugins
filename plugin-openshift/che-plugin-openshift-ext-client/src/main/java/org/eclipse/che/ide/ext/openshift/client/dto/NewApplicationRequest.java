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
package org.eclipse.che.ide.ext.openshift.client.dto;

import org.eclipse.che.api.workspace.shared.dto.ProjectConfigDto;
import org.eclipse.che.dto.shared.DTO;
import org.eclipse.che.ide.ext.openshift.shared.dto.Project;
import org.eclipse.che.ide.ext.openshift.shared.dto.ProjectRequest;
import org.eclipse.che.ide.ext.openshift.shared.dto.Template;

/**
 *
 * @author Vlad Zhukovskiy
 */
@DTO
public interface NewApplicationRequest {
    ProjectRequest getProjectRequest();

    void setProjectRequest(ProjectRequest projectRequest);

    NewApplicationRequest withProjectRequest(ProjectRequest projectRequest);

    Project getProject();

    void setProject(Project project);

    NewApplicationRequest withProject(Project project);

    Template getTemplate();

    void setTemplate(Template template);

    NewApplicationRequest withTemplate(Template template);

    ProjectConfigDto getProjectConfigDto();

    void setProjectConfigDto(ProjectConfigDto projectConfigDto);

    NewApplicationRequest withProjectConfigDto(ProjectConfigDto projectConfigDto);
}
