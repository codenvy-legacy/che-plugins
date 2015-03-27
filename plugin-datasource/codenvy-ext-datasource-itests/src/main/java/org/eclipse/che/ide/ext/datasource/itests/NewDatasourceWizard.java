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
package org.eclipse.che.ide.ext.datasource.itests;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import org.eclipse.che.test.framework.AbstractIntegrationTest;
import org.eclipse.che.test.framework.selenium.pages.IDEMainPage;

/**
 * Test datasource creation. See specs reports in src/main/resources/org/eclipse/che/ide/ext/datasource/itests/NewDatasourceWizard.html
 */
public class NewDatasourceWizard extends AbstractIntegrationTest {


    protected IDEMainPage             mainPage;
    protected NewDatasourceWizardPage newDatasourceWizard;

    public String access(String url) {
        driver.get(url);
        mainPage = PageFactory.initElements(driver, IDEMainPage.class);
        return "access";
    }


    public String displayDatasourceMenu() {
        return mainPage.getMainMenuItem("datasourceMainMenu").getText();
    }

    public String displayDatasourceNewDatasourceAction() {
        mainPage.getMainMenuItem("datasourceMainMenu").click();
        return mainPage.getMainMenuAction("Datasource/New Datasource Connection").getText();
    }

    public String clickOnNewDatasourceAction() {
        mainPage.getMainMenuAction("Datasource/New Datasource Connection").click();
        newDatasourceWizard = new NewDatasourceWizardPage(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 15), newDatasourceWizard);
        return "clicks";
    }


    public String displayNewDatasourceWizard() {
        return newDatasourceWizard.getWizardTitle();
    }

    public String hostedDatabaseCategoryIsAvailable() {
        if (newDatasourceWizard.isDatasourceCategoryAvailable("hosted_database")) {
            return "is present";
        }
        return "is not present";
    }

    public String googleCategoryIsAvailable() {
        if (newDatasourceWizard.isDatasourceCategoryAvailable("google")) {
            return "is present";
        }
        return "is not present";
    }

    public String amazonCategoryIsAvailable() {
        if (newDatasourceWizard.isDatasourceCategoryAvailable("amazon")) {
            return "is present";
        }
        return "is not present";
    }
    
    public String hostedDatabaseCategoryTextIsCorrect() {
        WebElement category = newDatasourceWizard.getHostedDatabaseCategoryElement();
        if (category.getText().equals("HOSTED DATABASE")) {
            return "correctly named";
        }
        return "badly named";
    }
    
    public String googleCategoryTextIsCorrect() {
        WebElement category = newDatasourceWizard.getGoogleCategoryElement();
        if (category.getText().equals("GOOGLE")) {
            return "correctly named";
        }
        return "badly named";
    }
    
    public String amazonCategoryTextIsCorrect() {
        WebElement category = newDatasourceWizard.getAmazonCategoryElement();
        if (category.getText().equals("AMAZON")) {
            return "correctly named";
        }
        return "badly named";
    }

    public String clickOnHostedDatabaseCategory() {
        WebElement category = newDatasourceWizard.getHostedDatabaseCategoryElement();
        Actions action = new Actions(driver);
        action.doubleClick(category);
        action.perform();
        return "unfolds";
    }

    public String clickOnGoogleCategory() {
        WebElement category = newDatasourceWizard.getGoogleCategoryElement();
        Actions action = new Actions(driver);
        action.doubleClick(category);
        action.perform();
        return "unfolds";
    }

    public String clickOnAmazonCategory() {
        WebElement category = newDatasourceWizard.getAmazonCategoryElement();
        Actions action = new Actions(driver);
        action.doubleClick(category);
        action.perform();
        return "unfolds";
    }

    public String postgresDsTypeIsAvailable() {
        if (newDatasourceWizard.isDatasourceTypeAvailable("postgres")) {
            return "is enabled";
        }
        return "is disabled";
    }


    public String mySqlDsTypeIsAvailable() {
        if (newDatasourceWizard.isDatasourceTypeAvailable("mysql")) {
            return "is enabled";
        }
        return "is disabled";
    }

    public String msSQLServerDsTypeIsAvailable() {
        if (newDatasourceWizard.isDatasourceTypeAvailable("sqlserver")) {
            return "is enabled";
        }
        return "is disabled";
    }

    public String oracleDsTypeIsNotAvailable() {
        if (newDatasourceWizard.isDatasourceTypeNotAvailable("oracle")) {
            return "is disabled";
        }
        return "is enabled";
    }

}
