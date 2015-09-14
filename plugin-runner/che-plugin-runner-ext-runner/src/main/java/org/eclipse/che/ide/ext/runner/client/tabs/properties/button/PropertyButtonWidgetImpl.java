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
package org.eclipse.che.ide.ext.runner.client.tabs.properties.button;

import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.tabs.container.tab.Background;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.validation.constraints.NotNull;

/**
 * Class provides view representation of property button on properties panel.
 *
 * @author Dmitry Shnurenko
 */
public class PropertyButtonWidgetImpl extends Composite implements PropertyButtonWidget, ClickHandler {
    interface ButtonWidgetImplUiBinder extends UiBinder<Widget, PropertyButtonWidgetImpl> {
    }

    private static final ButtonWidgetImplUiBinder UI_BINDER = GWT.create(ButtonWidgetImplUiBinder.class);

    @UiField
    Label button;

    @UiField(provided = true)
    final RunnerLocalizationConstant locale;
    @UiField(provided = true)
    final RunnerResources            resources;

    private ActionDelegate delegate;
    private boolean        isEnable;

    @Inject
    public PropertyButtonWidgetImpl(RunnerLocalizationConstant locale,
                                    RunnerResources resources,
                                    @Assisted String title,
                                    @Assisted Background background) {
        this.locale = locale;
        this.resources = resources;

        initWidget(UI_BINDER.createAndBindUi(this));

        getElement().getStyle().setBackgroundColor(background.toString());

        button.setText(title);

        addDomHandler(this, ClickEvent.getType());

        setEnable(true);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnable(boolean isEnable) {
        this.isEnable = isEnable;

        if (isEnable) {
            button.removeStyleName(resources.runnerCss().opacityButton());
        } else {
            button.addStyleName(resources.runnerCss().opacityButton());
        }
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
}