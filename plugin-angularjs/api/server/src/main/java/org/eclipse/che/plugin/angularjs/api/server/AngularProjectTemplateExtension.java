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
package org.eclipse.che.plugin.angularjs.api.server;

import org.eclipse.che.api.project.server.ProjectTemplateDescription;

import java.util.List;

/**
 * This interfaces allows to plug new AngularJS templates.
 * Modules implementing this interface need to use for example Singleton
 * @author Florent Benoit
 */
public interface AngularProjectTemplateExtension {

    /**
     * @return a list of templates that can be used for AngularJS
     */
    List<ProjectTemplateDescription> getTemplates();
}
