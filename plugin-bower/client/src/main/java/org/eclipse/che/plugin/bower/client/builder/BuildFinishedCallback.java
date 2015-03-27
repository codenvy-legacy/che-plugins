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
package org.eclipse.che.plugin.bower.client.builder;

import org.eclipse.che.api.builder.BuildStatus;

/**
 * @author Florent Benoit
 */
public interface BuildFinishedCallback {

    void onFinished(BuildStatus buildStatus);
}
