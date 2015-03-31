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
package org.eclipse.che.plugin.yeoman.client.panel;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.base.BaseActionDelegate;

/**
 * Interface of the view panel that will manage the yeoman generator view.
 */
public interface YeomanPartView extends View<YeomanPartView.ActionDelegate> {

    void setTitle(String title);

    void removeItem(YeomanGeneratorType type, String name, GeneratedItemView generatedItemView);

    void clear();

    /**
     * Enable the button
     */
    void enableGenerateButton();

    /**
     * Disable the generate button
     */
    void disableGenerateButton();

    /**
     * Disable spinner on the generate button
     */
    void disableProgressOnGenerateButton();

    /**
     * Enable spinner on the generate button
     */
    void enableProgressOnGenerateButton();


    void addFoldingPanel(FoldingPanel foldingPanel);
    void removeFoldingPanel(FoldingPanel foldingPanel);


    public interface ActionDelegate extends BaseActionDelegate {

        void addItem(String name, YeomanGeneratorType type);

        void generate();

        void removeItem(YeomanGeneratorType type, String name, GeneratedItemView itemView);

    }
}
