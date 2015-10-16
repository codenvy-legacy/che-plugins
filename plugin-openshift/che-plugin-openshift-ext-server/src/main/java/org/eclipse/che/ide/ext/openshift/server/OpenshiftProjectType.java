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
package org.eclipse.che.ide.ext.openshift.server;

import org.eclipse.che.api.project.server.type.ProjectType;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.eclipse.che.ide.ext.openshift.shared.OpenshiftProjectTypeConstants.OPENSHIFT_NAMESPACE_VARIABLE_NAME;
import static org.eclipse.che.ide.ext.openshift.shared.OpenshiftProjectTypeConstants.OPENSHIFT_APPLICATION_VARIABLE_NAME;
import static org.eclipse.che.ide.ext.openshift.shared.OpenshiftProjectTypeConstants.OPENSHIFT_PROJECT_TYPE_DISPLAY_NAME;
import static org.eclipse.che.ide.ext.openshift.shared.OpenshiftProjectTypeConstants.OPENSHIFT_PROJECT_TYPE_ID;


/**
 * The openshift project type definition.
 *
 * @author Sergii Leschenko
 */
@Singleton
public class OpenshiftProjectType extends ProjectType {
    @Inject
    public OpenshiftProjectType() {
        super(OPENSHIFT_PROJECT_TYPE_ID, OPENSHIFT_PROJECT_TYPE_DISPLAY_NAME, false, true);

        addVariableDefinition(OPENSHIFT_NAMESPACE_VARIABLE_NAME, "Openshift namespace", true);
        addVariableDefinition(OPENSHIFT_APPLICATION_VARIABLE_NAME, "Openshift application name", true);
    }
}
