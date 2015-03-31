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
package org.eclipse.che.ide.ext.datasource.shared;

public enum MultipleRequestExecutionMode {

    /** All SQL requests are executed, continue after error. */
    ONE_BY_ONE,
    /** All SQL requests are executed, but stop at first error. */
    STOP_AT_FIRST_ERROR,
    /** All requests are executed or none. If one fails, stop and rollback. */
    TRANSACTIONAL
}
