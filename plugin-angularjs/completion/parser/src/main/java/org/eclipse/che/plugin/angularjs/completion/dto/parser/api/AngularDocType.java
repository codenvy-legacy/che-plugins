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
package org.eclipse.che.plugin.angularjs.completion.dto.parser.api;

/**
 * @author Florent Benoit
 */
public enum AngularDocType {

    DIRECTIVE("directive"),
    EVENT("event"),
    FILTER("filter"),
    FUNCTION("function"),
    INTERFACE("interface"),
    INPUT("input"),
    INPUTTYPE("inputtype"),
    METHOD("method"),
    MODULE("module"),
    OBJECT("object"),
    OVERVIEW("overview"),
    PROPERTY("property"),
    PROVIDER("provider"),
    SERVICE("service"),
    TYPE("type"),
    UNKNOWN("unknown");

    private String type;

    AngularDocType(String type) {
        this.type = type;
    }


    String getType() {
        return  type;
    }
}
