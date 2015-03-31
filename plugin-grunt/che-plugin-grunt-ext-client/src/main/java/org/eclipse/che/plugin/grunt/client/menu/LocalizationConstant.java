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
package org.eclipse.che.plugin.grunt.client.menu;

import com.google.gwt.i18n.client.Messages;

/**
 * Gets properties.
 * @author Florent Benoit
 */
public interface LocalizationConstant extends Messages {

    @Key("control.gruntCustomRun.id")
    String gruntCustomRunId();

    @Key("control.gruntCustomRun.text")
    String gruntCustomRunText();

    @Key("control.gruntCustomRun.description")
    String gruntCustomRunDescription();
}
