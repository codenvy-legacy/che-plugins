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
import org.eclipse.che.plugin.angularjs.completion.dto.Function;
import org.eclipse.che.plugin.angularjs.completion.dto.Param;
import org.eclipse.che.plugin.angularjs.completion.dto.parser.api.CodeCommentParser;
import org.eclipse.che.plugin.angularjs.completion.dto.parser.api.CommentContext;
import org.eclipse.che.plugin.angularjs.completion.dto.TemplateDotProvider;
import org.eclipse.che.plugin.angularjs.completion.dto.parser.api.AngularDocType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse functions
 * @author Florent Benoit
 */
public class FunctionCommentParser implements CodeCommentParser {

    private DtoFactory dtoFactory;

    private AngularTemplate angularTemplate;

    private static final Pattern PARAM_PATTERN  = Pattern.compile("\\{(.*?)\\}\\s(.*?)\\s(.*)");
    private static final Pattern METHOD_PATTERN = Pattern.compile("(.*?)#(.*)");


    public FunctionCommentParser(DtoFactory dtoFactory, AngularTemplate angularTemplate) {
        this.dtoFactory = dtoFactory;
        this.angularTemplate = angularTemplate;
    }

    @Override
    public void onComment(CommentContext commentContext) {
        // register a new Method for the given provider
        Function function = dtoFactory.createDto(Function.class);
        List<Param> params = new ArrayList<>();
        function.setParams(params);
        List<String> paramNames = commentContext.getAttributeValues("param");
        String methodName = commentContext.getAttributeValue("name");

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

        // extract method name
        Matcher methodNameMatcher = METHOD_PATTERN.matcher(methodName);
        if (methodNameMatcher.find()) {
            // get provider from method name
            String providerName = methodNameMatcher.group(1);
            String mName = methodNameMatcher.group(2);

            function.setName(mName);

            TemplateDotProvider templateDotProvider = angularTemplate.getTemplateProvider(providerName);
            if (templateDotProvider != null) {
                templateDotProvider.getFunctions().add(function);
            }

        }


    }

    @Override
    public AngularDocType getSupportedType() {
        return AngularDocType.FUNCTION;
    }
}
