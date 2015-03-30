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
package org.eclipse.che.ide.ext.java.jdi.client.fqn;

import org.eclipse.che.ide.collections.Collections;
import org.eclipse.che.ide.collections.StringMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** @author Evgen Vidolob */
@Singleton
public class FqnResolverFactory {
    private StringMap<FqnResolver> resolvers;

    /** Create factory. */
    @Inject
    protected FqnResolverFactory() {
        this.resolvers = Collections.createStringMap();
    }

    public void addResolver(@Nonnull String mimeType, @Nonnull FqnResolver resolver) {
        resolvers.put(mimeType, resolver);
    }

    @Nullable
    public FqnResolver getResolver(@Nonnull String mimeType) {
        return resolvers.get(mimeType);
    }

    public boolean isResolverExist(@Nonnull String mimeType) {
        return resolvers.containsKey(mimeType);
    }
}