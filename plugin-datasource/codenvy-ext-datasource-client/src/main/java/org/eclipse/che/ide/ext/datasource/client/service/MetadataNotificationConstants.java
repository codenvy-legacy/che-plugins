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
package org.eclipse.che.ide.ext.datasource.client.service;

import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.Messages;

@DefaultLocale("en")
public interface MetadataNotificationConstants extends Messages {

    @DefaultMessage("Fetching database metadata...")
    String notificationFetchStart();

    @DefaultMessage("Succesfully fetched database metadata")
    String notificationFetchSuccess();

    @DefaultMessage("Failed fetching database metadata")
    String notificationFetchFailure();

    @DefaultMessage("Invalid configuration for this datasource")
    String invalidConfigurationNotification();

    @DefaultMessage("Failed fetching database metadata, retrying in 20 seconds")
    String notificationRetry();
}
