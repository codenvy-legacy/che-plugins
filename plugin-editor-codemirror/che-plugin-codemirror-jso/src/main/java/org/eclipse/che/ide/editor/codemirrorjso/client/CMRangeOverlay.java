/*******************************************************************************
 * Copyright (c) 2014-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.editor.codemirrorjso.client;

import com.google.gwt.core.client.JavaScriptObject;

public class CMRangeOverlay extends JavaScriptObject {
    protected CMRangeOverlay() {
    }

    public final native CMPositionOverlay getFrom()/*-{
        return this.from;
    }-*/;

    public final native CMPositionOverlay getTo()/*-{
        return this.to;
    }-*/;

    public static final native CMRangeOverlay create(CMPositionOverlay from, CMPositionOverlay to) /*-{
        var result = {};
        result.to= to;
        result.from = from;
        return result;
    }-*/;
}
