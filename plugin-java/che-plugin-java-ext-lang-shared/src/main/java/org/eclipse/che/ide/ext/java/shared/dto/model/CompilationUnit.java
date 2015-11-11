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

package org.eclipse.che.ide.ext.java.shared.dto.model;

import org.eclipse.che.dto.shared.DTO;

import java.util.List;

/**
 * Represents an entire Java compilation unit (source file with one of the Java-like extensions).
 *
 * @author Evgen Vidolob
 */
@DTO
public interface CompilationUnit extends TypeRoot, LabelElement {
    /**
     * Returns the top-level types declared in this compilation unit
     * in the order in which they appear in the source.
     *
     * @return the top-level types declared in this compilation unit
     */
    List<Type> getTypes();

    void setTypes(List<Type> types);

    /**
     * Returns the import declarations in this compilation unit
     * in the order in which they appear in the source. This is
     * a convenience method - import declarations can also be
     * accessed from a compilation unit's import container.
     *
     * @return the import declarations in this compilation unit
     */
    List<ImportDeclaration> getImports();

    void setImports(List<ImportDeclaration> imports);
}
