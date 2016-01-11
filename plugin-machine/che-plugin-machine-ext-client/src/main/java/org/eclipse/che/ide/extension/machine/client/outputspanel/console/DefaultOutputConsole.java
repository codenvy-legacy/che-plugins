/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.extension.machine.client.outputspanel.console;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.ide.extension.machine.client.command.CommandConfiguration;

/**
 * Console panel for some text outputs.
 *
 * @author Valeriy Svydenko
 */
public class DefaultOutputConsole implements OutputConsole {

    private final OutputConsoleView view;
    private       String            title;

    @Inject
    public DefaultOutputConsole(OutputConsoleView view, @Assisted String title) {
        this.view = view;
        this.title = title;

        this.view.hideCommand();
    }

    /**
     * Print message in the console.
     *
     * @param text
     *         message which should be printed
     */
    public void printText(String text) {
        view.print(text, text.endsWith("\r"));
        view.scrollBottom();
    }

    /**
     * Print message in console. If next string repeat previous, the previous string will be removed and the next string will be shown.
     *
     * @param text
     *         message which will be printed
     * @param isRepeat
     *         flag which define string repeats or not {@code true} string repeats, {@code false} string doesn't repeat
     */
    public void printText(String text, boolean isRepeat) {
        text = text.trim();

        if (text.isEmpty()) {
            return;
        }

        view.print(text, isRepeat);
        view.scrollBottom();
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public CommandConfiguration getCommand() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return title;
    }

    /** {@inheritDoc} */
    @Override
    public void listenToOutput(String wsChannel) {

    }

    /** {@inheritDoc} */
    @Override
    public void attachToProcess(int pid) {

    }

    /** {@inheritDoc} */
    @Override
    public boolean isFinished() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void onClose() {

    }
}
