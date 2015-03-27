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
package org.eclipse.che.plugin.angularjs.core.client;

import org.eclipse.che.ide.api.icon.Icon;
import org.eclipse.che.ide.api.icon.IconRegistry;
import org.eclipse.che.plugin.angularjs.core.client.editor.AngularJSResources;

/**
 * JavaScript Extension allowing to register new javascript type
 * @author Florent Benoit
 */
public class JsExtension {

    public JsExtension(String projectTypeId,
                       IconRegistry iconRegistry,
                       AngularJSResources resources)        {
        iconRegistry.registerIcon(new Icon(projectTypeId + ".projecttype.big.icon", "angularjs-extension/newproject-angularjs.png"));
        iconRegistry.registerIcon(new Icon(projectTypeId + ".projecttype.small.icon", "angularjs-extension/newproject-angularjs.png"));

        // filename icons
        iconRegistry.registerIcon(new Icon(projectTypeId + "/bower.json.file.small.icon", resources.bowerFile()));
        iconRegistry.registerIcon(new Icon(projectTypeId + "/Gruntfile.js.file.small.icon", resources.gruntFile()));
        iconRegistry.registerIcon(new Icon(projectTypeId + "/gulpfile.js.file.small.icon", resources.gulpFile()));
        iconRegistry.registerIcon(new Icon(projectTypeId + "/package.json.file.small.icon", resources.npmFile()));
        iconRegistry.registerIcon(new Icon(projectTypeId + "/README.file.small.icon", resources.textFile()));
        iconRegistry.registerIcon(new Icon(projectTypeId + "/LICENSE.file.small.icon", resources.textFile()));

        // register also file extension icons
        iconRegistry.registerIcon(new Icon(projectTypeId + "/css.file.small.icon", resources.cssFile()));
        iconRegistry.registerIcon(new Icon(projectTypeId + "/scss.file.small.icon", resources.cssFile()));
        iconRegistry.registerIcon(new Icon(projectTypeId + "/less.file.small.icon", resources.lessFile()));
        iconRegistry.registerIcon(new Icon(projectTypeId + "/html.file.small.icon", resources.htmlFile()));
        iconRegistry.registerIcon(new Icon(projectTypeId + "/js.file.small.icon", resources.jsFile()));
        iconRegistry.registerIcon(new Icon(projectTypeId + "/json.file.small.icon", resources.jsonFile()));
        iconRegistry.registerIcon(new Icon(projectTypeId + "/pom.xml.file.small.icon", resources.mavenFile()));
        iconRegistry.registerIcon(new Icon(projectTypeId + "/xml.file.small.icon", resources.xmlFile()));
        // images
        iconRegistry.registerIcon(new Icon(projectTypeId + "/gif.file.small.icon", resources.imageIcon()));
        iconRegistry.registerIcon(new Icon(projectTypeId + "/jpg.file.small.icon", resources.imageIcon()));
        iconRegistry.registerIcon(new Icon(projectTypeId + "/png.file.small.icon", resources.imageIcon()));

        // text
        iconRegistry.registerIcon(new Icon(projectTypeId + "/log.file.small.icon", resources.textFile()));
        iconRegistry.registerIcon(new Icon(projectTypeId + "/txt.file.small.icon", resources.textFile()));
        iconRegistry.registerIcon(new Icon(projectTypeId + "/md.file.small.icon", resources.textFile()));

    }
}
