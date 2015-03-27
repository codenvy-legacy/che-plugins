/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/

package org.eclipse.che.plugin.tour.client.html;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.UriUtils;

import javax.inject.Singleton;

/**
 * Allow to use Wiki syntax hyperlink : [http://myurl my title]
 * @author Florent Benoit
 */
@Singleton
public class WikiHyperLinks {

    /**
     * Add the wiki hyperlink result from the given text
     * @param text the text to parse
     * @return the string with hyperlinks added inside
     */
    public String addLinks(String text) {

        RegExp REGEXP_LINKS = RegExp.compile("\\[.*?\\s*(.*?)\\]", "g");


        // wiki link : [http://www.codenvy.com] or [http://www.codenvy.com web site]
        MatchResult matchResult = REGEXP_LINKS.exec(text);


        if (matchResult != null && matchResult.getGroupCount() == 2) {
            String content = matchResult.getGroup(1);


            StringBuilder sb = new StringBuilder("<a href=\"");
            int space = content.indexOf(' ');
            if (space == -1) {
                sb.append(UriUtils.sanitizeUri(content));
                sb.append("\">");
                sb.append(UriUtils.sanitizeUri(content));
            } else {
                sb.append(UriUtils.sanitizeUri(content.substring(0, space)));
                sb.append("\">");
                sb.append(content.substring(space + 1, content.length()));
            }
            sb.append("</a>");

            String hyperLinksValue = text.replace(matchResult.getGroup(0), sb.toString());
            // Check also if there are other links
            return addLinks(hyperLinksValue);
        }

        return text;
    }
}
