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

import org.eclipse.che.ide.editor.codemirrorjso.client.CMEditorOverlay;
import org.eclipse.che.ide.editor.codemirrorjso.client.CMPixelCoordinatesOverlay;
import org.eclipse.che.ide.editor.codemirrorjso.client.CMPositionOverlay;
import org.eclipse.che.ide.editor.codemirrorjso.client.scroll.CMPixelRangeOverlay;
import org.eclipse.che.ide.jseditor.client.position.PositionConverter;
import org.eclipse.che.ide.jseditor.client.text.TextPosition;

public class CodemirrorPositionConverter implements PositionConverter {

    private final CMEditorOverlay editorOverlay;

    public CodemirrorPositionConverter(final CMEditorOverlay editorOverlay) {
        this.editorOverlay = editorOverlay;
    }

    public PixelCoordinates textToPixel(final TextPosition textPosition) {
        final CMPositionOverlay position = CMPositionOverlay.create(textPosition.getLine(),
                                                                    textPosition.getCharacter());
        return textToPixel(position);
    }

    private PixelCoordinates textToPixel(final CMPositionOverlay position) {
        final CMPixelRangeOverlay pixelPosition = this.editorOverlay.charCoords(position, "window");
        return new PixelCoordinates(pixelPosition.getLeft(),
                                    pixelPosition.getTop());
    }

    public PixelCoordinates offsetToPixel(final int textOffset) {
        final CMPositionOverlay position = this.editorOverlay.getDoc().posFromIndex(textOffset);
        return textToPixel(position);
    }

    public TextPosition pixelToText(final PixelCoordinates coordinates) {
        final CMPositionOverlay cmTextPos = pixelToCmText(coordinates);
        return new TextPosition(cmTextPos.getLine(), cmTextPos.getCharacter());
    }

    private CMPositionOverlay pixelToCmText(final PixelCoordinates coordinates) {
        final CMPixelCoordinatesOverlay cmPixel = CMPixelCoordinatesOverlay.create(coordinates.getX(),
                                                                                   coordinates.getY());
        return  this.editorOverlay.coordsChar(cmPixel, "window");
    }

    @Override
    public int pixelToOffset(final PixelCoordinates coordinates) {
        final CMPositionOverlay cmTextPos = pixelToCmText(coordinates);
        return this.editorOverlay.getDoc().indexFromPos(cmTextPos);
    }

}
