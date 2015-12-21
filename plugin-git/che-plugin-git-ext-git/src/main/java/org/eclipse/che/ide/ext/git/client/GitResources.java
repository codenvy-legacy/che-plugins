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
package org.eclipse.che.ide.ext.git.client;

import org.eclipse.che.ide.ext.git.client.importer.page.GitImporterPageViewImpl;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 22, 2011 2:39:07 PM anya $
 */
public interface GitResources extends ClientBundle {
    public interface GitCSS extends CssResource {
        String textFont();

        String cells();

        String simpleListContainer();

        String emptyBorder();

        String spacing();
    }

    @Source({"importer/page/GitImporterPage.css", "org/eclipse/che/ide/api/ui/style.css"})
    GitImporterPageViewImpl.Style gitImporterPageStyle();

    @Source({"git.css", "org/eclipse/che/ide/api/ui/style.css"})
    GitCSS gitCSS();

    @Source("push/arrow.png")
    ImageResource arrow();

    @Source("controls/remove.svg")
    SVGResource removeFiles();

    @Source("controls/reset.svg")
    SVGResource reset();

    @Source("controls/init.svg")
    SVGResource initRepo();

    @Source("controls/delete-repo.svg")
    SVGResource deleteRepo();

    @Source("controls/merge.svg")
    SVGResource merge();

    @Source("controls/add.svg")
    SVGResource addToIndex();

    @Source("controls/branches.svg")
    SVGResource branches();

    @Source("controls/remotes.svg")
    SVGResource remotes();

    @Source("controls/commit.svg")
    SVGResource commit();

    @Source("controls/push.svg")
    SVGResource push();

    @Source("controls/pull.svg")
    SVGResource pull();

    @Source("controls/checkoutReference.svg")
    SVGResource checkoutReference();

    @Source("history/arrows.png")
    ImageResource arrows();

    @Source("history/history.png")
    ImageResource history();

    @Source("controls/show-history.svg")
    SVGResource showHistory();

    @Source("history/project_level.png")
    ImageResource projectLevel();

    @Source("history/resource_level.png")
    ImageResource resourceLevel();

    @Source("history/diff_index.png")
    ImageResource diffIndex();

    @Source("history/diff_working_dir.png")
    ImageResource diffWorkTree();

    @Source("history/diff_prev_version.png")
    ImageResource diffPrevVersion();

    @Source("history/refresh.png")
    ImageResource refresh();

    @Source("controls/fetch.svg")
    SVGResource fetch();

    @Source("controls/status.svg")
    SVGResource status();

    @Source("branch/current.png")
    ImageResource currentBranch();

    @Source("controls/remote.svg")
    SVGResource remote();

    @Source("controls/revert.svg")
    SVGResource revert();

    @Source("controls/git-url.svg")
    SVGResource projectReadOnlyGitUrl();
}