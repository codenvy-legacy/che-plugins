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
package org.eclipse.che.ide.ext.github.client;

import org.eclipse.che.ide.ext.github.client.importer.page.GithubImporterPageViewImpl;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 22, 2011 2:39:07 PM anya $
 */
public interface GitHubResources extends ClientBundle {

    @Source({"importer/page/GithubImporterPage.css", "org/eclipse/che/ide/ui/Styles.css"})
    GithubImporterPageViewImpl.GithubStyle githubImporterPageStyle();

    @Source("buttons/ok.png")
    ImageResource ok();

    @Source("buttons/add.png")
    ImageResource add();

    @Source("buttons/cancel.png")
    ImageResource cancel();

    @Source("buttons/next.png")
    ImageResource next();

    @Source("welcome/import-from-github.svg")
    SVGResource importFromGithub();

    @Source("welcome/clone-git-repository.png")
    ImageResource welcomeClone();

    @Source("welcome/project_open.png")
    ImageResource project();
}