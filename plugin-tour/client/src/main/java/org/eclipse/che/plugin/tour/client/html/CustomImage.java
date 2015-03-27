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
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.UriUtils;

import javax.inject.Singleton;

/**
 * This class inserts image based on the custom markdown syntax ![aklt name](src of the image = widthxheight)
 * The height value is optional.
 * @author Florent Benoit
 */
@Singleton
public class CustomImage {

    /**
     * Add the markdown images from the given text
     * @param text the text to parse
     * @return the string with images added inside
     */
    public String addImages(String text) {

        RegExp REGEXP_IMAGES       = RegExp.compile("!\\[(.*?)\\]\\((.+?)\\)", "g");
        RegExp REGEXP_IMAGE_DETAIL = RegExp.compile("(.*?)\\s*=\\s*(\\d+)x(\\d*)", "g");

        //Image format : ![](./pic/pic1_50.png =100x20)
        MatchResult matchResult = REGEXP_IMAGES.exec(text);

        if (matchResult != null && matchResult.getGroupCount() == 3) {
            String alt = matchResult.getGroup(1);
            String uri = matchResult.getGroup(2);
            int width = -1;
            int height = -1;

            MatchResult imgDetails = REGEXP_IMAGE_DETAIL.exec(uri);
            if (imgDetails != null && imgDetails.getGroupCount() == 4) {
                uri = imgDetails.getGroup(1);
                width = Integer.parseInt(imgDetails.getGroup(2));
                String valHeight = imgDetails.getGroup(3);
                if (!valHeight.isEmpty()) {
                    height = Integer.parseInt(imgDetails.getGroup(3));
                }
            }

            StringBuilder imageData = new StringBuilder();
            imageData.append("<img src='");
            imageData.append(UriUtils.sanitizeUri(uri));
            imageData.append("' alt='");
            imageData.append(SafeHtmlUtils.fromString(alt).asString());
            imageData.append("'");
            if (width != -1) {
                imageData.append(" width='");
                imageData.append(width);
                imageData.append("'");
            }
            if (height != -1) {
                imageData.append(" height='");
                imageData.append(height);
                imageData.append("'");
            }


            imageData.append(" />");

            return text.replace(matchResult.getGroup(0), imageData.toString());
        }

        return text;
    }


}
