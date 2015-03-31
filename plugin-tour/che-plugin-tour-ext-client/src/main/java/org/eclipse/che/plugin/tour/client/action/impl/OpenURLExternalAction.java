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
package org.eclipse.che.plugin.tour.client.action.impl;

import org.eclipse.che.plugin.tour.client.action.ExternalAction;
import org.eclipse.che.plugin.tour.client.log.Log;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;

/**
 * Action for opening a external link in a new tab.
 * @author Florent Benoit
 */
public class OpenURLExternalAction implements ExternalAction {

    /**
     * Logger.
     */
    @Inject
    private Log log;


    /**
     * Accept the category that is "openurl"
     * @param category that should match "openurl" for this action
     * @return true if "openurl" was given as category
     */
    @Override
    public boolean accept(String category) {
        return "openurl".equals(category);
    }

    /**
     * Open URL link (after performing a check).
     * @param link the URL link to open
     */
    @Override
    public void execute(String link) {
        String uri = UriUtils.sanitizeUri(link);
        log.debug("Opening URI {0}", uri);
        Window.open(uri, "_blank", "");
    }

}
