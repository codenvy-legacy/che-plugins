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
package org.eclipse.che.ide.ext.openshift.client.project.wizard.page.template;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.callback.AsyncPromiseHelper;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.ide.api.wizard.AbstractWizardPage;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftServiceClient;
import org.eclipse.che.ide.ext.openshift.shared.dto.Template;
import org.eclipse.che.ide.ext.openshift.client.dto.NewApplicationRequest;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.rest.Unmarshallable;

import java.util.List;

import static org.eclipse.che.api.promises.client.callback.AsyncPromiseHelper.createFromAsyncRequest;

/**
 * Presenter for select template wizard page.
 *
 * @author Vlad Zhukovskiy
 */
@Singleton
public class SelectTemplatePresenter extends AbstractWizardPage<NewApplicationRequest> implements SelectTemplateView.ActionDelegate {

    private final SelectTemplateView     view;
    private final OpenshiftServiceClient openshiftClient;
    private final DtoUnmarshallerFactory dtoUnmarshaller;

    public static final String DEF_NAMESPACE = "openshift";

    private Template template;

    @Inject
    public SelectTemplatePresenter(SelectTemplateView view,
                                   OpenshiftServiceClient openshiftClient,
                                   DtoUnmarshallerFactory dtoUnmarshaller) {
        this.view = view;
        this.openshiftClient = openshiftClient;
        this.dtoUnmarshaller = dtoUnmarshaller;

        view.setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public void init(NewApplicationRequest dataObject) {
        super.init(dataObject);

        getTemplateList(DEF_NAMESPACE).thenPromise(showTemplates(false));
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return template != null;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    private Promise<List<Template>> getTemplateList(String namespace) {
        return createFromAsyncRequest(getTemplateListRC(namespace));
    }

    private AsyncPromiseHelper.RequestCall<List<Template>> getTemplateListRC(final String namespace) {
        return new AsyncPromiseHelper.RequestCall<List<Template>>() {
            @Override
            public void makeCall(AsyncCallback<List<Template>> callback) {
                openshiftClient.getTemplates(namespace, _callback(callback, dtoUnmarshaller.newListUnmarshaller(Template.class)));
            }
        };
    }

    private Function<List<Template>, Promise<List<Template>>> showTemplates(final boolean keepExisting) {
        return new Function<List<Template>, Promise<List<Template>>>() {
            @Override
            public Promise<List<Template>> apply(List<Template> templates) throws FunctionException {
                view.setTemplates(templates, keepExisting);
                return Promises.resolve(templates);
            }
        };
    }

    protected <T> AsyncRequestCallback<T> _callback(final AsyncCallback<T> callback, Unmarshallable<T> u) {
        return new AsyncRequestCallback<T>(u) {
            @Override
            protected void onSuccess(T result) {
                callback.onSuccess(result);
            }

            @Override
            protected void onFailure(Throwable e) {
                callback.onFailure(e);
            }
        };
    }

    /** {@inheritDoc} */
    @Override
    public void onTemplateSelected(Template template) {
        this.template = template;
        dataObject.setTemplate(template);
        updateDelegate.updateControls();
    }
}
