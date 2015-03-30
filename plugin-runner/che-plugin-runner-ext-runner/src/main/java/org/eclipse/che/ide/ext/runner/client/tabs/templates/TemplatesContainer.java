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
package org.eclipse.che.ide.ext.runner.client.tabs.templates;

import com.google.inject.ImplementedBy;

import org.eclipse.che.api.project.shared.dto.RunnerEnvironmentTree;
import org.eclipse.che.ide.ext.runner.client.models.Environment;
import org.eclipse.che.ide.ext.runner.client.tabs.common.TabPresenter;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides methods which allow work with templates panel.
 *
 * @author Dmitry Shnurenko
 */
@ImplementedBy(TemplatesPresenter.class)
public interface TemplatesContainer extends TabPresenter {
    /**
     * Calls special method on view which set current environment selected.
     *
     * @param environment
     *         environment which was selected
     */
    void select(@Nullable Environment environment);

    /**
     * Calls method on view which adds environment widget on templates panel.
     *
     * @param tree
     *         list of environments which need add
     * @param scope
     *         scope of environments which are saved in list
     */
    void addEnvironments(@Nonnull RunnerEnvironmentTree tree, @Nonnull Scope scope);

    /**
     * Adds language type item in special lis box on templates panel.
     *
     * @param item
     *         item which need set to special list box on templates panel
     */
    void setTypeItem(@Nonnull String item);

    /** Shows environments when user click on templates tab the first time. */
    void showEnvironments();

    /** Selects environment when we click on configs tab. */
    void selectEnvironment();

    /** Changes enable state run button */
    void changeEnableStateRunButton();

}