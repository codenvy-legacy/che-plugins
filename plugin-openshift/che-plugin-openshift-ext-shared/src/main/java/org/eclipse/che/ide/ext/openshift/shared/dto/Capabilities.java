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
package org.eclipse.che.ide.ext.openshift.shared.dto;

import org.eclipse.che.dto.shared.DTO;

import java.util.List;

@DTO
public interface Capabilities {
    List<Capability> getAdd();

    void setAdd(List<Capability> add);

    Capabilities withAdd(List<Capability> add);

    List<Capability> getDrop();

    void setDrop(List<Capability> drop);

    Capabilities withDrop(List<Capability> drop);

}
