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
package org.eclipse.che.ide.ext.openshift.client.url;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.ext.openshift.client.OpenshiftLocalizationConstant;
import org.eclipse.che.ide.ui.window.Window;
import org.eclipse.che.ide.ui.zeroclipboard.ClipboardButtonBuilder;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * The implementation of View.
 *
 * @author Sergii Leschenko
 */
@Singleton
public class ShowApplicationUrlViewImpl extends Window implements ShowApplicationUrlView {
    interface ShowApplicationUrlViewImplUiBinder extends UiBinder<Widget, ShowApplicationUrlViewImpl> {
    }

    private static ShowApplicationUrlViewImplUiBinder ourUiBinder = GWT.create(ShowApplicationUrlViewImplUiBinder.class);

    @UiField
    FlowPanel urlsPanel;

    private final OpenshiftLocalizationConstant locale;
    private final ClipboardButtonBuilder buttonBuilder;
    private       ActionDelegate         delegate;

    @Inject
    protected ShowApplicationUrlViewImpl(OpenshiftLocalizationConstant locale,
                                         ClipboardButtonBuilder buttonBuilder) {
        this.locale = locale;
        this.buttonBuilder = buttonBuilder;
        this.ensureDebugId("openshiftApplicationUrl-window");

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setTitle(locale.applicationURLWindowTitle());
        this.setWidget(widget);

        Button btnClose = createButton(locale.buttonClose(), "openshiftApplicationUrl-btnClose", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCloseClicked();
            }
        });
        addButtonToFooter(btnClose);
    }

    @Override
    public void setURLs(@NotNull List<String> urLs) {
        urlsPanel.clear();

        this.setTitle(urLs.size() <= 1 ? locale.applicationURLWindowTitle() : locale.applicationURLsWindowTitle());

        if (urLs.size() == 0) {
            urlsPanel.add(new Label(locale.noApplicationUrlLabel()));
            return;
        }

        for (String url : urLs) {
            if (url == null) {
                continue;
            }

            TextBox remoteUrl = new TextBox();
            remoteUrl.setReadOnly(true);
            remoteUrl.setText(url);

            remoteUrl.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    final TextBox source = (TextBox)event.getSource();
                    com.google.gwt.user.client.Window.open("http://" + source.getText(), "_blank", "");
                }
            });

            urlsPanel.add(remoteUrl);
            buttonBuilder.withResourceWidget(remoteUrl).build();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
    }
}
