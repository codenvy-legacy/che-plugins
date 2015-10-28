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
package org.eclipse.che.ide.ext.openshift.client.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;

import org.eclipse.che.api.git.shared.Remote;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftLocalizationConstant;
import org.eclipse.che.ide.ext.openshift.shared.dto.BuildConfig;
import org.eclipse.che.ide.ui.listbox.CustomListBox;
import org.eclipse.che.ide.ui.list.CategoriesList;
import org.eclipse.che.ide.ui.list.Category;
import org.eclipse.che.ide.ui.list.CategoryRenderer;
import org.eclipse.che.ide.ui.window.Window;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * View implementation for {@LinkProjectWithExistingApplicationView}.
 *
 * @author Ann Shumilova
 */
public class LinkProjectWithExistingApplicationViewImpl extends Window implements LinkProjectWithExistingApplicationView {

    interface LinkProjectWithExistingApplicationViewUiBinder extends UiBinder<DockLayoutPanel, LinkProjectWithExistingApplicationViewImpl> {
    }

    private static LinkProjectWithExistingApplicationViewUiBinder uiBinder =
            GWT.create(LinkProjectWithExistingApplicationViewUiBinder.class);

    private ActionDelegate delegate;

    @UiField(provided = true)
    OpenshiftLocalizationConstant locale;

    @UiField
    SimplePanel categoriesPanel;

    @UiField
    TextBox buildConfigGitUrl;

    @UiField
    CustomListBox remoteUrls;

    @UiField
    TextBox remoteUrl;

    @UiField
    Label replaceWarningLabel;

    private Button linkButton;

    private Button cancelButton;

    private CategoriesList buildConfigList;

    private final Category.CategoryEventDelegate<BuildConfig> buildConfigDelegate =
            new Category.CategoryEventDelegate<BuildConfig>() {
                @Override
                public void onListItemClicked(Element listItemBase, BuildConfig itemData) {
                    delegate.onBuildConfigSelected(itemData);
                }
            };

    private final CategoryRenderer<BuildConfig> buildConfigCategoryRenderer = new CategoryRenderer<BuildConfig>() {
        @Override
        public void renderElement(Element element, BuildConfig data) {
            element.setInnerText(data.getMetadata().getName());
        }

        @Override
        public SpanElement renderCategory(Category<BuildConfig> category) {
            SpanElement element = Document.get().createSpanElement();
            element.setInnerText(category.getTitle().toUpperCase());
            return element;
        }
    };


    @Inject
    public LinkProjectWithExistingApplicationViewImpl(OpenshiftLocalizationConstant locale, CoreLocalizationConstant constants,
                                                      org.eclipse.che.ide.Resources resources) {
        this.locale = locale;
        ensureDebugId("linkProjectWithExistingApplication");
        setTitle(locale.linkProjectWithExistingApplicationViewTitle());

        setWidget(uiBinder.createAndBindUi(this));

        linkButton = createPrimaryButton(locale.linkProjectWithExistingApplicationLinkButton(),
                                         "linkProjectWithExistingApplication-link-button", new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        delegate.onLinkApplicationClicked();
                    }
                });
        addButtonToFooter(linkButton);

        cancelButton = createButton(constants.cancel(), "linkProjectWithExistingApplication-cancel-button", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        addButtonToFooter(cancelButton);

        buildConfigList = new CategoriesList(resources);
        categoriesPanel.add(buildConfigList);
        remoteUrl.setVisible(false);
        remoteUrls.setVisible(false);

        getWidget().getElement().getStyle().setPadding(0, Style.Unit.PX);
    }

    @Override
    public void showView() {
        show();
    }

    @Override
    public void closeView() {
        hide();
    }

    @Override
    public void setBuildConfigs(Map<String, List<BuildConfig>> buildConfigs) {
        buildConfigList.clear();
        List<Category<?>> categoriesList = new ArrayList<>();
        for (String categoryTitle : buildConfigs.keySet()) {
            Category<BuildConfig> category = new Category<BuildConfig>(categoryTitle,
                                                                       buildConfigCategoryRenderer,
                                                                       buildConfigs.get(categoryTitle),
                                                                       buildConfigDelegate);
            categoriesList.add(category);
        }
        buildConfigList.render(categoriesList);
    }

    @Override
    public void setBuildConfigGitUrl(String url) {
        buildConfigGitUrl.setValue(url);
    }

    @Override
    public void enableLinkButton(boolean isEnabled) {
        linkButton.setEnabled(isEnabled);
    }

    @Override
    public void setReplaceWarningMessage(String message) {
        replaceWarningLabel.getElement().setInnerHTML(message);
    }

    @Override
    public void setGitRemotes(List<Remote> remotes) {
        remoteUrl.setVisible(remotes.size() == 1);
        remoteUrls.setVisible(remotes.size() > 1);
        if (remotes.size() == 1) {
            remoteUrl.setValue(remotes.get(0).getUrl());
        } else {
            for(Remote remote: remotes){
                remoteUrls.addItem(remote.getUrl());
            }
            remoteUrls.isItemSelected(0);
        }
    }

    @Override
    public String getGitRemoteUrl() {
        if (remoteUrl.isVisible()) {
            return remoteUrl.getValue();
        }
        return remoteUrls.getValue();
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }
}
