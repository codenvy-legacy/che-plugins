/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.java.client.editor;

import com.google.gwt.resources.client.ImageResource;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.icon.Icon;
import org.eclipse.che.ide.api.file.VirtualFile;
import org.eclipse.che.ide.ext.java.client.JavaResources;
import org.eclipse.che.ide.ext.java.client.projecttree.JavaSourceFolderUtil;
import org.eclipse.che.ide.ext.java.client.refactoring.RefactoringUpdater;
import org.eclipse.che.ide.ext.java.shared.dto.ProposalPresentation;
import org.eclipse.che.ide.ext.java.shared.dto.Proposals;
import org.eclipse.che.ide.jseditor.client.codeassist.CodeAssistCallback;
import org.eclipse.che.ide.jseditor.client.codeassist.CodeAssistProcessor;
import org.eclipse.che.ide.jseditor.client.codeassist.CompletionProposal;
import org.eclipse.che.ide.jseditor.client.link.HasLinkedMode;
import org.eclipse.che.ide.jseditor.client.texteditor.TextEditor;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.rest.Unmarshallable;
import org.eclipse.che.ide.util.loging.Log;
import org.vectomatic.dom.svg.ui.SVGResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaCodeAssistProcessor implements CodeAssistProcessor {

    private static Map<String, ImageResource> images;
    private static Map<String, SVGResource>   svgs;

    private final EditorPartPresenter  editor;
    private final AnalyticsEventLogger eventLogger;
    private final JavaResources        resources;
    private final RefactoringUpdater   refactoringUpdater;

    private       JavaCodeAssistClient   client;
    private final EditorAgent            editorAgent;
    private DtoUnmarshallerFactory unmarshallerFactory;

    private String errorMessage;

    @AssistedInject
    public JavaCodeAssistProcessor(@Assisted final EditorPartPresenter editor,
                                   final JavaCodeAssistClient client,
                                   final JavaResources javaResources,
                                   RefactoringUpdater refactoringUpdater,
                                   EditorAgent editorAgent,
                                   DtoUnmarshallerFactory unmarshallerFactory,
                                   final AnalyticsEventLogger eventLogger) {
        this.editor = editor;
        this.client = client;
        this.resources = javaResources;
        this.refactoringUpdater = refactoringUpdater;
        this.editorAgent = editorAgent;
        this.unmarshallerFactory = unmarshallerFactory;
        this.eventLogger = eventLogger;
        if (images == null) {
            initImages(javaResources);
        }
    }

    private void initImages(JavaResources resources) {
        images = new HashMap<>();
        svgs = new HashMap<>();

        images.put("template", resources.template());
        images.put("javadoc", resources.javadoc());
        svgs.put("annotation", resources.annotationItem());
        //todo create images for annotations
        svgs.put("privateAnnotation", resources.annotationItem());
        svgs.put("protectedAnnotation", resources.annotationItem());
        svgs.put("defaultAnnotation", resources.annotationItem());

        svgs.put("enum", resources.enumItem());
        svgs.put("defaultEnum", resources.enumItem());
        svgs.put("privateEnum", resources.enumItem());
        svgs.put("protectedEnum", resources.enumItem());

        svgs.put("interface", resources.svgInterfaceItem());

        images.put("defaultInterface", resources.interfaceItem());
        images.put("innerInterfacePublic", resources.interfaceItem());
        images.put("innerInterfacePrivate", resources.interfaceItem());
        images.put("innerInterfaceProtected", resources.interfaceItem());

        svgs.put("class", resources.svgClassItem());

        images.put("defaultClass", resources.classDefaultItem());
        images.put("innerClassPrivate", resources.classItem());
        images.put("innerClassProtected", resources.classItem());
        images.put("innerClassDefault", resources.classItem());

        svgs.put("privateMethod", resources.privateMethod());
        svgs.put("publicMethod", resources.publicMethod());
        svgs.put("protectedMethod", resources.protectedMethod());
        images.put("defaultMethod", resources.defaultMethod());

        images.put("publicField", resources.publicField());
        images.put("protectedField", resources.protectedField());
        images.put("privateField", resources.privateField());
        images.put("defaultField", resources.defaultField());

        images.put("localVariable", resources.local_var());
        images.put("package", resources.packageItem());

        images.put("correctionLocal", resources.correction_change()); // ????
        images.put("correctionChange", resources.correction_change());
        images.put("correctionAdd", resources.correction_change()); //????
        images.put("jexception", resources.exceptionProp());
        images.put("correctionRemove", resources.remove_correction());
        images.put("correctionCast", resources.correction_cast());
        images.put("correctionMove", resources.remove_correction()); // ????
        images.put("correctionDeleteImport", resources.correction_delete_import());
        images.put("correctionRename", resources.breakpointCurrent()); //????
        images.put("impObj", resources.add_obj()); //????
        images.put("toolDelete", resources.delete_obj()); //????

        images.put("linkedRename", resources.linkedRename());
    }

    public static String insertStyle(final JavaResources javaResources, final String display) {
        if (display.contains("#FQN#")) {
            return display.replace("#FQN#", javaResources.css().fqnStyle());
        } else if (display.contains("#COUNTER#")) {
            return display.replace("#COUNTER#", javaResources.css().counter());
        } else {
            return display;
        }
    }

    public static Icon getIcon(final String image) {
        if (svgs.containsKey(image)) {
            return new Icon("", svgs.get(image));
        }

        return new Icon("", images.get(image));
    }

    @Override
    public void computeCompletionProposals(final TextEditor textEditor, final int offset,
                                           final CodeAssistCallback callback) {
        if (errorMessage != null) {
            return;
        }
        this.eventLogger.log(this, "Autocompleting");
        final VirtualFile file = editor.getEditorInput().getFile();
        final String projectPath = file.getProject().getProjectConfig().getPath();
        String fqn = JavaSourceFolderUtil.getFQNForFile(file);
        Unmarshallable<Proposals> unmarshaller = unmarshallerFactory.newUnmarshaller(Proposals.class);
        client.computeProposals(projectPath, fqn, offset, textEditor.getDocument().getContents(),
                                new AsyncRequestCallback<Proposals>(unmarshaller) {
                                    @Override
                                    protected void onSuccess(Proposals proposals) {
                                        showProposals(callback, proposals);
                                    }

            @Override
            protected void onFailure(Throwable throwable) {
                Log.error(JavaCodeAssistProcessor.class, throwable);
            }
        });
    }

    private void showProposals(final CodeAssistCallback callback, final Proposals respons) {
        List<ProposalPresentation> presentations = respons.getProposals();
        final List<CompletionProposal> proposals = new ArrayList<>(presentations.size());
        HasLinkedMode linkedEditor = editor instanceof HasLinkedMode ? (HasLinkedMode)editor : null;
        for (final ProposalPresentation proposal : presentations) {
            final CompletionProposal completionProposal = new JavaCompletionProposal(proposal.getIndex(),
                                                                                     insertStyle(resources, proposal.getDisplayString()),
                                                                                     getIcon(proposal.getImage()),
                                                                                     client,
                                                                                     respons.getSessionId(),
                                                                                     linkedEditor,
                                                                                     refactoringUpdater,
                                                                                     editorAgent);

            proposals.add(completionProposal);
        }

        callback.proposalComputed(proposals);
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void disableCodeAssistant() {
        this.errorMessage = "Code Assistant currently unavailable due to file parsing. Try again in a moment.";
    }

    public void enableCodeAssistant() {
        this.errorMessage = null;
    }
}
