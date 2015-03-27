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
 * Defines a representation of a Step tour in Codenvy
 * @author Florent Benoit
 */
@DTO
public interface GuidedTourStep {

    /**
     * @return the title of the step
     */
    String getTitle();

    /**
     * Defines the title of the popup
     * @param title the title
     */
    void setTitle(String title);

    /**
     * Defines the title of the popup
     * @param title the title
     * @return this
     */
    GuidedTourStep withTitle(String title);

    /**
     * @return label of the next button
     */
    String getNextButtonLabel();

    /**
     * Defines the label of the "next" button
     * @param label the text to display
     */
    void setNextButtonLabel(String label);

    /**
     * Defines the label of the "next" button
     * @param label the text to display
     * @return this
     */
    GuidedTourStep withNextButtonLabel(String label);

    /**
     * @return boolean true if the skip button has to be displayed
     */
    Boolean getSkipButton();

    /**
     * Defines if skip button needs to be displayed or not
     * @param skip if true it will be skipped
     */
    void setSkipButton(Boolean skip);

    /**
     * Defines if skip button needs to be displayed or not
     * @param skip if true it will be skipped
     * @return this
     */
    GuidedTourStep withSkipButton(Boolean skip);

    /**
     * @return label of the skip button
     */
    String getSkipButtonLabel();

    /**
     * Defines the label of the "skip" button
     * @param label the text to display
     */
    void setSkipButtonLabel(String label);

    /**
     * Defines the label of the "skip" button
     * @param label the text to display
     * @return this
     */
    GuidedTourStep withSkipButtonLabel(String label);

    /**
     * @return content that will appear on the bubble
     */
    String getContent();

    /**
     * Defines the content
     * @param content the content of the step
     */
    void setContent(String content);

    /**
     * Defines the content
     * @param content the content of the step
     * @return this
     */
    GuidedTourStep withContent(String content);

    /**
     * @return HTML element on which the step will be applied
     */
    String getElement();

    /**
     * Defines the HTML element on which the step will be applied
     * @param element the step
     */
    void setElement(String element);

    /**
     * Defines the HTML element on which the step will be applied
     * @param element the step
     * @return this
     */
    GuidedTourStep withElement(String element);

    /**
     * @return the placement of the tooltip/tour : TOP, LEFT, RIGHT, BOTTOM
     */
    String getPlacement();

    /**
     * Sets the placement of the tooltip/tour
     * @param placement the value : TOP, BOTTOM, LEFT, RIGHT
     */
    void setPlacement(String placement);

    /**
     * Sets the placement of the tooltip/tour
     * @param placement the value : TOP, BOTTOM, LEFT, RIGHT
     * @return this
     */
    GuidedTourStep withPlacement(String placement);

    /**
     * @return offset to apply on X axis
     */
    String getXOffset();

    /**
     * Defines shift on X Axis of the bubble
     * @param xOffset the shift on X axis
     */
    void setXOffset(String xOffset);

    /**
     * Defines shift on X Axis of the bubble
     * @param xOffset the shift on X axis
     * @return this
     */
    GuidedTourStep withXOffset(String xOffset);

    /**
     * @return offset to apply on Y axis
     */
    String getYOffset();

    /**
     * Defines shift on Y Axis of the bubble
     * @param yOffset the shift on Y axis
     */
    void setYOffset(String yOffset);

    /**
     * Defines shift on Y Axis of the bubble
     * @param yOffset the shift on Y axis
     * @return this
     */
    GuidedTourStep withYOffset(String yOffset);

    /**
     * @return offset to apply on the arrow
     */
    String getArrowOffset();

    /**
     * Defines a shift for the arrow
     * @param arrowOffset the offset to apply on the arrow
     */
    void setArrowOffset(String arrowOffset);

    /**
     * Defines a shift for the arrow
     * @param arrowOffset the offset to apply on the arrow
     * @return this
     */
    GuidedTourStep withArrowOffset(String arrowOffset);

    /**
     * @return width of the step bubble
     */
    String getWidth();

    /**
     * Defines the width of the step bubble
     * @param width the step bubble width
     */
    void setWidth(String width);

    /**
     * Defines the width of the step bubble
     * @param width the step bubble width
     * @return this
     */
    GuidedTourStep withWidth(String width);

    /**
     * @return all the post actions defined for this step
     */
    List<GuidedTourAction> getActions();

    /**
     * Defines list of actions for this step
     * @param actions the list of actions
     */
    void setActions(List<GuidedTourAction> actions);

    /**
     * Defines list of actions for this step
     * @param actions the list of actions
     * @return this
     */
    GuidedTourStep withActions(List<GuidedTourAction> actions);

    /**
     * @return all the image overlays for this step
     */
    List<GuidedTourImageOverlay> getOverlays();

    /**
     * Defines list of overlays for this step
     * @param overlays the list of overlays
     */
    void setOverlays(List<GuidedTourImageOverlay> overlays);

    /**
     * Defines list of overlays for this step
     * @param overlays the list of overlays
     * @return this
     */
    GuidedTourStep withOverlays(List<GuidedTourImageOverlay> overlays);

    /**
     * @return true if arrow needs to be hidden
     */
    Boolean getHideArrow();

    /**
     * Hides the arrow
     * @param hide boolean for hiding or showing the arrow
     */
    void setHideArrow(Boolean hide);

    /**
     * Hides the arrow
     * @param hide boolean for hiding or showing the arrow
     * @return this
     */
    GuidedTourStep withHideArrow(Boolean hide);


    /**
     * @return true if bubble number needs to be hidden
     */
    Boolean getHideBubbleNumber();

    /**
     * Hides the bubble number
     * @param hide boolean for hiding or showing the bubble number
     */
    void setHideBubbleNumber(Boolean hide);

    /**
     * Hides the bubble number
     * @param hide boolean for hiding or showing the bubble number
     * @return this
     */
    GuidedTourStep withHideBubbleNumber(Boolean hide);

}
