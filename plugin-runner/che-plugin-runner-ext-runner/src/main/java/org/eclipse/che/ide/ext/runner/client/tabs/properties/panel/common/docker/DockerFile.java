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
package org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.docker;

import org.eclipse.che.api.core.rest.shared.dto.Link;
import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ItemReference;
import org.eclipse.che.ide.api.project.tree.TreeStructure;
import org.eclipse.che.ide.api.project.tree.generic.FileNode;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.util.loging.Log;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;

/**
 * The file node that represents recipe file item in the project explorer tree. It needs just for opening recipe for editing (it is a
 * problem of API of editor agent).
 *
 * @author Artem Zatsarynnyy
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 */
public class DockerFile extends FileNode {

    public static final String GET_CONTENT = "get content";

    public DockerFile(@Nonnull EventBus eventBus,
                      @Nonnull ProjectServiceClient projectServiceClient,
                      @Nonnull DtoUnmarshallerFactory dtoUnmarshallerFactory,
                      @Nonnull ItemReference data,
                      @Nonnull TreeStructure treeStructure) {
        super(null, data, treeStructure, eventBus, projectServiceClient, dtoUnmarshallerFactory);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isReadOnly() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void getContent(AsyncCallback<String> callback) {
        for (Link link : getData().getLinks()) {
            if (GET_CONTENT.equals(link.getRel())) {
                sendRequest(callback, link.getHref());
                break;
            }
        }
    }

    private void sendRequest(@Nonnull final AsyncCallback<String> callback, @Nonnull String href) {
        try {
            new RequestBuilder(RequestBuilder.GET, href).sendRequest("", new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    callback.onSuccess(response.getText());
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    Log.error(DockerFile.class, exception);
                }
            });
        } catch (RequestException e) {
            Log.error(DockerFile.class, e);
        }
    }

}