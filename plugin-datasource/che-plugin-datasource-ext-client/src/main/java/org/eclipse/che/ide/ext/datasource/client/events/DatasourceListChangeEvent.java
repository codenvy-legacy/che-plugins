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
package org.eclipse.che.ide.ext.datasource.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event thrown when the datasource list changes.
 * 
 * @author "Mickaël Leduque"
 */
public class DatasourceListChangeEvent extends GwtEvent<DatasourceListChangeHandler> {

    /** Handler type. */
    private static Type<DatasourceListChangeHandler> TYPE;

    @Override
    public GwtEvent.Type<DatasourceListChangeHandler> getAssociatedType() {
        if (TYPE == null) {
            TYPE = new GwtEvent.Type<DatasourceListChangeHandler>();
        }
        return TYPE;
    }

    @Override
    protected void dispatch(final DatasourceListChangeHandler handler) {
        handler.onDatasourceListChange(this);
    }

    /**
     * Returns the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<DatasourceListChangeHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<DatasourceListChangeHandler>();
        }
        return TYPE;
    }
}
