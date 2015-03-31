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
package org.eclipse.che.plugin.grunt.runner;

import org.eclipse.che.api.runner.internal.ApplicationLogger;
import com.google.common.io.CharStreams;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Logger that will print all the logs of the grunt process
 * @author Florent Benoit
 */
public class GruntApplicationLogger implements ApplicationLogger {

    private File logFile;

    public GruntApplicationLogger(File logFile) {
        this.logFile = logFile;
    }

    @Override
    public void getLogs(Appendable output) throws IOException {
        try (Reader r =  new InputStreamReader(new FileInputStream(logFile), "UTF-8")) {
            CharStreams.copy(r, output);
        }
    }

    @Override
    public String getContentType() {
        return "text/plain";
    }

    @Override
    public void writeLine(String line) throws IOException {
        throw new UnsupportedOperationException("Read-Only logger");
    }

    @Override
    public void close() throws IOException {

    }
}
