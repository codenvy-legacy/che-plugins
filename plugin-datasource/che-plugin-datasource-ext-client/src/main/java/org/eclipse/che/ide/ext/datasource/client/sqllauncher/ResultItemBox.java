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
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Widget that embeds one result item (one table, one row count or one error.
 * 
 * @author "Mickaël Leduque"
 */
public class ResultItemBox extends Composite implements RequestResultHeader.OpenCloseDelegate {

    private static ResultItemBoxUiBinder uiBinder = GWT.create(ResultItemBoxUiBinder.class);

    @UiField
    HTMLPanel                            mainPanel;

    @UiField(provided = true)
    RequestResultHeader                  header;

    @UiField
    FlowPanel                            resultPlace;

    @UiField
    SimplePanel                          footerPlace;

    @UiField(provided = true)
    final DatasourceUiResources          datasourceUiResources;

    @UiField
    InternalStyle                        style;

    /** The opened/closed state of the component. */
    private boolean                      opened   = true;

    @Inject
    public ResultItemBox(final @NotNull DatasourceUiResources datasourceUiResources,
                         final @NotNull @Assisted RequestResultHeader header) {
        this.datasourceUiResources = datasourceUiResources;
        this.header = header;
        initWidget(uiBinder.createAndBindUi(this));

        header.setOpenCloseDelegate(this);
    }

    /**
     * Insert a widget in the result section.
     * 
     * @param item the widget to include
     */
    public void addResultItem(final Widget item) {
        resultPlace.add(item);
    }

    /**
     * Decides the widget used as result footer.
     * 
     * @param footer the footer
     */
    public void setFooter(final Widget footer) {
        footerPlace.setWidget(footer);
    }

    @Override
    public void onOpenClose() {
        this.opened = !this.opened;
        setContentVisible(this.opened);
        this.header.setOpen(this.opened);
    }

    private void setContentVisible(boolean visible) {
        if (visible) {
            this.resultPlace.removeStyleName(style.folded());
            this.footerPlace.removeStyleName(style.folded());
        } else {
            this.resultPlace.addStyleName(style.folded());
            this.footerPlace.addStyleName(style.folded());
        }
    }

    /**
     * UIBinder interface for the ResultItemBox.
     * 
     * @author "Mickaël Leduque"
     */
    interface ResultItemBoxUiBinder extends UiBinder<Widget, ResultItemBox> {
    }

    public interface InternalStyle extends CssResource {
        String folded();
    }
}
