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

import java.util.List;

import org.eclipse.che.dto.shared.DTO;

import javax.validation.constraints.NotNull;

@DTO
public interface InfoResponse {

    /**************************************************************************
     *
     *  Subversion command
     *
     **************************************************************************/

    String getCommand();

    InfoResponse withCommand(@NotNull final String command);

    /**************************************************************************
     *
     *  Execution output
     *
     **************************************************************************/

    List<String> getOutput();

    InfoResponse withOutput(@NotNull final List<String> output);

    /**************************************************************************
     *
     *  Error output
     *
     **************************************************************************/

    List<String> getErrorOutput();

    InfoResponse withErrorOutput(List<String> errorOutput);

    /**************************************************************************
     *
     *  Item list
     *
     **************************************************************************/

    List<SubversionItem> getItems();

    InfoResponse withItems(List<SubversionItem> items);

}
