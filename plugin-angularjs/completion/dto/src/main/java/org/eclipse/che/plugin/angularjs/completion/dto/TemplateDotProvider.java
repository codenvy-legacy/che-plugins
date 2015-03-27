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
package org.eclipse.che.plugin.angularjs.completion.dto;

import org.eclipse.che.dto.shared.DTO;

import java.util.List;

/**
 * @author Florent Benoit
 */
@DTO
public interface TemplateDotProvider {

    void setName(String name);
    String getName();

    void setType(String type);
    String getType();

    void setConstructors(List<Param> args);
    List<Param> getConstructors();

    void setMethods(List<Method> methods);
    List<Method> getMethods();

    void setFunctions(List<Function> functions);
    List<Function> getFunctions();

    void setObjects(List<NgObject> ngObjects);
    List<NgObject> getObjects();

    void setEvents(List<Event> events);
    List<Event> getEvents();


}
