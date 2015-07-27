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

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.eclipse.che.ide.ext.datasource.shared.DatabaseConfigurationDTO;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public interface DatasourceManager extends Iterable<DatabaseConfigurationDTO> {

    Iterator<DatabaseConfigurationDTO> getDatasources();

    void add(final DatabaseConfigurationDTO configuration);

    void remove(final DatabaseConfigurationDTO configuration);

    DatabaseConfigurationDTO getByName(final String name);

    Set<String> getNames();

    void persist(AsyncCallback<Map<String, String>> callback);
}
