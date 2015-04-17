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
package org.eclipse.che.env.local.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.eclipse.che.ide.ui.cellview.CellTableResources;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Vitalii Parfonov
 */
public class WorkspaceMappingViewImpl extends Composite implements WorkspaceMappingView {
    interface WorkspaceMappingViewImplUiBinder extends UiBinder<Widget, WorkspaceMappingViewImpl> {
    }


    private static WorkspaceMappingViewImplUiBinder uiBinder = GWT.create(WorkspaceMappingViewImplUiBinder.class);
    @UiField
    Button btnAdd;
    @UiField
    Button btnDelete;

   // @UiField(provided = true)
    //CellTable<Map<String, String>> ws;

    @UiField(provided = true)
    final   LocalizationConstant locale;
    private ActionDelegate       delegate;

    /**
     * Create view.
     *
     * @param locale
     */
    @Inject
    protected WorkspaceMappingViewImpl(LocalizationConstant locale, CellTableResources res) {
        this.locale = locale;
        initWidget(uiBinder.createAndBindUi(this));
    }


    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnAdd")
    public void onAddClicked(ClickEvent event) {
        delegate.onAddClicked();
    }

    @UiHandler("btnDelete")
    public void onDeleteClicked(ClickEvent event) {
        delegate.onDeleteClicked();
    }

    @Override
    public void setWorkspaces(@Nonnull Map<String, String> ws) {

    }
}