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
package org.eclipse.che.ide.ext.java.client.documentation;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class QuickDocViewImpl implements QuickDocView {


    private final PopupPanel popupPanel;
    private ActionDelegate delegate;
    private Frame frame;

    @Inject
    public QuickDocViewImpl() {
        popupPanel = new PopupPanel(true, true);
        popupPanel.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                delegate.onCloseView();
            }
        });

        popupPanel.setSize("400px", "200px");
        Style style = popupPanel.getElement().getStyle();
        style.setProperty("resize", "both");
        style.setPaddingBottom(0, Style.Unit.PX);
        style.setPaddingTop(3, Style.Unit.PX);
        style.setPaddingLeft(3, Style.Unit.PX);
        style.setPaddingRight(3, Style.Unit.PX);
        createFrame();
        popupPanel.add(frame);

    }

    private void createFrame() {
        frame = new Frame();
        frame.setSize("100%", "100%");
        frame.getElement().getStyle().setBorderStyle(Style.BorderStyle.NONE);
        frame.getElement().setAttribute("sandbox", ""); // empty value, not null
        frame.getElement().getStyle().setProperty("resize", "both");
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public Widget asWidget() {
        return popupPanel;
    }

    @Override
    public void show(String url, int x, int y) {
        popupPanel.remove(frame);
        createFrame();
        popupPanel.add(frame);
        frame.setUrl(url);
        popupPanel.setPopupPosition(x, y);
        popupPanel.show();
    }
}
