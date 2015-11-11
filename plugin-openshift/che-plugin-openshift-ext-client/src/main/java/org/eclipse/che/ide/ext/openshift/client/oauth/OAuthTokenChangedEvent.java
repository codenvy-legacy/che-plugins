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
package org.eclipse.che.ide.ext.openshift.client.oauth;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Sergii Leschenko
 */
public class OAuthTokenChangedEvent extends GwtEvent<OAuthTokenChangedEventHandler> {
    public static Type<OAuthTokenChangedEventHandler> TYPE = new Type<>();

    private String token;

    public OAuthTokenChangedEvent(String token) {
        this.token = token;
    }

    @Override
    public Type<OAuthTokenChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(OAuthTokenChangedEventHandler handler) {
        handler.onTokenChange(token);
    }
}
