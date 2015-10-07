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
public interface GitSourceRevision {
    SourceControlUser getCommitter();

    void setCommitter(SourceControlUser committer);

    GitSourceRevision withCommitter(SourceControlUser committer);

    SourceControlUser getAuthor();

    void setAuthor(SourceControlUser author);

    GitSourceRevision withAuthor(SourceControlUser author);

    String getCommit();

    void setCommit(String commit);

    GitSourceRevision withCommit(String commit);

    String getMessage();

    void setMessage(String message);

    GitSourceRevision withMessage(String message);

}
