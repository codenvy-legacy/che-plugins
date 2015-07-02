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
package org.eclipse.che.ide.ext.java;

import org.eclipse.che.core.internal.resources.ResourcesPlugin;
import org.eclipse.che.jdt.javadoc.JavaElementLinks;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.codeassist.impl.AssistOptions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Evgen Vidolob
 */
public class BaseTest {

    protected static Map<String, String> options = new HashMap<>();
    protected static JavaProject project;
    protected static final String wsPath = BaseTest.class.getResource("/projects").getFile();
    protected static ResourcesPlugin plugin = new ResourcesPlugin(wsPath + "/index", BaseTest.class.getResource("/projects").getFile());
    protected static JavaPlugin javaPlugin = new JavaPlugin(wsPath + "/set");

    static {
        plugin.start();
        javaPlugin.start();
    }


    public BaseTest() {
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_7);
        options.put(JavaCore.CORE_ENCODING, "UTF-8");
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_7);
        options.put(CompilerOptions.OPTION_TargetPlatform, JavaCore.VERSION_1_7);
        options.put(AssistOptions.OPTION_PerformVisibilityCheck, AssistOptions.ENABLED);
        options.put(CompilerOptions.OPTION_ReportUnusedLocal, CompilerOptions.WARNING);
        options.put(CompilerOptions.OPTION_TaskTags, CompilerOptions.WARNING);
        options.put(CompilerOptions.OPTION_ReportUnusedPrivateMember, CompilerOptions.WARNING);
        options.put(CompilerOptions.OPTION_SuppressWarnings, CompilerOptions.DISABLED);
        options.put(JavaCore.COMPILER_TASK_TAGS, "TODO,FIXME,XXX");
        options.put(JavaCore.COMPILER_PB_UNUSED_PARAMETER_INCLUDE_DOC_COMMENT_REFERENCE, JavaCore.ENABLED);
        options.put(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, JavaCore.ENABLED);
        options.put(CompilerOptions.OPTION_Process_Annotations, JavaCore.DISABLED);
    }

    protected static String getHandldeForRtJarStart() {
        String javaHome = System.getProperty("java.home") + "/lib/rt.jar";
        javaHome = javaHome.replaceAll("/", "\\\\/");
        return String.valueOf(JavaElementLinks.LINK_SEPARATOR) + "=test/" + javaHome;
    }

    @Before
    public void setUp() throws Exception {
        project = (JavaProject)JavaModelManager.getJavaModelManager().getJavaModel().getJavaProject("/test");
    }

    @After
    public void closeProject() throws Exception {
        File pref = new File(wsPath + "/test/.codenvy/project.preferences");
        project.close();
        if(pref.exists()){
            pref.delete();
        }

    }


}