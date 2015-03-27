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
package org.eclipse.che.ide.ext.git.client;

import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.parts.base.BaseView;
import org.eclipse.che.ide.api.parts.base.ToolButton;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.vectomatic.dom.svg.ui.SVGImage;


/**
 * Implements {@link GitOutputPartView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class GitOutputPartViewImpl extends BaseView<GitOutputPartView.ActionDelegate> implements GitOutputPartView {

    interface GitOutputPartViewImplUiBinder extends UiBinder<Widget, GitOutputPartViewImpl> {
    }

    private static final String INFO_COLOR = "lightgreen";
    private static final String WARNING_COLOR = "cyan";
    private static final String ERROR_COLOR = "#F62217";

    @UiField
    FlowPanel                                  consoleArea;

    @UiField
    ScrollPanel                                scrollPanel;

    @Inject
    public GitOutputPartViewImpl(PartStackUIResources resources,
                                 Resources coreResources,
                                 GitOutputPartViewImplUiBinder uiBinder) {
        super(resources);
        setContentWidget(uiBinder.createAndBindUi(this));

        ToolButton clearButton = new ToolButton(new SVGImage(coreResources.clear()));
        clearButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onClearClicked();
            }
        });
        clearButton.ensureDebugId("console-clear");
        minimizeButton.ensureDebugId("console-minimizeBut");
        toolBar.addEast(clearButton, 20);
    }

    /** {@inheritDoc} */
    @Override
    public void print(String text) {
        String preStyle = " style='margin:0px; font-size: 12px;' ";

        HTML html = new HTML();

        String TEXT = text.toUpperCase();
        if (TEXT.startsWith("[INFO]")) {
            html.setHTML("<pre" + preStyle + ">[<span style='color:" + INFO_COLOR + ";'><b>INFO</b></span>] " +
                    SimpleHtmlSanitizer.sanitizeHtml(text.substring(6)).asString() + "</pre>");

        } else if (TEXT.startsWith("[ERROR]")) {
            html.setHTML("<pre" + preStyle + ">[<span style='color:" + ERROR_COLOR + ";'><b>ERROR</b></span>] " +
                    SimpleHtmlSanitizer.sanitizeHtml(text.substring(7)).asString() + "</pre>");

        } else if (TEXT.startsWith("[WARNING]")) {
            html.setHTML("<pre" + preStyle + ">[<span style='color:" + WARNING_COLOR + ";'><b>WARNING</b></span>] " +
                    SimpleHtmlSanitizer.sanitizeHtml(text.substring(9)).asString() + "</pre>");

        } else {
            html.setHTML("<pre" + preStyle + ">" + SimpleHtmlSanitizer.sanitizeHtml(text).asString() + "</pre>");
        }

        html.getElement().setAttribute("style", "padding-left: 2px;");
        consoleArea.add(html);
    }

    @Override
    public void print(String text, String color) {
        String preStyle = " style='margin:0px; font-size: 12px;' ";

        HTML html = new HTML();
        html.setHTML("<pre" + preStyle + "><span style='color:" + SimpleHtmlSanitizer.sanitizeHtml(color).asString() +
                ";'>" + SimpleHtmlSanitizer.sanitizeHtml(text).asString() + "</span></pre>");

        html.getElement().setAttribute("style", "padding-left: 2px;");
        consoleArea.add(html);
    }

    @Override
    public void printInfo(String text) {
        print(text, INFO_COLOR);
    }

    @Override
    public void printWarn(String text) {
        print(text, WARNING_COLOR);
    }

    @Override
    public void printError(String text) {
        print(text, ERROR_COLOR);
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        consoleArea.clear();
    }

    /** {@inheritDoc} */
    @Override
    public void scrollBottom() {
        scrollPanel.getElement().setScrollTop(scrollPanel.getElement().getScrollHeight());
    }

}
