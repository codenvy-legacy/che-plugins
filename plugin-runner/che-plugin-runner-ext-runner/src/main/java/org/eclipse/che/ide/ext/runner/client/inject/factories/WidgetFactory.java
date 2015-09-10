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
package org.eclipse.che.ide.ext.runner.client.inject.factories;

import org.eclipse.che.ide.ext.runner.client.manager.button.ButtonWidget;
import org.eclipse.che.ide.ext.runner.client.manager.info.MoreInfo;
import org.eclipse.che.ide.ext.runner.client.manager.menu.MenuWidget;
import org.eclipse.che.ide.ext.runner.client.manager.menu.entry.MenuEntry;
import org.eclipse.che.ide.ext.runner.client.models.Environment;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.tabs.console.button.ConsoleButton;
import org.eclipse.che.ide.ext.runner.client.tabs.console.panel.Console;
import org.eclipse.che.ide.ext.runner.client.tabs.console.panel.FullLogMessageWidget;
import org.eclipse.che.ide.ext.runner.client.tabs.container.tab.Background;
import org.eclipse.che.ide.ext.runner.client.tabs.container.tab.TabType;
import org.eclipse.che.ide.ext.runner.client.tabs.container.tab.TabWidget;
import org.eclipse.che.ide.ext.runner.client.tabs.history.runner.RunnerWidget;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.button.PropertyButtonWidget;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.PropertiesPanel;
import org.eclipse.che.ide.ext.runner.client.tabs.templates.environment.EnvironmentWidget;
import org.eclipse.che.ide.ext.runner.client.tabs.terminal.panel.Terminal;
import org.eclipse.che.ide.ext.runner.client.util.annotations.EnvironmentProperties;
import org.eclipse.che.ide.ext.runner.client.util.annotations.RunnerProperties;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.validation.constraints.NotNull;

/**
 * The factory for creating an instances of the widget.
 *
 * @author Dmitry Shnurenko
 */
public interface WidgetFactory {

    /**
     * Creates button widget with special icon.
     *
     * @param prompt
     *         prompt for current button which is displayed on special popup widget
     * @param resource
     *         icon which need set to button
     * @return an instance of {@link ButtonWidget}
     */
    @NotNull
    ButtonWidget createButton(@NotNull String prompt, @NotNull SVGResource resource);

    /**
     * Creates console button widget with special icon.
     *
     * @param prompt
     *         prompt for current button which is displayed on special popup widget
     * @param resource
     *         icon which need set to button
     * @return an instance of {@link ConsoleButton}
     */
    @NotNull
    ConsoleButton createConsoleButton(@NotNull String prompt, @NotNull SVGResource resource);

    /**
     * Creates tab widget with special title.
     *
     * @param title
     *         title which need set to widget's special place
     * @param tabType
     *         enum which contains values of height and width
     * @return an instance of {@link TabWidget}
     */
    @NotNull
    TabWidget createTab(@NotNull String title, @NotNull TabType tabType);

    /**
     * Creates runner widget.
     *
     * @return an instance of {@link RunnerWidget}
     */
    @NotNull
    RunnerWidget createRunner();

    /**
     * Creates environment widget.
     *
     * @return an instance of {@link EnvironmentWidget}
     */
    @NotNull
    EnvironmentWidget createEnvironment();

    /**
     * Creates a console widget for a given runner.
     *
     * @param runner
     *         runner that needs to be bound with a widget
     * @return an instance of {@link org.eclipse.che.ide.ext.runner.client.tabs.console.panel.Console}
     */
    @NotNull
    Console createConsole(@NotNull Runner runner);

    /**
     * Creates terminal widget.
     *
     * @return an instance of {@link Terminal}
     */
    @NotNull
    Terminal createTerminal();

    /**
     * Creates a properties panel widget for a given runner.
     *
     * @param runner
     *         runner that needs to be bound with a widget
     * @return an instance of {@link PropertiesPanel}
     */
    @NotNull
    @RunnerProperties
    PropertiesPanel createPropertiesPanel(@NotNull Runner runner);

    /**
     * Creates a properties panel widget for a given environment.
     *
     * @param environment
     *         environment that needs to be bound with a widget
     * @return an instance of {@link PropertiesPanel}
     */
    @NotNull
    @EnvironmentProperties
    PropertiesPanel createPropertiesPanel(@NotNull Environment environment);

    /**
     * Creates stab of properties panel widget
     *
     * @return an instance of {@link PropertiesPanel}
     */
    @NotNull
    PropertiesPanel createPropertiesPanel();

    /**
     * Creates more info popup widget.
     *
     * @return an instance of {@link MoreInfo}
     */
    @NotNull
    MoreInfo createMoreInfo();

    /**
     * Creates message widget that need to be displayed in the console.
     *
     * @param logUrl
     *         url where full log is located
     * @return an instance of {@link FullLogMessageWidget}
     */
    @NotNull
    FullLogMessageWidget createFullLogMessage(@NotNull String logUrl);

    /**
     * Creates property button widget.
     *
     * @param title
     *         title of button
     * @param background
     *         background of button
     * @return an instance of {@link org.eclipse.che.ide.ext.runner.client.tabs.properties.button.PropertyButtonWidget}
     */
    @NotNull
    PropertyButtonWidget createPropertyButton(@NotNull String title, @NotNull Background background);

    /**
     * Creates menu widget on which we can add different entities to control panel displaying.
     *
     * @return an instance of {@link MenuWidget}
     */
    @NotNull
    MenuWidget createMenuWidget();

    /**
     * Creates entry widget which will be displayed in {@link MenuWidget}
     *
     * @param entryName
     *         name which need set to entry
     * @return an instance of {@link MenuEntry}
     */
    @NotNull
    MenuEntry createMenuEntry(@NotNull String entryName);

}