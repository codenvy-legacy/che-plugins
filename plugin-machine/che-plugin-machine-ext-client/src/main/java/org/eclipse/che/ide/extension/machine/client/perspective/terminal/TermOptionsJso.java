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

package org.eclipse.che.ide.extension.machine.client.perspective.terminal;

import org.eclipse.che.ide.collections.Jso;

/**
 * @author Evgen Vidolob
 */
public class TermOptionsJso extends Jso{
    protected TermOptionsJso() {
    }

    public static native TermOptionsJso createDefault() /*-{
        return {
            cols: 200,
            rows: 60,
            useStyle: true,
            screenKeys: true
        }
    }-*/;
}
