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

import java.util.List;

/**
 * Defines the configuration of Guided Tour
 * @author Florent Benoit
 */
@DTO
public interface GuidedTourConfiguration {

    /**
     * @return the name
     */
    String getName();

    /**
     * Defines the name of the tour
     * @param name the name of the tour
     */
    void setName(String name);

    /**
     * Defines the name of the tour
     * @param name the name of the tour
     * @return this
     */
    GuidedTourConfiguration withName(final String name);

    /**
     * @return true if we have to use debug when the tour is running. With <em>false</em> no debug data will be printed
     */
    boolean getDebugMode();

    /**
     * Sets the debug mode parameter
     * @param debugMode false/true. If true, logs will be printed in the javascript console
     */
    void setDebugMode(final boolean debugMode);

    /**
     * Sets the debug mode parameter
     * @param debugMode false/true. If true, logs will be printed in the javascript console
     * @return this
     */
    GuidedTourConfiguration withDebugMode(final boolean debugMode);

    /**
     * @return the steps of this Guided tour
     */
    List<GuidedTourStep> getSteps();

    /**
     * Define the steps of this Guided Tour
     * @param steps the steps that will be showed on the tour
     */
    void setSteps(List<GuidedTourStep> steps);

    /**
     * Define the steps of this Guided Tour
     * @param steps the steps that will be showed on the tour
     * @return this
     */
    GuidedTourConfiguration withSteps(List<GuidedTourStep> steps);

    /**
     * @return true if first step is a welcome step
     */
    boolean getHasWelcomeStep();

    /**
     * Defines if first step is seen as a welcome step
     * @param hasWelcomeStep true if first step of the tour is a welcome step
     */
    void setHasWelcomeStep(boolean hasWelcomeStep);

    /**
     /**
     * Defines if first step is seen as a welcome step
     * @param hasWelcomeStep true if first step of the tour is a welcome step
     * @return this
     */
    GuidedTourConfiguration withHasWelcomeStep(boolean hasWelcomeStep);

}
