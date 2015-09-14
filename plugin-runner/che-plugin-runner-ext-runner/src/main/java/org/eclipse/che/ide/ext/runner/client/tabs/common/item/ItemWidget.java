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
package org.eclipse.che.ide.ext.runner.client.tabs.common.item;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.inject.ImplementedBy;

import org.eclipse.che.ide.api.mvp.View;
import org.vectomatic.dom.svg.ui.SVGImage;

import javax.validation.constraints.NotNull;
import org.eclipse.che.commons.annotation.Nullable;

/**
 * Provides methods which allow change visual representation of runner.
 *
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
@ImplementedBy(ItemWidgetImpl.class)
public interface ItemWidget extends View<ItemWidget.ActionDelegate> {

    /** Performs some actions when tab is selected. */
    void select();

    /** Performs some actions when tab is unselected. */
    void unSelect();

    /**
     * Sets name to special place on widget.
     *
     * @param name
     *         name which need set
     */
    void setName(@NotNull String name);

    /**
     * Sets description to special place on widget.
     *
     * @param description
     *         description which need set
     */
    void setDescription(@Nullable String description);

    /**
     * Sets start time of runner to special place on widget.
     *
     * @param time
     *         time which need set
     */
    void setStartTime(@NotNull String time);

    /**
     * Sets svg image to special place on widget.
     *
     * @param image
     *         image which need set
     */
    void setImage(@NotNull SVGImage image);

    /**
     * Sets image to special place on widget.
     *
     * @param imageResource
     *         image which need set
     */
    void setImage(@NotNull ImageResource imageResource);

    /** @return an instance of {@link FlowPanel} on which is displayed runner status icon. */
    @NotNull
    SimpleLayoutPanel getImagePanel();

    interface ActionDelegate {
        /** Performs some actions when user click on widget. */
        void onWidgetClicked();
    }

}