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
package org.eclipse.che.ide.extension.maven.client.build;

import org.eclipse.che.api.builder.dto.BuildOptions;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.extension.builder.client.build.BuildController;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Presenter for customizing building of Maven project.
 *
 * @author Artem Zatsarynnyy
 * @author Oleksii Orel
 */
@Singleton
public class MavenBuildPresenter implements MavenBuildView.ActionDelegate {
    private final BuildController buildController;
    private       MavenBuildView  view;
    private       DtoFactory      dtoFactory;

    /** Create presenter. */
    @Inject
    protected MavenBuildPresenter(BuildController buildController, DtoFactory dtoFactory, MavenBuildView view) {
        this.view = view;
        this.view.setDelegate(this);
        this.view.setBuildCommand("clean install");
        this.dtoFactory = dtoFactory;
        this.buildController = buildController;
    }

    public void showDialog() {
        view.showDialog();
    }

    @Override
    public void onStartBuildClicked() {
        buildController.buildActiveProject(getBuildOptions(), true);
        view.close();
    }

    private BuildOptions getBuildOptions() {//TODO : need create smarter parser for command line
        BuildOptions buildOptions = dtoFactory.createDto(BuildOptions.class);
        buildOptions.setSkipTest(view.isSkipTestSelected());
        String buildCommand = view.getBuildCommand();
        if (buildCommand != null && !buildCommand.isEmpty()) {
            Map<String, String> options = new HashMap<>();
            List<String> targets = new ArrayList<>();

            String[] splited = buildCommand.split("\\s+");
            for (int i = 0; i < splited.length; i++) {
                String str = splited[i];
                if (str.startsWith("-")) {
                    if (str.contains("=")) {
                        String[] split = str.split("=");
                        options.put(split[0], split[1]);
                    } else
                        options.put(str, null);
                } else {
                    targets.add(str);
                }
            }
            buildOptions.setOptions(options);
            buildOptions.setTargets(targets);
        }
        return buildOptions;
    }

    @Override
    public void onCancelClicked() {
        view.close();
    }

    @Override
    public void onSkipTestValueChange(ValueChangeEvent<Boolean> event) {
        if (event.getValue()) {
            String command = view.getBuildCommand().concat(" -Dmaven.test.skip");
            view.setBuildCommand(command.replaceAll("\\s+", " "));
        } else {
            String buildCommand = view.getBuildCommand();
            view.setBuildCommand(buildCommand.replaceAll("-Dmaven.test.skip", "").replaceAll("\\s+", " ")); //TODO: need improve it
        }
    }

    @Override
    public void onUpdateSnapshotValueChange(ValueChangeEvent<Boolean> event) {
        if (event.getValue()) {
            String command = view.getBuildCommand().concat(" -U");
            view.setBuildCommand(command.replaceAll("\\s+", " "));
        } else {
            String buildCommand = view.getBuildCommand();
            view.setBuildCommand(
                    buildCommand.replaceAll("-U", "").replaceAll("--update-snapshots", "").replaceAll("\\s+", " ")); //TODO: need improve it
        }
    }

    @Override
    public void onOfflineValueChange(ValueChangeEvent<Boolean> event) {
        if (event.getValue()) {
            String command = view.getBuildCommand().concat(" -o");
            view.setBuildCommand(command.replaceAll("\\s+", " "));
        } else {
            String buildCommand = view.getBuildCommand();
            view.setBuildCommand(
                    buildCommand.replaceAll("-o", "").replaceAll("--offline", "").replaceAll("\\s+", " ")); //TODO: need improve it
        }
    }
}