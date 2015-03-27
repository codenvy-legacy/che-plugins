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


import org.eclipse.che.dto.server.DtoFactory;
import org.eclipse.che.plugin.angularjs.completion.dto.AngularTemplate;
import org.eclipse.che.plugin.angularjs.completion.dto.Event;
import org.eclipse.che.plugin.angularjs.completion.dto.TemplateDotProvider;
import org.eclipse.che.plugin.angularjs.completion.dto.parser.api.AngularDocType;
import org.eclipse.che.plugin.angularjs.completion.dto.parser.api.CodeCommentParser;
import org.eclipse.che.plugin.angularjs.completion.dto.parser.api.CommentContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse events
 * @author Florent Benoit
 */
public class EventCommentParser implements CodeCommentParser {

    private DtoFactory dtoFactory;

    private AngularTemplate angularTemplate;

    private static final Pattern EVENT_PATTERN = Pattern.compile("(.*?)#(.*)");


    public EventCommentParser(DtoFactory dtoFactory, AngularTemplate angularTemplate) {
        this.dtoFactory = dtoFactory;
        this.angularTemplate = angularTemplate;
    }

    @Override
    public void onComment(CommentContext commentContext) {
        // Create a new event
        Event event = dtoFactory.createDto(Event.class);

        String eventName = commentContext.getAttributeValue("name");

        // extract method name
        Matcher methodNameMatcher = EVENT_PATTERN.matcher(eventName);
        if (methodNameMatcher.find()) {
            // get provider from method name
            String directiveName = methodNameMatcher.group(1);
            String eName = methodNameMatcher.group(2);

            event.setName(eName);

            TemplateDotProvider templateDotProvider = angularTemplate.getTemplateProvider(directiveName);
            if (templateDotProvider != null) {
                templateDotProvider.getEvents().add(event);
            }

        }


    }

    @Override
    public AngularDocType getSupportedType() {
        return AngularDocType.EVENT;
    }
}
