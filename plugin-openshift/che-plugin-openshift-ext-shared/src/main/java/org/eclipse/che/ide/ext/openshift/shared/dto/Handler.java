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
public interface Handler {
    TCPSocketAction getTcpSocket();

    void setTcpSocket(TCPSocketAction tcpSocket);

    Handler withTcpSocket(TCPSocketAction tcpSocket);

    ExecAction getExec();

    void setExec(ExecAction exec);

    Handler withExec(ExecAction exec);

    HTTPGetAction getHttpGet();

    void setHttpGet(HTTPGetAction httpGet);

    Handler withHttpGet(HTTPGetAction httpGet);

}
