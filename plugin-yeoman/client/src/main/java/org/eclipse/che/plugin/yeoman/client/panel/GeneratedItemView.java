/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.yeoman.client.panel;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Item that is added to FoldingPanel and providing the name of an element and a button/trash to remove element
 *
 * @author Florent Benoit
 */
public interface GeneratedItemView extends IsWidget {

    /**
     * Sets the anchor of this element (upper level than a direct parent)
     *
     * @param yeomanPartView
     */
    void setAnchor(YeomanPartView yeomanPartView);

}
