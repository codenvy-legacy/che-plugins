/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/

package org.eclipse.che.ide.ext.java.client.search.node;

import org.eclipse.che.ide.ext.java.shared.dto.model.CompilationUnit;
import org.eclipse.che.ide.ext.java.shared.dto.model.JavaProject;
import org.eclipse.che.ide.ext.java.shared.dto.model.Method;
import org.eclipse.che.ide.ext.java.shared.dto.model.PackageFragment;
import org.eclipse.che.ide.ext.java.shared.dto.model.Type;
import org.eclipse.che.ide.ext.java.shared.dto.search.FindUsagesResponse;
import org.eclipse.che.ide.ext.java.shared.dto.search.Match;

import java.util.List;
import java.util.Map;

/**
 * Factory for creating tree element for search result tree.
 *
 * @author Evgen Vidolob
 */
public interface NodeFactory {

    ResultNode create(FindUsagesResponse response);

    JavaProjectNode create(JavaProject javaProject, Map<String, List<Match>> matches);

    PackageFragmentNode create(PackageFragment packageFragment, Map<String, List<Match>> matches);

    TypeNode create(Type type, CompilationUnit compilationUnit, Map<String, List<Match>> matches);

    MethodNode create(Method method, Map<String, List<Match>> matches, CompilationUnit compilationUnit);

    MatchNode create(Match match, CompilationUnit compilationUnit);

}
