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

package org.eclipse.che.plugin.tour.client.hopscotch.core;

import org.eclipse.che.plugin.tour.client.hopscotch.HopscotchTour;
import com.eemi.gwt.tour.client.GwtTour;
import com.eemi.gwt.tour.client.Tour;

/**
 * Implementation which handle with HopScotch implementation
 * @author Florent Benoit
 */
public class HopscotchTourImpl implements HopscotchTour {

    /**
     * @return the current tour
     */
    @Override
    public Tour getCurrentTour() {
        return GwtTour.getCurrTour();
    }

    /**
     * Initialize the tour.
     */
    @Override
    public void init() {
        GwtTour.load();

        // remove all callouts
        GwtTour.removeAllCallOuts();
        GwtTour.endTour(true);

    }

    /**
     * @return the current step number
     */
    @Override
    public int getCurrentStepNum() {
        return GwtTour.getCurrStepNum();
    }

    /**
     * Starts the given tour with the given step number
     * @param tour the tour describing all the steps
     * @param currentStep the number of the step to execute
     */
    @Override
    public void startTour(Tour tour, int currentStep) {
        GwtTour.startTour(tour, currentStep);
    }

    /**
     * Starts the given tour with the given step number
     * @param tour the tour describing all the steps
     */
    @Override
    public void startTour(Tour tour) {
        GwtTour.startTour(tour);
    }

    /**
     * Ends the current tour (if any)
     */
    public void endTour() {
        GwtTour.removeAllCallOuts();
        GwtTour.endTour(true);
    }
}
