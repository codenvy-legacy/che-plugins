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
package org.eclipse.che.ide.ext.runner.client.tabs.common.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.vectomatic.dom.svg.ui.SVGImage;

import javax.validation.constraints.NotNull;
import org.eclipse.che.commons.annotation.Nullable;

/**
 * Class provides general view representation for runners and environments.
 *
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
public class ItemWidgetImpl extends Composite implements ItemWidget, ClickHandler {

    interface RunnerViewImplUiBinder extends UiBinder<Widget, ItemWidgetImpl> {
    }

    private static final RunnerViewImplUiBinder UI_BINDER = GWT.create(RunnerViewImplUiBinder.class);

    @UiField
    Label             runnerName;
    @UiField
    Label             ram;
    @UiField
    Label             startTime;
    @UiField
    SimpleLayoutPanel image;
    @UiField
    SimpleLayoutPanel imagePanel;

    @UiField(provided = true)
    final RunnerResources resources;

    private final SimpleLayoutPanel svgImage;
    private final Image             pngImage;

    private ActionDelegate delegate;

    @Inject
    public ItemWidgetImpl(RunnerResources resources) {
        this.resources = resources;

        initWidget(UI_BINDER.createAndBindUi(this));

        this.svgImage = new SimpleLayoutPanel();
        this.pngImage = new Image();

        addDomHandler(this, ClickEvent.getType());
    }

    /** {@inheritDoc} */
    @Override
    public void select() {
        removeStyleName(resources.runnerCss().runnerShadow());
        addStyleName(resources.runnerCss().runnerWidgetBorders());
    }

    /** {@inheritDoc} */
    @Override
    public void unSelect() {
        addStyleName(resources.runnerCss().runnerShadow());
        removeStyleName(resources.runnerCss().runnerWidgetBorders());
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(@NotNull ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void onClick(ClickEvent event) {
        delegate.onWidgetClicked();
    }

    /** {@inheritDoc} */
    @Override
    public void setName(@NotNull String name) {
        this.ensureDebugId(name);
        runnerName.setText(name);
    }

    /** {@inheritDoc} */
    @Override
    public void setDescription(@Nullable String description) {
        ram.setText(description);
    }

    /** {@inheritDoc} */
    @Override
    public void setStartTime(@NotNull String time) {
        startTime.setText(time);
    }

    /** {@inheritDoc} */
    @Override
    public void setImage(@NotNull SVGImage svgImageResource) {
        svgImage.clear();
        svgImage.add(svgImageResource);
        svgImage.getElement().getStyle().setZIndex(-1);

        image.setWidget(svgImage);
    }

    /** {@inheritDoc} */
    @Override
    public void setImage(@NotNull ImageResource imageResource) {
        pngImage.setResource(imageResource);
        image.setWidget(pngImage);
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public SimpleLayoutPanel getImagePanel() {
        return imagePanel;
    }
}