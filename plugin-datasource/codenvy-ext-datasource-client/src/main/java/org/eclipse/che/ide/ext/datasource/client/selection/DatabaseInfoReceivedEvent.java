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

public class DatabaseInfoReceivedEvent extends GwtEvent<DatabaseInfoReceivedHandler> {

    /** Handler type. */
    private static Type<DatabaseInfoReceivedHandler> TYPE;

    /** The received item. */
    private final String                             databaseId;

    public DatabaseInfoReceivedEvent(final String databaseId) {
        super();
        this.databaseId = databaseId;
    }

    @Override
    public GwtEvent.Type<DatabaseInfoReceivedHandler> getAssociatedType() {
        if (TYPE == null) {
            TYPE = new GwtEvent.Type<DatabaseInfoReceivedHandler>();
        }
        return TYPE;
    }

    @Override
    protected void dispatch(DatabaseInfoReceivedHandler handler) {
        handler.onDatabaseInfoReceived(this);
    }

    /**
     * Returns the id of the datasource the metadata was received for.
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
    public static Type<DatabaseInfoReceivedHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<DatabaseInfoReceivedHandler>();
        }
        return TYPE;
    }
}
