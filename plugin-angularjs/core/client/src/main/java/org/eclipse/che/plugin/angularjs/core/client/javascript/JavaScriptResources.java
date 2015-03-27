/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.angularjs.core.client.javascript;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

/**
 * Defines image used for tab completion for AngularJS.
 * 
 * @author Florent Benoit
 */
public interface JavaScriptResources extends ClientBundle {

    @Source("org/eclipse/che/plugin/angularjs/core/client/completion-item-js.png")
    ImageResource property();

    @Source("org/eclipse/che/plugin/angularjs/core/client/completion-item-angularjs.png")
    ImageResource propertyAngular();


    @Source("org/eclipse/che/plugin/angularjs/core/client/esprima/esprima.js")
    TextResource esprima();

    @Source("org/eclipse/che/plugin/angularjs/core/client/esprima/esprimaJsContentAssist.js")
    TextResource esprimaJsContentAssist();

    @Source("org/eclipse/che/plugin/angularjs/core/client/templating/angular-completion.json")
    TextResource completionTemplatingJson();


}
