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
package org.eclipse.che.ide.extension.machine.client.outputspanel;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.parts.base.BaseView;
import org.eclipse.che.ide.extension.machine.client.MachineResources;
import org.vectomatic.dom.svg.ui.SVGImage;

/**
 * Implementation of {@link OutputsContainerView}.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class OutputsContainerViewImpl extends BaseView<OutputsContainerView.ActionDelegate> implements OutputsContainerView {

    @UiField
    MachineResources resources;
    @UiField
    FlowPanel       tabsPanel;
    @UiField
    DeckLayoutPanel contentPanel;

    @Inject
    public OutputsContainerViewImpl(PartStackUIResources partStackUIResources, OutputsContainerViewImplUiBinder uiBinder) {
        super(partStackUIResources);

        setContentWidget(uiBinder.createAndBindUi(this));

        minimizeButton.ensureDebugId("outputs-console-minimizeButton");

        // this hack used for adding box shadow effect to tabsPanel
        tabsPanel.getElement().getParentElement().getStyle().setOverflow(Overflow.VISIBLE);
        tabsPanel.getElement().getParentElement().getStyle().setZIndex(1);
    }

    @Override
    public void addConsole(String title, IsWidget widget) {
        final TabButton tabButton = new TabButton(null, title);
        tabButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                delegate.onConsoleSelected(tabsPanel.getWidgetIndex(tabButton));
            }
        });
        tabsPanel.add(tabButton);

        contentPanel.add(widget);
        showConsole(contentPanel.getWidgetCount() - 1);
    }

    @Override
    public void showConsole(int index) {
        for (int i = 0; i < tabsPanel.getWidgetCount(); i++) {
            final Widget widget = tabsPanel.getWidget(i);
            if (i == index) {
                widget.addStyleName(resources.getCss().outputsContainerConsoleTabSelected());
            } else {
                widget.removeStyleName(resources.getCss().outputsContainerConsoleTabSelected());
            }
        }

        contentPanel.showWidget(index);
    }

    @Override
    public void closeAllConsoles() {
        tabsPanel.clear();
        contentPanel.clear();
    }

    @Override
    protected void focusView() {
        contentPanel.getElement().focus();
    }

    interface OutputsContainerViewImplUiBinder extends UiBinder<Widget, OutputsContainerViewImpl> {
    }

    private class TabButton extends Composite {

        FlowPanel   tabPanel;
        InlineLabel tabTitleLabel;

        TabButton(SVGImage icon, String title) {
            tabPanel = new FlowPanel();
            tabPanel.ensureDebugId("outputs-container-tabButton");
            initWidget(tabPanel);

            setStyleName(resources.getCss().outputsContainerConsoleTab());

            if (icon != null) {
                tabPanel.add(icon);
            }

            tabTitleLabel = new InlineLabel(title);
            tabTitleLabel.addStyleName(resources.getCss().outputsContainerConsoleTabLabel());
            tabPanel.add(tabTitleLabel);
        }

        HandlerRegistration addClickHandler(ClickHandler handler) {
            return addDomHandler(handler, ClickEvent.getType());
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            tabPanel.addStyleName(resources.getCss().outputsContainerConsoleTabPanel());
        }
    }
}
