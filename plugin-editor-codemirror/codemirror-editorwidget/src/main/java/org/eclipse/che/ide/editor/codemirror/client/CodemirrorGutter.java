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

import java.util.logging.Logger;

import org.eclipse.che.ide.editor.codemirrorjso.client.CMEditorOverlay;
import org.eclipse.che.ide.editor.codemirrorjso.client.CMPositionOverlay;
import org.eclipse.che.ide.editor.codemirrorjso.client.CodeMirrorOverlay;
import org.eclipse.che.ide.editor.codemirrorjso.client.EventTypes;
import org.eclipse.che.ide.editor.codemirrorjso.client.EventHandlers.EventHandlerMixedParameters;
import org.eclipse.che.ide.editor.codemirrorjso.client.event.CMChangeEventOverlay;
import org.eclipse.che.ide.editor.codemirrorjso.client.line.CMGutterMarkersOverlay;
import org.eclipse.che.ide.editor.codemirrorjso.client.line.CMLineInfoOverlay;
import org.eclipse.che.ide.jseditor.client.gutter.Gutter;
import org.eclipse.che.ide.jseditor.client.gutter.Gutters;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;

import elemental.dom.Element;

/**
 * Gutter for codemirror editors.
 */
public class CodemirrorGutter implements Gutter {

    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(CodemirrorGutter.class.getSimpleName());

    /**
     * The style for the line number gutter in codemirror.
     */
    public static final String CODE_MIRROR_GUTTER_LINENUMBERS = "CodeMirror-linenumbers";

    /**
     * The style for the fold gutter in codemirror.
     */
    public static final String CODE_MIRROR_GUTTER_FOLDGUTTER = "CodeMirror-foldgutter";

    /**
     * The style for the breakpoint gutter in codemirror.
     */
    public static final String CODE_MIRROR_GUTTER_BREAKPOINTS = "CodeMirror-breakpoints";

    /**
     * The style for the breakpoint gutter in codemirror.
     */
    public static final String CODE_MIRROR_GUTTER_ANNOTATIONS = "CodeMirror-annotations";

    /**
     * The gutter map.
     */
    public static final GutterMap GUTTER_MAP = new GutterMap() {
        @Override
        public String logicalToCm(final String gutterId) {
            if (gutterId == null) {
                return null;
            }
            switch (gutterId) {
                case Gutters.LINE_NUMBERS_GUTTER:
                    return CODE_MIRROR_GUTTER_LINENUMBERS;
                case Gutters.BREAKPOINTS_GUTTER:
                    return CODE_MIRROR_GUTTER_BREAKPOINTS;
                case Gutters.ANNOTATION_GUTTER:
                    return CODE_MIRROR_GUTTER_ANNOTATIONS;
                default:
                    return gutterId;
            }
        }

        @Override
        public String cmToLogical(final String gutterStyle) {
            if (gutterStyle == null) {
                return null;
            }
            switch (gutterStyle) {
                case CODE_MIRROR_GUTTER_LINENUMBERS:
                    return Gutters.LINE_NUMBERS_GUTTER;
                case CODE_MIRROR_GUTTER_BREAKPOINTS:
                    return Gutters.BREAKPOINTS_GUTTER;
                case CODE_MIRROR_GUTTER_ANNOTATIONS:
                    return Gutters.ANNOTATION_GUTTER;
                default:
                    return gutterStyle;
            }
        }

        @Override
        public boolean isProtectedLogical(final String gutterId) {
            if (gutterId == null) {
                return true;
            }
            switch (gutterId) {
                case Gutters.LINE_NUMBERS_GUTTER:
                    return true;
                default:
                    return false;
            }
        }
    };

    /**
     * The editor instance
     */
    private final CMEditorOverlay editorOverlay;

    /**
     * The codemirror object.
     */
    private final CodeMirrorOverlay codeMirror;

    public CodemirrorGutter(final CodeMirrorOverlay codemirror, final CMEditorOverlay editorOverlay) {
        this.editorOverlay = editorOverlay;
        this.codeMirror = codemirror;
    }

    @Override
    public void addGutterItem(final int line, final String gutterId, final Element element,
                              final LineNumberingChangeCallback lineCallback) {
        // condition reversed from the other methods here
        if (GUTTER_MAP.isProtectedLogical(gutterId)) {
            return;
        }
        this.editorOverlay.setGutterMarker(line, GUTTER_MAP.logicalToCm(gutterId), element);
        this.codeMirror.on(editorOverlay, EventTypes.CHANGE,
                           new EventHandlerMixedParameters() {
                               @Override
                               public void onEvent(final JsArrayMixed params) {
                                   // 0->editor, 1->event object
                                   final CMChangeEventOverlay event = params.getObject(1);
                                   final JsArrayString newText = event.getText();
                                   final CMPositionOverlay from = event.getFrom();
                                   final CMPositionOverlay to = event.getTo();

                                   // if the first character of the line is not included, the (potential) line
                                   // numbering change only starts at the following line.
                                   int changeStart = from.getLine() + 1;

                                   int removedCount = 0;
                                   if (from.getLine() != to.getLine()) {
                                       // no lines were removed
                                       // don't count first line yet
                                       removedCount = Math.abs(from.getLine() - to.getLine()) - 1;
                                       if (from.getCharacter() == 0) {
                                           // start of first line is included, 'to' is on another line, so the line is deleted
                                           removedCount = removedCount + 1;
                                           changeStart = changeStart - 1;
                                       }
                                       // if 'to' is at the end of the line, the line is _not_ removed, just emptied
                                   }
                                   // else no lines were removed

                                   final int addedCount = newText.length() - 1;

                                   // only call back if there is a change in the lines
                                   if (removedCount > 0 || addedCount > 0) {
                                       LOG.fine("Line change from l." + changeStart + " removed " + removedCount + " added " + addedCount);
                                       lineCallback.onLineNumberingChange(changeStart,
                                                                          removedCount,
                                                                          addedCount);
                                   }
                               }

                           });
    }


    @Override
    public Element getGutterItem(final int line, final String gutterId) {
        final CMLineInfoOverlay lineInfo = this.editorOverlay.lineInfo(line);
        if (lineInfo == null) {
            LOG.fine("No lineInfo for line" + line);
            return null;
        }
        if (lineInfo.getGutterMarkers() == null) {
            LOG.fine("No gutter markers for line" + line);
            return null;
        }
        final CMGutterMarkersOverlay markers = lineInfo.getGutterMarkers();
        if (markers.hasMarker(GUTTER_MAP.logicalToCm(gutterId))) {
            return markers.getMarker(GUTTER_MAP.logicalToCm(gutterId));
        } else {
            LOG.fine("No markers found for gutter " + gutterId + "/" + GUTTER_MAP.logicalToCm(gutterId) + "on line " + line);
            return null;
        }
    }

    @Override
    public void clearGutter(final String gutterId) {
        if (!GUTTER_MAP.isProtectedLogical(gutterId)) {
            this.editorOverlay.clearGutter(GUTTER_MAP.logicalToCm(gutterId));
        }
    }

    public void addGutterItem(final int line, final String gutterId, final com.google.gwt.dom.client.Element element) {
        if (!GUTTER_MAP.isProtectedLogical(gutterId)) {
            this.editorOverlay.setGutterMarker(line, GUTTER_MAP.logicalToCm(gutterId), element);
        }
    }

    public void removeGutterItem(final int line, final String gutterId) {
        if (!GUTTER_MAP.isProtectedLogical(gutterId)) {
            this.editorOverlay.setGutterMarker(line, GUTTER_MAP.logicalToCm(gutterId), (Element)null);
        }
    }

    public void addGutterItem(final int line, final String gutterId, final elemental.dom.Element element) {
        if (!GUTTER_MAP.isProtectedLogical(gutterId)) {
            this.editorOverlay.setGutterMarker(line, GUTTER_MAP.logicalToCm(gutterId), element);
        }
    }

    /**
     * Interface to map generic gutter identifiers to codemirror gutter styles.
     */
    interface GutterMap {

        /**
         * Return the native sutter style for this gutter id.
         * 
         * @param gutterId the gutter id
         * @return the codemirror name
         */
        String logicalToCm(String gutterId);

        /**
         * Convert the logical gutter id for the native gutter style.
         * 
         * @param gutterStyle the gutter style
         * @return the logical gutter id
         */
        String cmToLogical(String gutterStyle);

        /**
         * Tells if the given gutter is protected against change.
         * 
         * @param gutterId the gutter identifier
         * @return true iff the gutter is protected
         */
        boolean isProtectedLogical(String gutterId);
    }
}
