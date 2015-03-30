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
package org.eclipse.che.ide.extension.ant.client.wizard;

import org.eclipse.che.api.project.shared.dto.ImportProject;
import org.eclipse.che.ide.api.project.type.wizard.ProjectWizardMode;
import org.eclipse.che.ide.api.wizard.AbstractWizardPage;
import org.eclipse.che.ide.extension.ant.shared.AntAttributes;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.eclipse.che.ide.api.project.type.wizard.ProjectWizardMode.CREATE;
import static org.eclipse.che.ide.api.project.type.wizard.ProjectWizardRegistrar.WIZARD_MODE_KEY;

/**
 * Wizard page for Ant project.
 *
 * @author Vladyslav Zhukovskii
 * @author Artem Zatsarynnyy
 */
public class AntPagePresenter extends AbstractWizardPage<ImportProject> implements AntPageView.ActionDelegate {

    private final AntPageView view;

    /** Create instance of {@link AntPagePresenter}. */
    @Inject
    public AntPagePresenter(AntPageView view) {
        super();
        this.view = view;
        view.setDelegate(this);
    }

    @Override
    public void init(ImportProject dataObject) {
        super.init(dataObject);

        final ProjectWizardMode wizardMode = ProjectWizardMode.parse(context.get(WIZARD_MODE_KEY));
        if (CREATE == wizardMode) {
            // set default values
            Map<String, List<String>> attributes = dataObject.getProject().getAttributes();
            attributes.put(AntAttributes.SOURCE_FOLDER, Arrays.asList(AntAttributes.DEF_SRC_PATH));
            attributes.put(AntAttributes.TEST_SOURCE_FOLDER, Arrays.asList(AntAttributes.DEF_TEST_SRC_PATH));
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean canSkip() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }
}
