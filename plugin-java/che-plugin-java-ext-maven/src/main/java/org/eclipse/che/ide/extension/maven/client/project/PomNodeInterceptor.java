package org.eclipse.che.ide.extension.maven.client.project;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.ide.api.project.node.Node;
import org.eclipse.che.ide.api.project.node.interceptor.NodeInterceptor;
import org.eclipse.che.ide.extension.maven.client.MavenResources;
import org.eclipse.che.ide.project.node.FileReferenceNode;

import java.util.List;

/**
 * @author Vlad Zhukovskiy
 */
@Singleton
public class PomNodeInterceptor implements NodeInterceptor {

    private MavenResources resources;

    @Inject
    public PomNodeInterceptor(MavenResources resources) {
        this.resources = resources;
    }

    @Override
    public Promise<List<Node>> intercept(Node parent, List<Node> children) {
        for (Node child : children) {
            if (child instanceof FileReferenceNode && "pom.xml".equals(child.getName())) {
                FileReferenceNode pom = (FileReferenceNode)child;
                pom.getPresentation(false).setPresentableIcon(resources.maven());
            }
        }

        return Promises.resolve(children);
    }

    @Override
    public Integer weightOrder() {
        return 0;
    }
}
