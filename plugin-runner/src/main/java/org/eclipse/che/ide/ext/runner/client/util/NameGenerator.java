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
package org.eclipse.che.ide.ext.runner.client.util;

import com.google.gwt.i18n.client.DateTimeFormat;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * The class contains business logic which allows us to generate names for environments
 *
 * @author Dmitry Shnurenko
 */
public class NameGenerator {
    public static final DateTimeFormat DATE_TIME_FORMAT = DateTimeFormat.getFormat("hh-mm-ss_dd-mm-yyyy");
    public static final String PREFIX_NAME = "Environment_";

    private NameGenerator() {
        throw new UnsupportedOperationException("Creation instance for this class is unsupported operation");
    }

    /** @return environment name which consists of string 'Environment ' and current date */
    @Nonnull
    public static String generate() {
        return "Environment_" + DATE_TIME_FORMAT.format(new Date());
    }
}