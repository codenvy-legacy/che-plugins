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
package org.eclipse.che.ide.ext.svn.client.common;

import org.eclipse.che.ide.api.parts.PartPresenter;
import org.eclipse.che.ide.api.parts.base.BasePresenter;
import org.eclipse.che.ide.workspace.WorkBenchPartControllerImpl;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.ext.svn.client.SubversionExtensionLocalizationConstants;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Presenter for the {@link RawOutputView}.
 */
@Singleton
public class RawOutputPresenter extends BasePresenter implements RawOutputView.ActionDelegate {

    private final SubversionExtensionLocalizationConstants constants;

    private final RawOutputView view;

    /**
     * Constructor.
     */
    @Inject
    public RawOutputPresenter(final SubversionExtensionLocalizationConstants constants, final RawOutputView view) {
        this.constants = constants;
        this.view = view;

        view.setTitle(getTitle());
        view.setDelegate(this);
    }

    @Override
    public String getTitle() {
        return constants.subversionLabel();
    }

    @Override
    public void onOpen() {
        super.onOpen();

        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                view.scrollBottom();

                return false;
            }
        }, WorkBenchPartControllerImpl.DURATION);
    }

    /**
     * Prints text to subversion output.
     *
     * @param multilineText multiline text to print
     */
    public void print(@NotNull final String multilineText) {
        if (multilineText != null) {
            for (final String line : multilineText.split("\n")) {
                view.print(line);
            }
        }

        performPostOutputActions();
    }

    /**
     * Clears output.
     */
    public void clear() {
        view.clear();
    }

    @Override
    public void onClearClicked() {
        clear();
    }

    @Nullable
    @Override
    public ImageResource getTitleImage() { return null; }

    @Override
    public SVGResource getTitleSVGImage() {
        return null;
    }

    @Nullable
    @Override
    public String getTitleToolTip() {
        return "Displays Subversion command output";
    }

    @Override
    public void go(final AcceptsOneWidget container) {
        container.setWidget(this.view);
    }

    private void performPostOutputActions() {
        final PartPresenter activePart = partStack.getActivePart();

        if (activePart == null || !activePart.equals(this)) {
            partStack.setActivePart(this);
        }

        view.scrollBottom();
    }

}
