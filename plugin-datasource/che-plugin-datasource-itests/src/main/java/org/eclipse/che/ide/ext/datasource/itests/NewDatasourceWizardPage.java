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

import org.eclipse.che.test.framework.AbstractIntegrationTest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;


/**
 * A webdriver page object for Datasource plugin creation/edit wizard box
 */
public class NewDatasourceWizardPage {

    protected WebDriver driver;

    @FindBy(id = "gwt-debug-newdatasource-headerLabel")
    WebElement          title;

    @FindBy(id = "category-hosted_database")
    WebElement          hosted_database;

    @FindBy(id = "category-google")
    WebElement          google;
    
    @FindBy(id = "category-amazon")
    WebElement          amazon;
    
    public NewDatasourceWizardPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getWizardTitle() {
        return title.getText();
    }

    public WebElement getHostedDatabaseCategoryElement() {
        return hosted_database;
    }

    public WebElement getGoogleCategoryElement() {
        return google;
    }    
    
    public WebElement getAmazonCategoryElement() {
        return amazon;
    }
    
    public boolean isDatasourceTypeAvailable(String dbType) {
        new WebDriverWait(driver, 10).until(AbstractIntegrationTest.gwtTreeNodeElementToBeEnable(
                                                                   By.id("connector-" + dbType)));
        return true;
    }

    public boolean isDatasourceTypeNotAvailable(String dbType) {
        new WebDriverWait(driver, 10).until(AbstractIntegrationTest.gwtTreeNodeElementToBeDisable(
                                                                   By.id("connector-" + dbType)));
        return true;
    }

    public boolean isDatasourceCategoryAvailable(String dbCategory) {
        new WebDriverWait(driver, 10).until(AbstractIntegrationTest.gwtTreeNodeElementToBeEnable(
                                                                   By.id("category-" + dbCategory)));
        return true;
    }
}
