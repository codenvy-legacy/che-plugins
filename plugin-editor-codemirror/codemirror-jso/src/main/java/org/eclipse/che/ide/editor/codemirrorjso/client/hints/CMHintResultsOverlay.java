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
import com.google.gwt.core.client.JsArrayMixed;

public class CMHintResultsOverlay extends JavaScriptObject {

    protected CMHintResultsOverlay() {
    }

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

    public final native JsArrayMixed getList() /*-{
        return this.list;
    }-*/;

    public final native void setList(JsArrayMixed list) /*-{
        this.list = list;
    }-*/;

    public final native boolean completionItemIsString(int i) /*-{
        return (typeof(this.list[i]) === 'string');
    }-*/;

    public final native String getCompletionItemAsString(int i) /*-{
        return this.list[i];
    }-*/;

    public final native CMCompletionObjectOverlay getCompletionItemAsObject(int i) /*-{
        return this.list[i];
    }-*/;

    public final native boolean isString(int i) /*-{
        return typeof(this.list[i]) === "string";
    }-*/;

    public static final native CMHintResultsOverlay create() /*-{
        return {"list": []};
    }-*/;

}
