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
package org.eclipse.che.ide.extension.machine.client.outputspanel.console;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * View for {@link CommandOutputConsole}.
 *
 * @author Artem Zatsarynnyy
 */
public class CommandOutputConsoleView extends Composite implements OutputConsoleView {

    private static final CommandOutputConsoleViewUiBinder UI_BINDER = GWT.create(CommandOutputConsoleViewUiBinder.class);

    private static final String PRE_STYLE = "style='margin:0px;'";

    private ActionDelegate delegate;

    @UiField
    ScrollPanel scrollPanel;
    @UiField
    FlowPanel   consoleArea;

    /** If true - next printed line should replace the previous one. */
    private boolean carriageReturn;

    @Inject
    public CommandOutputConsoleView() {
        initWidget(UI_BINDER.createAndBindUi(this));
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void printCommandLine(String commandLine) {
        final HTML html = new HTML(buildSafeHtmlMessage(commandLine));
        html.getElement().getStyle().setPaddingLeft(2, Style.Unit.PX);
        html.getElement().getStyle().setColor("gray");
        consoleArea.add(html);
    }

    @Override
    public void print(String message, boolean cr) {
        final HTML html = new HTML(buildSafeHtmlMessage(message));
        html.getElement().getStyle().setPaddingLeft(2, Style.Unit.PX);

        if (carriageReturn) {
            consoleArea.remove(consoleArea.getWidgetCount() - 1);
            carriageReturn = false;
        }

        carriageReturn = cr;
        consoleArea.add(html);
    }

    /** Return sanitized message (with all restricted HTML-tags escaped) in {@link SafeHtml}. */
    private SafeHtml buildSafeHtmlMessage(String message) {
        return new SafeHtmlBuilder()
                .appendHtmlConstant("<pre " + PRE_STYLE + ">")
                .append(SimpleHtmlSanitizer.sanitizeHtml(message))
                .appendHtmlConstant("</pre>")
                .toSafeHtml();
    }

    /** {@inheritDoc} */
    @Override
    public void scrollBottom() {
        scrollPanel.getElement().setScrollTop(scrollPanel.getElement().getScrollHeight());
    }

    interface CommandOutputConsoleViewUiBinder extends UiBinder<Widget, CommandOutputConsoleView> {
    }
}
