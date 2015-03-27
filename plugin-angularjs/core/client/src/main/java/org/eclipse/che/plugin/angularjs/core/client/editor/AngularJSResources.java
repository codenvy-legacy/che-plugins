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
package org.eclipse.che.plugin.angularjs.core.client.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Defines image used for tab completion for AngularJS.
 *
 * @author Florent Benoit
 */
public interface AngularJSResources extends ClientBundle {

    AngularJSResources INSTANCE = GWT.create(AngularJSResources.class);


    @Source("org/eclipse/che/plugin/angularjs/core/client/completion-item-angularjs.png")
    ImageResource property();

    @Source("org/eclipse/che/plugin/angularjs/core/client/svg/bower.svg")
    SVGResource bowerFile();
    
    @Source("org/eclipse/che/plugin/angularjs/core/client/svg/css.svg")
    SVGResource cssFile();
    
    @Source("org/eclipse/che/plugin/angularjs/core/client/svg/grunt.svg")
    SVGResource gruntFile();
    
    @Source("org/eclipse/che/plugin/angularjs/core/client/svg/gulp.svg")
    SVGResource gulpFile();
    
    @Source("org/eclipse/che/plugin/angularjs/core/client/svg/html.svg")
    SVGResource htmlFile();
    
    @Source("org/eclipse/che/plugin/angularjs/core/client/svg/image-icon.svg")
    SVGResource imageIcon();
    
    @Source("org/eclipse/che/plugin/angularjs/core/client/svg/json.svg")
    SVGResource jsonFile();
    
    @Source("org/eclipse/che/plugin/angularjs/core/client/svg/js.svg")
    SVGResource jsFile();
    
    @Source("org/eclipse/che/plugin/angularjs/core/client/svg/less.svg")
    SVGResource lessFile();
    
    @Source("org/eclipse/che/plugin/angularjs/core/client/svg/maven.svg")
    SVGResource mavenFile();
    
    @Source("org/eclipse/che/plugin/angularjs/core/client/svg/npm.svg")
    SVGResource npmFile();
    
    @Source("org/eclipse/che/plugin/angularjs/core/client/svg/text.svg")
    SVGResource textFile();
    
    @Source("org/eclipse/che/plugin/angularjs/core/client/svg/xml.svg")
    SVGResource xmlFile();


}
