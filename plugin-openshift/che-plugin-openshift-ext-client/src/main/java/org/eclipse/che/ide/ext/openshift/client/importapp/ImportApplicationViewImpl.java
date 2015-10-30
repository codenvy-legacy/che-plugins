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
package org.eclipse.che.ide.ext.openshift.client.importapp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;

import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftLocalizationConstant;
import org.eclipse.che.ide.ext.openshift.shared.dto.BuildConfig;
import org.eclipse.che.ide.ui.list.CategoriesList;
import org.eclipse.che.ide.ui.list.Category;
import org.eclipse.che.ide.ui.list.CategoryRenderer;
import org.eclipse.che.ide.ui.window.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * View implementation for {@ImportApplicationView}.
 *
 * @author Anna Shumilova
 */
public class ImportApplicationViewImpl extends Window implements ImportApplicationView {

    interface ImportApplicationViewUiBinder extends UiBinder<DockLayoutPanel, ImportApplicationViewImpl> {
    }

    private static ImportApplicationViewUiBinder uiBinder =
            GWT.create(ImportApplicationViewUiBinder.class);

    private ActionDelegate delegate;

    @UiField(provided = true)
    OpenshiftLocalizationConstant locale;

    @UiField
    TextBox projectName;

    @UiField
    TextArea projectDescription;

    @UiField
    Label branchName;

    @UiField
    Label contextDir;

    @UiField
    Label sourceUrl;

    @UiField
    SimplePanel categoriesPanel;

    @UiField
    FlowPanel branchPanel;

    @UiField
    FlowPanel contextDirPanel;

    private Button importButton;

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
    public ImportApplicationViewImpl(OpenshiftLocalizationConstant locale, CoreLocalizationConstant constants,
                                     org.eclipse.che.ide.Resources resources) {
        this.locale = locale;
        ensureDebugId("importApplication");
        setTitle(locale.importApplicationViewTitle());

        setWidget(uiBinder.createAndBindUi(this));

        importButton = createPrimaryButton(locale.importApplicationImportButton(),
                                           "importApplication-import-button", new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        delegate.onImportApplicationClicked();
                    }
                });
        addButtonToFooter(importButton);

        cancelButton = createButton(constants.cancel(), "importApplication-cancel-button", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        addButtonToFooter(cancelButton);

        buildConfigList = new CategoriesList(resources);
        categoriesPanel.add(buildConfigList);
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
    public void enableImportButton(boolean isEnabled) {
        importButton.setEnabled(isEnabled);
    }

    @Override
    public void setProjectName(String name) {
        projectName.setValue(name);
    }

    @Override
    public void setProjectDescription(String description) {
        projectDescription.setValue(description);
    }

    @Override
    public void setApplicationInfo(BuildConfig buildConfig) {
        sourceUrl.setText(buildConfig.getSpec().getSource().getGit().getUri());
        contextDir.setText(buildConfig.getSpec().getSource().getContextDir());
        branchName.setText(buildConfig.getSpec().getSource().getGit().getRef());

        contextDirPanel.setVisible(
                buildConfig.getSpec().getSource().getContextDir() != null && !buildConfig.getSpec().getSource().getContextDir().isEmpty());
        branchPanel.setVisible(buildConfig.getSpec().getSource().getGit().getRef() != null &&
                               !buildConfig.getSpec().getSource().getGit().getRef().isEmpty());
    }

    @Override
    public String getProjecName() {
        return projectName.getValue();
    }

    @Override
    public String getProjectDescription() {
        return projectDescription.getValue();
    }

    @Override
    public void setErrorMessage(String message) {
        //TODO
    }

    @UiHandler({"projectName"})
    public void onProjectNameChanged(KeyUpEvent event) {
        delegate.onProjectNameChanged(projectName.getValue());
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }
}
