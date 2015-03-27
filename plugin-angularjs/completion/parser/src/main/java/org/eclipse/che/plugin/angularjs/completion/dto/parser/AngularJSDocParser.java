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

import org.eclipse.che.plugin.angularjs.completion.dto.parser.api.CodeCommentParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AngularJSDocParser {

    private List<CodeCommentParser> callbacks;

    public AngularJSDocParser() {
        this.callbacks = new ArrayList<>();
    }

    public void parse(Path path) throws IOException {
        ParseFile parseFile = new ParseFile();
        for (CodeCommentParser codeCommentParser : callbacks) {
            parseFile.addCodeCommentParser(codeCommentParser);
        }
        Files.walkFileTree(path, parseFile);
    }


    public void addCodeCommentParser(CodeCommentParser codeCommentParser) {
        this.callbacks.add(codeCommentParser);
    }


}