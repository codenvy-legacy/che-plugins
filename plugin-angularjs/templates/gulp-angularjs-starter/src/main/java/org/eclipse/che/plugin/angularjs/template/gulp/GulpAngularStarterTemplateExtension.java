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
package org.eclipse.che.plugin.angularjs.template.gulp;

import org.eclipse.che.api.project.server.ProjectTemplateDescription;
import org.eclipse.che.api.project.shared.Runners;
import org.eclipse.che.plugin.angularjs.api.server.AngularProjectTemplateExtension;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides the Gulp AngularJS starter template for AngularJS project type.
 *
 * @author Florent Benoit
 */
@Singleton
public class GulpAngularStarterTemplateExtension implements AngularProjectTemplateExtension {

    @Override
    public List<ProjectTemplateDescription> getTemplates() {
        Map<String, String> params = new HashMap<>(2);
        params.put("branch", "3.1.0");
        params.put("cleanVcs", "true");
        final List<ProjectTemplateDescription> list = new ArrayList<>(1);
        list.add(new ProjectTemplateDescription("Samples - Hello World",
                                                "git",
                                                "AngularJS - Gulp",
                                                "Project using gulp and AngularJS scaffolding.",
                                                "https://github.com/codenvy-templates/web-angularjs-javascript-gulp-starter",
                                                params,
                                                null,
                                                new Runners("system:/javascript/web/gulp")));
        return list;
    }
}
