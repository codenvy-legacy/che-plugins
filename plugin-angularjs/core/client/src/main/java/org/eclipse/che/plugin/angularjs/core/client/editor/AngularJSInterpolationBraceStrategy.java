/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.angularjs.core.client.editor;

import org.eclipse.che.ide.jseditor.client.changeintercept.TextChange;
import org.eclipse.che.ide.jseditor.client.changeintercept.TextChangeInterceptor;
import org.eclipse.che.ide.jseditor.client.document.ReadOnlyDocument;
import org.eclipse.che.ide.jseditor.client.text.TextPosition;

/**
 * This strategy will add }} if user is entering {{
 * 
 * @author Florent Benoit
 */
public class AngularJSInterpolationBraceStrategy implements TextChangeInterceptor {

    @Override
    public TextChange processChange(TextChange change, ReadOnlyDocument document) {

        // early pruning to slow down normal typing as little as possible
        if (!change.getTo().equals(change.getFrom()) || change.getNewText().isEmpty()) {
            return null;
        }

        // Current character
        final char character = change.getNewText().charAt(0);
        if ('{' != character) {
            return null;
        }

        // current cursor position
        final int offset = document.getCursorOffset();

        // not enough characters
        if (offset <= 1) {
            return null;
        }

        // character before the {
        final String before = document.getContentRange(offset - 1, 1);

        // well we have two {{ then we can close these brackets (and don't forget to add the current character which is {
        if (" ".equals(before)) {
            return new TextChange.Builder().from(change.getFrom())
                                           .to(new TextPosition(change.getTo().getLine(),
                                                                change.getTo().getCharacter() + 2))
                                           .insert("{{}}").build();
        } else {
            return null;
        }
    }
}
