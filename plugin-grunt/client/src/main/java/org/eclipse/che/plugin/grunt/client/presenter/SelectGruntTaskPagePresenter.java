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

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.runner.dto.RunOptions;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.wizard.AbstractWizardPage;
import org.eclipse.che.ide.api.wizard.Wizard;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.runner.client.manager.RunnerManagerPresenter;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.StringUnmarshaller;
import org.eclipse.che.ide.util.loging.Log;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Presenter for displaying the grunt tasks
 * @author Florent Benoit
 */
@Singleton
public class SelectGruntTaskPagePresenter extends AbstractWizardPage implements SelectGruntTaskPageView.ActionDelegate {

    private SelectGruntTaskPageView view;
    private RunnerManagerPresenter  runnerManagerPresenter;
    private DtoFactory              dtoFactory;
    private String                  taskSelected;
    private ProjectServiceClient    projectServiceClient;
    private AppContext              appContext;
    private NotificationManager     notificationManager;

    /**
     * Create wizard page
     */
    @Inject
    public SelectGruntTaskPagePresenter(SelectGruntTaskPageView view,
                                        RunnerManagerPresenter runnerManagerPresenter,
                                        ProjectServiceClient projectServiceClient,
                                        DtoFactory dtoFactory,
                                        NotificationManager notificationManager,
                                        AppContext appContext
                                       ) {
//        super("Select Grunt Task", null);
        this.view = view;
        this.runnerManagerPresenter = runnerManagerPresenter;
        this.dtoFactory = dtoFactory;
        this.projectServiceClient = projectServiceClient;
        this.appContext = appContext;
        this.notificationManager = notificationManager;
        view.setDelegate(this);
    }



    @Override
    public boolean isCompleted() {
        return true;
    }


    @Override
    public void setUpdateDelegate(@NotNull Wizard.UpdateDelegate delegate) {
        super.setUpdateDelegate(delegate);
        requestTasks();
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
        requestTasks();
    }

    protected void requestTasks() {

        CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            return;
        }

        String projectPath = currentProject.getProjectDescription().getPath();
        if (!projectPath.endsWith("/")) {
            projectPath += "/";
        }

        projectServiceClient.getFileContent(projectPath + "Gruntfile.js", new AsyncRequestCallback<String>(new StringUnmarshaller()) {
            @Override
            protected void onSuccess(String content) {

                Log.info(SelectGruntTaskPagePresenter.class, "content is" + content);

                List<String> tasks = new ArrayList<>();

                // needs to parse
                RegExp regExp = RegExp.compile("grunt.registerTask\\('(.*?)',", "g");
                MatchResult matchResult = regExp.exec(content);
                while (matchResult != null) {
                    int groupCount = matchResult.getGroupCount();
                    if (groupCount == 2) {
                        tasks.add(matchResult.getGroup(1));
                    }
                    matchResult = regExp.exec(content);
                }
                view.setTaskNames(tasks);

            }

            @Override
            protected void onFailure(Throwable throwable) {
                // cancel the view
                onCancelClicked();
                Log.error(SelectGruntTaskPagePresenter.class, throwable);
                notificationManager.showNotification(new Notification("No GruntFile found", Notification.Type.ERROR));
            }
        });
    }

    public void showDialog() {
        requestTasks();
        view.showDialog();
    }

    @Override
    public void taskSelected(String taskName) {
        this.taskSelected = taskName;
    }

    @Override
    public void onCancelClicked() {
        view.close();

    }

    @Override
    public void onStartRunClicked() {
        view.close();
        Map<String, String> options = new HashMap<>();
        options.put("taskname", taskSelected);
        RunOptions runOptions = dtoFactory.createDto(RunOptions.class).withSkipBuild(true).withOptions(options);
        runnerManagerPresenter.launchRunner(runOptions);
    }

}
