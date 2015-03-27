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

import javax.validation.constraints.NotNull;

import org.eclipse.che.ide.ext.datasource.client.DatasourceUiResources;
import org.eclipse.che.ide.ext.datasource.shared.request.RequestResultDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Result header is displayed along (on top of) the results themselves. It shows information on the request and controls on the results.
 * 
 * @author "Mickaël Leduque"
 */
public class RequestResultHeaderImpl extends Composite implements RequestResultHeader {

    /** The default name for the CSV resource. */
    private static final String         DEFAULT_CSV_FILENAME = "data.csv";

    /** The template used to generate the header. */
    private static final HeaderTemplate TEMPLATE             = GWT.create(HeaderTemplate.class);

    private static int                  TRUNCATE_LIMIT       = 150;

    /** A reminder of the SQL query that caused this result. */
    @UiField
    SimplePanel                         queryReminderPlace;

    /** A button to export the content to a CSV resource. */
    @UiField
    SimplePanel                         csvButtonPlace;

    /** The place where the CSV link will be shown when built. */
    @UiField
    SimplePanel                         csvLinkPlace;

    /** The open/close marker. */
    @UiField
    Label                               openCloseMarker;

    @UiField(provided = true)
    final DatasourceUiResources         datasourceUiResources;

    private final RequestResultDelegate delegate;

    /** The delegate for open/close actions. */
    private OpenCloseDelegate           openCloseDelegate;

    /** the internal style used for layout purpose. */
    @UiField
    HeaderInternalStyle                 style;

    @AssistedInject
    public RequestResultHeaderImpl(@NotNull final DatasourceUiResources datasourceUiResources,
                                   @NotNull final RequestResultHeaderImplUiBinder uiBinder,
                                   @NotNull @Assisted final RequestResultDelegate delegate,
                                   @NotNull @Assisted final String query) {
        super();
        this.datasourceUiResources = datasourceUiResources;
        initWidget(uiBinder.createAndBindUi(this));

        setRequestReminder(query);

        this.delegate = delegate;
        addStyleName(datasourceUiResources.datasourceUiCSS().resultItemHeaderBar());

        setOpen(true);
    }

    @AssistedInject
    public RequestResultHeaderImpl(@NotNull final DatasourceUiResources datasourceUiResources,
                                   @NotNull final RequestResultHeaderImplUiBinder uiBinder,
                                   @NotNull final SqlRequestLauncherConstants constants,
                                   @NotNull @Assisted final RequestResultDelegate delegate,
                                   @NotNull @Assisted final RequestResultDTO requestResult) {
        this(datasourceUiResources, uiBinder, delegate, requestResult.getOriginRequest());
        withExportButton(requestResult, constants.exportCsvLabel());
    }

    private RequestResultHeaderImpl setRequestReminder(final String query) {
        // limit size of displayed query - just a bit over display overflow
        final String queryPart = query.substring(0, Math.min(query.length(), TRUNCATE_LIMIT));
        SafeHtml queryHtml = TEMPLATE.queryReminder(datasourceUiResources.datasourceUiCSS().resultItemQueryReminder(),
                                                    queryPart);
        final HTML queryReminder = new HTML(queryHtml);
        this.queryReminderPlace.setWidget(queryReminder);
        return this;
    }

    private RequestResultHeaderImpl withExportButton(final RequestResultDTO requestResult, final String text) {
        final Button exportButton = new Button(text);
        this.csvButtonPlace.setWidget(exportButton);
        exportButton.setStyleName(datasourceUiResources.datasourceUiCSS().resultItemCsvButton());
        exportButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent event) {
                delegate.triggerCsvExport(requestResult, RequestResultHeaderImpl.this);
            }
        });
        return this;
    }

    public void showCsvLink(final String contentData) {
        final Anchor csvLink = new Anchor(TEMPLATE.csvExportLink(datasourceUiResources.datasourceUiCSS().resultItemCsvLink(),
                                                                 contentData, "Download CSV", DEFAULT_CSV_FILENAME));
        this.csvLinkPlace.setWidget(csvLink);
    }

    @Override
    public void setOpenCloseDelegate(final OpenCloseDelegate delegate) {
        this.openCloseDelegate = delegate;
    }

    @UiHandler("openCloseMarker")
    public void handleOpenCloseClick(final ClickEvent event) {
        if (this.openCloseDelegate != null) {
            this.openCloseDelegate.onOpenClose();
        }
    }

    @Override
    public final void setOpen(final boolean open) {
        if (open) {
            this.openCloseMarker.removeStyleName(style.openCloseMarkerClosed());
        } else {
            this.openCloseMarker.addStyleName(style.openCloseMarkerClosed());
        }
    }

    /**
     * Template for the different pieces of the header.
     * 
     * @author "Mickaël Leduque"
     */
    interface HeaderTemplate extends SafeHtmlTemplates {

        /**
         * Template for the "query reminder" part of the result header. It shows the query that was made and gave this result.
         * 
         * @param className the CSS class name
         * @param query the query string to display
         * @return the html
         */
        @Template("<div class='{0}'>{1}</div>")
        SafeHtml queryReminder(String className, String query);

        /**
         * Template for the header CSV link part.
         * 
         * @param className the CSS class name
         * @param csvDataContent the Base64-encoded content to include in the data: URI
         * @param label the label of the link
         * @param filename the saved file name
         * @return the html
         */
        @Template("<a class='{0}' target='_blank' href='data:text/csv;charset=utf8;base64,{1}' download='{3}'>{2}</a>")
        SafeHtml csvExportLink(String className, String csvDataContent, String label, String filename);
    }

    /**
     * Interface for the control delegate for the RequestResultHeader actions.
     * 
     * @author "Mickaël Leduque"
     */
    public interface RequestResultDelegate {
        /**
         * Causes the given request result to be converted to CSV and sent to user.
         * 
         * @param requestResult the request result
         * @param target the header that triggered the action, to be updated on completion
         */
        void triggerCsvExport(RequestResultDTO requestResult, RequestResultHeaderImpl target);
    }

    /**
     * UIBinder interface for {@link RequestResultHeaderImpl}.
     * 
     * @author "Mickaël Leduque"
     */
    interface RequestResultHeaderImplUiBinder extends UiBinder<Widget, RequestResultHeaderImpl> {
    }

    public interface HeaderInternalStyle extends CssResource {

        @ClassName("openCloseMarker-closed")
        String openCloseMarkerClosed();
    }
}
