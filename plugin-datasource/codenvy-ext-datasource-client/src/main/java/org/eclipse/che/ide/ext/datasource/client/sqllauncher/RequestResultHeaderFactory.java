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

import org.eclipse.che.ide.ext.datasource.shared.request.RequestResultDTO;

/**
 * Factory for {@link RequestResultHeaderImpl}.
 * 
 * @author "Mickaël Leduque"
 */
public interface RequestResultHeaderFactory {

    /**
     * Creates an instance of {@link RequestResultHeaderImpl}.
     * 
     * @param delegate the action delegate
     * @return a {@link org.eclipse.che.ide.ext.datasource.client.sqllauncher.RequestResultHeaderImpl.RequestResultDelegate}
     */
    RequestResultHeader createRequestResultHeader(RequestResultHeaderImpl.RequestResultDelegate delegate, String query);

    /**
     * Creates an instance of {@link RequestResultHeaderImpl} with a CSV export button.
     * 
     * @param delegate the action delegate
     * @return a {@link org.eclipse.che.ide.ext.datasource.client.sqllauncher.RequestResultHeaderImpl.RequestResultDelegate}
     */
    RequestResultHeader createRequestResultHeaderWithExport(RequestResultHeaderImpl.RequestResultDelegate delegate, RequestResultDTO result);
}
