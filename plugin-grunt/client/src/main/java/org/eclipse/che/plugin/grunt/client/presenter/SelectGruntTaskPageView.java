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

package org.eclipse.che.plugin.grunt.client.presenter;

import org.eclipse.che.ide.api.mvp.View;
import com.google.inject.ImplementedBy;

import java.util.Collection;

/**
 * @author Florent Benoit
 */
@ImplementedBy(SelectGruntTaskPageViewImpl.class)
public interface SelectGruntTaskPageView extends View<SelectGruntTaskPageView.ActionDelegate> {

    void setTaskNames(Collection<String> taskNames);

    void selectTask(String taskName);

    void showDialog();

    /** Close dialog. */
    void close();


    public interface ActionDelegate{
        void taskSelected(String taskName);

        void onCancelClicked();

        void onStartRunClicked();

    }
}
