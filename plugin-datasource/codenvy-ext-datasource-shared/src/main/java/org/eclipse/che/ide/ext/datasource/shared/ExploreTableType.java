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

/**
 * Explore mode, to tell which table types are returned.
 * 
 * @author "Mickaël Leduque"
 */
public enum ExploreTableType {

    /** Explore simplest mode. Only tables and views. */
    SIMPLE(0),
    /** Default explore mode. Adds materialized views, alias and synonyms toSIMPLE. */
    STANDARD(1),
    /** Explore mode with STANDARD tables plus system tables and views. */
    SYSTEM(2),
    /** Explore mode for all table/entities types. */
    ALL(3);

    private final int index;

    private ExploreTableType(final int index) {
        this.index = index;
    }

    /**
     * Returns the index of the enum.
     * 
     * @return the index
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Retrieve the enum value that has this index.
     * 
     * @param searchedIndex the index
     * @return the enum value or null if there is none
     */
    public static ExploreTableType fromIndex(final int searchedIndex) {
        for (final ExploreTableType type : values()) {
            if (type.getIndex() == searchedIndex) {
                return type;
            }
        }
        return null;
    }
}
