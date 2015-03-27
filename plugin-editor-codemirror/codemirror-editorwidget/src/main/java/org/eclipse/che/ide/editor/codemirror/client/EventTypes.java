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
package org.eclipse.che.ide.editor.codemirror.client;

/**
 * Constants for codemirror event types.
 */
public final class EventTypes {

    private EventTypes() {}

    // not complete, added when needed

    /** Event type constant: before the selection changes. */
    public static final String BEFORE_SELECTION_CHANGE = "beforeSelectionChange";

    /** Event type constant: when the contents changes. */
    public static final String CHANGE = "change";

    /** Event type constant: before the contents changes. */
    public static final String BEFORE_CHANGE = "beforeChange";

    /** Event type constant: like change, but batched by operation. */
    public static final String CHANGES = "changes";

    /** Event type constant: when the editor gains focus. */
    public static final String FOCUS = "focus";

    /** Event type constant: when the editor looses focus. */
    public static final String BLUR = "blur";

    /** Event type constant: when scroll happens in the editor. */
    public static final String SCROLL = "scroll";

    /** Event type constant: when the cursor moves in the editor. */
    public static final String CURSOR_ACTIVITY = "cursorActivity";

    /** Event type constant: when the editor viewport (currently visible part) changes. */
    public static final String VIEWPORT_CHANGE = "viewportChange";

    /** Event type constant: when the user clicks on the gutter. */
    public static final String GUTTER_CLICK = "gutterClick";

    /** Event type constant: when the context menu event is triggered on the gutter. */
    public static final String GUTTER_CONTEXT_MENU = "gutterContextMenu";

    /** Event type constant: when the context menu event is triggered on the editor. */
    public static final String CONTEXT_MENU = "contextmenu";

    /** Event type constant: when a drag start event is triggered on the editor. */
    public static final String DRAG_START = "dragstart";

    /** Event type constant: when a drag enter event is triggered on the editor. */
    public static final String DRAG_ENTER = "dragenter";

    /** Event type constant: when a drag over event is triggered on the editor. */
    public static final String DRAG_OVER = "dragover";

    /** Event type constant: when a drop event is triggered on the editor. */
    public static final String DRAG_DROP = "drop";

    /* Line events */

    /** Event type constant: when a change happens on the line. */
    public static final String LINE_CHANGE = "change";

    /** Event type constant: when the line is deleted. */
    public static final String LINE_DELETE = "delete";

    /* Marked ranges events */

    /** Event type constant: when the cursor enters the marked range. */
    public static final String MARK_BEFORE_CURSOR_ENTER = "beforeCursorEnter";

    /** Event type constant: when the range is cleared. */
    public static final String MARK_CLEAR = "clear";

    /* Line widget events */

    /** Event type constant: when the editor re-adds the widget to the DOM. */
    public static final String LINEWIDGET_REDRAW = "redraw";

    /* Completion events. */


    /** Fired when the pop-up is shown. */
    public static final String COMPLETION_SHOWN= "shown";

    /** Fired when a completion is selected. */
    public static final String COMPLETION_SELECT= "select";

    /** Fired when a completion is picked. */
    public static final String COMPLETION_PICK = "pick";

    /** Fired when the completion is finished. */
    public static final String COMPLETION_CLOSE = "close";

}
