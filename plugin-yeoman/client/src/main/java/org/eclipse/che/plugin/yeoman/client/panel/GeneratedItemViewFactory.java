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
 * Creates an element with the given name and type
 *
 * @author Florent Benoit
 */
public interface GeneratedItemViewFactory {

    /**
     * Build a new element based on its name and type and also UI resources
     *
     * @param name
     *         the given name
     * @param type
     *         the tpe of this element
     * @return a new UI element
     */
    GeneratedItemView create(String name, YeomanGeneratorType type);
}
