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
package org.eclipse.che.ide.ext.github.client.importer.page;

import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.ext.github.client.GitHubLocalizationConstant;
import org.eclipse.che.ide.ext.github.client.GitHubResources;
import org.eclipse.che.ide.ext.github.client.load.ProjectData;

import org.eclipse.che.ide.ui.Styles;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Roman Nikitenko
 */
public class GithubImporterPageViewImpl extends Composite implements GithubImporterPageView {

    interface GithubImporterPageViewImplUiBinder extends UiBinder<DockLayoutPanel, GithubImporterPageViewImpl> {
    }

    private ActionDelegate delegate;

    @UiField(provided = true)
    GithubStyle            style;
    @UiField
    Label                  labelUrlError;
    @UiField
    TextBox                projectName;
    @UiField
    TextArea               projectDescription;
    @UiField
    RadioButton            projectPrivate;
    @UiField
    RadioButton            projectPublic;
    @UiField
    TextBox                projectUrl;
    @UiField
    FlowPanel              bottomPanel;
    @UiField
    DockLayoutPanel        githubPanel;
    @UiField
    Button                 loadRepo;
    @UiField
    ListBox                accountName;
    @UiField(provided = true)
    CellTable<ProjectData> repositories;
    @UiField(provided = true)
    final GitHubResources            resources;
    @UiField(provided = true)
    final GitHubLocalizationConstant locale;

    @Inject
    public GithubImporterPageViewImpl(GitHubResources resources,
                                      GitHubLocalizationConstant locale,
                                      Resources ideResources,
                                      GithubImporterPageViewImplUiBinder uiBinder) {

        this.resources = resources;
        this.locale = locale;

        style = resources.githubImporterPageStyle();
        style.ensureInjected();

        createRepositoriesTable(ideResources);
        initWidget(uiBinder.createAndBindUi(this));

        projectName.getElement().setAttribute("maxlength", "32");
        projectDescription.getElement().setAttribute("maxlength", "256");
        closeGithubPanel();

        loadRepo.addStyleName(ideResources.Css().buttonLoader());
        loadRepo.sinkEvents(Event.ONCLICK);
        loadRepo.addHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onLoadRepoClicked();

            }
        }, ClickEvent.getType());
    }

    /**
     * Creates table what contains list of available repositories.
     *
     * @param ideResources
     */
    private void createRepositoriesTable(@Nonnull Resources ideResources) {
        repositories = new CellTable<ProjectData>(15, ideResources);

        Column<ProjectData, ImageResource> iconColumn = new Column<ProjectData, ImageResource>(new ImageResourceCell()) {
            @Override
            public ImageResource getValue(ProjectData item) {
                return resources.project();
            }
        };

        Column<ProjectData, SafeHtml> repositoryColumn = new Column<ProjectData, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(final ProjectData item) {
                return SafeHtmlUtils.fromString(item.getName());
            }
        };

        Column<ProjectData, SafeHtml> descriptionColumn = new Column<ProjectData, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(final ProjectData item) {
                return new SafeHtmlBuilder()
                        .appendHtmlConstant("<span>")
                        .appendEscaped(item.getDescription())
                        .appendHtmlConstant("</span>")
                        .toSafeHtml();
            }
        };

        repositories.addColumn(iconColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
        repositories.setColumnWidth(iconColumn, 28, Style.Unit.PX);

        repositories.addColumn(repositoryColumn, locale.samplesListRepositoryColumn());
        repositories.addColumn(descriptionColumn, locale.samplesListDescriptionColumn());

        // don't show loading indicator
        repositories.setLoadingIndicator(null);

        final SingleSelectionModel<ProjectData> selectionModel = new SingleSelectionModel<ProjectData>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                ProjectData selectedObject = selectionModel.getSelectedObject();
                delegate.onRepositorySelected(selectedObject);
            }
        });
        repositories.setSelectionModel(selectionModel);
    }

    @UiHandler("projectName")
    void onProjectNameChanged(KeyUpEvent event) {
        String projectNameValue = projectName.getValue();

        if (projectNameValue != null && projectNameValue.contains(" ")) {
            projectNameValue = projectNameValue.replace(" ", "-");
            projectName.setValue(projectNameValue);
        }

        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            return;
        }

        delegate.projectNameChanged(projectName.getValue());
    }

    @UiHandler("projectUrl")
    void onProjectUrlChanged(KeyUpEvent event) {
        delegate.projectUrlChanged(projectUrl.getValue());
    }

    @UiHandler("projectDescription")
    void onProjectDescriptionChanged(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            return;
        }
        delegate.projectDescriptionChanged(projectDescription.getValue());
    }

    @UiHandler({"projectPublic", "projectPrivate"})
    void visibilityHandler(ValueChangeEvent<Boolean> event) {
        delegate.projectVisibilityChanged(projectPublic.getValue());
    }

    @UiHandler("accountName")
    public void onAccountChange(ChangeEvent event) {
        delegate.onAccountChanged();
    }

    @Override
    public void setProjectUrl(@Nonnull String url) {
        projectUrl.setText(url);
        delegate.projectUrlChanged(url);
    }

    @Override
    public void setVisibility(boolean visible) {
        projectPublic.setValue(visible, false);
        projectPrivate.setValue(!visible, false);
    }

    @Override
    public void reset() {
        projectUrl.setText("");
        projectName.setText("");
        projectDescription.setText("");
        projectPublic.setValue(true);
        projectPrivate.setValue(false);
        githubPanel.removeFromParent();
        hideUrlError();
        hideNameError();
    }

    @Override
    public void showNameError() {
        projectName.addStyleName(style.inputError());
    }

    @Override
    public void hideNameError() {
        projectName.removeStyleName(style.inputError());
    }

    @Override
    public void showUrlError(@Nonnull String message) {
        projectUrl.addStyleName(style.inputError());
        labelUrlError.setText(message);
    }

    @Override
    public void hideUrlError() {
        projectUrl.removeStyleName(style.inputError());
        labelUrlError.setText("");
    }

    @Nonnull
    @Override
    public String getProjectName() {
        return projectName.getValue();
    }

    @Override
    public void setProjectName(@Nonnull String projectName) {
        this.projectName.setValue(projectName);
        delegate.projectNameChanged(projectName);
    }

    @Override
    public void setProjectDescription(@Nonnull String projectDescription) {
        this.projectDescription.setText(projectDescription);
        delegate.projectDescriptionChanged(projectDescription);
    }

    @Override
    public void focusInUrlInput() {
        projectUrl.setFocus(true);
    }

    @Override
    public void setInputsEnableState(boolean isEnabled) {
        projectName.setEnabled(isEnabled);
        projectDescription.setEnabled(isEnabled);
        projectUrl.setEnabled(isEnabled);

        if (isEnabled) {
            focusInUrlInput();
        }
    }

    @Override
    public void setDelegate(@Nonnull ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setRepositories(@Nonnull Array<ProjectData> repositories) {
        // Wraps Array in java.util.List
        List<ProjectData> list = new ArrayList<ProjectData>();
        for (int i = 0; i < repositories.size(); i++) {
            list.add(repositories.get(i));
        }
        this.repositories.setRowData(list);
    }

    @Nonnull
    @Override
    public String getAccountName() {
        int index = accountName.getSelectedIndex();
        return index != -1 ? accountName.getItemText(index) : "";
    }

    @Override
    public void setAccountNames(@Nonnull Array<String> names) {
        this.accountName.clear();
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            this.accountName.addItem(name);
        }
    }

    @Override
    public void closeGithubPanel() {
        githubPanel.removeFromParent();
    }

    @Override
    public void showGithubPanel() {
        bottomPanel.add(githubPanel);
    }

    @Override
    public void setLoaderVisibility(boolean isVisible) {
        if (isVisible) {
            loadRepo.setHTML("<i></i>");
            loadRepo.setEnabled(false);
        } else {
            loadRepo.setText("Load Repo");
            loadRepo.setEnabled(true);
        }
    }

    public interface GithubStyle extends Styles {
        String mainPanel();

        String namePanel();

        String labelPosition();

        String marginTop();

        String alignRight();

        String alignLeft();

        String labelErrorPosition();

        String radioButtonPosition();

        String description();

        String label();

        String horizontalLine();

        String bottomSpace();

        String textPosition();

        String rightSpace();

        String loadRepo();

    }
}
