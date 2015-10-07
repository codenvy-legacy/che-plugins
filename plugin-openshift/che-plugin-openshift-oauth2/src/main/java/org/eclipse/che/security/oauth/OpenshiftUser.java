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
package org.eclipse.che.security.oauth;


import org.eclipse.che.security.oauth.shared.User;

/** Represents Openshift user. */
public class OpenshiftUser implements User {
    private String name;
    private String email;

    @Override
    public final String getId() {
        return email;
    }

    @Override
    public final void setId(String id) {
        // JSON response from Github API contains key 'id' but it has different purpose.
        // Ignore calls of this method. Email address is used as user identifier.
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public OpenshiftUser withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    public OpenshiftUser withEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public String toString() {
        return "OpenshiftUser{" +
               "id='" + getId() + '\'' +
               ", name='" + name + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}
