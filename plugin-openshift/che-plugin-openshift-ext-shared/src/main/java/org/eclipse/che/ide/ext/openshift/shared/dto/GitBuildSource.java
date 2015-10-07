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
public interface GitBuildSource {
    String getRef();

    void setRef(String ref);

    GitBuildSource withRef(String ref);

    String getHttpProxy();

    void setHttpProxy(String httpProxy);

    GitBuildSource withHttpProxy(String httpProxy);

    String getHttpsProxy();

    void setHttpsProxy(String httpsProxy);

    GitBuildSource withHttpsProxy(String httpsProxy);

    String getUri();

    void setUri(String uri);

    GitBuildSource withUri(String uri);

}
