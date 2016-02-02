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
package org.eclipse.che.ide.extension.maven.server.core.project;

import org.eclipse.che.ide.extension.maven.server.MavenServerManager;
import org.eclipse.che.ide.extension.maven.server.MavenServerWrapper;
import org.eclipse.che.maven.data.MavenArtifact;
import org.eclipse.che.maven.data.MavenConstants;
import org.eclipse.che.maven.data.MavenKey;
import org.eclipse.che.maven.data.MavenModel;
import org.eclipse.che.maven.data.MavenPlugin;
import org.eclipse.che.maven.data.MavenProjectProblem;
import org.eclipse.che.maven.data.MavenRemoteRepository;
import org.eclipse.che.maven.data.MavenResource;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * @author Evgen Vidolob
 */
public class MavenProject {

    private volatile Info info = new Info();


    public MavenKey getParentKey() {
        return info.parentKey;
    }

    public MavenKey getMavenKey() {
        return info.mavenKey;
    }

    public String getPackaging() {
        return info.packaging;
    }

    public Properties getProperties() {
        return info.properties;
    }

    public List<String> getSources() {
        return info.sources;
    }

    public List<MavenResource> getResources() {
        return info.resources;
    }

    public List<String> getTestSources() {
        return info.testSources;
    }

    public List<MavenResource> getTestResources() {
        return info.testResources;
    }

    public String getSourceLevel() {
        //todo
        throw new UnsupportedOperationException();
    }

    public String getTargetLevel() {
        //todo
        throw new UnsupportedOperationException();
    }

    public List<String> getModules() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public List<MavenPlugin> getPlugins() {
        return info.plugins;
    }

    public List<MavenProjectProblem> getProblems() {
        //TODO
        throw new UnsupportedOperationException();
    }


    public List<MavenArtifact> getDependencies() {
        return info.dependencies;
    }

    /**
     * Invoke maven to build project model.
     * @param project to resolve
     * @param mavenServer the maven server
     * @return the modification types that applied to this project
     */
    public MavenProjectModifications resolve(IProject project, MavenServerWrapper mavenServer, MavenServerManager serverManager) {
        MavenModelReader reader = new MavenModelReader();

        MavenModelReaderResult modelReaderResult =
                reader.resolveMavenProject(getPom(project), mavenServer, info.activeProfiles, info.inactiveProfiles, serverManager);

        return setModel(modelReaderResult, modelReaderResult.getProblems().isEmpty(), false);
    }

    public MavenProjectModifications read(IProject project, MavenServerManager serverManager) {
        MavenModelReader reader = new MavenModelReader();
        return setModel(reader.readMavenProject(getPom(project), serverManager), false, true);
    }

    private MavenProjectModifications setModel(MavenModelReaderResult readerResult, boolean clearArtifacts, boolean clearProfiles) {
        Info newInfo = info.clone();
        newInfo.problems = readerResult.getProblems();
        newInfo.activeProfiles = readerResult.getActiveProfiles();
        MavenModel model = readerResult.getMavenModel();
        newInfo.mavenKey = model.getMavenKey();
        if (model.getParent() != null) {
            newInfo.parentKey = model.getParent().getMavenKey();
        }

        newInfo.packaging = model.getPackaging();

        newInfo.sources = model.getBuild().getSources();
        newInfo.testSources = model.getBuild().getTestSources();
        newInfo.resources = model.getBuild().getResources();
        newInfo.testResources = model.getBuild().getTestResources();
        newInfo.properties = model.getProperties();

        Set<MavenRemoteRepository> remoteRepositories = new HashSet<>();
        Set<MavenArtifact> extensions = new HashSet<>();
        Set<MavenArtifact> dependencies = new HashSet<>();
        Set<MavenPlugin> plugins = new HashSet<>();
        Set<MavenKey> unresolvedArtifacts = new HashSet<>();

        if (!clearArtifacts) {
            if (info.remoteRepositories != null) {
                remoteRepositories.addAll(info.remoteRepositories);
            }
            if (info.extensions != null) {
                extensions.addAll(info.extensions);
            }
            if (info.dependencies != null) {
                dependencies.addAll(info.dependencies);
            }
            if (info.plugins != null) {
                plugins.addAll(info.plugins);
            }
            if (info.unresolvedArtifacts != null) {
                unresolvedArtifacts.addAll(info.unresolvedArtifacts);
            }
        }

        remoteRepositories.addAll(model.getRemoteRepositories());
        extensions.addAll(model.getExtensions());
        dependencies.addAll(model.getDependencies());
        plugins.addAll(model.getPlugins());
        unresolvedArtifacts.addAll(readerResult.getUnresolvedArtifacts());

        newInfo.remoteRepositories = new ArrayList<>(remoteRepositories);
        newInfo.extensions = new ArrayList<>(extensions);
        newInfo.dependencies = new ArrayList<>(dependencies);
        newInfo.plugins = new ArrayList<>(plugins);
        newInfo.unresolvedArtifacts = unresolvedArtifacts;
        //TODO add profiles

        return setInfo(newInfo);
    }

    private MavenProjectModifications setInfo(Info newInfo) {
        MavenProjectModifications modifications = info.generateChanges(newInfo);
        info = newInfo;
        return modifications;
    }


    private File getPom(IProject project) {
        IFile file = project.getFile(MavenConstants.POM_FILE_NAME);
        if (file == null) {
            return null;
        }

        return file.getFullPath().toFile();
    }


    private static class Info implements Cloneable {
        public MavenKey mavenKey;
        public MavenKey parentKey;

        public String packaging;

        public Properties properties;

        public List<String>        sources;
        public List<String>        testSources;
        public List<MavenResource> resources;
        public List<MavenResource> testResources;

        public List<String> activeProfiles;
        public List<String> inactiveProfiles;

        public List<MavenArtifact>         dependencies;
        public List<MavenArtifact>         extensions;
        public List<MavenPlugin>           plugins;
        public List<MavenProjectProblem>   problems;
        public List<MavenRemoteRepository> remoteRepositories;

        public Set<MavenKey> unresolvedArtifacts;

        public Info clone() {
            try {
                Info newInfo = (Info)super.clone();
                return newInfo;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        public MavenProjectModifications generateChanges(Info newInfo) {
            MavenProjectModifications result = new MavenProjectModifications();
            result.setPackaging(!Objects.equals(packaging, newInfo.packaging));
            result.setSources(!Objects.equals(sources, newInfo.sources)
                              || !Objects.equals(resources, newInfo.resources)
                              || !Objects.equals(testSources, newInfo.testSources)
                              || !Objects.equals(testResources, newInfo.testResources));

            result.setDependencies(!Objects.equals(dependencies, newInfo.dependencies));
            result.setPlugins(!Objects.equals(plugins, newInfo.plugins));

            return result;
        }
    }

}
