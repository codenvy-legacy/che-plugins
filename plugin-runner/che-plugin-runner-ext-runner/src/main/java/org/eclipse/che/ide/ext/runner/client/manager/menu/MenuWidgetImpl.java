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
package org.eclipse.che.ide.ext.runner.client.manager.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.eclipse.che.ide.ext.runner.client.manager.menu.entry.MenuEntry;

import javax.validation.constraints.NotNull;

/**
 * The class describes special widget which is header menu and contains menu entries.
 *
 * @author Dmitry Shnurenko
 */
public class MenuWidgetImpl extends Composite implements MenuWidget {
    interface MenuWidgetImplUiBinder extends UiBinder<Widget, MenuWidgetImpl> {
    }

    private final static MenuWidgetImplUiBinder UI_BINDER = GWT.create(MenuWidgetImplUiBinder.class);

    @UiField
    FlowPanel   entityPanel;
    @UiField
    SimplePanel span;
    @UiField
    FlowPanel   menuPanel;

    @Inject
    public MenuWidgetImpl() {
        initWidget(UI_BINDER.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public SimplePanel getSpan() {
        return span;
    }

    /** {@inheritDoc} */
    @Override
    public void addEntry(@NotNull MenuEntry entry) {
        entityPanel.add(entry);
    }
}