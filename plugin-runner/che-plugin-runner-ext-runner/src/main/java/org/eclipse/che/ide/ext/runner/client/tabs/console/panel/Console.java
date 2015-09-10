/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.runner.client.tabs.console.panel;

import com.google.gwt.user.client.ui.IsWidget;

import javax.validation.constraints.NotNull;

/**
 * The widget that provides an ability to show different messages. It contains methods for showing messages and cleaning message (removing
 * all messages from area).
 *
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 */
public interface Console extends IsWidget {

    /**
     * Prints message with s given content.
     *
     * @param message
     *         message that needs to be printed
     */
    void print(@NotNull String message);

    /**
     * Prints Info message with a given content.
     * Printed line will look like this: [INFO] some string
     *
     * @param line
     *         line that needs to be printed
     */
    void printInfo(@NotNull String line);

    /**
     * Prints Error message with a given content.
     * Printed line will look like this: [ERROR] some string
     *
     * @param line
     *         line that needs to be printed
     */
    void printError(@NotNull String line);

    /**
     * Prints Warning message with a given content.
     * Printed line will look like this: [WARNING] some string
     *
     * @param line
     *         line that needs to be printed
     */
    void printWarn(@NotNull String line);

    /** Scroll to bottom of the view. */
    void scrollBottom();

    /** Removes all messages from widget. */
    void clear();

    /**
     * Changes visibility of the console.
     *
     * @param isVisible
     *         <code>true</code> console is visible,<code>false</code> console isn't visible
     */
    void setVisible(boolean isVisible);

    /** Changes wrap text param to opposite value that the widget has now. */
    void changeWrapTextParam();

    /**
     * @return <code>true</code> when console output is wrapped it means that a content that does not fit in one line the widget will
     * separate it, <code>false</code> otherwise
     */
    boolean isWrapText();

}