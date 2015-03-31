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

import org.eclipse.che.dto.shared.DTO;

/**
 * Interface for all database entity info.
 * 
 * @author "Mickaël Leduque"
 */
@DTO
public interface DatabaseMetadataEntityDTO {

    /**
     * Returns the (human readable) name of the object.
     * 
     * @return the name of the object.
     */
    String getName();

    /**
     * Affects the (human readable) name of the object.
     * 
     * @param name the new value
     */
    void setName(String name);


    /**
     * Chainable version of the name property setter.
     * 
     * @param name the value for name
     * @return this object
     */
    DatabaseMetadataEntityDTO withName(String name);

    /**
     * Returns an identifier for the object relative to the database.
     * 
     * @return the identifier
     */
    String getLookupKey();

    /**
     * Affect the identifier for the object in the database.
     * 
     * @param lookupKey the new value
     */
    void setLookupKey(String lookupKey);


    /**
     * Chainable version of the lookupKey property setter.
     * 
     * @param lookupKey the value for lookupKey
     * @return this object
     */
    DatabaseMetadataEntityDTO withLookupKey(String lookupKey);

    /**
     * Returns the comment for the database entity (if available).
     * 
     * @return the comment
     */
    String getComment();

    /**
     * Affect the comment for the database entity.
     * 
     * @param comment the new value
     */
    void setComment(String comment);

    /**
     * Chainable version of the comment property setter.
     * 
     * @param comment the value for comment
     * @return this object
     */
    DatabaseMetadataEntityDTO withComment(String comment);
}
