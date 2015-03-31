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
package org.eclipse.che.ide.ext.git.shared;

import org.eclipse.che.dto.shared.DTO;

import java.util.Map;

/**
 * Abstract request to {@link org.eclipse.che.ide.ext.git.server.GitConnection}.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GitRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
@DTO
public interface GitRequest {
    /** @return set of request attributes */
    Map<String, String> getAttributes();
}
