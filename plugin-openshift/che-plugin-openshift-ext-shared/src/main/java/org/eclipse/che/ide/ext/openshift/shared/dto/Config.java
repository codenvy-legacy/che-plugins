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
import org.eclipse.che.dto.shared.JsonFieldName;

import java.util.List;
import java.util.Map;

@DTO
public interface Config {

    @JsonFieldName("Hostname")
    String getHostname();

    void setHostname(String hostname);

    Config withHostname(String hostname);

    @JsonFieldName("User")
    String getUser();

    void setUser(String user);

    Config withUser(String user);

    @JsonFieldName("ExposedPorts")
    Object getExposedPorts();

    void setExposedPorts(Object exposedPorts);

    Config withExposedPorts(Object exposedPorts);

    @JsonFieldName("Env")
    List<String> getEnv();

    void setEnv(List<String> env);

    Config withEnv(List<String> env);

    @JsonFieldName("Cmd")
    List<String> getCmd();

    void setCmd(List<String> cmd);

    Config withCmd(List<String> cmd);

    @JsonFieldName("Image")
    String getImage();

    void setImage(String image);

    Config withImage(String image);

    @JsonFieldName("WorkingDir")
    String getWorkingDir();

    void setWorkingDir(String workingDir);

    Config withWorkingDir(String workingDir);

    @JsonFieldName("Labels")
    Map<String, String> getLabels();

    void setLabels(Map<String, String> labels);

    Config withLabels(Map<String, String> labels);
}
