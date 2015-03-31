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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Implementation of the folding panel
 *
 * @author Florent Benoit
 */
public class FoldingPanelImpl extends Composite implements FoldingPanel {

    /**
     * UI binder.
     */
    private static FoldingPanelImplUiBinder uiBinder = GWT.create(FoldingPanelImplUiBinder.class);

    /**
     * The toggle button.
     */
    @UiField
    Label toggleButton;

    /**
     * Inner css.
     */
    @UiField
    FoldingCss style;

    /**
     * Toggle panel.
     */
    @UiField
    FlowPanel togglePanel;

    /**
     * Title of the element
     */
    @UiField
    Label foldingTitle;

    /**
     * State of the folding panel.
     */
    private boolean open = true;

    /**
     * Constructor that is used with Dependency Injection
     *
     * @param name
     *         the title of this panel
     */
    @AssistedInject
    public FoldingPanelImpl(@Assisted String name) {
        super();
        initWidget(uiBinder.createAndBindUi(this));
        setOpen(true);
        this.foldingTitle.setText(name);
    }

    /**
     * Toggle the state.
     */
    protected void toggle() {
        this.open = !this.open;
        setOpen(this.open);
    }

    /**
     * Handle the click on the toggleButton
     *
     * @param event
     */
    @UiHandler("toggleButton")
    public void handleOpenCloseClick(final ClickEvent event) {
        toggle();
    }

    protected void setOpen(final boolean open) {
        if (open) {
            this.togglePanel.setVisible(true);
            this.togglePanel.removeStyleName(style.folded());
            this.toggleButton.removeStyleName(style.toggleButtonClosed());
        } else {
            this.togglePanel.setVisible(false);
            this.togglePanel.addStyleName(style.folded());
            this.toggleButton.addStyleName(style.toggleButtonClosed());
        }
    }

    @Override
    public String getName() {
        return this.foldingTitle.getText();
    }

    @Override
    public void add(GeneratedItemView element) {
        togglePanel.add(element);
    }

    @Override
    public void remove(GeneratedItemView element) {
        togglePanel.remove(element);
    }


    interface FoldingPanelImplUiBinder extends UiBinder<Widget, FoldingPanelImpl> {
    }

    public interface FoldingCss extends CssResource {

        @ClassName("toggleButton-closed")
        String toggleButtonClosed();

        @ClassName("folded")
        String folded();

    }
}
