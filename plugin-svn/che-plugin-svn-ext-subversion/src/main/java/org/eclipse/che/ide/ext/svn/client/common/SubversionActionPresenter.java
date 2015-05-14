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
package org.eclipse.che.ide.ext.svn.client.common;

import org.eclipse.che.ide.api.project.tree.TreeNode;
import static org.eclipse.che.ide.ext.svn.client.common.PathTypeFilter.ALL;
import static org.eclipse.che.ide.ext.svn.client.common.PathTypeFilter.PROJECT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import org.eclipse.che.ide.api.event.RefreshProjectTreeEvent;
import org.eclipse.che.ide.api.parts.ProjectExplorerPart;
import org.eclipse.che.ide.ext.svn.client.action.SubversionAction;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.event.ProjectActionEvent;
import org.eclipse.che.ide.api.event.ProjectActionHandler;
import org.eclipse.che.ide.api.parts.PartStackType;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.api.project.tree.generic.FileNode;
import org.eclipse.che.ide.api.project.tree.generic.FolderNode;
import org.eclipse.che.ide.api.project.tree.generic.ProjectNode;
import org.eclipse.che.ide.api.project.tree.generic.StorableNode;
import org.eclipse.che.ide.api.selection.Selection;

import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter to be extended by all {@link SubversionAction} presenters.
 */
public class SubversionActionPresenter {

    static final String[][] LINE_STYLES = {
            {"$", "font-weight:bold; font-style:italic;"},
            {"M", "color:rgb(247, 47, 47);"},
            {"!", "color:grey;"},
            {"?", "color:lightskyblue;"},
            {"A", "color:chartreuse;"},
            {"X", "color:yellow;"},
            {"C", "color:yellow;"},
            {"D", "color:rgb(247, 47, 47);"},
            {"+", "color:chartreuse;"},
            {"-", "color:rgb(247, 47, 47);"},
            {"@", "color:cyan;"},
            {"U", "color:chartreuse;"},
            {"G", "color:chartreuse;"}
    };

    protected final AppContext       appContext;
    protected final EventBus         eventBus;
    protected final WorkspaceAgent   workspaceAgent;
    private final RawOutputPresenter console;

    private boolean                  viewOpened;

    private final ProjectExplorerPart projectExplorerPart;

    /**
     * Constructor.
     */
    protected SubversionActionPresenter(final AppContext appContext,
                                        final EventBus eventBus,
                                        final RawOutputPresenter console,
                                        final WorkspaceAgent workspaceAgent,
                                        final ProjectExplorerPart projectExplorerPart) {
        this.appContext = appContext;
        this.workspaceAgent = workspaceAgent;
        this.console = console;

        this.eventBus = eventBus;
        this.projectExplorerPart = projectExplorerPart;

        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(final ProjectActionEvent event) {
            }

            @Override
            public void onProjectClosing(ProjectActionEvent event) {
            }

            @Override
            public void onProjectClosed(final ProjectActionEvent event) {
                console.clear();
                workspaceAgent.hidePart(console);
                viewOpened = false;
            }

        });
    }

    /**
     * @return the current project path
     */
    protected String getCurrentProjectPath() {
        final CurrentProject currentProject = appContext.getCurrentProject();
        final ProjectDescriptor project;
        String projectPath = null;

        if (currentProject != null) {
            project = currentProject.getProjectDescription();

            if (project != null) {
                projectPath = project.getPath();
            }
        }

        return projectPath;
    }

    /**
     * Returns currently selected project item.
     * @return
     */
    protected TreeNode<?> getSelectedNode() {
        Object selectedNode = projectExplorerPart.getSelection().getHeadElement();
        return selectedNode != null && selectedNode instanceof StorableNode ? (StorableNode)selectedNode : null;
    }

    /**
     * @return the selected paths or an empty list of there is no selection
     */
    @NotNull
    protected List<String> getSelectedPaths(final Collection<PathTypeFilter> filters) {
        final Selection<?> selection = projectExplorerPart.getSelection();
        final List<String> paths = new ArrayList<>();

        if (selection != null && !selection.isEmpty()) {
            for (final Object item : selection.getAllElements()) {
                if (matchesFilter(item, filters)) {
                    final String path = relativePath((StorableNode)item);
                    if (!path.isEmpty()) {
                        paths.add(path);
                    } else {
                        paths.add("."); //it may be root path for our project
                    }
                }
            }
        }

        return paths;
    }

    /**
     * Returns relative node path in the project.
     *
     * @param node node
     * @return relative node path
     */
    protected String relativePath(final StorableNode node) {
        String path = node.getPath().replaceFirst(node.getProject().getPath(), ""); // TODO: Move to method

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        return path;
    }

    protected List<String> getSelectedPaths() {
        return getSelectedPaths(Collections.singleton(ALL));
    }

    protected boolean matchesFilter(final Object node, final Collection<PathTypeFilter> filters) {
        if (filters == null || filters.isEmpty()) {
            return true;
        }
        for (final PathTypeFilter filter : filters) {
            if (filter == ALL && node instanceof StorableNode
                || filter == PathTypeFilter.FILE && node instanceof FileNode
                || filter == PathTypeFilter.FOLDER && node instanceof FolderNode
                || filter == PathTypeFilter.PROJECT && (node instanceof ProjectNode
                    || node instanceof org.eclipse.che.ide.part.projectexplorer.ProjectListStructure.ProjectNode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Ensures view is opened.
     */
    protected void ensureViewOpened() {
        if (viewOpened) {
            return;
        }

        workspaceAgent.openPart(console, PartStackType.INFORMATION);
        viewOpened = true;
    }

    /**
     * Notify Project explorer to update.
     */
    protected void updateProjectExplorer() {
        final Selection<?> selection = projectExplorerPart.getSelection();

        final Collection<PathTypeFilter> filters = Collections.singleton(ALL);

        if (selection != null && !selection.isEmpty()) {
            for (final Object item : selection.getAll().asIterable()) {
                if (matchesFilter(item, filters)) {
                    final StorableNode node = (StorableNode)item;
                    if (node instanceof FileNode) {
                        eventBus.fireEvent(new RefreshProjectTreeEvent(node.getParent(), true));
                    } else {
                        eventBus.fireEvent(new RefreshProjectTreeEvent(node, true));
                    }
                }
            }
        }

    }

    /**
     * Prints empty line.
     */
    protected void print() {
        console.print("");
    }

    /**
     * Prints line as plain text.
     *
     * @param line line to print
     */
    protected void print(final String line) {
        ensureViewOpened();
        console.print(SafeHtmlUtils.htmlEscape(line != null ? line : ""));
    }

    /**
     * Prints lines as plain text.
     *
     * @param lines lines to print
     */
    protected void print(final List<String> lines) {
        if (lines != null) {
            for (final String line : lines) {
                print(line);
            }
        }
    }

    /**
     * Prints colored line.
     *
     * @param line line to print
     */
    protected void printColored(final String line) {
        ensureViewOpened();

        if (line == null) {
            print();
            return;
        }

        if (!line.trim().isEmpty()) {
            String prefix = line.trim().substring(0, 1);
            for (String[] style : LINE_STYLES) {
                if (style[0].equals(prefix)) {
                    console.print("<span style=\"" + style[1] +  "\">" + SafeHtmlUtils.htmlEscape(line) + "</span>");
                    return;
                }
            }
        }

        // Print line as plain text if appropriate style is not found
        console.print(SafeHtmlUtils.htmlEscape(line));
    }

    /**
     * Prints colored lines.
     *
     * @param lines lines to print
     */
    protected void printColored(final List<String> lines) {
        if (lines != null) {
            for (final String line : lines) {
                printColored(line);
            }
        }
    }

    /**
     * Prints error.
     *
     * @param line error line to print
     */
    protected void printError(final String line) {
        ensureViewOpened();

        if (line == null) {
            print();
        } else {
            console.print("<span style=\"color:red;\">" + SafeHtmlUtils.htmlEscape(line) + "</span>");
        }
    }

    /**
     * Prints error.
     *
     * @param lines error lines to print
     */
    protected void printError(final List<String> lines) {
        if (lines != null) {
            for (final String line : lines) {
                printError(line);
            }
        }
    }

    /**
     * Print command execution result in colors.
     *
     * @param command
     * @param output
     * @param errOutput
     */
    protected void printResponse(String command, List<String> output, List<String> errOutput) {
        if (command != null) {
            printColored("$ " + command);
        }

        printColored(output);
        printColored(errOutput);
        print();
    }

}
