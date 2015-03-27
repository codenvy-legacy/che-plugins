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
import org.eclipse.che.plugin.angularjs.completion.dto.Param;
import org.eclipse.che.plugin.angularjs.completion.dto.TemplateDotProvider;
import org.eclipse.che.plugin.angularjs.completion.dto.parser.api.AngularDocType;
import org.eclipse.che.plugin.angularjs.completion.dto.parser.api.CommentContext;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse service
 * @author Florent Benoit
 */
public class ServiceCommentParser implements CodeCommentParser {

    private DtoFactory dtoFactory;

    private AngularTemplate angularTemplate;

    private static final Pattern PARAM_PATTERN  = Pattern.compile("\\{(.*?)\\}\\s(.*?)\\s(.*)");
    private static final Pattern METHOD_PATTERN = Pattern.compile("(.*?)#(.*)");


    public ServiceCommentParser(DtoFactory dtoFactory, AngularTemplate angularTemplate) {
        this.dtoFactory = dtoFactory;
        this.angularTemplate = angularTemplate;
    }


    @Override
    public void onComment(CommentContext commentContext) {
        String name = commentContext.getAttributeValue("name");

        TemplateDotProvider templateDotProvider = dtoFactory.createDto(TemplateDotProvider.class);
        templateDotProvider.setName(name);
        templateDotProvider.setType(getSupportedType().name());

        List<Param> params = new ArrayList<>();
        templateDotProvider.setConstructors(params);
        List<String> paramNames = commentContext.getAttributeValues("param");

        if (paramNames != null) {
            for (String paramName : paramNames) {
                Param param = dtoFactory.createDto(Param.class);

                Matcher paramMatcher = PARAM_PATTERN.matcher(paramName);

                if (paramMatcher.find()) {
                    String pType = paramMatcher.group(1);
                    String pName = paramMatcher.group(2);
                    param.setName(pName);
                    param.setType(pType);

                    params.add(param);
                }
            }
        }




        // add it if not exist
        angularTemplate.addOrGet(templateDotProvider);


    }

    @Override
    public AngularDocType getSupportedType() {
        return AngularDocType.SERVICE;
    }

}
