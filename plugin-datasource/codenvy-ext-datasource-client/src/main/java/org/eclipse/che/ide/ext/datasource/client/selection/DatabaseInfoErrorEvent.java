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
package org.eclipse.che.ide.ext.datasource.client.selection;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event triggered when database info retrieval fails.
 * 
 * @author "Mickaël Leduque"
 */
public class DatabaseInfoErrorEvent extends GwtEvent<DatabaseInfoErrorHandler> {

    /** Handler type. */
    private static Type<DatabaseInfoErrorHandler> TYPE;

    /** The id of the datasource for which retrieval failed. */
    private final String                          databaseId;

    public DatabaseInfoErrorEvent(final String databaseId) {
        super();
        this.databaseId = databaseId;
    }

    @Override
    public GwtEvent.Type<DatabaseInfoErrorHandler> getAssociatedType() {
        if (TYPE == null) {
            TYPE = new GwtEvent.Type<DatabaseInfoErrorHandler>();
        }
        return TYPE;
    }

    @Override
    protected void dispatch(DatabaseInfoErrorHandler handler) {
        handler.onDatabaseInfoError(this);
    }

    /**
     * Returns the id of the datasource for which metadata retrieval failed.
     * 
     * @return the received datasource id
     */
    public String getDatabaseId() {
        return databaseId;
    }

    /**
     * Returns the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<DatabaseInfoErrorHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<DatabaseInfoErrorHandler>();
        }
        return TYPE;
    }
}
