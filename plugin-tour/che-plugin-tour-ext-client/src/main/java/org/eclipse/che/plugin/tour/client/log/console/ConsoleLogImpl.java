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
package org.eclipse.che.plugin.tour.client.log.console;

import org.eclipse.che.plugin.tour.client.TourExtension;
import org.eclipse.che.plugin.tour.client.log.Log;

/**
 * Implementation of a simple logger that can be turned off/on
 * @author Florent Benoit
 */
public class ConsoleLogImpl implements Log {

    /**
     * Debug mode enabled or disabled.
     */
    private boolean debugMode;

    /**
     * Toggle to debug mode
     * @param debugMode true/false
     */
    @Override
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    /**
     * Debug the given message with args
     * @param  message the text to display
     * @param args the optional arguments
     */
    public void debug(String message, Object... args) {
        if (debugMode) {
            org.eclipse.che.ide.util.loging.Log.info(TourExtension.class, format(message, args));
        }
    }

    /**
     * Display into Javascript console some message
     * @param  message the text to display
     * @param args the optional arguments
     */
    public void info(String message, Object... args) {
        org.eclipse.che.ide.util.loging.Log.info(TourExtension.class, message);
    }

    /**
     * Display into Javascript console some message
     * @param  message the text to display
     * @param args the optional arguments
     */
    public void error(String message, Object... args) {
        org.eclipse.che.ide.util.loging.Log.info(TourExtension.class, message);
    }


    /**
     * Format the given pattern and args like MessageFormat in JDK (but in GWT there)
     * @param  pattern the pattern to use
     * @param arguments the optional arguments
     */
    public static String format(String pattern, Object... arguments) {
        // A very simple implementation of format
        int i = 0;
        while (i < arguments.length) {
            String delimiter = "{" + i + "}";
            while (pattern.contains(delimiter)) {
                pattern = pattern.replace(delimiter, String.valueOf(arguments[i]));
            }
            i++;
        }
        return pattern;
    }

}
