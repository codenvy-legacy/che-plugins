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
package org.eclipse.che.ide.extension.maven.server.core;

import org.eclipse.che.api.core.util.Cancellable;
import org.eclipse.che.api.core.util.CancellableProcessWrapper;
import org.eclipse.che.api.core.util.ListLineConsumer;
import org.eclipse.che.api.core.util.ProcessUtil;
import org.eclipse.che.api.core.util.StreamPump;
import org.eclipse.che.api.core.util.Watchdog;
import org.eclipse.che.dto.server.DtoFactory;
import org.eclipse.che.ide.ext.java.server.classpath.ClassPathBuilder;
import org.eclipse.che.ide.ext.java.shared.dto.ClassPathBuilderResult;
import org.eclipse.che.ide.maven.tools.MavenUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of classpath building for the Maven.
 *
 * @author Valeriy Svydenko
 */
public class MavenClassPathBuilder implements ClassPathBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(MavenClassPathBuilder.class);

    private final ResourcesPlugin resourcesPlugin;

    @Inject
    public MavenClassPathBuilder(ResourcesPlugin resourcesPlugin) {
        this.resourcesPlugin = resourcesPlugin;
        JavaModelManager.getJavaModelManager().containerInitializersCache.put(MavenClasspathContainer.CONTAINER_ID,
                                                                              new MavenClasspathContainerInitializer());
    }

    /** {@inheritDoc} */
    @Override
    public ClassPathBuilderResult buildClassPath(String projectPath) {
        IJavaProject javaProject = JavaModelManager.getJavaModelManager().getJavaModel().getJavaProject(projectPath);
        ClassPathBuilderResult result = dependencyUpdateProcessor(projectPath);
        if (ClassPathBuilderResult.Status.SUCCESS.equals(result.getStatus())) {
            IClasspathContainer container = MavenClasspathUtil.readMavenClasspath(javaProject);
            try {
                JavaCore.setClasspathContainer(container.getPath(),
                                               new IJavaProject[]{javaProject},
                                               new IClasspathContainer[]{container},
                                               null);
            } catch (JavaModelException e) {
                LOG.error(e.getMessage(), e);
                e.printStackTrace();
            }
        }
        return result;
    }

    private ClassPathBuilderResult dependencyUpdateProcessor(String projectPath) {
        String command = MavenUtils.getMavenExecCommand();
        File projectDir = new File(projectPath);

        ProcessBuilder classPathProcessBuilder = new ProcessBuilder().command(command,
                                                                              "dependency:build-classpath",
                                                                              "-Dmdep.outputFile=.codenvy/classpath.maven")
                                                                     .directory(projectDir)
                                                                     .redirectErrorStream(true);

        ClassPathBuilderResult result = executeBuilderProcess(projectPath, classPathProcessBuilder);
        if (ClassPathBuilderResult.Status.SUCCESS.equals(result.getStatus())) {
            ProcessBuilder sourcesProcessBuilder = new ProcessBuilder().command(command,
                                                                                "dependency:sources",
                                                                                "-Dclassifier=sources")
                                                                       .directory(projectDir)
                                                                       .redirectErrorStream(true);
            result = executeBuilderProcess(projectPath, sourcesProcessBuilder);
        }

        return result;
    }

    private ClassPathBuilderResult executeBuilderProcess(final String projectPath, ProcessBuilder processBuilder) {
        StreamPump output = null;
        Watchdog watcher = null;
        ClassPathBuilderResult classPathBuilderResult = DtoFactory.newDto(ClassPathBuilderResult.class);
        int timeout = 10; //10 minutes
        int result = -1;
        try {
            Process process = processBuilder.start();

            watcher = new Watchdog("Maven classpath" + "-WATCHDOG", timeout, TimeUnit.MINUTES);
            watcher.start(new CancellableProcessWrapper(process, new Cancellable.Callback() {
                @Override
                public void cancelled(Cancellable cancellable) {
                    LOG.warn("Update dependency process has been shutdown due to timeout. Project: " + projectPath);
                }
            }));
            ListLineConsumer lines = new ListLineConsumer();
            output = new StreamPump();
            output.start(process, lines);
            try {
                result = process.waitFor();
                if (process.exitValue() != 0) {
                    classPathBuilderResult.setLogs(lines.getText());
                }
            } catch (InterruptedException e) {
                Thread.interrupted(); // we interrupt thread when cancel task
                ProcessUtil.kill(process);
            }
            try {
                output.await(); // wait for logger
            } catch (InterruptedException e) {
                Thread.interrupted(); // we interrupt thread when cancel task, NOTE: logs may be incomplete
            }
        } catch (IOException e) {
            LOG.error("", e);
        } finally {
            if (watcher != null) {
                watcher.stop();
            }
            if (output != null) {
                output.stop();
            }
        }
        classPathBuilderResult.setStatus(result == 0 ? ClassPathBuilderResult.Status.SUCCESS : ClassPathBuilderResult.Status.ERROR);
        return classPathBuilderResult;
    }
}
