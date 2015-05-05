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

/**
 * Definition of subversion item.
 */
@DTO
public interface SubversionItem {

    String getPath();
    SubversionItem withPath(String path);

    String getName();
    SubversionItem withName(String name);

    String getURL();
    SubversionItem withURL(String url);

    String getRelativeURL();
    SubversionItem withRelativeURL(String relativeURL);

    String getRepositoryRoot();
    SubversionItem withRepositoryRoot(String repositoryRoot);

    String getRepositoryUUID();
    SubversionItem withRepositoryUUID(String repositoryUUID);

    String getRevision();
    SubversionItem withRevision(String revision);

    String getNodeKind();
    SubversionItem withNodeKind(String nodeKind);

    String getSchedule();
    SubversionItem withSchedule(String schedule);

    String getLastChangedRev();
    SubversionItem withLastChangedRev(String lastChangedRev);

    String getLastChangedDate();
    SubversionItem withLastChangedDate(String lastChangedDate);

}
