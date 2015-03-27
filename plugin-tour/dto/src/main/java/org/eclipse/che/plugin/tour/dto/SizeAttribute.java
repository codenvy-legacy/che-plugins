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

package org.eclipse.che.plugin.tour.dto;

import org.eclipse.che.dto.shared.DTO;

/**
 * Defines the size of the element
 * @author Florent Benoit
 */
@DTO
public interface SizeAttribute {

    /**
     * @return a size length
     */
    int getValue();

    /**
     * Defines the size value
     * @param value the size value
     */
    void setValue(int value);

    /**
     * Defines the size value
     * @param value the size value
     * @return this
     */
    SizeAttribute withValue(int value);

    /**
     * @return a size unit (like px, percent)
     */
    String getUnit();

    /**
     * Defines the size unit
     * @param unit the size unit
     */
    void setUnit(String unit);

    /**
     * Defines the size unit
     * @param unit the size unit
     * @return this
     */
    SizeAttribute withUnit(String unit);
}
