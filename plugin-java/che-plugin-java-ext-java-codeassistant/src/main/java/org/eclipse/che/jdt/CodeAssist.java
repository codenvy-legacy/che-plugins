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

package org.eclipse.che.jdt;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.inject.Singleton;

import org.eclipse.che.dto.server.DtoFactory;
import org.eclipse.che.ide.ext.java.shared.dto.Change;
import org.eclipse.che.ide.ext.java.shared.dto.ProposalApplyResult;
import org.eclipse.che.ide.ext.java.shared.dto.ProposalPresentation;
import org.eclipse.che.ide.ext.java.shared.dto.Proposals;
import org.eclipse.che.ide.ext.java.shared.dto.Region;
import org.eclipse.che.jdt.javaeditor.TextViewer;
import org.eclipse.che.jface.text.contentassist.ICompletionProposal;
import org.eclipse.che.jface.text.contentassist.ICompletionProposalExtension;
import org.eclipse.che.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.che.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.internal.core.DocumentAdapter;
import org.eclipse.jdt.internal.ui.text.java.JavaAllCompletionProposalComputer;
import org.eclipse.jdt.internal.ui.text.java.RelevanceSorter;
import org.eclipse.jdt.internal.ui.text.java.TemplateCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class CodeAssist {

    private final Cache<String, CodeAssistContext> cache;

    public CodeAssist() {
        //todo configure expire time
        cache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).removalListener(
                new RemovalListener<String, CodeAssistContext>() {
                    @Override
                    public void onRemoval(RemovalNotification<String, CodeAssistContext> notification) {
                        if (notification.getValue() != null) {
                            notification.getValue().clean();
                        }
                    }
                }).build();
    }

    public Proposals computeProposals(IJavaProject project, String fqn, int offset, final String content) throws JavaModelException {

        WorkingCopyOwner copyOwner = new WorkingCopyOwner() {
            @Override
            public IBuffer createBuffer(ICompilationUnit workingCopy) {
                return new org.eclipse.jdt.internal.ui.javaeditor.DocumentAdapter(workingCopy, workingCopy.getPath(), content);
            }
        };
        ICompilationUnit compilationUnit = null;

        IType type = project.findType(fqn);
        if (type == null) {
            return null;
        }
        if (type.isBinary()) {
            compilationUnit = type.getClassFile().getWorkingCopy(copyOwner, null);
        } else {
            compilationUnit = type.getCompilationUnit().getWorkingCopy(copyOwner, null);
        }

        IBuffer buffer = compilationUnit.getBuffer();
        IDocument document;
        if (buffer instanceof org.eclipse.jdt.internal.ui.javaeditor.DocumentAdapter) {
            document = ((org.eclipse.jdt.internal.ui.javaeditor.DocumentAdapter)buffer).getDocument();
        } else {
            document = new DocumentAdapter(buffer);
        }
        TextViewer viewer = new TextViewer(document, new Point(offset, 0));
        JavaContentAssistInvocationContext context =
                new JavaContentAssistInvocationContext(viewer, offset, compilationUnit);

        List<ICompletionProposal> proposals = new ArrayList<>();
        proposals.addAll(new JavaAllCompletionProposalComputer().computeCompletionProposals(context, null));
        proposals.addAll(new TemplateCompletionProposalComputer().computeCompletionProposals(context, null));
//        for (IJavaCompletionProposalComputer proposalComputer : computers) {
//            proposals.addAll(proposalComputer.computeCompletionProposals(context, new NullProgressMonitor()));
//
//        }
        Collections.sort(proposals, new RelevanceSorter());
        Proposals result = DtoFactory.getInstance().createDto(Proposals.class);
        String sessionId = UUID.randomUUID().toString();
        result.setSessionId(sessionId);

        ArrayList<ProposalPresentation> presentations = new ArrayList<>();
        for (int i = 0; i < proposals.size(); i++) {
            ProposalPresentation presentation = DtoFactory.getInstance().createDto(ProposalPresentation.class);
            ICompletionProposal proposal = proposals.get(i);
            presentation.setIndex(i);
            presentation.setDisplayString(proposal.getDisplayString());
            String image = proposal.getImage() == null ? null : proposal.getImage().getImg();
            presentation.setImage(image);
            if (proposal instanceof ICompletionProposalExtension4) {
                presentation.setAutoInsertable(((ICompletionProposalExtension4)proposal).isAutoInsertable());
            }
            presentations.add(presentation);
        }
        result.setProposals(presentations);
        cache.put(sessionId, new CodeAssistContext(viewer, offset, proposals, compilationUnit));
        return result;
    }

    public ProposalApplyResult applyCompletion(String sessionId, int index, boolean insert) {
        CodeAssistContext context = cache.getIfPresent(sessionId);
        if (context != null) {
            try {
                return context.apply(index, insert);
            } finally {
                cache.invalidate(sessionId);
            }
        } else {
            throw new IllegalArgumentException("CodeAssist context doesn't exist or time of completion was expired");
        }
    }

    private class CodeAssistContext {
        private TextViewer                viewer;
        private int                       offset;
        private List<ICompletionProposal> proposals;
        private ICompilationUnit          cUnit;

        public CodeAssistContext(TextViewer viewer, int offset,
                                 List<ICompletionProposal> proposals, ICompilationUnit cUnit) {
            this.viewer = viewer;
            this.offset = offset;
            this.proposals = proposals;
            this.cUnit = cUnit;
        }

        public void clean() {
            if (cUnit != null) {
                try {
                    cUnit.discardWorkingCopy();
                } catch (JavaModelException e) {
                    //ignore
                }
            }
        }

        public ProposalApplyResult apply(int index, boolean insert) {
            IDocument document = viewer.getDocument();
            final List<Change> changes = new ArrayList<>();
            final DtoFactory dtoFactory = DtoFactory.getInstance();
            document.addDocumentListener(new IDocumentListener() {
                @Override
                public void documentAboutToBeChanged(DocumentEvent event) {
                }

                @Override
                public void documentChanged(DocumentEvent event) {
                    Change dto = dtoFactory.createDto(Change.class);
                    dto.setLength(event.getLength());
                    dto.setOffset(event.getOffset());
                    dto.setText(event.getText());
                    changes.add(dto);
                }
            });
            try {
                char trigger = (char)0;
                int stateMask = insert ? 0 : SWT.CTRL;
                ICompletionProposal p = proposals.get(index);
                if (p instanceof ICompletionProposalExtension2) {
                    ICompletionProposalExtension2 e = (ICompletionProposalExtension2)p;
                    e.apply(viewer, trigger, stateMask, offset);
                } else if (p instanceof ICompletionProposalExtension) {
                    ICompletionProposalExtension e = (ICompletionProposalExtension)p;
                    e.apply(document, trigger, offset);
                } else {
                    p.apply(document);
                }

                ProposalApplyResult result = dtoFactory.createDto(ProposalApplyResult.class);
                result.setChanges(changes);
                Region region = dtoFactory.createDto(Region.class);
                Point selection = p.getSelection(document);
                region.setOffset(selection.x);
                region.setLength(selection.y);
                result.setSelection(region);
                return result;


            } catch (IndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Can't find completion: " + index);
            }
        }
    }
}
