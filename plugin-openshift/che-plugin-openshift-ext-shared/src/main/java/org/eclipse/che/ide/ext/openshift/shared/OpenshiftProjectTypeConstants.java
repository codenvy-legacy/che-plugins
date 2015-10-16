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
package org.eclipse.che.ide.ext.openshift.shared;

/**
 * Shared constants for the openshift project type.
 *
 * @author Sergii Leschenko
 */
public final class OpenshiftProjectTypeConstants {
    public static final String OPENSHIFT_PROJECT_TYPE_ID = "openshift";

    public static final String OPENSHIFT_PROJECT_TYPE_DISPLAY_NAME = "openshift";

    /** Openshift namespace variable used to know in which namespace associated (with current project) openshift objects are stored */
    public static final String OPENSHIFT_NAMESPACE_VARIABLE_NAME = "openshift.namespace";

    /** Openshift application variable used to know which value of label named 'application' is used by openshift objects */
    public static final String OPENSHIFT_APPLICATION_VARIABLE_NAME = "openshift.application";

    private OpenshiftProjectTypeConstants() {}
}
