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
package org.eclipse.che.ide.extension.maven.server.rmi;

import org.eclipse.che.ide.extension.maven.server.MavenServerManager;
import org.eclipse.che.ide.extension.maven.server.execution.JavaParameters;
import org.eclipse.che.maven.server.MavenRemoteServer;
import org.eclipse.che.maven.server.MavenServer;
import org.eclipse.che.maven.server.MavenSettings;
import org.eclipse.che.maven.server.MavenTerminal;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Evgen Vidolob
 */
public class MavenServerManagerTest {

    private final String mavenServerPath = MavenServerManagerTest.class.getResource("/maven-server").getPath();

    private MavenServerManager manager = new MavenServerManager(mavenServerPath);


    @After
    public void cleanUp() {
        manager.shutdown();
    }


    @Test
    public void testBuildMavenServerParametersMainWorkDirExec() throws Exception {
        MavenServerManager test = new MavenServerManager("test");
        JavaParameters parameters = test.buildMavenServerParameters();
        assertThat(parameters.getMainClassName()).isEqualTo("org.eclipse.che.maven.server.MavenServerMain");
        assertThat(parameters.getWorkingDirectory()).isEqualTo(System.getProperty("java.io.tmpdir"));
        assertThat(parameters.getJavaExecutable()).isEqualTo("java");
    }

    @Test
    public void testBuildMavenServerParametersClassPathMain() throws Exception {
        JavaParameters parameters = manager.buildMavenServerParameters();
        List<String> classPath = parameters.getClassPath();
        assertThat(classPath).contains(mavenServerPath + "/maven-server-rmi.jar")
                             .contains(mavenServerPath + "/maven-server-impl.jar")
                             .contains(mavenServerPath + "/maven-server-api.jar");
    }

    @Test
    public void testBuildMavenServerParametersClassPathMavenLib() throws Exception {
        JavaParameters parameters = manager.buildMavenServerParameters();
        List<String> classPath = parameters.getClassPath();
        String mavenHome = System.getenv("M2_HOME");
        File libs = new File(mavenHome, "lib");
        File[] listFiles = libs.listFiles((dir, name) -> name.endsWith(".jar"));
        List<String> libPaths = Arrays.stream(listFiles).map(File::getAbsolutePath).collect(Collectors.toList());
        assertThat(classPath).contains(libPaths.toArray());
    }

    @Test
    public void testLaunchMavenServer() throws Exception {
        MavenRemoteServer remoteServer = manager.getOrCreateWrappedObject();
        MavenSettings mavenSettings = new MavenSettings();
        mavenSettings.setLoggingLevel(MavenTerminal.LEVEL_DEBUG);
        MavenServer server = remoteServer.createServer(mavenSettings);
        assertThat(server).isNotNull();
    }

    @Test
    public void testEffectivePom() throws Exception {
        MavenRemoteServer remoteServer = manager.getOrCreateWrappedObject();
        MavenSettings mavenSettings = new MavenSettings();
        mavenSettings.setLoggingLevel(MavenTerminal.LEVEL_DEBUG);
        MavenServer server = remoteServer.createServer(mavenSettings);
        String effectivePom = server.getEffectivePom                        (new File(MavenServerManagerTest.class.getResource("/EffectivePom/pom.xml").getFile()),
                                                     Collections.emptyList(),
                                                     Collections.emptyList());
        assertThat(effectivePom).isNotNull().isNotEmpty().contains("<!-- Effective POM for project")
                                .contains("'org.eclipse.che.parent:maven-parent-pom:pom:4.0.0-M6-SNAPSHOT'");
    }
}
