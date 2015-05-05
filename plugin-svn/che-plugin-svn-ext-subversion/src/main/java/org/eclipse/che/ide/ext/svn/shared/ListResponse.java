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
package org.eclipse.che.ide.ext.svn.shared;

import org.eclipse.che.dto.shared.DTO;

import javax.validation.constraints.NotNull;
import java.util.List;

@DTO
public interface ListResponse {

    /**************************************************************************
     *
     *  Subversion command
     *
     **************************************************************************/

    String getCommand();

    ListResponse withCommand(@NotNull final String command);

    /**************************************************************************
     *
     *  Execution output
     *
     **************************************************************************/

    List<String> getOutput();

    ListResponse withOutput(@NotNull final List<String> output);

    /**************************************************************************
     *
     *  Error output
     *
     **************************************************************************/

    List<String> getErrorOutput();

    ListResponse withErrorOutput(List<String> errorOutput);

}
