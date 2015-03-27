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
package org.eclipse.che.ide.ext.runner.client.manager.tooltip;

import com.google.inject.ImplementedBy;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Provides methods which allow work with tooltip widget.
 *
 * @author Dmitry Shnurenko
 */
@ImplementedBy(TooltipWidgetImpl.class)
public interface TooltipWidget {

    /**
     * Sets description which will be displayed on tooltip.
     *
     * @param description
     *         description which need set
     */
    void setDescription(@Nonnull String description);

    /**
     * Sets coordinates where will be displayed tooltip.
     *
     * @param x
     *         value of x coordinate
     * @param y
     *         value of y coordinate
     */
    void setPopupPosition(@Nonnegative int x, @Nonnegative int y);

    /** Shows tooltip. */
    void show();

    /** Hides tooltip. */
    void hide();

}