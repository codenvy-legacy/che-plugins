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
package org.eclipse.che.ide.extension.machine.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Client resources.
 *
 * @author Ann Shumilova
 */
public interface MachineResources extends ClientBundle {
    @Source("clear-logs.svg")
    SVGResource clear();

    interface Css extends CssResource {
        @ClassName("info-panel")
        String infoPanel();

        @ClassName("data-label")
        String dataLabel();

        @ClassName("partIcon")
        String partIcon();

        @ClassName("inQueue")
        String inQueue();

        @ClassName("inProgress")
        String inProgress();

        @ClassName("done")
        String done();

        @ClassName("failed")
        String failed();

        @ClassName("timeout")
        String timeout();
    }

    @Source({"machine.css", "org/eclipse/che/ide/api/ui/style.css"})
    Css machine();
}