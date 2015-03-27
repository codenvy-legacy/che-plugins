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
package org.eclipse.che.ide.editor.codemirror.client.minimap;

import elemental.dom.Element;

/**
 * Factory of {@link MinimapPresenter} instances.
 */
public class MinimapFactory {

    /**
     * Creates an instance of {@link MinimapPresenter}.
     * 
     * @param element the DOM element.
     * @return he minimap instance
     */
    public MinimapPresenter createMinimap(final Element element) {
        final MinimapView view = new MinimapViewImpl(element);
        final MinimapPresenter minimap = new MinimapPresenter(view);
        view.setDelegate(minimap);
        return minimap;
    }

}
