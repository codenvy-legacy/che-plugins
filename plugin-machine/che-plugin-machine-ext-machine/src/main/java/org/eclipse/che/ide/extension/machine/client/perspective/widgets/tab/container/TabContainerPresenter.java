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
package org.eclipse.che.ide.extension.machine.client.perspective.widgets.tab.container;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.extension.machine.client.perspective.widgets.tab.Tab;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.tab.content.TabPresenter;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.tab.header.TabHeader;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * The class provides methods which contains business logic to add and control tabs.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class TabContainerPresenter implements TabHeader.ActionDelegate {

    private final Map<TabHeader, TabPresenter> tabs;
    private final TabContainerView             view;

    @Inject
    public TabContainerPresenter(TabContainerView view) {
        this.view = view;

        this.tabs = new HashMap<>();
    }

    /** @return view which associated with current tab. */
    public TabContainerView getView() {
        return view;
    }

    /**
     * Adds tab to container. Tab contains header and associated content.
     *
     * @param tab
     *         tab which need add
     */
    public void addTab(@Nonnull Tab tab) {
        TabHeader header = tab.getHeader();
        header.setDelegate(this);

        TabPresenter content = tab.getContent();

        tabs.put(header, content);

        view.addHeader(header);
        view.addContent(content);
    }

    /**
     * Shows content of clicked tab.
     *
     * @param tab
     *         tab which need show
     */
    public void showTab(@Nonnull Tab tab) {
        onTabClicked(tab.getHeader());
    }

    /** {@inheritDoc} */
    @Override
    public void onTabClicked(@Nonnull TabHeader tabHeader) {
        for (TabHeader header : tabs.keySet()) {
            header.setDisable();
        }

        tabHeader.setEnable();

        for (TabPresenter presenter : tabs.values()) {
            presenter.setVisible(false);
        }

        TabPresenter content = tabs.get(tabHeader);

        content.setVisible(true);
    }
}
