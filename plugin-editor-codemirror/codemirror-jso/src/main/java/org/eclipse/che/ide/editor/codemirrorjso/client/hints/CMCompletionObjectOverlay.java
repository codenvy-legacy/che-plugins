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
package org.eclipse.che.ide.editor.codemirrorjso.client.hints;

import org.eclipse.che.ide.editor.codemirrorjso.client.CMPositionOverlay;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Completion item.
 */
public class CMCompletionObjectOverlay extends JavaScriptObject {

    protected CMCompletionObjectOverlay() {
    }

    public final native String getText() /*-{
        return this.text;
    }-*/;

    public final native void setText(String text) /*-{
        this.text = text;
    }-*/;

    public final native String getDisplayText() /*-{
        return this.displayText;
    }-*/;

    public final native void setDisplayText(String displayText) /*-{
        this.displayText = displayText;
    }-*/;

    public final native String getClassName() /*-{
        return this.className;
    }-*/;

    public final native void setClassName(String className) /*-{
        this.className = className;
    }-*/;

    public final native CMRenderFunctionOverlay getRender() /*-{
        return this.render;
    }-*/;

    public final native void setRender(CMRenderFunctionOverlay render) /*-{
        this.render = render;
    }-*/;

    public final native CMHintApplyOverlay getHint() /*-{
        return this.hint;
    }-*/;

    public final native void setHint(CMHintApplyOverlay hint) /*-{
        this.hint = hint;
    }-*/;

    public final native CMPositionOverlay getFrom() /*-{
        return this.from;
    }-*/;

    public final native void setFrom(CMPositionOverlay from) /*-{
        this.from = from;
    }-*/;

    public final native CMPositionOverlay getTo() /*-{
        return this.to;
    }-*/;

    public final native void setTo(CMPositionOverlay to) /*-{
        this.to = to;
    }-*/;
}
