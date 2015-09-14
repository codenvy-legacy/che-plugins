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
package org.eclipse.che.ide.ext.runner.client.tabs.console.panel;

import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.validation.constraints.NotNull;

/**
 * THe widget that show url where full log is located.
 *
 * @author Andrey Plotnikov
 */
public class FullLogMessageWidget extends HTML {
    @Inject
    public FullLogMessageWidget(RunnerResources resources, RunnerLocalizationConstant locale, @NotNull @Assisted String logUrl) {
        addStyleName(resources.runnerCss().logLink());

        Element text = DOM.createSpan();
        text.setInnerHTML(locale.fullLogTraceConsoleLink());

        Anchor link = new Anchor();
        link.setHref(logUrl);
        link.setText(logUrl);
        link.setTitle(logUrl);
        link.setTarget("_blank");
        link.getElement().getStyle().setColor("#61b7ef");

        getElement().appendChild(text);
        getElement().appendChild(link.getElement());
    }
}