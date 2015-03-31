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

import org.eclipse.che.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Florent Benoit
 */
@Singleton
public class SelectGruntTaskPageViewImpl extends Window implements SelectGruntTaskPageView {

    private static SelectGruntTaskViewImplUiBinder uiBinder = GWT.create(SelectGruntTaskViewImplUiBinder.class);

    @UiField
    ListBox tasksBox;

    Button btnCancel;

    Button btnRun;


    private ActionDelegate delegate;

    private List<String> taskNames = new ArrayList<>();

    @Inject
    public SelectGruntTaskPageViewImpl() {
        Widget widget = uiBinder.createAndBindUi(this);

        this.setTitle("Custom Grunt Runner");
        this.setWidget(widget);

        createButtons();

    }

    /**
     * this method called when user close Window
     */
    @Override
    protected void onClose() {

    }

    private void createButtons() {
        btnCancel = createButton("Cancel", "project-customGruntRun-cancel", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        getFooter().add(btnCancel);

        btnRun = createButton("Run", "project-customGruntRun-startRun", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onStartRunClicked();
            }
        });
        getFooter().add(btnRun);
    }


    @UiHandler("tasksBox")
    void runnerChanged(ChangeEvent event) {
        delegate.taskSelected(tasksBox.getValue(tasksBox.getSelectedIndex()));
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }


    @Override
    public void setTaskNames(Collection<String> taskNames) {
        this.taskNames.clear();
        this.tasksBox.clear();
        this.taskNames.addAll(taskNames);
        for (String taskName : taskNames) {
            tasksBox.addItem(taskName, taskName);
        }
    }

    @Override
    public void selectTask(String runnerName) {
        tasksBox.setSelectedIndex(taskNames.indexOf(runnerName));
        runnerChanged(null);
    }

    @Override
    public void showDialog() {
        this.show();
    }

    /** Close dialog. */
    @Override
    public void close() {
        this.hide();
    }

    interface SelectGruntTaskViewImplUiBinder
            extends UiBinder<DockLayoutPanel, SelectGruntTaskPageViewImpl> {
    }
}