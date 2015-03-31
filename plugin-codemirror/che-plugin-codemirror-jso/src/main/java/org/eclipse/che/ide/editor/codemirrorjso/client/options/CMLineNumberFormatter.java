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
package org.eclipse.che.ide.editor.codemirrorjso.client.options;

import com.google.gwt.core.client.JavaScriptObject;

public class CMLineNumberFormatter extends JavaScriptObject {

    protected CMLineNumberFormatter() {
    }

    public final native String apply(int lineNumber) /*-{
        return this(lineNumber);
    }-*/;

    public static final native CMLineNumberFormatter create(LineNumberFormatter formatter) /*-{
        return function(lineNumber) {
            return formatter.@org.eclipse.che.ide.editor.codemirrorjso.client.options.CMLineNumberFormatter.LineNumberFormatter::formatLineNumer(I)(lineNumber);
        }
    }-*/;

    /**
     * Interface to define codemirror lineformatters from GWT.
     * 
     * @author "Mickaël Leduque"
     */
    public interface LineNumberFormatter {
        String formatLineNumer(int lineNumer);
    }
}
