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
package org.eclipse.che.ide.ext.bitbucket.shared;

import org.eclipse.che.dto.shared.DTO;

import java.util.List;

/**
 * Represents a pull requests page in the Bitbucket API.
 *
 * @author Kevin Pollet
 */
@DTO
public interface BitbucketPullRequestsPage {
    int getSize();

    void setSize(int size);

    int getPage();

    void setPage(int page);

    int getPagelen();

    void setPagelen(int pagelen);

    String getNext();

    void setNext(String next);

    String getPrevious();

    void setPrevious(String previous);

    List<BitbucketPullRequest> getValues();

    void setValues(List<BitbucketPullRequest> values);
}
