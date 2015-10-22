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

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * The resource interface for the Openshift extension.
 *
 * @author Ann Shumilova
 */
public interface OpenshiftResources extends ClientBundle {

    /** Returns the CSS resource for the Openshift extension. */
    @Source({"openshift.css", "org/eclipse/che/ide/api/ui/style.css"})
    Css css();

    /** The CssResource interface for the Machine extension. */
    interface Css extends CssResource {

        String sectionTitle();

        String sectionSeparator();

        String choiceTitle();

        String textInput();

        String templateList();

        String projectApplicationBox();

        String warningLabel();
    }
}
