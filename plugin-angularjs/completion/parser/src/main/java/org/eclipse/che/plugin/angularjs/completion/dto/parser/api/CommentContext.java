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
package org.eclipse.che.plugin.angularjs.completion.dto.parser.api;

import java.util.List;

/**
 * @author Florent Benoit
 */
public interface CommentContext {

    List<String> getAttributeValues(String attributeName);

    String getAttributeValue(String attributeName);

    AngularDocType getType();
}
