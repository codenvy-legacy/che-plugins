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
package org.eclipse.che.ide.ext.datasource.client.store;

import java.util.Collection;

/**
 * High level service that provides schemas, tables and columns of a datasource as {@link String}.
 */
public interface DatabaseInfoOracle {

    Collection<String> getSchemasFor(String datasourceId);

    Collection<String> getTablesFor(String datasourceId, String schema);

    Collection<String> getColumnsFor(String datasourceId, String schema, String table);
}
