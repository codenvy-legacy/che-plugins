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
package org.kohsuke.github;

/**
 * Add mirror url original lib don't support it
 *
 * @author Vitalii Parfonov
 */
@SuppressWarnings({"UnusedDeclaration"})
public class GHRepositoryWithMirrorUrl extends GHRepository {


    private String mirror_url;


    public String getMirrorUrl() {
        return mirror_url;
    }

    @Override
    GHRepositoryWithMirrorUrl wrap(GitHub root) {
        this.root = root;
        return this;
    }
}
