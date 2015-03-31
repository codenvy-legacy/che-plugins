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
package org.eclipse.che.ide.editor.codemirrorjso.client.dialog;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

public class CMDialogOverlay extends JavaScriptObject {

    protected CMDialogOverlay() {}

    /**
     * Opens a notification, that can be closed with an optional timer (default 5000ms timer) and
     * always closes on click.<br>
     * If a notification is opened while another is opened, it will close the currently opened
     * one and open the new one immediately.
     * 
     * @param template string or html fragment
     * @param options dialog options
     */
    public final native void openNotification(String template, CMDialogOptionsOverlay options) /*-{
            this.openNotification(template, options);
    }-*/;
    
    /**
     * Same as {@link #openNotification(String, CMDialogOptionsOverlay)} with default options
     * (show on top, duration is 5s).
     * @param template the string or html fragment
     */
    public final native void openNotification(String template) /*-{
            this.openNotification(template);
    }-*/;
    
    /**
     * Opens a notification, that can be closed with an optional timer (default 5000ms timer) and
     * always closes on click.<br>
     * If a notification is opened while another is opened, it will close the currently opened
     * one and open the new one immediately.
     * 
     * @param template the (detached) element to insert in the message
     * @param options dialog options
     */
    public final native void openNotification(Element template, CMDialogOptionsOverlay options) /*-{
            this.openNotification(template, options);
    }-*/;

    /**
     * Same as {@link #openNotification(String, CMDialogOptionsOverlay)} with default options
     * (show on top, duration is 5s).
     * @param template the (detached) element to insert in the message
     */
    public final native void openNotification(Element template) /*-{
            this.openNotification(template);
    }-*/;

    /**
     * Opens a confirmation dialog.
     * 
     * @param template string or html fragment
     * @param callback the callback called on confirmation
     * @param options dialog options
     */
    public final native void openConfirm(String template, CMConfirmCallbackOverlay callback,
                                         CMDialogOptionsOverlay options) /*-{
            this.openConfirm(template, callback, options);
    }-*/;

    /**
     * Opens a confirmation dialog.
     * 
     * @param template the (detached) element to insert in the message
     * @param callback the callback called on confirmation
     * @param options dialog options
     */
    public final native void openConfirm(Element template, CMConfirmCallbackOverlay callback,
                                         CMDialogOptionsOverlay options) /*-{
            this.openConfirm(template, callback, options);
    }-*/;

    /**
     * Opens a confirmation dialog.
     * 
     * @param template string or html fragment
     * @param callback the callback called on confirmation
     */
    public final native void openConfirm(String template, CMConfirmCallbackOverlay callback) /*-{
            this.openConfirm(template, callback);
    }-*/;

    /**
     * Opens a confirmation dialog.
     * 
     * @param template the (detached) element to insert in the message
     * @param callback the callback called on confirmation
     */
    public final native void openConfirm(Element template, CMConfirmCallbackOverlay callback) /*-{
            this.openConfirm(template, callback);
    }-*/;

    /**
     * Opens an input dialog.
     * 
     * @param template string or html fragment
     * @param callback the callback called when the input is done
     * @param options dialog options
     */
    public final native void openDialog(String template, CMDialogCallbackOverlay callback,
                                         CMDialogOptionsOverlay options) /*-{
            this.openDialog(template, callback, options);
    }-*/;

    /**
     * Opens an input dialog.
     * 
     * @param template the (detached) element to insert in the message
     * @param callback the callback called when the input is done
     * @param options dialog options
     */
    public final native void openConfirm(Element template, CMDialogCallbackOverlay callback,
                                         CMDialogOptionsOverlay options) /*-{
            this.openDialog(template, callback, options);
    }-*/;

    /**
     * Opens an input dialog.
     * 
     * @param template string or html fragment
     * @param callback the callback called when the input is done
     */
    public final native void openConfirm(String template, CMDialogCallbackOverlay callback) /*-{
            this.openDialog(template, callback);
    }-*/;

    /**
     * Opens an input dialog.
     * 
     * @param template the (detached) element to insert in the message
     * @param callback the callback called when the input is done
     */
    public final native void openConfirm(Element template, CMDialogCallbackOverlay callback) /*-{
            this.openDialog(template, callback);
    }-*/;
}
