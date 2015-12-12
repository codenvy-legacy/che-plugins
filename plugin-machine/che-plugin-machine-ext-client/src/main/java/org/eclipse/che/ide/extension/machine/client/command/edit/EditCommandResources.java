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
package org.eclipse.che.ide.extension.machine.client.command.edit;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.DataResource;

import org.vectomatic.dom.svg.ui.SVGResource;


/**
 * Class contains references to resources which need to correct displaying of command wizard dialog.
 *
 * @author Oleksii Orel
 */
public interface EditCommandResources extends ClientBundle {

    interface EditCommandStyles extends CssResource {

            /** Returns the CSS class name for tree root. */
            String root();

            /** Returns the CSS class name for tree icons. */
            String icon();

            /** Returns the CSS class name for tree labels. */
            String label();

            /** Returns the CSS class name for category header. */
            String categoryHeader();

            /** Returns the CSS class name for category sub element header. */
            String categorySubElementHeader();

            /** Returns the CSS class name for button area. */
            String buttonArea();

            /** Returns the CSS class filter placeholder. */
            String filterPlaceholder();
    }

    @Source({"CommandRenderer.css", "org/eclipse/che/ide/api/ui/style.css"})
    EditCommandStyles getCss();

    @DataResource.MimeType("image/svg+xml")
    @Source("find-icon.svg")
    DataResource findIcon();

    @Source("add-command-button.svg")
    SVGResource addCommandButton();

    @Source("duplicate-command-button.svg")
    SVGResource duplicateCommandButton();

    @Source("remove-command-button.svg")
    SVGResource removeCommandButton();
}
