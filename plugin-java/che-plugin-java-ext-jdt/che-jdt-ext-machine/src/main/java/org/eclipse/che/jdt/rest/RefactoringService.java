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

package org.eclipse.che.jdt.rest;

import com.google.inject.Inject;

import org.eclipse.che.ide.ext.java.shared.dto.refactoring.ChangeCreationResult;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.CreateMoveRefactoring;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.JavaElement;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.MoveSettings;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.RefactoringSession;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.RefactoringStatus;
import org.eclipse.che.ide.ext.java.shared.dto.refactoring.ReorgDestination;
import org.eclipse.che.jdt.refactoring.RefactoringException;
import org.eclipse.che.jdt.refactoring.RefactoringManager;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaModel;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.corext.refactoring.RefactoringAvailabilityTester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.function.Function;

/**
 * @author Evgen Vidolob
 */
@Path("/jdt/{ws-id}/refactoring")
public class RefactoringService {
    private static final JavaModel model = JavaModelManager.getJavaModelManager().getJavaModel();
    private static final Logger    LOG   = LoggerFactory.getLogger(RefactoringService.class);
    private RefactoringManager manager;

    @Inject
    public RefactoringService(RefactoringManager manager) {
        this.manager = manager;
    }

    @POST
    @Path("move/create")
    @Consumes("application/json")
    @Produces("text/plain")
    public String createMoveRefactoring(CreateMoveRefactoring cmr) throws JavaModelException, RefactoringException {
        IJavaProject javaProject = model.getJavaProject(cmr.getProjectPath());
        IJavaElement[] javaElements;
        try {
            Function<JavaElement, IJavaElement> map = javaElement -> {
                try {
                    if (javaElement.isPack()) {
                        return javaProject.findPackageFragment(new org.eclipse.core.runtime.Path(javaElement.getPath()));
                    } else {
                        return javaProject.findType(javaElement.getPath()).getCompilationUnit();

                    }
                } catch (JavaModelException e) {
                    throw new IllegalArgumentException(e);
                }

            };
            javaElements = cmr.getElements().stream().map(map).toArray(IJavaElement[]::new);

        } catch (IllegalArgumentException e) {
            if (e.getCause() instanceof JavaModelException) {
                throw (JavaModelException)e.getCause();
            } else {
                throw e;
            }
        }
        if (RefactoringAvailabilityTester.isMoveAvailable(null, javaElements)) {
            return manager.createMoveRefactoringSession(javaElements);
        }

        throw new IllegalArgumentException("Can't create move refactoring.");
    }


    @POST
    @Path("set/destination")
    @Produces("application/json")
    @Consumes("application/json")
    public RefactoringStatus setDestination(ReorgDestination destination) throws RefactoringException, JavaModelException {
        return manager.setRefactoringDestination(destination);
    }

    @POST
    @Path("set/move/setting")
    @Consumes("application/json")
    public void setMoveSettings(MoveSettings settings) throws RefactoringException {
        manager.setMoveSettings(settings);
    }


    @POST
    @Path("create/change")
    @Produces("application/json")
    @Consumes("application/json")
    public ChangeCreationResult createChange(RefactoringSession refactoringSession) throws RefactoringException {
        return manager.createChange(refactoringSession.getSessionId());
    }
}