/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.svn.shared;

import java.util.List;

import org.eclipse.che.dto.shared.DTO;

@DTO
public interface CLIOutputResponseList {

    /**
     * @return {@link List} the list of repositories
     */
    List<CLIOutputResponse> getCLIOutputResponses();
    
    void setCLIOutputResponses(List<CLIOutputResponse> outputResponses);

    CLIOutputResponseList withCLIOutputResponses(List<CLIOutputResponse> outputResponses);
}