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
package org.eclipse.che.example.tree;

import com.google.inject.Inject;
import org.eclipse.che.ide.api.extension.Extension;
import org.eclipse.che.ide.api.parts.PartStackType;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;

/**
 * @author Vlad Zhukovskiy
 */
@Extension(title = "Tree example")
public class TreeExtension {

    @Inject
    public TreeExtension(TreePresenter presenter, WorkspaceAgent workspaceAgent) {
        workspaceAgent.getPartStack(PartStackType.NAVIGATION).addPart(presenter);
    }
}
