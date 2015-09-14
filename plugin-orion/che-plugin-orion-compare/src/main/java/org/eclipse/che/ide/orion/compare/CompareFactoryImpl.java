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
package org.eclipse.che.ide.orion.compare;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.callback.AsyncPromiseHelper;
import org.eclipse.che.api.promises.client.callback.PromiseHelper;
import org.eclipse.che.ide.jseditor.client.requirejs.ModuleHolder;
import org.eclipse.che.ide.jseditor.client.requirejs.RequireJsLoader;
import org.eclipse.che.ide.orion.compare.jso.CompareConfigJs;
import org.eclipse.che.ide.orion.compare.jso.CompareJs;
import org.eclipse.che.ide.orion.compare.jso.FileOptionsJs;

/**
 * @author Evgen Vidolob
 */
@Singleton
class CompareFactoryImpl implements CompareFactory {

    private final Promise<Boolean> loadPromise;
    private JavaScriptObject module;


    @Inject
    public CompareFactoryImpl(final RequireJsLoader loader, final ModuleHolder moduleHolder) {
        loadPromise = AsyncPromiseHelper.createFromAsyncRequest(new AsyncPromiseHelper.RequestCall<Boolean>() {
            @Override
            public void makeCall(final AsyncCallback<Boolean> callback) {
                // styler scripts are loaded on-demand by orion
                final String[] scripts = {"compare/built-compare-amd.min"};

                loader.require(new Callback<JavaScriptObject[], Throwable>() {
                    @Override
                    public void onFailure(Throwable reason) {
                        callback.onFailure(reason);
                    }

                    @Override
                    public void onSuccess(JavaScriptObject[] result) {
                        callback.onSuccess(Boolean.TRUE);
                    }
                }, scripts, new String[0]);
            }
        }).thenPromise(new Function<Boolean, Promise<Boolean>>() {
            @Override
            public Promise<Boolean> apply(Boolean arg) throws FunctionException {
                injectCssLink(GWT.getModuleBaseForStaticFiles() + "compare/built-compare.css");
                return AsyncPromiseHelper.createFromAsyncRequest(new AsyncPromiseHelper.RequestCall<Boolean>() {
                    @Override
                    public void makeCall(final AsyncCallback<Boolean> callback) {
                        loader.require(new Callback<JavaScriptObject[], Throwable>() {
                            @Override
                            public void onFailure(Throwable reason) {
                                callback.onFailure(reason);
                            }

                            @Override
                            public void onSuccess(JavaScriptObject[] result) {
                                module = moduleHolder.getModule("Compare");
                                callback.onSuccess(Boolean.TRUE);
                            }
                        }, new String[]{"compare/builder/compare"}, new String[]{"Compare"});
                    }
                });
            }
        });
    }

    @Override
    public Promise<Compare> createCompare(final CompareConfig config) {

        return loadPromise.thenPromise(new Function<Boolean, Promise<Compare>>() {
            @Override
            public Promise<Compare> apply(Boolean arg) throws FunctionException {
                return PromiseHelper.newPromise(new AsyncPromiseHelper.RequestCall<Compare>() {
                    @Override
                    public void makeCall(AsyncCallback<Compare> callback) {
                        Compare compare = CompareJs.createCompare(module, config);
                        callback.onSuccess(compare);
                    }
                });
            }
        });
    }

    @Override
    public Promise<Compare> createCompare(final CompareConfig config, final String commandSpanId) {
        return loadPromise.thenPromise(new Function<Boolean, Promise<Compare>>() {
            @Override
            public Promise<Compare> apply(Boolean arg) throws FunctionException {
                return PromiseHelper.newPromise(new AsyncPromiseHelper.RequestCall<Compare>() {
                    @Override
                    public void makeCall(AsyncCallback<Compare> callback) {
                        Compare compare = CompareJs.createCompare(module, config, commandSpanId);
                        callback.onSuccess(compare);
                    }
                });
            }
        });
    }

    @Override
    public Promise<Compare> createCompare(final CompareConfig config, final String commandSpanId, final String viewType) {
        return loadPromise.thenPromise(new Function<Boolean, Promise<Compare>>() {
            @Override
            public Promise<Compare> apply(Boolean arg) throws FunctionException {
                return PromiseHelper.newPromise(new AsyncPromiseHelper.RequestCall<Compare>() {
                    @Override
                    public void makeCall(AsyncCallback<Compare> callback) {
                        Compare compare = CompareJs.createCompare(module, config, commandSpanId, viewType);
                        callback.onSuccess(compare);
                    }
                });
            }
        });
    }

    @Override
    public Promise<Compare> createCompare(final CompareConfig config, final String commandSpanId, final String viewType, final  boolean toggleable) {
        return loadPromise.thenPromise(new Function<Boolean, Promise<Compare>>() {
            @Override
            public Promise<Compare> apply(Boolean arg) throws FunctionException {
                return PromiseHelper.newPromise(new AsyncPromiseHelper.RequestCall<Compare>() {
                    @Override
                    public void makeCall(AsyncCallback<Compare> callback) {
                        Compare compare = CompareJs.createCompare(module, config, commandSpanId, viewType, toggleable);
                        callback.onSuccess(compare);
                    }
                });
            }
        });
    }

    @Override
    public Promise<Compare> createCompare(final CompareConfig config, final String commandSpanId, final String viewType, final boolean toggleable,
                                          final String toggleCommandSpanId) {
        return loadPromise.thenPromise(new Function<Boolean, Promise<Compare>>() {
            @Override
            public Promise<Compare> apply(Boolean arg) throws FunctionException {
                return PromiseHelper.newPromise(new AsyncPromiseHelper.RequestCall<Compare>() {
                    @Override
                    public void makeCall(AsyncCallback<Compare> callback) {
                        Compare compare = CompareJs.createCompare(module, config, commandSpanId, viewType, toggleable, toggleCommandSpanId);
                        callback.onSuccess(compare);
                    }
                });
            }
        });
    }

    @Override
    public FileOptions createFieOptions() {
        return FileOptionsJs.createObject().<FileOptionsJs>cast();
    }

    @Override
    public CompareConfig createCompareConfig() {
        return CompareConfigJs.createObject().<CompareConfigJs>cast();
    }

    private static void injectCssLink(final String url) {
        final LinkElement link = Document.get().createLinkElement();
        link.setRel("stylesheet");
        link.setHref(url);
        nativeAttachToHead(link);
    }

    /**
     * Attach an element to document head.
     *
     * @param element the element to attach
     */
    private static native void nativeAttachToHead(Node element) /*-{
        $doc.getElementsByTagName("head")[0].appendChild(element);
    }-*/;
}
