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

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GitUser.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
@DTO
public interface GitUser {
    String getName();
    
    void setName(String name);
    
    GitUser withName(String name);

    String getEmail();
    
    void setEmail(String email);
    
    GitUser withEmail(String email);
}