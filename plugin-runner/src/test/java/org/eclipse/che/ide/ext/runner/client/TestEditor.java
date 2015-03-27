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
package org.eclipse.che.ide.ext.runner.client;

import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.texteditor.HandlesUndoRedo;
import org.eclipse.che.ide.api.texteditor.HasReadOnlyProperty;
import org.eclipse.che.ide.api.texteditor.UndoableEditor;
import org.eclipse.che.ide.imageviewer.ImageViewer;
import org.eclipse.che.ide.imageviewer.ImageViewerResources;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;

/**
 * This class for testing
 * @author Alexander Andrienko
 */
public class TestEditor extends ImageViewer implements HasReadOnlyProperty, UndoableEditor {

    public TestEditor(ImageViewerResources resources, CoreLocalizationConstant constant, DialogFactory dialogFactory) {
        super(resources, constant, dialogFactory);
    }

    @Override
    public void setReadOnly(boolean b) {
        //stubbing method
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public HandlesUndoRedo getUndoRedo() {
        return null;
    }

}