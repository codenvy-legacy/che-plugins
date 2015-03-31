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
package org.eclipse.che.ide.ext.datasource.shared;

import org.eclipse.che.dto.shared.DTO;

@DTO
public interface ColumnDTO extends DatabaseMetadataEntityDTO {

    ColumnDTO withName(String name);

    ColumnDTO withLookupKey(String lookupKey);

    ColumnDTO withComment(String comment);


    ColumnDTO withColumnDataType(String datatypeName);

    String getColumnDataType();

    void setColumnDataType(String columnDataTypeName);


    ColumnDTO withDefaultValue(String defaultValue);

    void setDefaultValue(String defaultValue);

    String getDefaultValue();


    ColumnDTO withDataSize(int size);

    void setDataSize(int size);

    int getDataSize();


    ColumnDTO withDecimalDigits(int digits);

    void setDecimalDigits(int digits);

    int getDecimalDigits();


    ColumnDTO withNullable(boolean nullable);

    void setNullable(boolean nullable);

    boolean getNullable();


    ColumnDTO withPartOfForeignKey(boolean partOfFK);

    void setPartOfForeignKey(boolean partOfFK);

    boolean isPartOfForeignKey();


    ColumnDTO withPartOfPrimaryKey(boolean partOfPK);

    void setPartOfPrimaryKey(boolean partOfPK);

    boolean isPartOfPrimaryKey();


    ColumnDTO withOrdinalPositionInTable(int position);

    void setOrdinalPositionInTable(int position);

    int getOrdinalPositionInTable();
}
