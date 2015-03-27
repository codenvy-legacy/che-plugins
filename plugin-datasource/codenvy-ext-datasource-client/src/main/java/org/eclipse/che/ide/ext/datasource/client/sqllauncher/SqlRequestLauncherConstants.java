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
package org.eclipse.che.ide.ext.datasource.client.sqllauncher;

import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.Messages;

@DefaultLocale("en")
public interface SqlRequestLauncherConstants extends Messages {

    @DefaultMessage("Open SQL editor")
    String menuEntryOpenSqlEditor();

    @DefaultMessage("SQL editor")
    String sqlEditorWindowTitle();

    @DefaultMessage("Datasource Target:")
    String selectDatasourceLabel();

    @DefaultMessage("Result limit:")
    String resultLimitLabel();

    @DefaultMessage("Execute Query")
    String executeButtonLabel();

    @DefaultMessage("{0} rows.")
    @AlternateMessage({"one", "{0} row."})
    String updateCountMessage(@PluralCount int count);

    @DefaultMessage("Export")
    String exportCsvLabel();

    @DefaultMessage("Execution mode:")
    String executionModeLabel();

    @DefaultMessage("Execute all - ignore and report errors")
    String executeAllModeItem();

    @DefaultMessage("First error - stop on first error")
    String stopOnErrorModeitem();

    @DefaultMessage("Transaction - rollback on first error")
    String transactionModeItem();

    @DefaultMessage("Query Results")
    String queryResultsTitle();

    @DefaultMessage("Query Error")
    String queryErrorTitle();

    @DefaultMessage("< empty result >")
    String emptyResult();

    @DefaultMessage("No datasource selected")
    String executeNoDatasourceTitle();

    @DefaultMessage("Please select the datasource to execute the request.")
    String executeNoDatasourceMessage();

    @DefaultMessage("No execution mode selected")
    String executeNoExecutionModeTitle();

    @DefaultMessage("Please choose an execution mode for the request.")
    String executeNoExecutionModeMessage();

    @DefaultMessage("The result limit can''t be negative. It was reset to the previous value.")
    String userErrorNegativeLimit();

    @DefaultMessage("The result limit can''t be zero. It was reset to the previous value.")
    String userErrorZeroLimit();

    @DefaultMessage("Clear all results")
    String clearButtonAltText();
}
