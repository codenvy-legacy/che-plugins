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
package org.eclipse.che.ide.ext.git.server.nativegit;

import org.eclipse.che.ide.ext.git.server.GitException;
import org.eclipse.che.ide.ext.git.shared.GitUser;

/**
 * Provides credentials to use with git commands that need it
 *
 * @author Eugene Voevodin
 * @author Sergii Kabashniuk
 */
public interface CredentialsProvider {
    /**
     * @return credentials for current user in this provider
     * to execute git operation.
     * @throws GitException
     */
    UserCredential getUserCredential() throws GitException;

    /**
     * @return information about current user in this provider
     * @throws GitException
     */
    GitUser getUser() throws GitException;

    /**
     * @return Provider id.
     */
    String getId();

    /**
     * @param url
     * @return return true if current provider can provide credentials for the given url.
     */
    boolean canProvideCredentials(String url);
}
