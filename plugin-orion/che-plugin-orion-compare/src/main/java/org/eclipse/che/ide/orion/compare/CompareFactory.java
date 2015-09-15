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
package org.eclipse.che.ide.orion.compare;

import org.eclipse.che.api.promises.client.Promise;

/**
 * @author Evgen Vidolob
 */
public interface CompareFactory {

    /**
     * Creates a compare view instance by given view options and other parameters.
     * @param config The compare view option.
     * @return
     */
    Promise<Compare> createCompare(CompareConfig config);

    /**
     * Creates a compare view instance by given view options and other parameters.
     * @param config The compare view option.
     * @param commandSpanId  The dom element id to render all the commands that toggles compare view and navigates diffs. If not defined, no command is rendered.
     * @return
     */
    Promise<Compare> createCompare(CompareConfig config, String commandSpanId);

    /**
     * Creates a compare view instance by given view options and other parameters.
     * @param config The compare view option.
     * @param commandSpanId  The dom element id to render all the commands that toggles compare view and navigates diffs. If not defined, no command is rendered.
     * @param viewType  The type of the compare view. Can be either "twoWay" or "inline". Id not defined default is "twoWay".
     * @return
     */
    Promise<Compare> createCompare(CompareConfig config, String commandSpanId, String viewType);

    /**
     * Creates a compare view instance by given view options and other parameters.
     * @param config The compare view option.
     * @param commandSpanId  The dom element id to render all the commands that toggles compare view and navigates diffs. If not defined, no command is rendered.
     * @param viewType  The type of the compare view. Can be either "twoWay" or "inline". Id not defined default is "twoWay".
     *                  "twoWay" represents a side by side compare editor while "inline" represents a unified compare view.
     * @param toggleable Weather or not the compare view is toggleable. A toggleable compare view provides a toggle button which toggles between the "twoWay" and "inline" view.
     * @return
     */
    Promise<Compare> createCompare(CompareConfig config, String commandSpanId, String viewType, boolean toggleable);

    /**
     * Creates a compare view instance by given view options and other parameters.
     * @param config The compare view option.
     * @param commandSpanId  The dom element id to render all the commands that toggles compare view and navigates diffs. If not defined, no command is rendered.
     * @param viewType  The type of the compare view. Can be either "twoWay" or "inline". Id not defined default is "twoWay".
     *                  "twoWay" represents a side by side compare editor while "inline" represents a unified compare view.
     * @param toggleable Weather or not the compare view is toggleable. A toggleable compare view provides a toggle button which toggles between the "twoWay" and "inline" view.
     * @param toggleCommandSpanId The dom element id to render the toggle command. If this is defined the toggle command will be rendered in this DIV rather than the commandSpanId.
     * @return
     */
    Promise<Compare> createCompare(CompareConfig config, String commandSpanId, String viewType, boolean toggleable, String toggleCommandSpanId);

    FileOptions createFieOptions();

    CompareConfig createCompareConfig();
}
