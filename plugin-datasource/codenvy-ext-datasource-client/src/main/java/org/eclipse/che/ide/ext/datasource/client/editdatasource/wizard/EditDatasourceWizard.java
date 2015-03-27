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
package org.eclipse.che.ide.ext.datasource.client.editdatasource.wizard;

import org.eclipse.che.ide.ext.datasource.client.events.DatasourceListChangeEvent;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseConfigurationDTO;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;

public class EditDatasourceWizard extends InitializableWizard<DatabaseConfigurationDTO> {

    private final EventBus eventBus;

    @Inject
    public EditDatasourceWizard(final EventBus eventBus,
                                @Assisted DatabaseConfigurationDTO dataObject) {
        super(dataObject);
        this.eventBus = eventBus;
    }

    @Override
    public void complete(@Nonnull CompleteCallback callback) {
        this.eventBus.fireEvent(new DatasourceListChangeEvent());
    }
}
