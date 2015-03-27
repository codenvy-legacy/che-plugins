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
package org.eclipse.che.ide.ext.datasource.client.action;

import org.eclipse.che.ide.ext.datasource.client.DatasourceUiResources;
import org.eclipse.che.ide.ext.datasource.client.SqlEditorExtension;
import org.eclipse.che.ide.newresource.AbstractNewResourceAction;
import com.google.inject.Inject;

/**
 * IDE action to create a new SQL file.
 *
 * @author Artem Zatsarynnyy
 */
public class NewSqlFileAction extends AbstractNewResourceAction {

    @Inject
    public NewSqlFileAction(DatasourceUiResources resources) {
        super("SQL File", "Creates new SQL file", resources.sqlIcon());
    }

    @Override
    protected String getExtension() {
        return SqlEditorExtension.SQL_FILE_EXTENSION;
    }

    @Override
    protected String getMimeType() {
        return SqlEditorExtension.GENERIC_SQL_MIME_TYPE;
    }

}
