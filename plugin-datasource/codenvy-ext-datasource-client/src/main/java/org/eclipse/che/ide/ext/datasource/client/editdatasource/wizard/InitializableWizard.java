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
package org.eclipse.che.ide.ext.datasource.client.editdatasource.wizard;

import org.eclipse.che.ide.api.wizard.AbstractWizard;
import org.eclipse.che.ide.api.wizard.WizardPage;
import org.eclipse.che.ide.ext.datasource.client.newdatasource.InitializableWizardPage;
import org.eclipse.che.ide.util.loging.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;

import javax.annotation.Nonnull;

/**
 * Version of the wizard that can be configured with values.
 * 
 * @author "Mickaël Leduque"
 * @param <T> the data type
 */
public class InitializableWizard<T> extends AbstractWizard<T> {

    /**
     * The initial data
     */
    private T configuration;

    public InitializableWizard(final T dataObject) {
        super(dataObject);
        Log.debug(InitializableWizard.class, "New InitializableWizard instance created.");
    }

    /**
     * Sets the initial data of the wizard.
     * 
     * @param configuration the data
     */
    public void initData(final T configuration) {
        this.configuration = configuration;
    }

    @Override
    public WizardPage<T> navigateToFirst() {
        final WizardPage<T> page = super.navigateToFirst();
        if (page == null) {
            Log.error(InitializableWizard.class, "flipToFirst returned a null page !");
            throw new NullPointerException();
        }
        if (page instanceof InitializableWizardPage) {
            Log.debug(InitializableWizard.class, "Schedule wizard page init : " + page.getClass());
            scheduleInit(page);

        } else {
            Log.warn(InitializableWizard.class, "Not an initializable wizard page : " + page.getClass());
        }
        return page;
    }

    @Override
    public WizardPage<T> navigateToNext() {
        WizardPage<T> page = super.navigateToNext();
        if (page instanceof InitializableWizardPage) {
            Log.debug(InitializableWizard.class, "Schedule wizard page init : " + page.getClass());
            scheduleInit(page);
        } else {
            Log.warn(InitializableWizard.class, "Not an initializable wizard page : " + page.getClass());
        }
        return page;
    }

    @Override
    public WizardPage<T> navigateToPrevious() {
        WizardPage<T> page = super.navigateToPrevious();
        if (page instanceof InitializableWizardPage) {
            Log.debug(InitializableWizard.class, "Schedule wizard page init : " + page.getClass());
            scheduleInit(page);
        } else {
            Log.warn(InitializableWizard.class, "Not an initializable wizard page : " + page.getClass());
        }
        return page;
    }

    /**
     * Delay the initialization of the page so that the page is properly configured before trying to configure it.
     * 
     * @param page the page to initialize
     */
    private void scheduleInit(final WizardPage page) {
        Command command = new Command() {

            @Override
            public void execute() {
                Log.debug(InitializableWizard.class, "Initializing wizard page : " + page.getClass());
                ((InitializableWizardPage)page).initPage(configuration);
                Log.debug(InitializableWizard.class, "Wizard page initialization done");
            }
        };
        Scheduler.get().scheduleDeferred(command);
    }

    @Override
    public void complete(@Nonnull CompleteCallback callback) {
    }
}
