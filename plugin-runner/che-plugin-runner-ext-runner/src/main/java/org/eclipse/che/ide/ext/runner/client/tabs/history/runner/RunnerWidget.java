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
package org.eclipse.che.ide.ext.runner.client.tabs.history.runner;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.models.Runner.Status;
import org.eclipse.che.ide.ext.runner.client.selection.SelectionManager;
import org.eclipse.che.ide.ext.runner.client.tabs.common.item.ItemWidget;
import org.eclipse.che.ide.ext.runner.client.tabs.common.item.RunnerItems;
import org.vectomatic.dom.svg.ui.SVGImage;

import javax.validation.constraints.NotNull;

import static org.eclipse.che.ide.ext.runner.client.models.Runner.Status.FAILED;
import static org.eclipse.che.ide.ext.runner.client.models.Runner.Status.STOPPED;

/**
 * The class contains methods which allow change view representation of runner.
 *
 * @author Dmitry Shnurenko
 */
public class RunnerWidget implements RunnerItems<Runner> {

    private final ItemWidget      itemWidget;
    private final RunnerResources resources;

    private final SVGImage inProgress;
    private final SVGImage inQueue;
    private final SVGImage failed;
    private final SVGImage timeout;
    private final SVGImage done;
    private final SVGImage stopped;

    private Runner         runner;
    private Status         runnerStatus;
    private ActionDelegate delegate;

    @Inject
    public RunnerWidget(ItemWidget itemWidget, RunnerResources resources, final SelectionManager selectionManager) {
        this.itemWidget = itemWidget;
        this.resources = resources;

        inProgress = new SVGImage(resources.runnerInProgress());
        inQueue = new SVGImage(resources.runnerInQueue());
        failed = new SVGImage(resources.runnerFailed());
        timeout = new SVGImage(resources.runnerTimeout());
        done = new SVGImage(resources.runnerDone());
        stopped = new SVGImage(resources.runnerDone());

        itemWidget.setDelegate(new ItemWidget.ActionDelegate() {
            @Override
            public void onWidgetClicked() {
                selectionManager.setRunner(runner);
            }
        });

        addImagePanelActions();
    }

    private void addImagePanelActions() {
        SimpleLayoutPanel imagePanel = itemWidget.getImagePanel();

        final SVGImage image = new SVGImage(resources.erase());
        image.addClassNameBaseVal(resources.runnerCss().whiteColor());

        imagePanel.addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent mouseOutEvent) {
                changeRunnerStatusIcon();
            }
        }, MouseOutEvent.getType());

        imagePanel.addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent mouseOverEvent) {
                if (FAILED.equals(runnerStatus) || STOPPED.equals(runnerStatus)) {
                    itemWidget.setImage(image);
                }
            }
        }, MouseOverEvent.getType());

        imagePanel.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if (FAILED.equals(runnerStatus) || STOPPED.equals(runnerStatus)) {
                    delegate.removeRunnerWidget(runner);
                }
            }
        }, ClickEvent.getType());
    }

    /** {@inheritDoc} */
    @Override
    public void select() {
        itemWidget.select();
    }

    /** {@inheritDoc} */
    @Override
    public void unSelect() {
        itemWidget.unSelect();
    }

    /** {@inheritDoc} */
    @Override
    public void update(@NotNull Runner runner) {
        this.runner = runner;
        this.runnerStatus = runner.getStatus();

        changeRunnerStatusIcon();

        itemWidget.setName(runner.getTitle());
        itemWidget.setDescription(runner.getRAM() + "MB");
        itemWidget.setStartTime(runner.getCreationTime());
    }

    private void changeRunnerStatusIcon() {
        switch (runner.getStatus()) {
            case IN_PROGRESS:
                inProgress.addClassNameBaseVal(resources.runnerCss().blueColor());
                itemWidget.setImage(inProgress);
                break;

            case IN_QUEUE:
                inQueue.addClassNameBaseVal(resources.runnerCss().yellowColor());
                itemWidget.setImage(inQueue);
                break;

            case FAILED:
                failed.addClassNameBaseVal(resources.runnerCss().redColor());
                itemWidget.setImage(failed);
                break;

            case TIMEOUT:
                timeout.addClassNameBaseVal(resources.runnerCss().whiteColor());
                itemWidget.setImage(timeout);
                break;

            case STOPPED:
                stopped.addClassNameBaseVal(resources.runnerCss().redColor());
                itemWidget.setImage(stopped);
                break;

            case DONE:
                done.addClassNameBaseVal(resources.runnerCss().greenColor());
                itemWidget.setImage(done);
                break;

            default:
        }
    }

    /** {@inheritDoc} */
    @Override
    public Widget asWidget() {
        return itemWidget.asWidget();
    }

    /**
     * Sets action delegate to provide special actions.
     *
     * @param delegate
     *         delegate which need set
     */
    public void setDelegate(@NotNull ActionDelegate delegate) {
        this.delegate = delegate;
    }

    public interface ActionDelegate {
        /** Performs some actions in respond to user's actions. */
        void removeRunnerWidget(@NotNull Runner runner);
    }
}