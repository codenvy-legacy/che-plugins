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
package org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.panel;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.api.machine.shared.dto.MachineStateDto;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.callback.AsyncPromiseHelper;
import org.eclipse.che.api.promises.client.callback.PromiseHelper;
import org.eclipse.che.ide.api.project.node.AbstractTreeNode;
import org.eclipse.che.ide.api.project.node.Node;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * The class describes node in machine tree and contains special information about node.
 *
 * @author Dmitry Shnurenko
 */
public class MachineNode extends AbstractTreeNode {

    public final static String ROOT = "root";

    private final String                  id;
    private final String                  name;
    private final MachineNode             parent;
    private final Object                  data;
    private final List<MachineNode> children;

    @Inject
    public MachineNode(@Assisted MachineNode parent,
                       @Assisted("data") Object data,
                       @Assisted List<MachineNode> children) {
        this.parent = parent;
        this.data = data;
        this.children = children;

        boolean isMachine = data instanceof MachineStateDto;

        id = isMachine ? ((MachineStateDto)data).getId() : ROOT;
        name = isMachine ? ((MachineStateDto)data).getName() : ROOT;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public MachineNode getParent() {
        return parent;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    protected Promise<List<Node>> getChildrenImpl() {
        return PromiseHelper.<List<Node>>newPromise(new AsyncPromiseHelper.RequestCall<List<Node>>() {
            @Override
            public void makeCall(AsyncCallback<List<Node>> callback) {
                callback.onSuccess(new ArrayList<Node>(children));//guava?
            }
        });
    }

    @NotNull
    public Object getData() {
        return data;
    }
}
