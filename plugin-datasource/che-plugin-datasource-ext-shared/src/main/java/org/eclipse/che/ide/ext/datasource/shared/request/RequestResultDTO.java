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
package org.eclipse.che.ide.ext.datasource.shared.request;

import java.util.List;

import org.eclipse.che.dto.shared.DTO;

@DTO
public interface RequestResultDTO {

    int getResultType();

    void setResultType(int type);

    RequestResultDTO withResultType(int type);

    List<String> getHeaderLine();

    void setHeaderLine(List<String> line);

    RequestResultDTO withHeaderLine(List<String> line);

    List<List<String>> getResultLines();

    void setResultLines(List<List<String>> lines);

    RequestResultDTO withResultLines(List<List<String>> lines);

    int getUpdateCount();

    void setUpdateCount(int count);

    RequestResultDTO withUpdateCount(int count);


    SqlExecutionError getSqlExecutionError();

    void setSqlExecutionError(SqlExecutionError error);

    RequestResultDTO withSqlExecutionError(SqlExecutionError error);


    String getOriginRequest();

    void setOriginRequest(String request);

    RequestResultDTO withOriginRequest(String request);
}
