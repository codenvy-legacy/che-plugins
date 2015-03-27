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

import org.eclipse.che.ide.jseditor.client.document.EmbeddedDocument;
import org.eclipse.che.ide.jseditor.client.text.TextPosition;
import org.eclipse.che.ide.jseditor.client.texteditor.TextEditor;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;


/**
 * Test of the code assist processor.
 *
 * @author Florent Benoit
 */
public class AngularJSHtmlCodeAssistProcessorTest {

    @Test
    public void testAngularAttributes() {

        AngularJSHtmlCodeAssistProcessor codeAssistProcessor = new AngularJSHtmlCodeAssistProcessor(null);

        // test space split
        List<String> attributes = codeAssistProcessor.getAngularAttributes("my attributes", false);
        assertNotNull(attributes);
        assertTrue(Arrays.equals(new String[]{"my", "attributes"}, attributes.toArray()));

        // test keep only attribute name
        attributes = codeAssistProcessor.getAngularAttributes("att1=\"val1\" att2=\"val2\"", false);
        assertNotNull(attributes);
        assertTrue(Arrays.equals(new String[]{"att1", "att2"}, attributes.toArray()));

        // test skip last
        attributes = codeAssistProcessor.getAngularAttributes("body att2", true);
        assertNotNull(attributes);
        assertTrue(Arrays.equals(new String[]{"body"}, attributes.toArray()));


    }


    @Test
    public void testGenerateQueryPrefix() {

        AngularJSHtmlCodeAssistProcessor codeAssistProcessor = new AngularJSHtmlCodeAssistProcessor(null);

        AngularJSQuery query = codeAssistProcessor.getQuery("body", " ");
        assertEquals("body", query.getPrefix());
        assertTrue(Arrays.equals(new String[]{}, query.getExistingAttributes().toArray()));

        query = codeAssistProcessor.getQuery("body ", "att1=\"val1\" att2=\"val2\"");
        assertEquals("", query.getPrefix());
        assertTrue(Arrays.equals(new String[]{"body", "att1", "att2"}, query.getExistingAttributes().toArray()));

        query = codeAssistProcessor.getQuery("body att1", "att2=\"val2\"");
        assertEquals("att1", query.getPrefix());
        assertTrue(Arrays.equals(new String[]{"body", "att2"}, query.getExistingAttributes().toArray()));

    }


    /**
     * PLGAJS-93 Test when we don't have valid XML document (like opening a new element before ending an element)
     */
    @Test
    public void testInvalidXML() {
         AngularJSHtmlCodeAssistProcessor codeAssistProcessor = new AngularJSHtmlCodeAssistProcessor(null);
        TextEditor textEditor = Mockito.mock(TextEditor.class);
        EmbeddedDocument document = Mockito.mock(EmbeddedDocument.class);
        doReturn(document).when(textEditor).getDocument();
        TextPosition cursorPosition = Mockito.mock(TextPosition.class);
        doReturn(cursorPosition).when(document).getCursorPosition();
        document.getCursorPosition();

        doReturn(0).when(cursorPosition).getCharacter();
        doReturn(2).when(cursorPosition).getLine();
        doReturn(8).when(document).getLineCount();

        doReturn("    <script>").when(document).getLineContent(1);
        doReturn("     ").when(document).getLineContent(2);
        doReturn("function activate_section(section_header) {").when(document).getLineContent(3);
        doReturn("     ").when(document).getLineContent(4);
        doReturn("if (section_to_activate.className.indexOf(\"section-active\") >= 0) {").when(document).getLineContent(5);
        doReturn("     ").when(document).getLineContent(6);
        doReturn("     ").when(document).getLineContent(7);
        doReturn("     ").when(document).getLineContent(8);
        AngularJSQuery query = codeAssistProcessor.getQuery(textEditor);

        assertEquals("script>", query.getPrefix());
    }
}
