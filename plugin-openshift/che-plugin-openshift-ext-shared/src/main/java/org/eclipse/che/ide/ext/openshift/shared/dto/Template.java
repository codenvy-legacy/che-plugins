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
public interface Template {
    ObjectMeta getMetadata();

    void setMetadata(ObjectMeta metadata);

    Template withMetadata(ObjectMeta metadata);

    String getApiVersion();

    void setApiVersion(String apiVersion);

    Template withApiVersion(String apiVersion);

    String getKind();

    void setKind(String kind);

    Template withKind(String kind);

    List<Object> getObjects();

    void setObjects(List<Object> objects);

    Template withObjects(List<Object> objects);

    List<Parameter> getParameters();

    void setParameters(List<Parameter> parameters);

    Template withParameters(List<Parameter> parameters);

    Map<String, String> getLabels();

    void setLabels(Map<String, String> labels);

    Template withLabels(Map<String, String> labels);

}
