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
package org.eclipse.che.ide.ext.java.client.projecttree;

import org.eclipse.che.api.project.server.dto.DtoServerImpls;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Anatoliy Bazko
 */
public class JavaSourceFolderUtilTest {

    private CurrentProject project;

    @Before
    public void setUp() throws Exception {
        ProjectDescriptor descriptor = new DtoServerImpls.ProjectDescriptorImpl();
        descriptor.setPath("/test");
        descriptor.setType("maven");
        descriptor.setAttributes(new HashMap<String, List<String>>() {{
            put("maven.source.folder", Collections.singletonList("src/main/java"));
        }});

        project = new CurrentProject(descriptor);
    }

    @Test
    public void testGetSourceFolders() throws Exception {
        List<String> sourceFolders = JavaSourceFolderUtil.getSourceFolders(project);

        assertEquals(sourceFolders.size(), 1);
        assertEquals(sourceFolders.get(0), "/test/src/main/java/");
    }
}