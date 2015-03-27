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

package org.eclipse.che.plugin.tour.client.html;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Allow to get HTML elements of the Guided Tour widget
 * @author Florent Benoit
 */
public class DomElementHelper {

    /**
     * root body element
     */
    private Element bodyElement;

    /**
     * Guided tour element
     */
    private Element guidedTourElement;

    /**
     * Container for elements
     */
    private Element bubbleContainerElement;

    /**
     * Arrow element
     */
    private Element arrowElement;

    /**
     * Content element
     */
    private Element contentElement;

    /**
     * Bubble number element
     */
    private Element bubbleNumberElement;

    /**
     * Initialize the elements at load
     */
    public DomElementHelper() {
        this.bodyElement = RootPanel.getBodyElement();
        initElements();
    }

    /***
     * Search all elements and sets their value
     */
    public void initElements() {
        this.guidedTourElement = getChildElement(bodyElement, "hopscotch-bubble animated");

        this.bubbleContainerElement = getChildElement(guidedTourElement, "hopscotch-bubble-container");

        // get arrow element
        this.arrowElement = getChildElement(guidedTourElement, "hopscotch-bubble-arrow-container");

        // get bubble number element
        this.bubbleNumberElement = getChildElement(bubbleContainerElement, "hopscotch-bubble-number");

        // get content element
        this.contentElement = getChildElement(bubbleContainerElement, "hopscotch-bubble-content");

    }


    public Element getBodyElement() {
        return bodyElement;
    }

    public Element getGuidedTourElement() {
        return guidedTourElement;
    }

    public Element getBubbleContainerElement() {
        return bubbleContainerElement;
    }

    public Element getArrowElement() {
        return arrowElement;
    }

    public Element getContentElement() {
        return contentElement;
    }

    public Element getBubbleNumberElement() {
        return bubbleNumberElement;
    }

    /**
     * Helper method used to search inside elements the given matching classname attribute
     * @param element root element to search
     * @param className the name of the class to search
     * @return the element, else null if not found
     */
    protected Element getChildElement(Element element, String className) {

        for (int i = 0; i < DOM.getChildCount(element); i++) {
            Element child = DOM.getChild(element, i);
            if (child.getClassName() != null && child.getClassName().startsWith(className)) {
                return child;
            }
        }

        return null;
    }


}
