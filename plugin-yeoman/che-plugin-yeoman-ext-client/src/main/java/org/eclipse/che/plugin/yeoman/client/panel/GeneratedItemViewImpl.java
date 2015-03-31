/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.yeoman.client.panel;

import org.eclipse.che.plugin.yeoman.client.YeomanResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import org.vectomatic.dom.svg.ui.SVGPushButton;

/**
 * Implementation of an item view.
 *
 * @author Florent Benoit
 */
public class GeneratedItemViewImpl extends Composite implements GeneratedItemView {

    /**
     * UI Binder.
     */
    private static GeneratedItemViewImplUiBinder uiBinder = GWT.create(GeneratedItemViewImplUiBinder.class);
    /**
     * Grid for adding elements.
     */
    @UiField
    Grid            gridEntry;
    /**
     * Text field for displaying the name.
     */
    @UiField
    Label           textEntry;
    /**
     * Trash button for removing the element.
     */
    @UiField
    SVGPushButton   trashButton;
    /**
     * CSS resource.
     */
    @UiField(provided = true)
    YeomanResources uiResources;
    /**
     * The anchor element.
     */
    private YeomanPartView      yeomanPartView;
    /**
     * Name of this element.
     */
    private String              name;
    /**
     * Type of this element.
     */
    private YeomanGeneratorType type;

    /**
     * Dependency injection through {@link GeneratedItemViewFactory} for building a new
     * element based on its name and type and also UI resources
     *
     * @param name
     *         the given name
     * @param type
     *         the tpe of this element
     * @param uiResources
     *         the resource for retrieving data
     * @return a new UI element
     */
    @AssistedInject
    public GeneratedItemViewImpl(@Assisted String name, @Assisted YeomanGeneratorType type, YeomanResources uiResources) {
        super();
        this.name = name;
        this.type = type;
        this.uiResources = uiResources;
        initWidget(uiBinder.createAndBindUi(this));
        gridEntry.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_RIGHT);
        textEntry.setText(name);
    }

    /**
     * Handle the click on the trash button.
     *
     * @param event
     */
    @UiHandler("trashButton")
    public void clickOnTrashdButton(final ClickEvent event) {
        yeomanPartView.removeItem(type, name, this);
    }

    /**
     * Sets the anchor/ upper parent for delegating removal
     *
     * @param yeomanPartView
     *         the anchor
     */
    @Override
    public void setAnchor(YeomanPartView yeomanPartView) {
        this.yeomanPartView = yeomanPartView;
    }


    /**
     * UI binder interface.
     */
    interface GeneratedItemViewImplUiBinder extends UiBinder<Widget, GeneratedItemViewImpl> {
    }

}
