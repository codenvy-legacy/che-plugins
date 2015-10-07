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
public interface SourceBuildStrategy {
    boolean getForcePull();

    void setForcePull(boolean forcePull);

    SourceBuildStrategy withForcePull(boolean forcePull);

    ObjectReference getFrom();

    void setFrom(ObjectReference from);

    SourceBuildStrategy withFrom(ObjectReference from);

    boolean getIncremental();

    void setIncremental(boolean incremental);

    SourceBuildStrategy withIncremental(boolean incremental);

    List<EnvVar> getEnv();

    void setEnv(List<EnvVar> env);

    SourceBuildStrategy withEnv(List<EnvVar> env);

    String getScripts();

    void setScripts(String scripts);

    SourceBuildStrategy withScripts(String scripts);

    LocalObjectReference getPullSecret();

    void setPullSecret(LocalObjectReference pullSecret);

    SourceBuildStrategy withPullSecret(LocalObjectReference pullSecret);

}
