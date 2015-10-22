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
package org.eclipse.che.plugin.docker.machine.local;

import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.machine.server.exception.SnapshotException;
import org.eclipse.che.api.machine.server.impl.SnapshotImpl;
import org.eclipse.che.api.machine.server.dao.SnapshotDao;

import javax.inject.Singleton;
import java.util.List;

/**
 * Dummy implementation of SnapshotStorage.
 * It does nothing, probably will be deleted later.
 * @author gazarenkov
 */
@Singleton
public class DummySnapshotDao implements SnapshotDao {

    @Override
    public SnapshotImpl getSnapshot(String snapshotId) throws NotFoundException, SnapshotException {
        throw new SnapshotException ("Not available for dummy implementation");
    }

    @Override
    public void saveSnapshot(SnapshotImpl snapshot) throws SnapshotException  {
        throw new SnapshotException ("Not available for dummy implementation");
    }

    @Override
    public List<SnapshotImpl> findSnapshots(String owner, String workspaceId) throws SnapshotException  {
        throw new SnapshotException ("Not available for dummy implementation");
    }

    @Override
    public void removeSnapshot(String snapshotId) throws NotFoundException, SnapshotException  {
        throw new SnapshotException ("Not available for dummy implementation");
    }
}
