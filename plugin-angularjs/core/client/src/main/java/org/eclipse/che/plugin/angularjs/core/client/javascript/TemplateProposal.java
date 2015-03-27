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
package org.eclipse.che.plugin.angularjs.core.client.javascript;

import org.eclipse.che.ide.api.icon.Icon;
import org.eclipse.che.ide.jseditor.client.codeassist.Completion;
import org.eclipse.che.ide.jseditor.client.codeassist.CompletionProposal;
import org.eclipse.che.ide.jseditor.client.document.EmbeddedDocument;
import org.eclipse.che.ide.jseditor.client.text.LinearRange;

import elemental.dom.Element;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class TemplateProposal implements CompletionProposal {

    private String prefix;
    private int offset;
    private String replaceName;
    private String displayName;
    private JavaScriptResources javaScriptResources;
    private boolean isMethod = false;

    public TemplateProposal(String prefix, String displayName, String replaceName, int offset, JavaScriptResources javaScriptResources) {
        super();
        this.prefix = prefix;
        this.displayName = displayName;
        this.replaceName = replaceName;
        this.offset = offset;
        this.javaScriptResources = javaScriptResources;
    }

    /*
     * public Point getSelection(IDocument document) { int escapePosition = prop.getEscapePosition(); if (escapePosition == -1) {
     * escapePosition = offset + prop.getProposal().length(); } return new Point(escapePosition, 0); }
     */

    @Override
    public Element getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public String getDisplayString() {
        return displayName;
    }

    @Override
    public Icon getIcon() {
        return new Icon("template.property", javaScriptResources.propertyAngular());
    }

    @Override
    public void getCompletion(CompletionCallback completionCallback) {
        completionCallback.onCompletion(new Completion() {
            /** {@inheritDoc} */
            @Override
            public void apply(EmbeddedDocument document) {
                document.replace(offset - prefix.length(), prefix.length(), replaceName);
            }

            /** {@inheritDoc} */
            @Override
            public LinearRange getSelection(EmbeddedDocument document) {
                if (isMethod) {
                    // search parenthesis
                    int leftPar = replaceName.substring(prefix.length()).indexOf("(");
                    int rightPar = replaceName.substring(replaceName.indexOf("(")).indexOf(")");
                    return LinearRange.createWithStart(offset + leftPar + 1).andLength(rightPar - 1);
                }
                return LinearRange.createWithStart(offset + replaceName.length() - prefix.length()).andLength(0);
            }
        });
    }


    public void setMethod() {
        this.isMethod = true;
    }
}
