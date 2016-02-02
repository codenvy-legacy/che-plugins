/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.extension.maven.server.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.extension.maven.server.MavenServerManager;
import org.eclipse.che.ide.extension.maven.server.MavenServerWrapper;
import org.eclipse.che.ide.extension.maven.server.core.project.MavenProject;
import org.eclipse.che.ide.extension.maven.server.core.project.MavenProjectModifications;
import org.eclipse.che.maven.data.MavenKey;
import org.eclipse.che.maven.data.MavenWorkspaceCache;
import org.eclipse.che.maven.server.MavenProgressNotifier;
import org.eclipse.che.maven.server.MavenTerminal;
import org.eclipse.core.resources.IProject;

import java.io.File;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Holds all maven projects in workspace
 *
 * @author Evgen Vidolob
 */
@Singleton
public class MavenProjectManager {

    private final MavenWorkspaceCache         mavenWorkspaceCache;
    private final Map<MavenKey, MavenProject> keyToProjectMap;
    private final Map<File, MavenProject>     pomToProjectMap;

    private final List<MavenProjectListener> listeners = new CopyOnWriteArrayList<>();

    private final MavenServerManager    serverManager;
    private final MavenTerminal         terminal;
    private final MavenProgressNotifier mavenNotifier;

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock                   readLock      = readWriteLock.readLock();
    private final Lock                   writeLock     = readWriteLock.writeLock();

    private final MavenProjectListener dispatcher;

    @Inject
    public MavenProjectManager(MavenServerManager serverManager, MavenTerminal terminal, MavenProgressNotifier mavenNotifier) {
        this.serverManager = serverManager;
        this.terminal = terminal;
        this.mavenNotifier = mavenNotifier;
        mavenWorkspaceCache = new MavenWorkspaceCache();
        keyToProjectMap = new HashMap<>();
        pomToProjectMap = new HashMap<>();
        dispatcher = createListenersDispatcher();
    }

    private MavenProjectListener createListenersDispatcher() {
        return (MavenProjectListener)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                                                            new Class[] {MavenProjectListener.class},
                                                            (proxy, method, args) -> {
                                                                for (MavenProjectListener listener : listeners) {
                                                                    method.invoke(listener, args);
                                                                }
                                                                return null;
                                                            });
    }

    public void addListener(MavenProjectListener listener) {
        listeners.add(listener);
    }

    public void removeListener(MavenProjectListener listener) {
        listeners.remove(listener);
    }

    public void resolveMavenProject(IProject project, MavenProject mavenProject) {
        MavenServerWrapper mavenServer = serverManager.createMavenServer();
        try {
            mavenServer.customize(copyWorkspaceCache(), terminal, mavenNotifier, false, true);
            MavenProjectModifications modifications = mavenProject.resolve(project, mavenServer, serverManager);
            dispatcher.projectResolved(mavenProject, modifications);

        } finally {
            mavenServer.reset();
        }

    }

    /**
     * Update all projects in workspace
     */
    public void udateWorkspace() {
        //TODO
    }

    public void update(List<IProject> projects, boolean recursive) {
        if (projects.isEmpty()) {
            return;
        }


    }

    private MavenWorkspaceCache copyWorkspaceCache() {
        readLock.lock();
        try {
            return mavenWorkspaceCache.copy();
        } finally {
            readLock.unlock();
        }
    }

    private class UpdateState {
        Map<MavenProject, MavenProjectModifications> projectWithModification = new HashMap<>();
    }
}
