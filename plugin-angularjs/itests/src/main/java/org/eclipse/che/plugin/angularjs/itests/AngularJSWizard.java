/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.angularjs.itests;

import org.eclipse.che.test.framework.AbstractIntegrationTest;
import org.eclipse.che.test.framework.selenium.pages.IDEMainPage;

import org.openqa.selenium.support.PageFactory;

/**
 * Check Angular stuff.
 * @author Florent Benoit
 */
public class AngularJSWizard extends AbstractIntegrationTest {

    private IDEMainPage mainPage = null;

    public String access(String url) {
        driver.get(url);
        mainPage = PageFactory.initElements(driver, IDEMainPage.class);
        return "access";
    }


    public String npmMenuIsAvailable() {
        try {
            mainPage.getMainMenuItem("NpmMenu");
            return "is here";
        } catch (Exception e) {
            return "is not here";
        }
    }

    public String bowerMenuIsAvailable() {
        try {
            mainPage.getMainMenuItem("BowerMenu");
            return "is here";
        } catch (Exception e) {
            return "is not here";
        }
    }

    public String yeomanTabIsAvailable() {
        try {
            mainPage.getTab("Yeoman");
            return "is here";
        } catch (Exception e) {
            return "is not here";
        }
    }


}
