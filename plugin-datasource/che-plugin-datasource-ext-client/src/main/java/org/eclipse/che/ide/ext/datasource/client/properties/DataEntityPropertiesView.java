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
package org.eclipse.che.ide.ext.datasource.client.properties;

import org.eclipse.che.ide.api.mvp.View;
import com.google.gwt.view.client.AbstractDataProvider;

/**
 * Interface for the database item properties display view.
 * 
 * @author Mickaël LEDUQUE
 */
public interface DataEntityPropertiesView extends View<DataEntityPropertiesView.ActionDelegate> {

    /**
     * Interface for the action delegate on data item properties display view.
     * 
     * @author Mickaël LEDUQUE
     */
    interface ActionDelegate {
    }

    void setShown(boolean shown);

    void bindDataProvider(AbstractDataProvider<Property> dataProvider);

}
