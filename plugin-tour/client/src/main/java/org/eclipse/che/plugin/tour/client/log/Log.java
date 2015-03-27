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

package org.eclipse.che.plugin.tour.client.log;

/**
 * Interface for logging
 * @author Florent Benoit
 */
public interface Log {

    /**
     * Toggle to debug mode
     * @param debugMode true/false
     */
    void setDebugMode(boolean debugMode);

    /**
     * Debug the given message with args
     * @param  message the text to display
     * @param args the optional arguments
     */
    void debug(String message, Object... args);

    /**
     * Display into Javascript console some message
     * @param  message the text to display
     * @param args the optional arguments
     */
    void info(String message, Object... args);

    /**
     * Display into Javascript console some message
     * @param message the text to display
     * @param args the optional arguments
     */
    void error(String message, Object... args);

}
