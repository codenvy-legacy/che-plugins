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
package org.eclipse.che.ide.ext.datasource.client.common;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.PartStackUIResources;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;

public abstract class ViewWithToolbar<T> extends Composite implements View<T> {

    private final LayoutPanel     toolBar;
    private final DockLayoutPanel container;
    private T                     delegate;

    public ViewWithToolbar(final PartStackUIResources resources) {
        container = new DockLayoutPanel(Style.Unit.PX);
        initWidget(container);
        container.setSize("100%", "100%");
        toolBar = new LayoutPanel();
        toolBar.addStyleName(resources.partStackCss().ideBasePartToolbar());
        container.addNorth(toolBar, 20);

        // this hack used for adding box shadow effect to toolbar
        toolBar.getElement().getParentElement().getStyle().setOverflow(Style.Overflow.VISIBLE);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(T delegate) {
        this.delegate = delegate;
    }

    public T getDelegate() {
        return delegate;
    }

    public DockLayoutPanel getContainer() {
        return container;
    }

    public LayoutPanel getToolBar() {
        return toolBar;
    }
}
