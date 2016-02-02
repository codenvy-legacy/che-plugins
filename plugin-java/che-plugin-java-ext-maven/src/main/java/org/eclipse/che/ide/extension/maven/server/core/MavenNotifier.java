/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.extension.maven.server.core;

import org.eclipse.che.maven.server.MavenProgressNotifier;

import java.rmi.RemoteException;

/**
 * Default implementation of {@link MavenProgressNotifier}
 *
 * @author Evgen Vidolob
 */
//TODO send messages via websocket
public class MavenNotifier implements MavenProgressNotifier {
    @Override
    public void setText(String text) throws RemoteException {
        System.out.println(text);
    }

    @Override
    public void setPercent(double percent) throws RemoteException {
        System.out.println(percent);
    }

    @Override
    public void setPercentUndefined(boolean undefined) throws RemoteException {

    }

    @Override
    public boolean isCanceled() throws RemoteException {
        return false;
    }
}
