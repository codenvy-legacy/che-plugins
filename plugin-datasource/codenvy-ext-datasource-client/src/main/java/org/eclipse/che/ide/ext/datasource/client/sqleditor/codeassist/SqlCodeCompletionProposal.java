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
package org.eclipse.che.ide.ext.datasource.client.sqleditor.codeassist;

import org.eclipse.che.ide.api.icon.Icon;
import org.eclipse.che.ide.jseditor.client.codeassist.Completion;
import org.eclipse.che.ide.jseditor.client.codeassist.CompletionProposal;
import org.eclipse.che.ide.jseditor.client.document.EmbeddedDocument;
import org.eclipse.che.ide.jseditor.client.text.LinearRange;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import elemental.dom.Element;

public class SqlCodeCompletionProposal implements CompletionProposal {

    private final String name;

    private String replacementString;
    private int cursorPosition;
    private int selectionLength = 0;
    private InvocationContext invocationContext;


    public SqlCodeCompletionProposal(String name) {
        this(name, name, name.length());
    }

    public SqlCodeCompletionProposal(String name, String replacementString, int cursorPosition) {
        this.name = name;
        this.replacementString = replacementString;
        this.cursorPosition = cursorPosition;

    }

    @Override
    public Element getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public String getDisplayString() {
        return new SafeHtmlBuilder().appendEscaped(name).toSafeHtml().asString();
    }

    @Override
    public Icon getIcon() {
        return new Icon("sql.completion.icon", invocationContext.getResources().sqlCompletionIcon());
    }

    @Override
    public void getCompletion(CompletionCallback completionCallback) {
        completionCallback.onCompletion(new Completion() {
            @Override
            public void apply(final EmbeddedDocument document) {
                final int offset = invocationContext.getOffset() - invocationContext.getQuery().getLastQueryPrefix().length();
                final int length = invocationContext.getQuery().getLastQueryPrefix().length();
                document.replace(offset, length, replacementString);
            }

            @Override
            public LinearRange getSelection(final EmbeddedDocument document) {
                final int start = invocationContext.getOffset() + cursorPosition
                                  - invocationContext.getQuery().getLastQueryPrefix().length();
                return LinearRange.createWithStart(start).andLength(selectionLength);
            }
        });
    }

    public String getName() {
        return name;
    }

    public void setInvocationContext(InvocationContext invocationContext) {
        this.invocationContext = invocationContext;
    }

    public String getReplacementString() {
        return replacementString;
    }

    public void setReplacementString(String replacementString) {
        this.replacementString = replacementString;
    }

    public int getCursorPosition() {
        return cursorPosition;
    }

    public void setCursorPosition(int cursorPosition) {
        this.cursorPosition = cursorPosition;
    }

    public void setSelectionLength(int selectionLength) {
        this.selectionLength = selectionLength;
    }

}
