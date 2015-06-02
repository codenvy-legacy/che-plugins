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
package org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.eclipse.che.ide.api.parts.PartStackView;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.tab.container.TabContainerView;
import org.vectomatic.dom.svg.ui.SVGImage;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * The class offers special main panel to add tab container. The class is a wrapper of tab container view.
 *
 * @author Dmitry Shnurenko
 */
public class InfoContainerViewImpl extends Composite implements InfoContainerView, PartStackView {
    interface MachineInfoContainerUiBinder extends UiBinder<Widget, InfoContainerViewImpl> {
    }

    private final static MachineInfoContainerUiBinder UI_BINDER = GWT.create(MachineInfoContainerUiBinder.class);

    @UiField
    SimplePanel infoContainer;

    @Inject
    public InfoContainerViewImpl() {
        initWidget(UI_BINDER.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public void addContainer(@Nonnull TabContainerView tabContainer) {
        this.infoContainer.setWidget(tabContainer);
    }

    /** {@inheritDoc} */
    @Override
    public TabItem addTab(SVGImage icon, String title, String toolTip, IsWidget widget, boolean closable) {
        //to do nothing
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void removeTab(int index) {
        //to do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void setActiveTab(int index) {
        //to do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void setTabpositions(List<Integer> partPositions) {
        //to do nothing
    }

    /** {@inheritDoc} */
    @Override
    public InsertPanel.ForIsWidget getContentPanel() {
        //to do nothing
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus(boolean focused) {
        //to do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void updateTabItem(int index, SVGImage icon, String title, String toolTip, IsWidget widget) {
        //to do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        //to do nothing
    }
}