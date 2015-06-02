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
package org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.info;

import com.google.inject.ImplementedBy;

import org.eclipse.che.ide.api.parts.PartStackView;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.tab.container.TabContainerView;

import javax.annotation.Nonnull;

/**
 * Provides methods to control view representation of info container.
 *
 * @author Dmitry Shnurenko
 */
@ImplementedBy(InfoContainerViewImpl.class)
public interface InfoContainerView extends PartStackView {

    /**
     * Adds tab container to main view.
     *
     * @param tabContainer
     *         container which need add
     */
    void addContainer(@Nonnull TabContainerView tabContainer);
}
