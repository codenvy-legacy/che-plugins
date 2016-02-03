/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.extension.maven.client.effectivepom;

import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.ide.MimeType;
import org.eclipse.che.ide.api.project.node.HasProjectConfig;
import org.eclipse.che.ide.api.project.tree.VirtualFile;

/**
 * Simplified implementation of the {@link VirtualFile}.
 * Keeps only necessary information for effective pom file.
 *
 * @author Valeriy Svydenko
 */
public class EffectivePom implements VirtualFile {
    private final static String NAME = "effective-pom.xml";
    private final static String PATH = "effective_pom";

    private final String content;

    public EffectivePom(String content) {
        this.content = content;
    }

    @Override
    public String getPath() {
        return  PATH;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public String getMediaType() {
        return MimeType.TEXT_XML;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public HasProjectConfig getProject() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContentUrl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Promise<String> getContent() {
        return Promises.resolve(content);
    }

    @Override
    public Promise<Void> updateContent(String content) {
        throw new UnsupportedOperationException();
    }
}
