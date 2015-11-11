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
package org.eclipse.che.ide.ext.openshift.client.dto;

import org.eclipse.che.dto.shared.DTO;
import org.eclipse.che.ide.ext.openshift.shared.dto.Build;

/**
 * @author Sergii Leschenko
 */
@DTO
public interface BuildChangeEvent {
    enum EventType {
        ADDED,
        MODIFIED;
    }

    EventType getType();

    void setType(EventType type);

    BuildChangeEvent withType(EventType type);

    Build getObject();

    void setObject(Build object);

    BuildChangeEvent withObject(Build object);
}
