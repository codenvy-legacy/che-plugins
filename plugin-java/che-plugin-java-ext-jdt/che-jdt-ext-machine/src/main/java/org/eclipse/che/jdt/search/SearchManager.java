/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Codenvy, S.A. - initial API and implementation
 *******************************************************************************/

package org.eclipse.che.jdt.search;

import com.google.inject.Singleton;

import org.eclipse.che.dto.server.DtoFactory;
import org.eclipse.che.ide.ext.java.shared.dto.Region;
import org.eclipse.che.ide.ext.java.shared.dto.model.JavaProject;
import org.eclipse.che.ide.ext.java.shared.dto.search.FindUsagesResponse;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.internal.core.DocumentAdapter;
import org.eclipse.jdt.internal.ui.search.JavaSearchQuery;
import org.eclipse.jdt.internal.ui.search.JavaSearchResult;
import org.eclipse.jdt.internal.ui.search.JavaSearchScopeFactory;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.search.NewSearchUI;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Performs all Java related search.
 *
 * @author Evgen Vidolob
 */
@Singleton
public class SearchManager {
    private static final Logger LOG = LoggerFactory.getLogger(SearchManager.class);

    private static final Class<?>[] TYPES_FOR_FIND_USAGE = new Class[]{ICompilationUnit.class,
                                                                       IType.class,
                                                                       IMethod.class,
                                                                       IField.class,
                                                                       IPackageDeclaration.class,
                                                                       IImportDeclaration.class,
                                                                       IPackageFragment.class,
                                                                       ILocalVariable.class,
                                                                       ITypeParameter.class};


    public FindUsagesResponse findUsage(IJavaProject javaProject, String filePath, int offset) throws SearchException {
        String packagePath = filePath.substring(0, filePath.lastIndexOf("/"));
        try {
            IPackageFragment packageFragment = javaProject.findPackageFragment(new Path(packagePath));
            ICompilationUnit compilationUnit = packageFragment.getCompilationUnit(filePath.substring(filePath.lastIndexOf('/') + 1));
            IJavaElement[] elements = compilationUnit.codeSelect(offset, 0);
            if (elements != null && elements.length == 1) {
                IJavaElement element = elements[0];
                if (isTypeValid(element, TYPES_FOR_FIND_USAGE)) {
                    return performFindUsageSearch(element);
                } else {
                    throw new SearchException("Find usage can't search for element: " + element.getElementName());
                }

            } else {
                throw new SearchException("Can't find element to search, try to move cursor to another place and invoke search again");
            }

        } catch (JavaModelException e) {
            LOG.error(e.getMessage(), e);
            throw new SearchException(String.format("Can't find project: %s or file: %s", javaProject.getPath().toOSString(), filePath), e);
        } catch (BadLocationException e) {
            LOG.error(e.getMessage(), e);
            throw new SearchException("Some error happened when formatting search result", e);
        }
    }

    private FindUsagesResponse performFindUsageSearch(IJavaElement element) throws JavaModelException, BadLocationException {
        JavaSearchQuery
                query = new JavaSearchQuery(new ElementQuerySpecification(element, IJavaSearchConstants.REFERENCES,
                                                                          JavaSearchScopeFactory.getInstance().createWorkspaceScope(true),
                                                                          "workspace scope"));
        NewSearchUI.runQueryInForeground(null, query);
        ISearchResult result = query.getSearchResult();
        JavaSearchResult javaResult = ((JavaSearchResult)result);
        FindUsagesResponse response = DtoFactory.newDto(FindUsagesResponse.class);
        Map<String, List<org.eclipse.che.ide.ext.java.shared.dto.search.Match>> mapMaches = new HashMap<>();
        JavaElementToDtoConverter converter = new JavaElementToDtoConverter(javaResult);
        for (Object o : javaResult.getElements()) {
            IJavaElement javaElement = (IJavaElement)o;
            converter.addElementToProjectHierarchy(javaElement);

            Match[] matches = javaResult.getMatches(o);
            List<org.eclipse.che.ide.ext.java.shared.dto.search.Match> matchList = new ArrayList<>();
            for (Match match : matches) {
                ICompilationUnit ancestor = (ICompilationUnit)(javaElement).getAncestor(IJavaElement.COMPILATION_UNIT);
                IBuffer buffer = ancestor.getBuffer();
                IDocument document;
                if (buffer instanceof org.eclipse.jdt.internal.ui.javaeditor.DocumentAdapter) {
                    document = ((org.eclipse.jdt.internal.ui.javaeditor.DocumentAdapter)buffer).getDocument();
                } else {
                    document = new DocumentAdapter(buffer);
                }
                IRegion lineInformation = document.getLineInformationOfOffset(match.getOffset());
                org.eclipse.che.ide.ext.java.shared.dto.search.Match dtoMatch = DtoFactory.newDto(
                        org.eclipse.che.ide.ext.java.shared.dto.search.Match.class);

                int offsetInLine = match.getOffset() - lineInformation.getOffset();
                Region matchInLine = DtoFactory.newDto(Region.class).withOffset(offsetInLine).withLength(match.getLength());

                dtoMatch.setFileMatchRegion(DtoFactory.newDto(Region.class).withOffset(match.getOffset()).withLength(match.getLength()));
                dtoMatch.setMatchInLine(matchInLine);
                dtoMatch.setMatchedLine(document.get(lineInformation.getOffset(), lineInformation.getLength()));
                dtoMatch.setMatchLineNumber(document.getLineOfOffset(match.getOffset()));
                matchList.add(dtoMatch);

            }
            mapMaches.put(javaElement.getHandleIdentifier(), matchList);
        }
        List<JavaProject> projects = converter.getProjects();
        response.setProjects(projects);
        response.setMatches(mapMaches);
        response.setSearchElementLabel(JavaElementLabels.getElementLabel(element, JavaElementLabels.ALL_DEFAULT));
        return response;
    }

    private boolean isTypeValid(IJavaElement element, Class<?>[] classes) {
        if (element == null) {
            return false;
        }

        for (Class<?> clazz : classes) {
            if (clazz.isInstance(element)) {
                if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
                    return hasChildren((IPackageFragment)element);
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasChildren(IPackageFragment packageFragment) {
        try {
            return packageFragment.hasChildren();
        } catch (JavaModelException ex) {
            return false;
        }
    }
}
