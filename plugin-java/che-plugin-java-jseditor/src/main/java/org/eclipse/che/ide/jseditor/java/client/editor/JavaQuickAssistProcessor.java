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
package org.eclipse.che.ide.jseditor.java.client.editor;

import org.eclipse.che.ide.api.icon.Icon;
import org.eclipse.che.ide.api.project.tree.VirtualFile;
import org.eclipse.che.ide.api.text.Position;
import org.eclipse.che.ide.api.text.annotation.Annotation;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.java.client.JavaResources;
import org.eclipse.che.ide.ext.java.client.editor.JavaAnnotation;
import org.eclipse.che.ide.ext.java.client.editor.JavaAnnotationUtil;
import org.eclipse.che.ide.ext.java.client.editor.JavaCodeAssistClient;
import org.eclipse.che.ide.ext.java.client.projecttree.JavaSourceFolderUtil;
import org.eclipse.che.ide.ext.java.shared.dto.Problem;
import org.eclipse.che.ide.ext.java.shared.dto.ProposalPresentation;
import org.eclipse.che.ide.ext.java.shared.dto.Proposals;
import org.eclipse.che.ide.jseditor.client.annotation.QueryAnnotationsEvent;
import org.eclipse.che.ide.jseditor.client.codeassist.CodeAssistCallback;
import org.eclipse.che.ide.jseditor.client.codeassist.CompletionProposal;
import org.eclipse.che.ide.jseditor.client.document.Document;
import org.eclipse.che.ide.jseditor.client.quickfix.QuickAssistInvocationContext;
import org.eclipse.che.ide.jseditor.client.quickfix.QuickAssistProcessor;
import org.eclipse.che.ide.jseditor.client.text.LinearRange;
import org.eclipse.che.ide.jseditor.client.texteditor.EmbeddedTextEditorPresenter;
import org.eclipse.che.ide.jseditor.client.texteditor.TextEditor;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.rest.Unmarshallable;
import org.eclipse.che.ide.util.loging.Log;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * {@link QuickAssistProcessor} for java files.
 */
public class JavaQuickAssistProcessor implements QuickAssistProcessor {

    private final JavaCodeAssistClient   client;
    /** The resources used for java assistants. */
    private final JavaResources          javaResources;
    private final DtoUnmarshallerFactory unmarshallerFactory;
    private final DtoFactory             dtoFactory;

    @Inject
    public JavaQuickAssistProcessor(final JavaCodeAssistClient client,
                                    final JavaResources javaResources,
                                    DtoUnmarshallerFactory unmarshallerFactory,
                                    DtoFactory dtoFactory) {
        this.client = client;
        this.javaResources = javaResources;
        this.unmarshallerFactory = unmarshallerFactory;
        this.dtoFactory = dtoFactory;
    }

    @Override
    public void computeQuickAssistProposals(final QuickAssistInvocationContext quickAssistContext, final CodeAssistCallback callback) {
        final TextEditor textEditor = quickAssistContext.getTextEditor();
        final Document document = textEditor.getDocument();

        LinearRange tempRange;
//        if (quickAssistContext.getOffset() != null) {
//            tempRange = document.getLinearRangeForLine(quickAssistContext.getLine());
//        } else {
        tempRange = textEditor.getSelectedLinearRange();
//        }
        final LinearRange range = tempRange;

        final boolean goToClosest = (range.getLength() == 0);

        final QueryAnnotationsEvent.AnnotationFilter filter = new QueryAnnotationsEvent.AnnotationFilter() {
            @Override
            public boolean accept(final Annotation annotation) {
                if (!(annotation instanceof JavaAnnotation)) {
                    return false;
                } else {
                    JavaAnnotation javaAnnotation = (JavaAnnotation)annotation;
                    return (!javaAnnotation.isMarkedDeleted()) /*&& JavaAnnotationUtil.hasCorrections(annotation)*/;
                }
            }
        };
        final QueryAnnotationsEvent.QueryCallback queryCallback = new QueryAnnotationsEvent.QueryCallback() {
            @Override
            public void respond(final Map<Annotation, Position> annotations) {
                List<Problem> problems = new ArrayList<>();
                /*final Map<Annotation, Position> problems =*/
                int offset = collectQuickFixableAnnotations(range, document, annotations, goToClosest, problems);
                if (offset != range.getStartOffset()) {
                    EmbeddedTextEditorPresenter presenter = ((EmbeddedTextEditorPresenter)textEditor);
                    presenter.getCursorModel().setCursorPosition(offset);
                }

                setupProposals(callback, textEditor, offset, problems);
            }
        };
        final QueryAnnotationsEvent event = new QueryAnnotationsEvent.Builder().withFilter(filter).withCallback(queryCallback).build();
        document.getDocumentHandle().getDocEventBus().fireEvent(event);

    }

    private void showProposals(final CodeAssistCallback callback, final Proposals responds) {
        List<ProposalPresentation> presentations = responds.getProposals();
        final List<CompletionProposal> proposals = new ArrayList<>(presentations.size());
        for (ProposalPresentation proposal : presentations) {
            CompletionProposal completionProposal;
            if (proposal.getActionId() != null) {
                completionProposal =
                        new ActionCompletonProposal(JavaCodeAssistProcessor.insertStyle(javaResources, proposal.getDisplayString()),
                                                    proposal.getActionId(),
                                                    new Icon("", JavaCodeAssistProcessor.getImage(javaResources, proposal.getImage())));
            } else {
                completionProposal = new JavaCompletionProposal(
                        proposal.getIndex(),
                        JavaCodeAssistProcessor.insertStyle(javaResources, proposal.getDisplayString()),
                        new Icon("", JavaCodeAssistProcessor.getImage(javaResources, proposal.getImage())),
                        client, responds.getSessionId());
            }
            proposals.add(completionProposal);
        }

        callback.proposalComputed(proposals);
    }

    private void setupProposals(final CodeAssistCallback callback,
                                final TextEditor textEditor,
                                final int offset,
                                final List<Problem> annotations) {
//        final JsoArray<ProblemLocationMessage> problems = JsoArray.create();
//        // collect problem locations and corrections from marker annotations
//        if (annotations != null) {
//            for (final Entry<Annotation, Position> entry : annotations.entrySet()) {
//                final Annotation annotation = entry.getKey();
//                if (annotation instanceof JavaAnnotation) {
//                    final ProblemLocationMessage problemLocation = getProblemLocation((JavaAnnotation)annotation, entry.getValue());
//                    if (problemLocation != null) {
//                        problems.add(problemLocation);
//                    }
//                }
//            }
//        }
//        worker.computeQAProposals(textEditor.getDocument().getContents(), range.getStartOffset(), range.getLength(),
//                                  false, problems, textEditor.getEditorInput().getFile().getPath(),
//                                  new JavaParserWorker.WorkerCallback<WorkerProposal>() {
//                                      @Override
//                                      public void onResult(final Array<WorkerProposal> problems) {
//                                          final List<CompletionProposal> proposals = buildProposals(problems);
//                                          callback.proposalComputed(proposals);
//                                      }
//                                  });
        final VirtualFile file = textEditor.getEditorInput().getFile();
        final String projectPath = file.getProject().getPath();
        String fqn = JavaSourceFolderUtil.getFQNForFile(file);
        Unmarshallable<Proposals> unmarshaller = unmarshallerFactory.newUnmarshaller(Proposals.class);
        client.computeAssistProposals(projectPath, fqn, offset, annotations, new AsyncRequestCallback<Proposals>(unmarshaller) {
            @Override
            protected void onSuccess(Proposals proposals) {
                showProposals(callback, proposals);
            }

            @Override
            protected void onFailure(Throwable throwable) {
                Log.error(JavaCodeAssistProcessor.class, throwable);
                com.google.gwt.user.client.Window.alert(throwable.getMessage());
            }
        });
    }

//    private static ProblemLocationMessage getProblemLocation(final JavaAnnotation javaAnnotation, final Position position) {
//        final int problemId = javaAnnotation.getId();
//        if (problemId != -1 && position != null) {
//            final MessagesImpls.ProblemLocationMessageImpl problemLocations = MessagesImpls.ProblemLocationMessageImpl.make();
//
//            problemLocations.setOffset(position.getOffset()).setLength(position.getLength());
//            problemLocations.setIsError(ProblemAnnotation.ERROR_ANNOTATION_TYPE.equals(javaAnnotation.getType()));
//
//            final String markerType = javaAnnotation.getMarkerType();
//            if (markerType != null) {
//                problemLocations.setMarkerType(markerType);
//            } else {
//                problemLocations.setMarkerType(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER);
//            }
//
//            problemLocations.setProblemId(javaAnnotation.getId());
//
//            if (javaAnnotation.getArguments() != null) {
//                problemLocations.setProblemArguments(JsoArray.from(javaAnnotation.getArguments()));
//            } else {
//                problemLocations.setProblemArguments(null);
//            }
//
//            return problemLocations;
//        } else {
//            return null;
//        }
//    }

//    private List<CompletionProposal> buildProposals(final Array<WorkerProposal> problems) {
//        final List<CompletionProposal> proposals = new ArrayList<>();
//        for (final WorkerProposal problem : problems.asIterable()) {
//            final String style = JavaCodeAssistProcessor.insertStyle(javaResources, problem.displayText());
//            final Icon icon = new Icon("", JavaCodeAssistProcessor.getImage(javaResources, problem.image()));
//            final CompletionProposal proposal = new JavaCompletionProposal(problem.id(), style, icon,
//                                                                           worker, respons.getSessionId());
//            proposals.add(proposal);
//        }
//        return proposals;
//    }


    private int collectQuickFixableAnnotations(final LinearRange lineRange,
                                               Document document, final Map<Annotation, Position> annotations,
                                               final boolean goToClosest, List<Problem> resultingProblems) {
        int invocationLocation = lineRange.getStartOffset();
        if (goToClosest) {


            LinearRange line =
                    document.getLinearRangeForLine(document.getPositionFromIndex(lineRange.getStartOffset()).getLine());
            int rangeStart = line.getStartOffset();
            int rangeEnd = rangeStart + line.getLength();

            ArrayList<Position> allPositions = new ArrayList<>();
            List<JavaAnnotation> allAnnotations = new ArrayList<>();
            int bestOffset = Integer.MAX_VALUE;
            for (Annotation problem : annotations.keySet()) {
                if (problem instanceof JavaAnnotation) {
                    JavaAnnotation ann = ((JavaAnnotation)problem);

                    Position pos = annotations.get(problem);
                    if (pos != null && isInside(pos.offset, rangeStart, rangeEnd)) { // inside our range?

                        allAnnotations.add(ann);
                        allPositions.add(pos);
                        bestOffset = processAnnotation(problem, pos, invocationLocation, bestOffset);
                    }
                }
            }
            if (bestOffset == Integer.MAX_VALUE) {
                return invocationLocation;
            }
            for (int i = 0; i < allPositions.size(); i++) {
                Position pos = allPositions.get(i);
                if (isInside(bestOffset, pos.offset, pos.offset + pos.length)) {
                    resultingProblems.add(createProblem(allAnnotations.get(i), pos));
                }
            }
            return bestOffset;
        } else {
            for (Annotation problem : annotations.keySet()) {
                Position pos = annotations.get(problem);
                if (pos != null && isInside(invocationLocation, pos.offset, pos.offset + pos.length)) {
                    resultingProblems.add(createProblem((JavaAnnotation)problem, pos));
                }
            }
            return invocationLocation;
        }
    }

    private Problem createProblem(JavaAnnotation javaAnnotation, Position pos) {
        Problem problem = dtoFactory.createDto(Problem.class);
        //server use only this fields
        problem.setID(javaAnnotation.getId());
        problem.setError(javaAnnotation.isError());
        problem.setArguments(Arrays.asList(javaAnnotation.getArguments()));
        problem.setSourceStart(pos.getOffset());
        //TODO I don't know why but in that place source end is bugger on 1 char
        problem.setSourceEnd(pos.getOffset() + pos.getLength() - 1);

        return problem;
    }

    private static int processAnnotation(Annotation annot, Position pos, int invocationLocation, int bestOffset) {
        final int posBegin = pos.offset;
        final int posEnd = posBegin + pos.length;
        if (isInside(invocationLocation, posBegin, posEnd)) { // covers invocation location?
            return invocationLocation;
        } else if (bestOffset != invocationLocation) {
            final int newClosestPosition = computeBestOffset(posBegin, invocationLocation, bestOffset);
            if (newClosestPosition != -1) {
                if (newClosestPosition != bestOffset) { // new best
                    if (JavaAnnotationUtil.hasCorrections(annot)) { // only jump to it if there are proposals
                        return newClosestPosition;
                    }
                }
            }
        }
        return bestOffset;
    }

    /**
     * Computes and returns the invocation offset given a new position, the initial offset and the best invocation offset found so far.
     * <p>
     * The closest offset to the left of the initial offset is the best. If there is no offset on the left, the closest on the right is the
     * best.
     * </p>
     *
     * @param newOffset the offset to llok at
     * @param invocationLocation the invocation location
     * @param bestOffset the current best offset
     * @return -1 is returned if the given offset is not closer or the new best offset
     */
    private static int computeBestOffset(int newOffset, int invocationLocation, int bestOffset) {
        if (newOffset <= invocationLocation) {
            if (bestOffset > invocationLocation) {
                return newOffset; // closest was on the right, prefer on the left
            } else if (bestOffset <= newOffset) {
                return newOffset; // we are closer or equal
            }
            return -1; // further away
        }

        if (newOffset <= bestOffset) {
            return newOffset; // we are closer or equal
        }

        return -1; // further away
    }

    /**
     * Tells is the offset is inside the (inclusive) range defined by start-end.
     *
     * @param offset the offset
     * @param start the start of the range
     * @param end the end of the range
     * @return true iff offset is inside
     */
    private static boolean isInside(int offset, int start, int end) {
        return offset == start || offset == end || (offset > start && offset < end); // make sure to handle 0-length ranges
    }
}
