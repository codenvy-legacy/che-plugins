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
package org.eclipse.che.ide.ext.datasource.client.properties;

import com.google.gwt.i18n.client.Constants;

public interface DataEntityPropertiesConstants extends Constants {

    @DefaultStringValue("Product name")
    String productNameLabel();

    @DefaultStringValue("Product version")
    String productVersionLabel();

    @DefaultStringValue("User name")
    String usernameLabel();

    @DefaultStringValue("Primary key")
    String primaryKeyLabel();

    @DefaultStringValue("Table type")
    String tableTypeLabel();


    @DefaultStringValue("Data type")
    String dataTypeLabel();

    @DefaultStringValue("Column size")
    String columnSizeLabel();

    @DefaultStringValue("Decimal digits")
    String decimalDigitsLabel();

    @DefaultStringValue("Nullable")
    String nullableLabel();

    @DefaultStringValue("Default value")
    String defaultValueLabel();

    @DefaultStringValue("Position in table")
    String ordinalPositionLabel();

    @DefaultStringValue("Object type")
    String objectTypeLabel();

    @DefaultStringValue("Object name")
    String objectNameLabel();

    @DefaultStringValue("Comment")
    String commentLabel();

    @DefaultStringValue("None")
    String noValue();

    @DefaultStringValue("Columns")
    String columnCountLabel();

    @DefaultStringValue("Tables")
    String tableCountLabel();

    @DefaultStringValue("Schemas")
    String schemaCountLabel();


    @DefaultStringValue("database")
    String objectTypeDatabase();

    @DefaultStringValue("schema")
    String objectTypeSchema();

    @DefaultStringValue("table")
    String objectTypeTable();

    @DefaultStringValue("column")
    String objectTypeColumn();

}
