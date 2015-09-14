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
package org.eclipse.che.ide.ext.runner.client.manager.button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.manager.tooltip.TooltipWidget;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.validation.constraints.NotNull;

/**
 * Class provides view representation of button.
 *
 * @author Dmitry Shnurenko
 */
public class ButtonWidgetImpl extends Composite implements ButtonWidget, ClickHandler, MouseOverHandler, MouseOutHandler {

    interface ButtonWidgetImplUiBinder extends UiBinder<Widget, ButtonWidgetImpl> {
    }

    private static final int TOP_TOOLTIP_SHIFT = 35;

    private static final ButtonWidgetImplUiBinder UI_BINDER = GWT.create(ButtonWidgetImplUiBinder.class);

    @UiField
    SimpleLayoutPanel image;
    @UiField(provided = true)
    final RunnerResources resources;

    private final TooltipWidget tooltip;

    private ActionDelegate delegate;
    private boolean        isEnable;

    @Inject
    public ButtonWidgetImpl(RunnerResources resources,
                            TooltipWidget tooltip,
                            @NotNull @Assisted String prompt,
                            @NotNull @Assisted SVGResource image) {
        this.resources = resources;
        this.tooltip = tooltip;
        this.tooltip.setDescription(prompt);

        initWidget(UI_BINDER.createAndBindUi(this));

        this.image.getElement().appendChild(image.getSvg().getElement());

        addDomHandler(this, ClickEvent.getType());
        addDomHandler(this, MouseOutEvent.getType());
        addDomHandler(this, MouseOverEvent.getType());
    }

    /** {@inheritDoc} */
    @Override
    public void setDisable() {
        isEnable = false;

        image.addStyleName(resources.runnerCss().opacityButton());
    }

    /** {@inheritDoc} */
    @Override
    public void setEnable() {
        isEnable = true;

        image.removeStyleName(resources.runnerCss().opacityButton());
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(@NotNull ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void onClick(ClickEvent event) {
        if (isEnable) {
            delegate.onButtonClicked();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onMouseOut(MouseOutEvent mouseOutEvent) {
        tooltip.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void onMouseOver(MouseOverEvent event) {
        tooltip.setPopupPosition(getAbsoluteLeft(), getAbsoluteTop() + TOP_TOOLTIP_SHIFT);

        tooltip.show();
    }
}