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
import org.eclipse.che.plugin.angularjs.completion.dto.parser.api.CodeCommentParser;
import org.eclipse.che.plugin.angularjs.completion.dto.AngularTemplate;
import org.eclipse.che.plugin.angularjs.completion.dto.NgObject;
import org.eclipse.che.plugin.angularjs.completion.dto.TemplateDotProvider;
import org.eclipse.che.plugin.angularjs.completion.dto.parser.api.AngularDocType;
import org.eclipse.che.plugin.angularjs.completion.dto.parser.api.CommentContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse ngObjects
 * @author Florent Benoit
 */
public class ObjectCommentParser implements CodeCommentParser {

    private DtoFactory dtoFactory;

    private AngularTemplate angularTemplate;

    private static final Pattern OBJECT_PATTERN = Pattern.compile("(.*?)\\.(.*)");


    public ObjectCommentParser(DtoFactory dtoFactory, AngularTemplate angularTemplate) {
        this.dtoFactory = dtoFactory;
        this.angularTemplate = angularTemplate;
    }

    @Override
    public void onComment(CommentContext commentContext) {
        // register a new NgObject for the given provider
        NgObject ngObject = dtoFactory.createDto(NgObject.class);
        String objectName = commentContext.getAttributeValue("name");

        // extract ngObject name
        Matcher objectNameMatcher = OBJECT_PATTERN.matcher(objectName);
        if (objectNameMatcher.find()) {
            // get provider from method name
            String providerName = objectNameMatcher.group(1);
            String oName = objectNameMatcher.group(2);

            ngObject.setName(oName);

            TemplateDotProvider templateDotProvider = angularTemplate.getTemplateProvider(providerName);
            if (templateDotProvider == null) {
                templateDotProvider = dtoFactory.createDto(TemplateDotProvider.class);
                templateDotProvider.setName(providerName);
                angularTemplate.addOrGet(templateDotProvider);
            }
            templateDotProvider.getObjects().add(ngObject);

        } else {
            // add complete name

            TemplateDotProvider templateDotProvider = angularTemplate.getTemplateProvider(objectName);
            if (templateDotProvider == null) {
                templateDotProvider = dtoFactory.createDto(TemplateDotProvider.class);
                templateDotProvider.setName(objectName);
                templateDotProvider.setType(getSupportedType().name());
                angularTemplate.addOrGet(templateDotProvider);
            }


        }
    }

    @Override
    public AngularDocType getSupportedType() {
        return AngularDocType.OBJECT;
    }
}
