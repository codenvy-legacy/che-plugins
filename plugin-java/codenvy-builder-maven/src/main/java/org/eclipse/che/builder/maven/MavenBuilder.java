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
package org.eclipse.che.builder.maven;

import org.eclipse.che.api.builder.BuilderException;
import org.eclipse.che.api.builder.dto.BuildRequest;
import org.eclipse.che.api.builder.dto.BuilderEnvironment;
import org.eclipse.che.api.builder.internal.BuildLogger;
import org.eclipse.che.api.builder.internal.BuildResult;
import org.eclipse.che.api.builder.internal.Builder;
import org.eclipse.che.api.builder.internal.BuilderConfiguration;
import org.eclipse.che.api.builder.internal.BuilderTaskType;
import org.eclipse.che.api.builder.internal.Constants;
import org.eclipse.che.api.builder.internal.DelegateBuildLogger;
import org.eclipse.che.api.builder.internal.SourceManagerEvent;
import org.eclipse.che.api.builder.internal.SourceManagerListener;
import org.eclipse.che.api.builder.internal.SourcesManager;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.core.util.CommandLine;
import org.eclipse.che.commons.json.JsonHelper;
import org.eclipse.che.commons.lang.IoUtil;
import org.eclipse.che.commons.lang.ZipUtils;
import org.eclipse.che.dto.server.DtoFactory;
import org.eclipse.che.ide.maven.tools.MavenArtifact;
import org.eclipse.che.ide.maven.tools.MavenUtils;
import org.eclipse.che.ide.maven.tools.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builder based on Maven.
 *
 * @author andrew00x
 * @author Eugene Voevodin
 */
@Singleton
public class MavenBuilder extends Builder {
    private static final Logger LOG = LoggerFactory.getLogger(MavenBuilder.class);

    /** Rules for builder assembly plugin. Use it for create jar with included dependencies */
    public static final String ASSEMBLY_DESCRIPTOR_FOR_JAR_WITH_DEPENDENCIES = "<assembly>\n" +
                                                                               "  <id>jar-with-dependencies</id>\n" +
                                                                               "  <formats>\n" +
                                                                               "    <format>zip</format>\n" +
                                                                               "  </formats>\n" +
                                                                               "  <includeBaseDirectory>false</includeBaseDirectory>\n" +
                                                                               "  <dependencySets>\n" +
                                                                               "    <dependencySet>\n" +
                                                                               "      <outputDirectory>/lib</outputDirectory>\n" +
                                                                               "      <unpack>false</unpack>\n" +
                                                                               "      <useProjectArtifact>false</useProjectArtifact>\n" +
                                                                               "    </dependencySet>\n" +
                                                                               "  </dependencySets>\n" +
                                                                               "  <files>\n" +
                                                                               "    <file>\n" +
                                                                               "      <source>${project.build.directory}/${project.build.finalName}.jar</source>\n" +
                                                                               "      <outputDirectory>/</outputDirectory>\n" +
                                                                               "      <destName>application.jar</destName>\n" +
                                                                               "    </file>\n" +
                                                                               "  </files>\n" +
                                                                               "</assembly>\n";

    private static final String ASSEMBLY_DESCRIPTOR_FOR_JAR_WITH_DEPENDENCIES_FILE = "jar-with-dependencies-assembly-descriptor.xml";
    private static final String DEPENDENCIES_JSON_FILE                             = "dependencies.json";

    private static final FilenameFilter SOURCES_AND_DOCS_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(java.io.File dir, String name) {
            return !(name.endsWith("-sources.jar") || name.endsWith("-javadoc.jar"));
        }
    };

    private final Map<String, String> mavenProperties;

    @Inject
    public MavenBuilder(@Named(Constants.BASE_DIRECTORY) java.io.File rootDirectory,
                        @Named(Constants.NUMBER_OF_WORKERS) int numberOfWorkers,
                        @Named(Constants.QUEUE_SIZE) int queueSize,
                        @Named(Constants.KEEP_RESULT_TIME) int cleanupTime,
                        EventService eventService) {
        super(rootDirectory, numberOfWorkers, queueSize, cleanupTime, eventService);

        Map<String, String> myMavenProperties = null;
        try {
            myMavenProperties = MavenUtils.getMavenVersionInformation();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        if (myMavenProperties == null) {
            mavenProperties = Collections.emptyMap();
        } else {
            mavenProperties = Collections.unmodifiableMap(myMavenProperties);
        }
    }

    @Override
    public String getName() {
        return "maven";
    }

    @Override
    public String getDescription() {
        return "Apache Maven based builder implementation";
    }

    @Override
    public Map<String, BuilderEnvironment> getEnvironments() {
        final Map<String, BuilderEnvironment> env = new HashMap<>(4);
        final Map<String, String> properties = new HashMap<>(mavenProperties);
        properties.remove("Maven home");
        properties.remove("Java home");
        final BuilderEnvironment def = DtoFactory.getInstance().createDto(BuilderEnvironment.class)
                                                 .withId("default")
                                                 .withIsDefault(true)
                                                 .withDisplayName(properties.get("Maven version"))
                                                 .withProperties(properties);
        env.put(def.getId(), def);
        return env;
    }

    @Override
    protected CommandLine createCommandLine(BuilderConfiguration config) throws BuilderException {
        final CommandLine commandLine = new CommandLine(MavenUtils.getMavenExecCommand());
        commandLine.add("--batch-mode");
        final List<String> targets = config.getTargets();
        final java.io.File workDir = config.getWorkDir();
        switch (config.getTaskType()) {
            case DEFAULT:
                if (!targets.isEmpty()) {
                    commandLine.add(targets);
                } else {
                    commandLine.add("clean", "install");
                }
                if (((BuildRequest)config.getRequest()).isSkipTest()) {
                    commandLine.add("-Dmaven.test.skip");
                }
                if (config.getRequest().isIncludeDependencies()) {
                    // Project sources isn't available yet. Postpone parsing of pom.xml file until sources becomes available.
                    final SourcesManager sourcesManager = getSourcesManager();
                    final SourceManagerListener sourceListener = new SourceManagerListener() {
                        @Override
                        public void afterDownload(SourceManagerEvent event) {
                            if (workDir.equals(event.getWorkDir())) {
                                try {
                                    final Model model = Model.readFrom(workDir);
                                    final String packaging = model.getPackaging();
                                    if ((packaging == null || "jar".equals(packaging)) && !MavenUtils.isCodenvyExtensionProject(model)) {
                                        addJarWithDependenciesAssemblyDescriptor(workDir, commandLine);
                                    }
                                } catch (Exception e) {
                                    throw new IllegalStateException(e);
                                } finally {
                                    sourcesManager.removeListener(this);
                                }
                            }
                        }
                    };
                    sourcesManager.addListener(sourceListener);
                }
                break;
            case LIST_DEPS:
                if (!targets.isEmpty()) {
                    LOG.warn("Targets {} ignored when list dependencies", targets);
                }
                commandLine.add("clean", "dependency:list");
                break;
            case COPY_DEPS:
                if (!targets.isEmpty()) {
                    LOG.warn("Targets {} ignored when copy dependencies", targets);
                }
                commandLine.add("clean", "dependency:copy-dependencies").addPair("-Dmdep.failOnMissingClassifierArtifact", "false");
                break;
        }
        commandLine.add(config.getOptions());
        return commandLine;
    }

    private void addJarWithDependenciesAssemblyDescriptor(java.io.File workDir, CommandLine commandLine) throws IOException {
        Files.write(new java.io.File(workDir, ASSEMBLY_DESCRIPTOR_FOR_JAR_WITH_DEPENDENCIES_FILE).toPath(),
                    ASSEMBLY_DESCRIPTOR_FOR_JAR_WITH_DEPENDENCIES.getBytes());
        commandLine.add("assembly:single");
        commandLine.addPair("-Ddescriptor", ASSEMBLY_DESCRIPTOR_FOR_JAR_WITH_DEPENDENCIES_FILE);
    }

    @Override
    protected BuildResult getTaskResult(FutureBuildTask task, boolean successful) throws BuilderException {
        if (!successful) {
            return new BuildResult(false, getBuildReport(task));
        }

        if (!isMavenTaskSuccess(task)) {
            return new BuildResult(false, getBuildReport(task));
        }

        final BuilderConfiguration config = task.getConfiguration();
        final java.io.File workDir = config.getWorkDir();
        final BuildResult result = new BuildResult(true, getBuildReport(task));
        java.io.File[] files = null;
        switch (config.getTaskType()) {
            case DEFAULT:
                final Model model;
                try {
                    model = Model.readFrom(workDir);
                } catch (IOException e) {
                    throw new BuilderException(e);
                }
                String packaging = model.getPackaging();
                if (packaging == null) {
                    packaging = "jar";
                }
                if (packaging.equals("pom")) {
                    final List<Model> modules;
                    final List<java.io.File> results = new LinkedList<>();
                    try {
                        modules = MavenUtils.getModules(workDir);
                    } catch (IOException e) {
                        throw new BuilderException(e);
                    }
                    for (Model child : modules) {
                        String childPackaging = child.getPackaging();
                        if (childPackaging == null) {
                            childPackaging = "jar";
                        }
                        final String fileExt;
                        String ext = MavenUtils.getFileExtensionByPackaging(childPackaging);
                        if (ext == null) {
                            ext = '.' + childPackaging;
                        }
                        fileExt = ext;
                        final java.io.File[] a = new java.io.File(child.getProjectDirectory(), "target").listFiles(new FilenameFilter() {
                            @Override
                            public boolean accept(java.io.File dir, String name) {
                                return SOURCES_AND_DOCS_FILTER.accept(dir, name) && name.endsWith(fileExt);
                            }
                        });
                        if (a != null && a.length > 0) {
                            Collections.addAll(results, a);
                        }
                    }
                    files = results.toArray(new java.io.File[results.size()]);
                } else {
                    final String fileExt;
                    String ext = MavenUtils.getFileExtensionByPackaging(packaging);
                    if (ext == null) {
                        ext = packaging.equals("jar")
                              && config.getRequest().isIncludeDependencies()
                              && !MavenUtils.isCodenvyExtensionProject(model) ? "jar-with-dependencies.zip" : ('.' + packaging);
                    }
                    fileExt = ext;
                    files = new java.io.File(workDir, "target").listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(java.io.File dir, String name) {
                            return SOURCES_AND_DOCS_FILTER.accept(dir, name) && name.endsWith(fileExt);
                        }
                    });
                }
                if (files.length == 0) {
                    files = new java.io.File(workDir, "target").listFiles(SOURCES_AND_DOCS_FILTER);
                }
                break;
            case LIST_DEPS:
                files = workDir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(java.io.File dir, String name) {
                        return name.equals(DEPENDENCIES_JSON_FILE);
                    }
                });
                break;
            case COPY_DEPS:
                final java.io.File target = new java.io.File(workDir, "target");
                final java.io.File dependencies = new java.io.File(target, "dependency");
                if (dependencies.isDirectory() && dependencies.list().length > 0) {
                    final java.io.File zip = new java.io.File(target, "dependencies.zip");
                    try {
                        ZipUtils.zipDir(dependencies.getAbsolutePath(), dependencies, zip, IoUtil.ANY_FILTER);
                    } catch (IOException e) {
                        throw new BuilderException(e);
                    }
                    files = new java.io.File[]{zip};
                }
                break;
        }

        if (files != null && files.length > 0) {
            Collections.addAll(result.getResults(), files);
        }

        return result;
    }

    private boolean isMavenTaskSuccess(FutureBuildTask task) throws BuilderException {
        boolean mavenSuccess = false;
        BufferedReader logReader = null;
        try {
            logReader = new BufferedReader(task.getBuildLogger().getReader());
            String line;
            while ((line = logReader.readLine()) != null) {
                line = MavenUtils.removeLoggerPrefix(line);
                if ("BUILD SUCCESS".equals(line)) {
                    mavenSuccess = true;
                    break;
                }
            }
        } catch (IOException e) {
            throw new BuilderException(e);
        } finally {
            if (logReader != null) {
                try {
                    logReader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return mavenSuccess;
    }

    /**
     * Get build report. By default show link to the surefire reports.
     *
     * @param task
     *         task
     * @return report or {@code null} if surefire reports is not available
     */
    protected java.io.File getBuildReport(FutureBuildTask task) {
        final java.io.File dir = task.getConfiguration().getWorkDir();
        final String reports = "target" + java.io.File.separatorChar + "surefire-reports";
        final java.io.File reportsDir = new java.io.File(dir, reports);
        return reportsDir.exists() ? reportsDir : null;
    }

    @Override
    protected BuildLogger createBuildLogger(BuilderConfiguration configuration, java.io.File logFile) throws BuilderException {
        final BuildLogger buildLogger = super.createBuildLogger(configuration, logFile);
        if (configuration.getTaskType() == BuilderTaskType.LIST_DEPS) {
            // collect dependencies in json file
            return new DependencyBuildLogger(buildLogger, new java.io.File(configuration.getWorkDir(), DEPENDENCIES_JSON_FILE));
        }
        return buildLogger;
    }

    private static class DependencyBuildLogger extends DelegateBuildLogger {
        final java.io.File jsonFile;

        boolean             dependencyStarted;
        List<MavenArtifact> dependencies;

        DependencyBuildLogger(BuildLogger buildLogger, java.io.File jsonFile) {
            super(buildLogger);
            this.jsonFile = jsonFile;
            this.dependencies = new LinkedList<>();
        }

        @Override
        public void writeLine(String line) throws IOException {
            if (line != null) {
                final String trimmed = MavenUtils.removeLoggerPrefix(line);
                if (dependencyStarted) {
                    if (trimmed.isEmpty()) {
                        dependencyStarted = false;
                        try (Writer writer = Files.newBufferedWriter(jsonFile.toPath(), Charset.forName("UTF-8"))) {
                            writer.write(JsonHelper.toJson(dependencies));
                        }
                    } else {
                        final MavenArtifact artifact = MavenUtils.parseMavenArtifact(line);
                        if (artifact != null) {
                            dependencies.add(artifact);
                        }
                    }
                } else if ("The following files have been resolved:".equals(trimmed)) {
                    dependencyStarted = true;
                }
            }

            super.writeLine(line);
        }
    }
}
