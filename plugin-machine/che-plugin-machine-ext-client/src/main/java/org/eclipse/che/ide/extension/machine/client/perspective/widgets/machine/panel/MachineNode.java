package org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.panel;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.callback.AsyncPromiseHelper;
import org.eclipse.che.api.promises.client.callback.PromiseHelper;
import org.eclipse.che.ide.api.project.node.AbstractTreeNode;
import org.eclipse.che.ide.api.project.node.Node;
import org.eclipse.che.ide.extension.machine.client.machine.Machine;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class MachineNode extends AbstractTreeNode {

    private Machine machine;

    @Inject
    public MachineNode(@Assisted Machine machine) {
        this.machine = machine;
    }

    @Override
    protected Promise<List<Node>> getChildrenImpl() {
       return PromiseHelper.<List<Node>>newPromise(new AsyncPromiseHelper.RequestCall<List<Node>>() {
            @Override
            public void makeCall(AsyncCallback<List<Node>> callback) {
                callback.onSuccess(Collections.<Node>emptyList());
            }
        });
    }

    @Override
    public String getName() {
        return machine.getDisplayName();
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    public Machine getData() {
        return machine;
    }
}
