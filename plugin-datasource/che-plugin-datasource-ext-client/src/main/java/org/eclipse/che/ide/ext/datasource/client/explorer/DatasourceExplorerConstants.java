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
package org.eclipse.che.ide.ext.datasource.client.explorer;

import com.google.gwt.i18n.client.Constants;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;

/**
 * Constant interface for the datasource explorer.
 * 
 * @author "Mickaël Leduque"
 */
@DefaultLocale("en")
public interface DatasourceExplorerConstants extends Constants {

    /** The tooltip for the refresh button. */
    @DefaultStringValue("Refresh and explore")
    String exploreButtonTooltip();

    /** The string used in the part (top). */
    @DefaultStringValue("Datasource Explorer")
    String datasourceExplorerPartTitle();

    /** The string used in the side tab. */
    @DefaultStringValue("Datasource")
    String datasourceExplorerTabTitle();

    @DefaultStringArrayValue({"Simplest - Tables and views",
            "Standard - adds materialized views, aliases, syn.",
            "System - add system tables/views",
            "All existing table types"})
    String[] tableCategories();
}
