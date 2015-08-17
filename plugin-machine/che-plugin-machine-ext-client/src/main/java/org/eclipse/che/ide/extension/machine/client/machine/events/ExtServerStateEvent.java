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
package org.eclipse.che.ide.extension.machine.client.machine.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event that describes the fact that extension server state has been changed.
 *
 * @author Roman Nikitenko
 */
public class ExtServerStateEvent extends GwtEvent<ExtServerStateHandler> {

    /** Type class used to register this event. */
    public static Type<ExtServerStateHandler> TYPE = new Type<>();
    private final ExtServerAction extServerAction;

    /**
     * Create new {@link ExtServerStateEvent}.
     *
     * @param extServerAction
     *         the type of action
     */
    protected ExtServerStateEvent(ExtServerAction extServerAction) {
        this.extServerAction = extServerAction;
    }

    /**
     * Creates a extension server Action Started event.
     */
    public static ExtServerStateEvent createExtServerStartedEvent() {
        return new ExtServerStateEvent(ExtServerAction.STARTED);
    }

    /**
     * Creates a extension server Action Stopped event.
     */
    public static ExtServerStateEvent createExtServerStoppedEvent() {
        return new ExtServerStateEvent(ExtServerAction.STOPPED);
    }

    @Override
    public Type<ExtServerStateHandler> getAssociatedType() {
        return TYPE;
    }

    /** @return the type of action */
    public ExtServerAction getExtServerAction() {
        return extServerAction;
    }

    @Override
    protected void dispatch(ExtServerStateHandler handler) {
        switch (extServerAction) {
            case STARTED:
                handler.onExtServerStarted(this);
                break;
            case STOPPED:
                handler.onExtServerStopped(this);
                break;
            default:
                break;
        }
    }

    /** Set of possible type of machine actions. */
    public enum ExtServerAction {
        STARTED, STOPPED
    }
}
