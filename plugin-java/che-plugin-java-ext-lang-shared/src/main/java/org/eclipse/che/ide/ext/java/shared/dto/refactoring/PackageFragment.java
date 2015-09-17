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

package org.eclipse.che.ide.ext.java.shared.dto.refactoring;

import org.eclipse.che.dto.shared.DTO;

/**
 * DTO represents java package fragment.
 * @author Evgen Vidolob
 */
@DTO
public interface PackageFragment extends JavaProjectElement {

    /**
     * name of this package ("foo.bar.goo")
     * @return name
     */
    String getName();

    /**
     * Set package name
     * @param name of the package
     */
    void setName(String name);
}
