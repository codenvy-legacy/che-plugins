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
package org.eclipse.che.ide.ext.java.client;

import com.google.gwt.resources.client.CssResource;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public interface JavaCss extends CssResource {

    @ClassName("exo-autocomplete-fqn")
    String fqnStyle();

    @ClassName("exo-codeassistant-counter")
    String counter();

    @ClassName("imports")
    String imports();

    @ClassName("importItem")
    String importItem();

    @ClassName("publicField")
    String publicField();

    @ClassName("protectedField")
    String protectedField();

    @ClassName("privateField")
    String privateField();

    @ClassName("defaultField")
    String defaultField();

    @ClassName("packageItem")
    String packageItem();

    @ClassName("overview-bottom-mark-error")
    String overviewBottomMarkError();

    @ClassName("overview-mark-warning")
    String overviewMarkWarning();

    @ClassName("overview-bottom-mark-warning")
    String overviewBottomMarkWarning();

    @ClassName("overview-mark-error")
    String overviewMarkError();

    @ClassName("overview-mark-task")
    String overviewMarkTask();

    @ClassName("mark-element")
    String markElement();

    @ClassName("error-border")
    String errorBorder();

    @ClassName("disable-text-color")
    String disableTextColor();

    @ClassName("search-match")
    String searchMatch();
}
