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
package org.eclipse.che.ide.ext.datasource.client.sqllauncher;

import java.util.Collection;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.ext.datasource.shared.MultipleRequestExecutionMode;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Interface for the SQL editor view component.
 * 
 * @author "Mickaël Leduque"
 */
public interface SqlRequestLauncherView extends View<SqlRequestLauncherView.ActionDelegate> {

    /** Change the displayed value of the request result limit. */
    void setResultLimit(int newResultLimit);

    /** Returns the zone in which the SQL editor is to be shown. */
    AcceptsOneWidget getEditorZone();

    /** Replaces the items in the datasources list. */
    void setDatasourceList(Collection<String> datasourceIds);

    /** Add a request result block in the result zone. */
    void appendResult(Widget widget);

    /** Removes the contents of the result zone. */
    void clearResultZone();

    /** Sets the value in the execution mode input. */
    void setExecutionMode(MultipleRequestExecutionMode executionMode);

    /** Required for delegating functions in view. */
    public interface ActionDelegate {

        /** Reaction to the change of the datasource input value. */
        void datasourceChanged(String newDataSourceId);

        /** Reaction to the change of the result limit input value. */
        void resultLimitChanged(String newResultLimitString);

        /** Reaction to the execution click. */
        void executeRequested();

        /** Reaction to the change of the execution mode input value. */
        void executionModeChanged(MultipleRequestExecutionMode oneByOne);

        /** Removes the contents of the result zone. */
        void clearResults();
    }
}
