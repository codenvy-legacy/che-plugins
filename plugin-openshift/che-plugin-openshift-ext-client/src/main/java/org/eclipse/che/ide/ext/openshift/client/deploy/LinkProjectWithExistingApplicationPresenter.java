/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.openshift.client.deploy;

/**
 * Presenter, which handles logic for linking current project with OpenShift application.
 *
 * @author Anna Shumilova
 */
public class LinkProjectWithExistingApplicationPresenter implements LinkProjectWithExistingApplicationView.ActionDelegate {

    private final LinkProjectWithExistingApplicationView view;

    public LinkProjectWithExistingApplicationPresenter(LinkProjectWithExistingApplicationView view) {
        this.view = view;
        this.view.setDelegate(this);
    }

    public void show() {
        this.view.show();
    }
}
