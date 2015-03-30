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
package org.eclipse.che.ide.extension.maven.client.event;

import org.eclipse.che.ide.extension.maven.client.projecttree.ModuleNode;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Evgen Vidolob
 */
public class BeforeModuleOpenEvent extends GwtEvent<BeforeModuleOpenHandler> {
    public static Type<BeforeModuleOpenHandler> TYPE = new Type<BeforeModuleOpenHandler>();

    private ModuleNode module;

    public BeforeModuleOpenEvent(ModuleNode module) {
        this.module = module;
    }

    public ModuleNode getModule() {
        return module;
    }

    public Type<BeforeModuleOpenHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(BeforeModuleOpenHandler handler) {
        handler.onBeforeModuleOpen(this);
    }
}
