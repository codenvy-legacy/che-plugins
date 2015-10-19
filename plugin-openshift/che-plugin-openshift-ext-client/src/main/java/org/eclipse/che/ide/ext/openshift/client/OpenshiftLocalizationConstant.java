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
package org.eclipse.che.ide.ext.openshift.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author Sergii Leschenko
 */
public interface OpenshiftLocalizationConstant extends Messages {
    @Key("authorization.request.title")
    String authorizationRequestTitle();

    @Key("authorization.request.message")
    String authorizationRequestMessage();

    @Key("openshift.connect.account.title")
    String connectAccountTitle();

    @Key("openshift.disconnect.account.title")
    String disconnectAccountTitle();

    @Key("openshift.login.successful")
    String loginSuccessful();

    @Key("openshift.login.failed")
    String loginFailed();

    @Key("openshift.logout.successful")
    String logoutSuccessful();

    @Key("openshift.logout.failed")
    String logoutFailed();
}
