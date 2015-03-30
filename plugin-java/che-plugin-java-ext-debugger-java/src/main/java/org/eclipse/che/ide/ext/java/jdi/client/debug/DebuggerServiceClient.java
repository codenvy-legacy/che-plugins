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
package org.eclipse.che.ide.ext.java.jdi.client.debug;

import org.eclipse.che.ide.ext.java.jdi.shared.BreakPoint;
import org.eclipse.che.ide.ext.java.jdi.shared.DebuggerEventList;
import org.eclipse.che.ide.ext.java.jdi.shared.DebuggerInfo;
import org.eclipse.che.ide.ext.java.jdi.shared.StackFrameDump;
import org.eclipse.che.ide.ext.java.jdi.shared.UpdateVariableRequest;
import org.eclipse.che.ide.ext.java.jdi.shared.Value;
import org.eclipse.che.ide.ext.java.jdi.shared.Variable;
import org.eclipse.che.ide.rest.AsyncRequestCallback;

import javax.annotation.Nonnull;

/**
 * The client for service to debug java application.
 *
 * @author Vitaly Parfonov
 */
public interface DebuggerServiceClient {
    /**
     * Attach debugger.
     *
     * @param host
     * @param port
     * @param callback
     */
    void connect(@Nonnull String host, int port, @Nonnull AsyncRequestCallback<DebuggerInfo> callback);

    /**
     * Disconnect debugger.
     *
     * @param id
     * @param callback
     */
    void disconnect(@Nonnull String id, @Nonnull AsyncRequestCallback<Void> callback);

    /**
     * Adds breakpoint.
     *
     * @param id
     * @param breakPoint
     * @param callback
     */
    void addBreakpoint(@Nonnull String id, @Nonnull BreakPoint breakPoint, @Nonnull AsyncRequestCallback<Void> callback);

    /**
     * Returns list of breakpoints.
     *
     * @param id
     * @param callback
     */
    void getAllBreakpoints(@Nonnull String id, @Nonnull AsyncRequestCallback<String> callback);

    /**
     * Deletes breakpoint.
     *
     * @param id
     * @param breakPoint
     * @param callback
     */
    void deleteBreakpoint(@Nonnull String id, @Nonnull BreakPoint breakPoint, @Nonnull AsyncRequestCallback<Void> callback);

    /**
     * Remove all breakpoints.
     *
     * @param id
     * @param callback
     */
    void deleteAllBreakpoints(@Nonnull String id, @Nonnull AsyncRequestCallback<String> callback);

    /**
     * Checks event.
     *
     * @param id
     * @param callback
     */
    void checkEvents(@Nonnull String id, @Nonnull AsyncRequestCallback<DebuggerEventList> callback);

    /**
     * Get dump of fields and local variable of current stack frame.
     *
     * @param id
     * @param callback
     */
    void getStackFrameDump(@Nonnull String id, @Nonnull AsyncRequestCallback<StackFrameDump> callback);

    /**
     * Resume process.
     *
     * @param id
     * @param callback
     */
    void resume(@Nonnull String id, @Nonnull AsyncRequestCallback<Void> callback);

    /**
     * Returns value of a variable.
     *
     * @param id
     * @param var
     * @param callback
     */
    void getValue(@Nonnull String id, @Nonnull Variable var, @Nonnull AsyncRequestCallback<Value> callback);

    /**
     * Sets value of a variable.
     *
     * @param id
     * @param request
     * @param callback
     */
    void setValue(@Nonnull String id, @Nonnull UpdateVariableRequest request, @Nonnull AsyncRequestCallback<Void> callback);

    /**
     * Do step into.
     *
     * @param id
     * @param callback
     */
    void stepInto(@Nonnull String id, @Nonnull AsyncRequestCallback<Void> callback);

    /**
     * Do step over.
     *
     * @param id
     * @param callback
     */
    void stepOver(@Nonnull String id, @Nonnull AsyncRequestCallback<Void> callback);

    /**
     * Do step return.
     *
     * @param id
     * @param callback
     */
    void stepReturn(@Nonnull String id, @Nonnull AsyncRequestCallback<Void> callback);

    /**
     * Evaluate an expression.
     *
     * @param id
     * @param expression
     * @param callback
     */
    void evaluateExpression(@Nonnull String id, @Nonnull String expression, @Nonnull AsyncRequestCallback<String> callback);
}