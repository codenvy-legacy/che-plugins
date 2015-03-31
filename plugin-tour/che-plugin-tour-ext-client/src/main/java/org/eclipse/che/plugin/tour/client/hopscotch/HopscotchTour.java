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

package org.eclipse.che.plugin.tour.client.hopscotch;

import com.eemi.gwt.tour.client.Tour;

/**
 * Allow to interface with real implementation
 * @author Florent Benoit
 */
public interface HopscotchTour {

    /**
     * @return the current tour
     */
    Tour getCurrentTour();

    /**
     * Initialize the tour.
     */
    void init();

    /**
     * @return the current step number
     */
    int getCurrentStepNum();

    /**
     * Starts the given tour with the given step number
     * @param tour the tour describing all the steps
     * @param currentStep the number of the step to execute
     */
    void startTour(Tour tour, int currentStep);


    /**
     * Starts the given tour with the given step number
     * @param tour the tour describing all the steps
     */
    void startTour(Tour tour);

    /**
     * Ends the current tour (if any)
     */
    void endTour();

}
