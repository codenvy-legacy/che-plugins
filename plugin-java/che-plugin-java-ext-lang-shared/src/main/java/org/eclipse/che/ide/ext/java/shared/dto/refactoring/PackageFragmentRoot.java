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

import java.util.List;

/**
 * DTO represents package fragment root.
 * @author Evgen Vidolob
 */
@DTO
public interface PackageFragmentRoot extends JavaProjectElement {

    /**
     * All package fragments in this package fragment root.
     * @return list of the package fragments
     */
    List<PackageFragment> getPackageFragments();

    /**
     * Set package fragments
     * @param fragments list of the package fragments
     */
    void setPackageFragments(List<PackageFragment> fragments);

}
