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

package org.eclipse.che.plugin.tour.client.action;

/**
 * Each external action needs to implement this interface.
 * Call is made on two steps. First, if the category match, Guided Tour will call the execute action.
 * @author Florent Benoit
 */
public interface ExternalAction {

    /**
     * Checks if the given category will be handled by the implementation of the action
     * @param category a short name for defining a category
     * @return true if category is accepted by the underlying implementation
     */
    boolean accept(String category);

    /**
     * Once action has been accepted by accept method, executes the given action
     * @param action the name of the action
     */
    void execute(String action);
}
