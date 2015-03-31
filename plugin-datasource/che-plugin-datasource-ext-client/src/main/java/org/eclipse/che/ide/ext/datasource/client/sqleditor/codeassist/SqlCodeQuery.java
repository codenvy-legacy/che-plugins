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
package org.eclipse.che.ide.ext.datasource.client.sqleditor.codeassist;

/**
 * All the elements needed after having parse the context to compute results.
 */
public class SqlCodeQuery {
    private String lastQueryPrefix;

    /**
     * @param lastQueryPrefix : Prefix of the last query in the SQL file or selected element. for instance "SELECT * FRO".
     */
    public SqlCodeQuery(String lastQueryPrefix) {
        this.lastQueryPrefix = lastQueryPrefix;
    }

    public String getLastQueryPrefix() {
        return lastQueryPrefix;
    }

    public void setLastQueryPrefix(String queryPrefix) {
        this.lastQueryPrefix = queryPrefix;
    }
}
