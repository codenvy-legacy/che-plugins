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
package org.eclipse.che.ide.editor.codemirror.style.client;

import org.eclipse.che.ide.api.extension.Extension;
import org.eclipse.che.ide.editor.codemirror.resources.client.BasePathConstant;
import org.eclipse.che.ide.util.dom.Elements;
import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;

import elemental.client.Browser;
import elemental.dom.Element;
import elemental.dom.NodeList;
import elemental.html.HeadElement;
import elemental.html.LinkElement;

@Extension(title = "CodeMirror Editor IDE Style", version = "1.1.0")
public class CodeMirrorIDEStyleExtension {

    @Inject
    public CodeMirrorIDEStyleExtension(final CodeMirrorResource highlightResource,
                                       final BasePathConstant basePathConstant) {


        highlightResource.highlightStyle().ensureInjected();
        highlightResource.editorStyle().ensureInjected();
        highlightResource.dockerfileModeStyle().ensureInjected();
        highlightResource.gutterStyle().ensureInjected();
        highlightResource.scrollStyle().ensureInjected();

        injectCodeMirrorIDEStyle(basePathConstant);
    }

    private void injectCodeMirrorIDEStyle(final BasePathConstant basePathConstant) {
        /** GWT.getModuleBaseForStaticFiles() works incorrectly when running Super Dev Mode is used */
        final String codemirrorBase = GWT.getModuleBaseURL() + basePathConstant.basePath();

        injectCssLink(codemirrorBase + "lib/codemirror.css");
        injectCssLink(codemirrorBase + "addon/dialog/dialog.css");
        injectCssLink(codemirrorBase + "addon/fold/foldgutter.css");
        injectCssLinkAtTop(codemirrorBase + "addon/hint/show-hint.css");
        injectCssLink(codemirrorBase + "addon/search/matchesonscrollbar.css");
        injectCssLink(codemirrorBase + "addon/scroll/simplescrollbars.css");
    }

    private static void injectCssLink(final String url) {
        LinkElement link = Browser.getDocument().createLinkElement();
        link.setRel("stylesheet");
        link.setHref(url);
        nativeAttachToHead(link);
    }

    private static void injectCssLinkAtTop(final String url) {
        LinkElement link = Browser.getDocument().createLinkElement();
        link.setRel("stylesheet");
        link.setHref(url);
        nativeAttachFirstLink(link);
    }

    /**
     * Attach an element to document head.
     * 
     * @param newElement the element to attach
     */
    private static void nativeAttachToHead(Element newElement) {
        Elements.getDocument().getHead().appendChild(newElement);
    }

    private static void nativeAttachFirstLink(Element styleElement) {
        final HeadElement head = Elements.getDocument().getHead();
        final NodeList nodes = head.getElementsByTagName("link");
        if (nodes.length() > 0) {
            head.insertBefore(styleElement, nodes.item(0));
        } else {
            head.appendChild(styleElement);
        }
    }
}
