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
package org.eclipse.che.ide.ext.datasource.client.common;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;

public class AlignableColumnHeader extends SafeHtmlHeader {

    public AlignableColumnHeader(final String text, final HorizontalAlignmentConstant alignment) {
        super(getSafeHtml(text, alignment));
    }

    private static SafeHtml getSafeHtml(final String text, final HorizontalAlignmentConstant alignment) {
        SafeHtmlBuilder safeBuilder = new SafeHtmlBuilder();
        boolean needCloseTag = false;
        if (HasHorizontalAlignment.ALIGN_RIGHT.equals(alignment)) {
            safeBuilder.appendHtmlConstant("<p style=\"text-align:right;\">");
            needCloseTag = true;
        } else if (HasHorizontalAlignment.ALIGN_CENTER.equals(alignment)) {
            safeBuilder.appendHtmlConstant("<p style=\"text-align:center;\">");
            needCloseTag = true;
        }
        safeBuilder.appendEscaped(text);
        if (needCloseTag) {
            safeBuilder.appendHtmlConstant("</p>");
        }
        return safeBuilder.toSafeHtml();
    }
}
