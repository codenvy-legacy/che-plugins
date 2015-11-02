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
package org.eclipse.che.ide.ext.openshift.client.webhooks;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.ext.openshift.client.OpenshiftLocalizationConstant;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftResources;
import org.eclipse.che.ide.ext.openshift.shared.dto.WebHook;
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
public class ShowWebhooksViewImpl extends Window implements ShowWebhooksView {
    interface ShowWebhooksViewImplUiBinder extends UiBinder<Widget, ShowWebhooksViewImpl> {
    }

    private static ShowWebhooksViewImplUiBinder ourUiBinder = GWT.create(ShowWebhooksViewImplUiBinder.class);

    @UiField
    FlowPanel webhooksPanel;

    private final OpenshiftLocalizationConstant locale;
    private final ClipboardButtonBuilder        buttonBuilder;
    private final OpenshiftResources            openshiftResources;
    private       ActionDelegate                delegate;

    @Inject
    protected ShowWebhooksViewImpl(OpenshiftLocalizationConstant locale,
                                   ClipboardButtonBuilder buttonBuilder,
                                   OpenshiftResources openshiftResources) {
        this.locale = locale;
        this.buttonBuilder = buttonBuilder;
        this.openshiftResources = openshiftResources;
        this.ensureDebugId("openshiftWebhook-window");

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setTitle(locale.webhookWindowTitle());
        this.setWidget(widget);

        Button btnClose = createButton(locale.buttonClose(), "openshiftWebhook-btnClose", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCloseClicked();
            }
        });
        addButtonToFooter(btnClose);
    }

    @Override
    public void close() {
        this.hide();
    }

    @Override
    public void showDialog() {
        this.show();
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onClose() {
    }

    @Override
    public void setWebhooks(@NotNull List<WebHook> webHooks) {
        webhooksPanel.clear();

        this.setTitle(webHooks.size() <= 1 ? locale.webhookWindowTitle() : locale.webhooksWindowTitle());

        if (webHooks.size() == 0) {
            webhooksPanel.add(new Label(locale.noWebhookLabel()));
            return;
        }

        for (WebHook webHook : webHooks) {
            final Label label = new Label(webHook.getType());
            label.addStyleName(openshiftResources.css().sectionTitle());
            webhooksPanel.add(label);

            displayReadOnlyBox(webhooksPanel, locale.webhookURLLabelTitle(), webHook.getUrl());
            displayReadOnlyBox(webhooksPanel, locale.webhookSecretLabelTitle(), webHook.getSecret());
        }
    }

    private void displayReadOnlyBox(Panel parent, String label, String content) {
        final FlowPanel container = new FlowPanel();
        parent.add(container);
        container.add(new Label(label));

        final TextBox readOnlyBox = new TextBox();
        readOnlyBox.setReadOnly(true);
        readOnlyBox.setText(content);
        container.add(readOnlyBox);

        buttonBuilder.withResourceWidget(readOnlyBox).build();
    }
}
