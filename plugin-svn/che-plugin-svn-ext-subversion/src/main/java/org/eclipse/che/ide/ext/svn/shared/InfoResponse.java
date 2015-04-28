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

    /**
     * @return the executed command
     */
    String getCommand();

    /**
     * @param command the executed command
     */
    void setCommand(@NotNull final String command);

    /**
     * @param command the executed command
     *
     * @return the response
     */
    InfoResponse withCommand(@NotNull final String command);

    /**
     * @return the update output
     */
    List<String> getOutput();

    /**
     * @param output the update output to set
     */
    void setOutput(@NotNull final List<String> output);

    /**
     * @param output the update output to use
     *
     * @return the response
     */
    InfoResponse withOutput(@NotNull final List<String> output);


    int getExitCode();
    InfoResponse withExitCode(int exitCode);


    List<String> getErrorOutput();
    InfoResponse withErrorOutput(List<String> stderr);

//    Path: .
//    URL: svn://localhost/myproj/trunk
//    Relative URL: ^/trunk
//    Repository Root: svn://localhost/myproj
//    Repository UUID: 5d5b3ac5-18a3-4240-a205-376b3b68caaa
//    Revision: 33
//    Node Kind: directory
//    Schedule: normal
//    Last Changed Rev: 32
//    Last Changed Date: 2015-04-22 14:20:33 +0300 (Wed, 22 Apr 2015)


    String getPath();
    InfoResponse withPath(String path);

    String getURL();
    InfoResponse withURL(String url);

    String getRelativeUrl();
    InfoResponse withRelativeUrl(String relativeUrl);

    String getRepositoryRoot();
    InfoResponse withRepositoryRoot(String repositoryRoot);

    String getRepositoryUUID();
    InfoResponse withRepositoryUUID(String repositoryUUID);

    String getRevision();
    InfoResponse withRevision(String revision);

    String getNodeKind();
    InfoResponse withNodeKind(String nodeKind);

    String getLastChangedRev();
    InfoResponse withLastChangedRev(String lastChangedRev);

    String getLastChangedDate();
    InfoResponse withLastChangedDate(String lastChangedDate);

}
