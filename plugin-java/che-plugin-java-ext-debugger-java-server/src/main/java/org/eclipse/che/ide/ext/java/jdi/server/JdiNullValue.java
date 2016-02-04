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
package org.eclipse.che.ide.ext.java.jdi.server;

/** @author andrew00x */
public final class JdiNullValue implements JdiValue {
    @Override
    public String getAsString() throws DebuggerException {
        return "null";
    }

    @Override
    public JdiVariable[] getVariables() throws DebuggerException {
        return new JdiVariable[0];
    }

    @Override
    public JdiVariable getVariableByName(String name) throws DebuggerException {
        return null;
    }
}