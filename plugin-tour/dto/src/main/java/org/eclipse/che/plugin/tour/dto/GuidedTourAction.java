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

package org.eclipse.che.plugin.tour.dto;

import org.eclipse.che.dto.shared.DTO;

/**
 * Defines an action that could be performed at the end of a step
 * @author Florent Benoit
 */
@DTO
public interface GuidedTourAction {

    /**
     * Defines the action
     * @param action the given action to set
     */
    void setAction(String action);

    /**
     * @return the current action
     */
    String getAction();

}
