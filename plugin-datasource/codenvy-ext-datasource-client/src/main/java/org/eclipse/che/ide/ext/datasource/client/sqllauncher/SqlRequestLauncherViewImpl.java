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
package org.eclipse.che.ide.ext.datasource.client.sqllauncher;

import java.util.Collection;

import org.eclipse.che.ide.ext.datasource.client.DatasourceUiResources;
import org.eclipse.che.ide.ext.datasource.client.common.SimpleView;
import org.eclipse.che.ide.ext.datasource.shared.MultipleRequestExecutionMode;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Implementation of the view component of the SQL request launcher.
 * 
 * @author "Mickaël Leduque"
 */
public class SqlRequestLauncherViewImpl extends SimpleView<SqlRequestLauncherView.ActionDelegate> implements SqlRequestLauncherView {

    private static final String                 EXECUTE_ALL   = "all";
    private static final String                 STOP_ON_ERROR = "stop";
    private static final String                 TRANSACTION   = "transaction";

    @UiField
    Widget                                      launcherContainer;

    /** The SQL edition zone. */
    @UiField
    AcceptsOneWidget                            editorZone;

    /** The request result display. */
    @UiField
    FlowPanel                                   resultZone;

    /** The request result display scroll component. */
    @UiField
    ScrollPanel                                 resultScroll;

    /** The label for the datasource selection widget. */
    @UiField
    Label                                       selectDatasourceLabel;

    /** The datasource selection widget. */
    @UiField
    ListBox                                     datasourceList;

    /** The label for the request result limit widget. */
    @UiField
    Label                                       resultLimitLabel;

    /** The request result limit widget. */
    @UiField
    TextBox                                     resultLimitInput;

    /** The label for the execution mode widget. */
    @UiField
    Label                                       executionModeLabel;

    /** The execution mode selection widget. */
    @UiField
    ListBox                                     executionModeList;

    /** The button that commands request execution. */
    @UiField
    Button                                      executeButton;

    /** The button that clears the result zone. */
    @UiField
    Label                                       clearResultsButton;

    /** The CSS resource. */
    @UiField(provided = true)
    protected final DatasourceUiResources       datasourceUiResources;

    /** The i18n constants. */
    @UiField(provided = true)
    protected final SqlRequestLauncherConstants constants;

    /** The split layout panel - needed so that we can set the splitter size */
    @UiField(provided = true)
    protected SplitLayoutPanel                  splitPanel;

    @Inject
    public SqlRequestLauncherViewImpl(final SqlRequestLauncherViewImplUiBinder uiBinder,
                                      final SqlRequestLauncherConstants constants,
                                      final DatasourceUiResources datasourceUiResources) {
        super();

        /* initialize provided fields */
        this.datasourceUiResources = datasourceUiResources;
        this.constants = constants;
        this.splitPanel = new SplitLayoutPanel(4);

        uiBinder.createAndBindUi(this);
        getContainer().add(this.launcherContainer);

        selectDatasourceLabel.setText(constants.selectDatasourceLabel());
        resultLimitLabel.setText(constants.resultLimitLabel());
        executionModeLabel.setText(constants.executionModeLabel());
        executeButton.setText(constants.executeButtonLabel());
        executeButton.ensureDebugId("dsExecuteButton");

        fillExecutionModeList(constants);
    }

    @Override
    public void setResultLimit(final int newResultLimit) {
        this.resultLimitInput.setValue(Integer.toString(newResultLimit));
    }

    @Override
    public AcceptsOneWidget getEditorZone() {
        return editorZone;
    }

    @Override
    public void appendResult(final Widget widget) {
        resultZone.add(widget);
    }

    @Override
    public void clearResultZone() {
        this.resultZone.clear();
    }

    @Override
    public void setDatasourceList(final Collection<String> datasourceIds) {
        if (datasourceIds == null || datasourceIds.isEmpty()) {
            this.datasourceList.clear();
            getDelegate().datasourceChanged(null);
            return;
        }

        // save the currently selected item
        int index = this.datasourceList.getSelectedIndex();
        String selectedValue = null;
        if (index != -1) {
            selectedValue = this.datasourceList.getValue(index);
        }

        this.datasourceList.clear();
        if (datasourceIds.size() > 1) {
            // add an empty item
            this.datasourceList.addItem("", "");
        }
        for (String datasourceId : datasourceIds) {
            this.datasourceList.addItem(datasourceId);
        }


        // restore selected value if needed
        if (index != -1) {
            for (int i = 0; i < this.datasourceList.getItemCount(); i++) {
                if (this.datasourceList.getItemText(i).equals(selectedValue)) {
                    this.datasourceList.setSelectedIndex(i);
                    break;
                }
            }
            getDelegate().datasourceChanged(getSelectedId());
        } else {
            if (this.datasourceList.getItemCount() == 1) {
                this.datasourceList.setSelectedIndex(0);
                getDelegate().datasourceChanged(this.datasourceList.getValue(0));
            }
        }
    }

    public String getSelectedId() {
        int index = this.datasourceList.getSelectedIndex();
        if (index != -1) {
            return this.datasourceList.getValue(index);
        } else {
            return null;
        }
    }

    @Override
    public void setExecutionMode(final MultipleRequestExecutionMode executionMode) {
        String searchValue = null;
        switch (executionMode) {
            case ONE_BY_ONE:
                searchValue = EXECUTE_ALL;
                break;
            case STOP_AT_FIRST_ERROR:
                searchValue = STOP_ON_ERROR;
                break;
            case TRANSACTIONAL:
                searchValue = TRANSACTION;
                break;
            default:
                return;
        }
        Integer foundIndex = null;
        for (int i = 0; i < this.executionModeList.getItemCount(); i++) {
            if (searchValue.equals(this.executionModeList.getValue(i))) {
                foundIndex = i;
                break;
            }
        }
        if (foundIndex != null) {
            for (int i = 0; i < this.executionModeList.getItemCount(); i++) {
                this.executionModeList.setItemSelected(i, (foundIndex == i));
            }
        }

    }

    /**
     * Handler for clicks on the execute button.
     * 
     * @param event the click event
     */
    @UiHandler("executeButton")
    void handleExecuteClick(final ClickEvent event) {
        getDelegate().executeRequested();
    }

    /**
     * Change handler on the datasource list input.
     * 
     * @param event the change event
     */
    @UiHandler("datasourceList")
    void handleDatasourceSelection(final ChangeEvent event) {
        getDelegate().datasourceChanged(getSelectedId());
    }

    /**
     * Change handler on the result limit input.
     * 
     * @param event the change event
     */
    @UiHandler("resultLimitInput")
    void handleResultLimitChange(final ChangeEvent event) {
        getDelegate().resultLimitChanged(this.resultLimitInput.getValue());
    }

    /**
     * Change handler on the execution mode input.
     * 
     * @param event the change event
     */
    @UiHandler("executionModeList")
    void handleExecutionModeChange(final ChangeEvent event) {
        final String newExecmode = this.executionModeList.getValue(this.executionModeList.getSelectedIndex());
        if (newExecmode == null) {
            getDelegate().executionModeChanged(null);
        } else if (newExecmode.equals(EXECUTE_ALL)) {
            getDelegate().executionModeChanged(MultipleRequestExecutionMode.ONE_BY_ONE);
        } else if (newExecmode.equals(STOP_ON_ERROR)) {
            getDelegate().executionModeChanged(MultipleRequestExecutionMode.STOP_AT_FIRST_ERROR);
        } else if (newExecmode.equals(TRANSACTION)) {
            getDelegate().executionModeChanged(MultipleRequestExecutionMode.TRANSACTIONAL);
        }
    }

    /**
     * Handler on the "clear results" button.
     * 
     * @param event the click event
     */
    @UiHandler("clearResultsButton")
    void handleResultLimitChange(final ClickEvent event) {
        getDelegate().clearResults();
    }

    /**
     * Sets up the items of the execution mode list input.
     * 
     * @param constants i18n constants object
     */
    private void fillExecutionModeList(final SqlRequestLauncherConstants constants) {
        this.executionModeList.addItem(constants.executeAllModeItem(), EXECUTE_ALL);
        this.executionModeList.addItem(constants.stopOnErrorModeitem(), STOP_ON_ERROR);
        /* Not supported at the moment. */
        /* this.executionModeList.addItem(constants.transactionModeItem(), TRANSACTION); */
    }

    /**
     * The UiBinder interface for this component.
     */
    interface SqlRequestLauncherViewImplUiBinder extends UiBinder<Widget, SqlRequestLauncherViewImpl> {
    }
}
