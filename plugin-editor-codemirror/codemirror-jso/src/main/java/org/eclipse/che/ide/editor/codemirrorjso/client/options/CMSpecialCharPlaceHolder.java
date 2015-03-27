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
import com.google.gwt.dom.client.Element;

public class CMSpecialCharPlaceHolder extends JavaScriptObject {

    protected CMSpecialCharPlaceHolder() {
    }

    public final native Element apply(int lineNumber) /*-{
        return this(lineNumber);
    }-*/;

    public static final native CMSpecialCharPlaceHolder create(SpecialCharPlaceHolder placeHolder) /*-{
        return function(specialChar) {
            return placeHolder.@org.eclipse.che.ide.editor.codemirrorjso.client.options.CMSpecialCharPlaceHolder.SpecialCharPlaceHolder::createPlaceHolder(C)(specialChar);
        }
    }-*/;

    /**
     * Interface to define codemirror specialCharPlaceholder functions from GWT.
     * 
     * @author "Mickaël Leduque"
     */
    public interface SpecialCharPlaceHolder {
        Element createPlaceHolder(char specialChar);
    }
}
