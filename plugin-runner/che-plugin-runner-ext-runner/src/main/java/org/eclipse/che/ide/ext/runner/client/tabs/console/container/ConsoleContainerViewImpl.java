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
package org.eclipse.che.ide.ext.runner.client.tabs.console.container;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ui.button.ConsoleButton;
import org.eclipse.che.ide.ui.button.ConsoleButtonFactory;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.validation.constraints.NotNull;

/**
 * @author Andrey Plotnikov
 */
public class ConsoleContainerViewImpl extends Composite implements ConsoleContainerView {

    interface ConsoleContainerViewImplUiBinder extends UiBinder<Widget, ConsoleContainerViewImpl> {
    }

    private static final ConsoleContainerViewImplUiBinder UI_BINDER = GWT.create(ConsoleContainerViewImplUiBinder.class);

    @UiField
    SimplePanel mainPanel;
    @UiField
    FlowPanel   buttons;
    @UiField
    Label       noRunnerLabel;
    @UiField(provided = true)
    final RunnerResources resources;

    private final ConsoleButtonFactory consoleButtonFactory;
    private final ConsoleButton        btnWrapText;
    private       ActionDelegate       delegate;

    @Inject
    public ConsoleContainerViewImpl(RunnerResources resources,
                                    PartStackUIResources buttonIcons,
                                    ConsoleButtonFactory consoleButtonFactory,
                                    RunnerLocalizationConstant locale) {
        this.resources = resources;
        this.consoleButtonFactory = consoleButtonFactory;

        initWidget(UI_BINDER.createAndBindUi(this));

        ConsoleButton.ActionDelegate wrapTextDelegate = new ConsoleButton.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                delegate.onWrapTextClicked();
            }
        };
        btnWrapText = createButton(buttonIcons.wrapText(), locale.consoleTooltipWraptext(), wrapTextDelegate);

        ConsoleButton.ActionDelegate scrollBottomDelegate = new ConsoleButton.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                delegate.onScrollBottomClicked();
            }
        };
        createButton(buttonIcons.arrowBottom(), locale.consoleTooltipScroll(), scrollBottomDelegate);

        ConsoleButton.ActionDelegate cleanDelegate = new ConsoleButton.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                delegate.onCleanClicked();
            }
        };
        createButton(buttonIcons.erase(), locale.consoleTooltipClear(), cleanDelegate);
    }

    @NotNull
    private ConsoleButton createButton(@NotNull SVGResource icon,
                                       @NotNull String prompt,
                                       @NotNull ConsoleButton.ActionDelegate delegate) {
        ConsoleButton button = consoleButtonFactory.createConsoleButton(prompt, icon);
        button.setDelegate(delegate);

        buttons.add(button);

        return button;
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void showWidget(@NotNull IsWidget console) {
        mainPanel.setWidget(console);
    }

    /** {@inheritDoc} */
    @Override
    public void removeWidget(@NotNull IsWidget console) {
        mainPanel.remove(console);
    }

    /** {@inheritDoc} */
    @Override
    public void selectWrapTextButton(boolean isChecked) {
        btnWrapText.setCheckedStatus(isChecked);
    }

    /** {@inheritDoc} */
    @Override
    public void setVisibleNoRunnerLabel(boolean visible) {
        noRunnerLabel.setVisible(visible);
    }
}