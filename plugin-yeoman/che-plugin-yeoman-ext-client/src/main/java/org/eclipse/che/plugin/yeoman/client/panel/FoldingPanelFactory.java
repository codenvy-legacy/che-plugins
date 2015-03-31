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

/**
 * Allow to build folding panel based on the given name
 *
 * @author Florent Benoit
 */
public interface FoldingPanelFactory {

    /**
     * Creates a new folding panel with the given name
     *
     * @param name
     *         the given name
     * @return the created widget
     */
    FoldingPanel create(String name);
}
