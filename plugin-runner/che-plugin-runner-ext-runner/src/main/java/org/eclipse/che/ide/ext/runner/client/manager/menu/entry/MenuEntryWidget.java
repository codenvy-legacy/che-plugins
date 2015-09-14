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
package org.eclipse.che.ide.ext.runner.client.manager.menu.entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.vectomatic.dom.svg.ui.SVGImage;

import javax.validation.constraints.NotNull;

/**
 * The class describes special widget which is entry in header menu.
 *
 * @author Dmitry Shnurenko
 */
public class MenuEntryWidget extends Composite implements MenuEntry, ClickHandler {
    interface MenuEntityWidgetUiBinder extends UiBinder<Widget, MenuEntryWidget> {
    }

    private final static MenuEntityWidgetUiBinder UI_BINDER = GWT.create(MenuEntityWidgetUiBinder.class);

    @UiField
    SimpleLayoutPanel image;
    @UiField
    Label             text;

    @UiField(provided = true)
    final RunnerResources resources;

    private final SVGImage icon;

    private ActionDelegate delegate;
    private boolean        isSplitterHidden;

    @Inject
    public MenuEntryWidget(RunnerResources resources, @NotNull @Assisted String entryName) {
        this.resources = resources;

        initWidget(UI_BINDER.createAndBindUi(this));

        icon = new SVGImage(resources.selectedMenuEntry());

        text.setText(entryName);

        addDomHandler(this, ClickEvent.getType());

        isSplitterHidden = true;
    }

    /** {@inheritDoc} */
    @Override
    public void onClick(@NotNull ClickEvent event) {
        image.getElement().setInnerHTML(isSplitterHidden ? icon.toString() : "");

        delegate.onEntryClicked(isSplitterHidden);

        isSplitterHidden = !isSplitterHidden;
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(@NotNull ActionDelegate delegate) {
        this.delegate = delegate;
    }

}