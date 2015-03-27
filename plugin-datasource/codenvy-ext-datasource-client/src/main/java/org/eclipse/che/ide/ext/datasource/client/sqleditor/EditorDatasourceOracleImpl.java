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
package org.eclipse.che.ide.ext.datasource.client.sqleditor;

import java.util.HashMap;
import java.util.Map;

public class EditorDatasourceOracleImpl implements EditorDatasourceOracle {
    private Map<String, String> fileDatasourcesMap;

    @Override
    public String getSelectedDatasourceId(final String editorInputFileId) {
        if (this.fileDatasourcesMap == null) {
            return null;
        }
        return fileDatasourcesMap.get(editorInputFileId);
    }

    @Override
    public void setSelectedDatasourceId(final String editorInputFileId, final String datasourceId) {
        if (this.fileDatasourcesMap == null) {
            this.fileDatasourcesMap = new HashMap<String, String>();
        }
        fileDatasourcesMap.put(editorInputFileId, datasourceId);
    }

    @Override
    public void forgetEditor(final String editorInputFileId) {
        if (this.fileDatasourcesMap != null) {
            this.fileDatasourcesMap.remove(editorInputFileId);
        }
    }
}
