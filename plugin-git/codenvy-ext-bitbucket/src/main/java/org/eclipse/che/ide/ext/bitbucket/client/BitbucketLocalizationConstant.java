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
package org.eclipse.che.ide.ext.bitbucket.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Localization constants for the Bitbucket plugin.
 *
 * @author Kevin Pollet
 */
public interface BitbucketLocalizationConstant extends Messages {
    @Key("bitbucket.ssh.key.title")
    String bitbucketSshKeyTitle();

    @Key("bitbucket.ssh.key.label")
    String bitbucketSshKeyLabel();

    @Key("bitbucket.ssh.key.update.failed")
    String bitbucketSshKeyUpdateFailed();
}