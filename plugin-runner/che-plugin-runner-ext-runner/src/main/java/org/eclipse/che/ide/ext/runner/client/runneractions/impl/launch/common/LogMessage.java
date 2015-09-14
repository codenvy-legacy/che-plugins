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
package org.eclipse.che.ide.ext.runner.client.runneractions.impl.launch.common;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * It contains information about received message from server.
 *
 * @author Artem Zatsarynnyy
 * @author Andrey Plotnikov
 */
public class LogMessage {
    private final int    lineNumber;
    private final String text;

    public LogMessage(@Min(value=0) int lineNumber, @NotNull String text) {
        this.lineNumber = lineNumber;
        this.text = text;
    }

    /** @return number of message line */
    @Min(value=0)
    public int getNumber() {
        return lineNumber;
    }

    /** @return content that needs to shown in the line */
    @NotNull
    public String getText() {
        return text;
    }

}