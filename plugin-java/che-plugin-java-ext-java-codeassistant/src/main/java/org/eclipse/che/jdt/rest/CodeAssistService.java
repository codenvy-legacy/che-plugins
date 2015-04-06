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

import org.eclipse.che.ide.ext.java.shared.dto.ProposalApplyResult;
import org.eclipse.che.ide.ext.java.shared.dto.Proposals;
import org.eclipse.che.jdt.CodeAssist;
import org.eclipse.che.jdt.internal.core.JavaModel;
import org.eclipse.che.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

/**
 * @author Evgen Vidolob
 */
@Path("code-assist")
public class CodeAssistService {

    private static final JavaModel model = JavaModelManager.getJavaModelManager().getJavaModel();

    @Inject
    private CodeAssist codeAssist;

    @GET
    @Path("compute/completion")
    @Produces("application/json")
    public Proposals computeCompletionProposals(@QueryParam("projectpath") String projectPath,
                                                @QueryParam("fqn") String fqn,
                                                @QueryParam("offset") int offset) {
        IJavaProject javaProject = model.getJavaProject(projectPath);
        try {
            return codeAssist.computeProposals(javaProject, fqn, offset);
        } catch (JavaModelException e) {
            JavaPlugin.log(e);
            throw new WebApplicationException(e.getMessage());
        }
    }

    @GET
    @Path("apply/completion")
    @Produces("application/json")
    public ProposalApplyResult applyCompletion(@QueryParam("sessionid") String sessionId,
                                               @QueryParam("index") int index,
                                               @DefaultValue("true") @QueryParam("insert") boolean insert) {
        return codeAssist.applyCompletion(sessionId, index, insert);
    }
}
