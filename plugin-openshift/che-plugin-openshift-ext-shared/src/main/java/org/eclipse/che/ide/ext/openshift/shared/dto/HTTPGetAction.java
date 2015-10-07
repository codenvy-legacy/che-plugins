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
public interface HTTPGetAction {
    String getPath();

    void setPath(String path);

    HTTPGetAction withPath(String path);

    String getScheme();

    void setScheme(String scheme);

    HTTPGetAction withScheme(String scheme);

    String getPort();

    void setPort(String port);

    HTTPGetAction withPort(String port);

    String getHost();

    void setHost(String host);

    HTTPGetAction withHost(String host);

}
