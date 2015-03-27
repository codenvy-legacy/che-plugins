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
 * Represents a list of {@link BitbucketRepository}.
 *
 * @author Kevin Pollet
 */
@DTO
public interface BitbucketRepositories {
    List<BitbucketRepository> getRepositories();

    void setRepositories(List<BitbucketRepository> repositories);

    BitbucketRepositories withRepositories(List<BitbucketRepository> repositories);
}
