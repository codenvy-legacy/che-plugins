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
package org.eclipse.che.ide.orion.compare.jso;

import com.google.gwt.core.client.JavaScriptObject;

import org.eclipse.che.ide.orion.compare.Compare;
import org.eclipse.che.ide.orion.compare.CompareConfig;

/**
 * @author Evgen Vidolob
 */
public class CompareJs extends JavaScriptObject implements Compare {
    protected CompareJs() {
    }



    @Override
    public final native void refresh() /*-{
        this.refresh();
    }-*/;

    public static native Compare createCompare(JavaScriptObject compareMod, CompareConfig config)/*-{
        return new compareMod(config);
    }-*/;


    public static native Compare createCompare(JavaScriptObject compareMod, CompareConfig config, String commandSpanId)/*-{
        return new compareMod(config, commandSpanId);
    }-*/;


    public static native Compare createCompare(JavaScriptObject compareMod, CompareConfig config, String commandSpanId, String viewType)/*-{
        return new compareMod(config, commandSpanId, viewType);
    }-*/;

    public static native Compare createCompare(JavaScriptObject compareMod, CompareConfig config, String commandSpanId, String viewType, boolean toggleable)/*-{
        return new compareMod(config, commandSpanId, viewType, toggleable);
    }-*/;

    public static native Compare createCompare(JavaScriptObject compareMod, CompareConfig config, String commandSpanId, String viewType, boolean toggleable, String toggleCommandSpanId)/*-{
        return new compareMod(config, commandSpanId, viewType, toggleable, toggleCommandSpanId);
    }-*/;


}
