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
package org.eclipse.che.ide.editor.codemirrorjso.client.event;

import org.eclipse.che.ide.editor.codemirrorjso.client.CMSelectionOverlay;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class BeforeSelectionEventParamOverlay extends JavaScriptObject {

    protected BeforeSelectionEventParamOverlay() {

    }

    public final native JsArray<CMSelectionOverlay> getRanges() /*-{
        return this.ranges;
    }-*/;

    public final native void update(JsArray<CMSelectionOverlay> newRanges) /*-{
        this.update(newRanges);
    }-*/;
}
