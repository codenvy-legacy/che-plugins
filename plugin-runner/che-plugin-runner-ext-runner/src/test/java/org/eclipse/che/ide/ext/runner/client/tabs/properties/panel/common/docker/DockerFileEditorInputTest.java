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
package org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.docker;

import org.eclipse.che.ide.api.filetypes.FileType;
import org.eclipse.che.ide.api.project.tree.VirtualFile;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.vectomatic.dom.svg.ui.SVGResource;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Andrienko
 */
@RunWith(GwtMockitoTestRunner.class)
public class DockerFileEditorInputTest {

    private static final String TEXT = "some text";

    //mocks for constructor
    @Mock
    private FileType    fileType;
    @Mock
    private VirtualFile file;

    @Mock
    private ImageResource image;
    @Mock
    private SVGResource   svgImage;

    @InjectMocks
    private DockerFileEditorInput dockerFileEditorInput;

    @Before
    public void setUp() {
        when(fileType.getContentDescription()).thenReturn(TEXT);
        when(file.getDisplayName()).thenReturn(TEXT);
        when(fileType.getImage()).thenReturn(image);
        when(fileType.getSVGImage()).thenReturn(svgImage);
    }

    @Test
    public void contentDescriptionShouldBeReturned() {
        assertThat(dockerFileEditorInput.getContentDescription(), is(TEXT));

        verify(fileType).getContentDescription();
    }

    @Test
    public void ToolTipTextShouldBeReturned() {
        assertThat(dockerFileEditorInput.getToolTipText(), is(""));
    }

    @Test
    public void nameShouldBeReturned() {
        assertThat(dockerFileEditorInput.getName(), is(TEXT));

        verify(file).getDisplayName();
    }

    @Test
    public void imageResourcesShouldBeReturned() {
        assertThat(dockerFileEditorInput.getImageResource(), is(image));

        verify(fileType).getImage();
    }

    @Test
    public void svgResourcesShouldBeReturned() {
        assertThat(dockerFileEditorInput.getSVGResource(), is(svgImage));

        verify(fileType).getSVGImage();
    }

    @Test
    public void fileShouldBeChanged() {
        VirtualFile file1 = mock(VirtualFile.class);

        assertThat(dockerFileEditorInput.getFile(), is(file));

        dockerFileEditorInput.setFile(file1);

        assertThat(dockerFileEditorInput.getFile(), is(file1));
    }
}
