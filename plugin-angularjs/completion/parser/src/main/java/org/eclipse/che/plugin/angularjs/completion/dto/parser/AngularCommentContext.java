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
package org.eclipse.che.plugin.angularjs.completion.dto.parser;


import org.eclipse.che.plugin.angularjs.completion.dto.parser.api.AngularDocType;
import org.eclipse.che.plugin.angularjs.completion.dto.parser.api.CommentContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Florent Benoit
 */
public class AngularCommentContext implements CommentContext {

    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("@(.*?)\\s(.*?)\n");

    private AngularDocType type;

    private String comment;


    private Map<String, List<String>> attributes;


    public AngularCommentContext(String type, String comment) {
        this.attributes = new HashMap<>();
        try {
            this.type = AngularDocType.valueOf(type.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            this.type = AngularDocType.UNKNOWN;
        }
        this.comment = comment;

        initAttributes();
    }


    protected void initAttributes() {
        Matcher matcher = ATTRIBUTE_PATTERN.matcher(comment);
        while (matcher.find()) {
            String attributeName = matcher.group(1).trim();
            String attributeValue = matcher.group(2);

            List<String> currentList = attributes.get(attributeName);
            if (currentList == null) {
                currentList = new ArrayList<>();
                attributes.put(attributeName, currentList);
            }
            currentList.add(attributeValue);

        }
    }


    public List<String> getAttributeValues(String attributeName) {
        return attributes.get(attributeName);
    }

    public String getAttributeValue(String attributeName) {
        List<String> values = attributes.get(attributeName);
        if (values != null && values.size() > 0) {
            return values.get(0);
        }
        return null;
    }

    public AngularDocType getType() {
        return type;
    }

}
