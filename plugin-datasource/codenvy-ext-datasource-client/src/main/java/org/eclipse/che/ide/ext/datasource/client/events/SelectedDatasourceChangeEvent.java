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
 * Event thrown when the explorer selected datasource changes.
 * 
 * @author "Mickaël Leduque"
 */
public class SelectedDatasourceChangeEvent extends GwtEvent<SelectedDatasourceChangeHandler> {

    /** Handler type. */
    private static Type<SelectedDatasourceChangeHandler> TYPE;

    /** The id of the selected datasource. */
    private final String                                 selectedDatasourceId;

    /**
     * Creates an event.
     * 
     * @param selectionId the id of the selected datasource
     */
    public SelectedDatasourceChangeEvent(final String selectionId) {
        this.selectedDatasourceId = selectionId;
    }

    @Override
    public GwtEvent.Type<SelectedDatasourceChangeHandler> getAssociatedType() {
        if (TYPE == null) {
            TYPE = new GwtEvent.Type<SelectedDatasourceChangeHandler>();
        }
        return TYPE;
    }

    @Override
    protected void dispatch(final SelectedDatasourceChangeHandler handler) {
        handler.onSelectedDatasourceChange(this);
    }

    /**
     * Returns the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<SelectedDatasourceChangeHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<SelectedDatasourceChangeHandler>();
        }
        return TYPE;
    }

    /**
     * Returns the id of the selected datasource.
     * 
     * @return the datasource id
     */
    public String getSelectedDatasourceId() {
        return selectedDatasourceId;
    }
}
