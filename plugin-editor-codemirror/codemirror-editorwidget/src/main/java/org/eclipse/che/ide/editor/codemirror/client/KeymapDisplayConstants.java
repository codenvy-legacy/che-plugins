/*******************************************************************************
 * Copyright (c) 2014-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.editor.codemirror.client;

import com.google.gwt.i18n.client.Constants;

public interface KeymapDisplayConstants extends Constants {

    @DefaultStringValue("Default")
    String defaultKeymap();

    @DefaultStringValue("Emacs")
    String emacs();

    @DefaultStringValue("Vim")
    String vim();

    @DefaultStringValue("Sublime")
    String sublime();
}
