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
import org.eclipse.che.plugin.angularjs.completion.dto.parser.api.CodeCommentParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Florent Benoit
 */
public class ParseFile extends SimpleFileVisitor<Path> {

    private Pattern pattern;

    private Pattern ngDocTypePattern;

    private List<CodeCommentParser> callbacks;

    public ParseFile() {
        this.callbacks = new ArrayList<>();
        this.pattern = Pattern.compile("/\\*\\*(.*?)\\*/", Pattern.DOTALL);

        this.ngDocTypePattern = Pattern.compile("@ngdoc(.*?)\n");
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

        if (!file.toFile().getName().endsWith(".js")) {
            return FileVisitResult.CONTINUE;
        }

        // Parse comments and send them to the parser
        BufferedReader bufferedReader = Files.newBufferedReader(file, Charset.defaultCharset());
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }

        String content = stringBuilder.toString();

        // parse comments
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {

            // Get a comment inside /** .... */
            String comment = matcher.group();

            // Check if there is a @ngdoc element
            Matcher docTypeMatcher = ngDocTypePattern.matcher(comment);
            if (docTypeMatcher.find()) {

                // Check if match
                String ngDocType = docTypeMatcher.group(1).trim();

                // not angular doc, continue
                if ("".equals(ngDocType)) {
                    continue;
                }

                // build the given context element
                AngularCommentContext angularCommentContext = new AngularCommentContext(ngDocType, comment);
                boolean missingCallback = true;
                for (CodeCommentParser codeCommentParser : callbacks) {
                    if (angularCommentContext.getType() == codeCommentParser.getSupportedType()) {
                        codeCommentParser.onComment(angularCommentContext);
                        missingCallback = false;
                    }
                }
                if (missingCallback) {
                    System.err.println("Unable to find callback for the type :" + ngDocType);
                }

                System.out.println("   Name: " + angularCommentContext.getAttributeValue("name"));
                System.out.println("   Type: " + angularCommentContext.getType());
                if (angularCommentContext.getType() == AngularDocType.METHOD) {
                    System.out.println("       params: " + angularCommentContext.getAttributeValues("param"));
                }


            }
        }


        return FileVisitResult.CONTINUE;
    }


    public void addCodeCommentParser(CodeCommentParser codeCommentParser) {
        this.callbacks.add(codeCommentParser);
    }
}
