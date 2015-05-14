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

import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.parts.base.BaseView;
import org.eclipse.che.ide.api.parts.base.ToolButton;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.vectomatic.dom.svg.ui.SVGImage;

/**
 * Implementation of {@link RawOutputView}.
 */
@Singleton
public class RawOutputViewImpl extends BaseView<RawOutputView.ActionDelegate> implements RawOutputView {

    interface RawOutputViewImplUiBinder extends UiBinder<Widget, RawOutputViewImpl> { }

    private static RawOutputViewImplUiBinder uiBinder = GWT.create(RawOutputViewImplUiBinder.class);

    private final Resources coreResources;

    @UiField
    ScrollPanel scrollPanel;

    @UiField
    FlowPanel lines;

    /**
     * Constructor.
     */
    @Inject
    public RawOutputViewImpl(final PartStackUIResources resources, final Resources coreResources) {
        super(resources);
        this.coreResources = coreResources;
        minimizeButton.ensureDebugId("console-minimizeBut");

        setContentWidget(uiBinder.createAndBindUi(this));

        addClearButton();
    }

    private void addClearButton() {
        final ToolButton clearButton = new ToolButton(new SVGImage(coreResources.clear()));
        addMenuButton(clearButton);

        clearButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onClearClicked();
            }
        });
        clearButton.ensureDebugId("console-clear");



//        FlowPanel f = new FlowPanel();
//        f.setWidth("100%");
//        f.setHeight("18px");
//        f.getElement().getStyle().setBackgroundColor(org.eclipse.che.ide.api.theme.Style.getPartBackground());
//
//        toolBar.addSouth(f, 18);
//        setToolbarHeight(22+18);
//
//        f.getElement().getStyle().setProperty("borderTop", "#333 1px solid");
//        f.add(clearButton);
    }

    @Override
    public void print(String safeText) {
        FlowPanel line = new FlowPanel();
        line.getElement().setInnerHTML("<pre>" + (safeText == null || safeText.isEmpty() ? " " : safeText) + "</pre>");
        lines.add(line);
    }

    @Override
    public void clear() {
        lines.clear();
    }

    @Override
    public void scrollBottom() {
        scrollPanel.getElement().setScrollTop(scrollPanel.getElement().getScrollHeight());
    }

}
