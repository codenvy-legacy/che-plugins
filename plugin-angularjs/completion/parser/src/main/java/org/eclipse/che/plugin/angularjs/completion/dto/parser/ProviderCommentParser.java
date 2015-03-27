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
import org.eclipse.che.plugin.angularjs.completion.dto.TemplateDotProvider;
import org.eclipse.che.plugin.angularjs.completion.dto.parser.api.AngularDocType;
import org.eclipse.che.plugin.angularjs.completion.dto.parser.api.CommentContext;

/**
 * Parse provider
 * @author Florent Benoit
 */
public class ProviderCommentParser implements CodeCommentParser {

    private DtoFactory dtoFactory;

    private AngularTemplate angularTemplate;

    public ProviderCommentParser(DtoFactory dtoFactory, AngularTemplate angularTemplate) {
        this.dtoFactory = dtoFactory;
        this.angularTemplate = angularTemplate;
    }


    @Override
    public void onComment(CommentContext commentContext) {
        String name = commentContext.getAttributeValue("name");
        // register a new Provider

        TemplateDotProvider templateDotProvider = dtoFactory.createDto(TemplateDotProvider.class);
        templateDotProvider.setName(name);
        templateDotProvider.setType(getSupportedType().name());

        // add it if not exist
        angularTemplate.addOrGet(templateDotProvider);

    }

    @Override
    public AngularDocType getSupportedType() {
        return AngularDocType.PROVIDER;
    }

}
