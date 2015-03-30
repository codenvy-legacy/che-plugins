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
package org.eclipse.che.ide.ext.runner.client.tabs.history;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.ext.runner.client.tabs.history.runner.RunnerWidget;

import javax.annotation.Nonnull;

/**
 * The class contains methods which allow change view representation of history panel.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class HistoryViewImpl extends Composite implements HistoryView {

    interface HistoryImplUiBinder extends UiBinder<Widget, HistoryViewImpl> {
    }

    private static final HistoryImplUiBinder UI_BINDER = GWT.create(HistoryImplUiBinder.class);

    @UiField
    FlowPanel   runnersPanel;
    @UiField
    ScrollPanel scrollPanel;

    @Inject
    public HistoryViewImpl() {
        initWidget(UI_BINDER.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public void addRunner(@Nonnull RunnerWidget runnerWidget) {
        runnersPanel.add(runnerWidget);
        scrollPanel.getElement().setScrollTop(scrollPanel.getElement().getScrollHeight());
    }

    /** {@inheritDoc} */
    @Override
    public void removeRunner(@Nonnull RunnerWidget runnerWidget) {
        runnersPanel.remove(runnerWidget);
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        runnersPanel.clear();
    }
}