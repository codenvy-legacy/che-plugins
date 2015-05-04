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
package org.eclipse.che.ide.extension.machine.client.command.configuration.edit;

import com.google.gwt.user.client.ui.AcceptsOneWidget;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.extension.machine.client.command.configuration.api.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.configuration.api.CommandType;

import java.util.Map;
import java.util.Set;

/**
 * The view of {@link EditConfigurationsPresenter}.
 *
 * @author Artem Zatsarynnyy
 */
public interface EditConfigurationsView extends View<EditConfigurationsView.ActionDelegate> {

    /** Show view. */
    void show();

    /** Close view. */
    void close();

    /** Returns content panel. */
    AcceptsOneWidget getContentPanel();

    /** Sets available command configurations. */
    void setCommandConfigurations(Map<CommandType, Set<CommandConfiguration>> commandConfigurations);

    /**
     * Select the given {@code configuration}.
     *
     * @param configuration
     *         configuration to select
     */
    void selectConfiguration(CommandConfiguration configuration);

    /** Needs for delegate some function into preferences view. */
    interface ActionDelegate {

        /** Called when 'Close' button is clicked. */
        void onCloseClicked();

        /**
         * Called when some configuration is selected.
         *
         * @param configuration
         *         selected configuration
         */
        void onConfigurationSelected(CommandConfiguration configuration);

        /** Called when 'Add' button is clicked. */
        void onAddClicked();

        /** Called when 'Delete' button is clicked. */
        void onDeleteClicked();

        /** Called when 'Execute' button is clicked. */
        void onExecuteClicked();
    }
}
