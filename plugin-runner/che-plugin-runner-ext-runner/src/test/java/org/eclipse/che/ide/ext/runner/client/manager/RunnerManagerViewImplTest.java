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
package org.eclipse.che.ide.ext.runner.client.manager;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.inject.factories.WidgetFactory;
import org.eclipse.che.ide.ext.runner.client.manager.button.ButtonWidget;
import org.eclipse.che.ide.ext.runner.client.manager.info.MoreInfo;
import org.eclipse.che.ide.ext.runner.client.manager.menu.MenuWidget;
import org.eclipse.che.ide.ext.runner.client.manager.menu.entry.MenuEntry;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.tabs.container.TabContainer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.vectomatic.dom.svg.ui.SVGResource;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrienko Alexander
 * @author Valeriy Svydenko
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class RunnerManagerViewImplTest {

    private static final String CONSOLE    = "Console";
    private static final String TERMINAL   = "Terminal";
    private static final String PROPERTIES = "Properties";

    private static final String TEXT                     = "some text";
    private static final String GWT_POPUP_STANDARD_STYLE = "gwt-PopupPanel";

    //mocks for constructor
    @Mock
    private PartStackUIResources       partStackUIResources;
    @Mock
    private RunnerResources            resources;
    @Mock
    private RunnerLocalizationConstant locale;
    @Mock
    private WidgetFactory              widgetFactory;
    @Mock
    private PopupPanel                 moreInfoPopup;
    @Mock
    private PopupPanel                 menuPopup;
    @Mock
    private ButtonWidget               buttonWidget;
    @Mock
    private AppContext                 appContext;

    //additional mocks
    @Mock
    private PartStackUIResources.PartStackCss css;
    @Mock
    private RunnerResources.RunnerCss         runnerCss;
    @Mock
    private MoreInfo                          moreInfoWidget;
    @Mock
    private SplitLayoutPanel                  splitLayoutPanel;
    @Mock
    private SVGResource                       imageReRun;
    @Mock
    private ButtonWidget                      reRun;
    @Mock
    private SVGResource                       imageRun;
    @Mock
    private ButtonWidget                      run;
    @Mock
    private SVGResource                       imageStop;
    @Mock
    private ButtonWidget                      stop;
    @Mock
    private SVGResource                       imageLogs;
    @Mock
    private ButtonWidget                      headerMenuBtn;
    @Mock
    private SVGResource                       menuIcon;
    @Mock
    private SimplePanel                       span;
    @Mock
    private ButtonWidget                      logs;
    @Mock
    private Runner                            runner;
    @Mock
    private TabContainer                      containerPresenter;
    @Mock
    private CurrentProject                    currentProject;
    @Mock
    private MenuWidget                        menuWidget;
    @Mock
    private MenuEntry                         entry;
    @Mock
    private RunnerManagerView.ActionDelegate  delegate;
    @Mock
    private ClickEvent                        clickEvent;
    @Mock
    private Element                           element;
    @Mock
    private Style                             style;
    @Mock
    private Widget                            splitter;

    @Captor
    private ArgumentCaptor<MenuEntry.ActionDelegate>    entryDelegateCaptor;
    @Captor
    private ArgumentCaptor<ButtonWidget.ActionDelegate> buttonDelegateCaptor;
    @Captor
    private ArgumentCaptor<ClickHandler>                clickHandlerCaptor;

    private RunnerManagerViewImpl view;

    @Before
    public void setUp() {
        when(locale.runnerTabConsole()).thenReturn(CONSOLE);
        when(locale.runnerTabProperties()).thenReturn(PROPERTIES);
        when(locale.runnerTabTerminal()).thenReturn(TERMINAL);

        when(partStackUIResources.partStackCss()).thenReturn(css);
        when(css.ideBasePartToolbar()).thenReturn(TEXT);
        when(partStackUIResources.minimize()).thenReturn(mock(SVGResource.class, RETURNS_DEEP_STUBS));

        when(resources.runnerCss()).thenReturn(runnerCss);
        when(runnerCss.opacityButton()).thenReturn(TEXT);

        when(resources.run()).thenReturn(imageRun);
        when(locale.tooltipRunButton()).thenReturn(TEXT);
        when(widgetFactory.createButton(TEXT, imageRun)).thenReturn(run);

        when(resources.reRun()).thenReturn(imageReRun);
        when(locale.tooltipRerunButton()).thenReturn(TEXT);
        when(widgetFactory.createButton(TEXT, imageReRun)).thenReturn(reRun);

        when(resources.stop()).thenReturn(imageStop);
        when(locale.tooltipStopButton()).thenReturn(TEXT);
        when(widgetFactory.createButton(TEXT, imageStop)).thenReturn(stop);

        when(resources.logs()).thenReturn(imageLogs);
        when(locale.tooltipLogsButton()).thenReturn(TEXT);
        when(widgetFactory.createButton(TEXT, imageLogs)).thenReturn(logs);

        when(locale.tooltipHeaderMenuButton()).thenReturn(TEXT);
        when(resources.menuIcon()).thenReturn(menuIcon);
        when(widgetFactory.createButton(TEXT, menuIcon)).thenReturn(headerMenuBtn);

        when(locale.menuToggleSplitter()).thenReturn(TEXT);
        when(widgetFactory.createMenuEntry(TEXT)).thenReturn(entry);

        when(widgetFactory.createMenuWidget()).thenReturn(menuWidget);

        when(menuWidget.getSpan()).thenReturn(span);

        when(resources.moreInfo()).thenReturn(mock(SVGResource.class, RETURNS_DEEP_STUBS));

        when(locale.tooltipDockerButton()).thenReturn(TEXT);

        when(widgetFactory.createMoreInfo()).thenReturn(moreInfoWidget);
        when(locale.runnersPanelTitle()).thenReturn(TEXT);
        when(appContext.getCurrentProject()).thenReturn(currentProject);

        view = new RunnerManagerViewImpl(partStackUIResources, resources, locale, widgetFactory, appContext, moreInfoPopup, menuPopup);
        view.setDelegate(delegate);
    }

    @Test
    public void prepareActionShouldBePerformed() {
        verify(widgetFactory).createMoreInfo();
        verify(locale).runnersPanelTitle();

        verify(moreInfoPopup).removeStyleName(GWT_POPUP_STANDARD_STYLE);
        verify(moreInfoPopup).add(moreInfoWidget);

        //set action delegate for testing handlers
        RunnerManagerView.ActionDelegate actionDelegate = mock(RunnerManagerView.ActionDelegate.class);
        view.setDelegate(actionDelegate);

        ArgumentCaptor<MouseOverHandler> mouseOverHandlerCaptor = ArgumentCaptor.forClass(MouseOverHandler.class);
        verify(view.moreInfoPanel).addDomHandler(mouseOverHandlerCaptor.capture(), eq(MouseOverEvent.getType()));
        MouseOverHandler mouseOverHandler = mouseOverHandlerCaptor.getValue();
        mouseOverHandler.onMouseOver(mock(MouseOverEvent.class));

        verify(view.image).addStyleName(TEXT);
        verify(actionDelegate).onMoreInfoBtnMouseOver();

        ArgumentCaptor<MouseOutHandler> mouseOutHandlerCaptor = ArgumentCaptor.forClass(MouseOutHandler.class);
        verify(view.moreInfoPanel).addDomHandler(mouseOutHandlerCaptor.capture(), eq(MouseOutEvent.getType()));
        MouseOutHandler mouseOutHandler = mouseOutHandlerCaptor.getValue();
        mouseOutHandler.onMouseOut(mock(MouseOutEvent.class));

        verify(resources, times(4)).runnerCss();
        verify(runnerCss, times(2)).opacityButton();
        verify(view.image).removeStyleName(TEXT);
        verify(moreInfoPopup).hide();

        /* verify initialize button */
        //run button
        verify(widgetFactory).createButton(TEXT, imageRun);
        verifyButton(imageRun, run, view.runButtonPanel);
        verify(actionDelegate).onRunButtonClicked();
        verify(run).setEnable();

        //re-run button
        verify(widgetFactory).createButton(TEXT, imageReRun);
        verifyButton(imageReRun, reRun, view.otherButtonsPanel);

        //stop button
        verify(widgetFactory).createButton(TEXT, imageStop);
        verifyButton(imageStop, stop, view.otherButtonsPanel);
        verify(actionDelegate).onStopButtonClicked();

        //stop button
        verify(widgetFactory).createButton(TEXT, imageLogs);
        verifyButton(imageLogs, logs, view.otherButtonsPanel);
        verify(actionDelegate).onLogsButtonClicked();
    }

    private void verifyButton(SVGResource imageResource, ButtonWidget btnWidget, FlowPanel buttonPanel) {
        ArgumentCaptor<ButtonWidget.ActionDelegate> btnCaptor = ArgumentCaptor.forClass(ButtonWidget.ActionDelegate.class);

        verify(widgetFactory).createButton(TEXT, imageResource);
        verify(btnWidget).setDelegate(btnCaptor.capture());
        verify(btnWidget).setDisable();
        verify(buttonPanel).add(btnWidget);

        ButtonWidget.ActionDelegate runButtonDelegate = btnCaptor.getValue();
        runButtonDelegate.onButtonClicked();
    }

    @Test
    public void menuShouldBeInitialized() throws Exception {
        verify(menuPopup).removeStyleName(GWT_POPUP_STANDARD_STYLE);

        verify(widgetFactory).createMenuWidget();
        verify(widgetFactory).createMenuEntry(TEXT);
        verify(entry).setDelegate(Matchers.<MenuEntry.ActionDelegate>anyObject());
        verify(menuWidget).addEntry(entry);
        verify(menuPopup).add(menuWidget);

        verify(widgetFactory).createButton(TEXT, menuIcon);
        headerMenuBtn.setEnable();
        headerMenuBtn.setDelegate(Matchers.<ButtonWidget.ActionDelegate>anyObject());

        verify(menuWidget).getSpan();
        verify(span).addDomHandler(Matchers.<ClickHandler>anyObject(), eq(ClickEvent.getType()));
    }

    @Test
    public void onMenuEntryShouldBeClicked() throws Exception {
        verify(entry).setDelegate(entryDelegateCaptor.capture());
        entryDelegateCaptor.getValue().onEntryClicked(true);

        verify(delegate).onToggleSplitterClicked(true);
        verify(menuPopup).hide();
    }

    @Test
    public void onMenuButtonClicked() throws Exception {
        verify(headerMenuBtn).setDelegate(buttonDelegateCaptor.capture());
        buttonDelegateCaptor.getValue().onButtonClicked();

        verify(menuPopup).setPopupPosition(anyInt(), anyInt());

        verify(menuPopup).show();
    }

    @Test
    public void menuShouldBtHiddenWhenClickNotOnMenu() throws Exception {
        verify(span).addDomHandler(clickHandlerCaptor.capture(), eq(ClickEvent.getType()));
        clickHandlerCaptor.getValue().onClick(clickEvent);
        verify(menuPopup).hide();
    }

    @Test
    public void allButtonsShouldBeDisabledWhenCurrentProjectIsNull() throws Exception {
        when(appContext.getCurrentProject()).thenReturn(null);

        view.update(runner);

        verify(run, times(2)).setDisable();
        verify(stop, times(2)).setDisable();
        verify(reRun, times(2)).setDisable();
        verify(logs, times(2)).setDisable();
    }

    @Test
    public void shouldUpdateWhenRunnerInStatusInQueue() {
        reset(run, stop, reRun, logs);
        when(runner.getStatus()).thenReturn(Runner.Status.IN_QUEUE);
        when(appContext.getCurrentProject()).thenReturn(currentProject);

        view.update(runner);

        verifyEnableAllButton();
        verify(stop).setDisable();
        verify(logs).setDisable();
    }

    @Test
    public void shouldUpdateWhenRunnerInStatusFailed() {
        reset(run, stop, reRun, logs);
        when(runner.getStatus()).thenReturn(Runner.Status.FAILED);
        when(appContext.getCurrentProject()).thenReturn(currentProject);

        view.update(runner);

        verifyEnableAllButton();
        verify(stop).setDisable();
        verify(reRun).setEnable();
        verify(logs).setDisable();
    }

    @Test
    public void shouldUpdateWhenRunnerInStatusStopped() {
        reset(run, stop, reRun, logs);
        when(runner.getStatus()).thenReturn(Runner.Status.STOPPED);
        when(appContext.getCurrentProject()).thenReturn(currentProject);

        view.update(runner);

        verifyEnableAllButton();
        verify(stop).setDisable();
        verify(reRun).setEnable();
        verify(logs).setDisable();
    }

    private void verifyEnableAllButton() {
        verify(run).setEnable();
        verify(reRun).setDisable();
        verify(stop).setEnable();
        verify(logs).setEnable();
    }

    @Test
    public void shouldSetCorrectApplicationURl() {
        String url = "http://some/url";
        when(runnerCss.cursor()).thenReturn(TEXT);

        view.setApplicationURl(url);

        verify(view.appReference).removeStyleName(TEXT);
        verify(view.appReference).addStyleName(TEXT);
        verify(view.appReference).setText(url);
    }

    @Test
    public void shouldSetInCorrectApplicationURl() {
        String url = "some/url";
        when(runnerCss.cursor()).thenReturn(TEXT);

        view.setApplicationURl(url);

        verify(view.appReference).removeStyleName(TEXT);
        verify(view.appReference, never()).addStyleName(TEXT);
        verify(view.appReference).setText(url);
    }

    @Test
    public void shouldSetTimeOut() {
        view.setTimeout(TEXT);

        verify(view.timeout).setText(TEXT);
    }

    @Test
    public void shouldShowMoreInfoPopup() {
        when(view.timeout.getAbsoluteLeft()).thenReturn(150);
        when(view.timeout.getAbsoluteTop()).thenReturn(150);

        view.showMoreInfoPopup(runner);

        verify(moreInfoWidget).update(runner);
        verify(view.timeout).getAbsoluteLeft();
        verify(view.timeout).getAbsoluteTop();
        verify(moreInfoPopup).setPopupPosition(70, 20);
        verify(moreInfoPopup).show();
    }

    @Test
    public void shouldUpdateMoreInfoPopup() {
        view.updateMoreInfoPopup(runner);

        verify(moreInfoWidget).update(runner);
    }

    @Test
    public void shouldSetLeftPanel() {
        view.setLeftPanel(containerPresenter);

        verify(containerPresenter).go(view.leftTabsPanel);
    }

    @Test
    public void rightPropertiesPanelShouldBeSet() {
        view.setRightPropertiesPanel(containerPresenter);

        verify(containerPresenter).showTabTitle(CONSOLE, false);
        verify(containerPresenter).showTabTitle(PROPERTIES, false);

        verify(containerPresenter).showTab(TERMINAL);

        verify(containerPresenter).go(view.rightPropertiesPanel);
    }

    @Test
    public void shouldHideOtherButtons() {
        view.hideOtherButtons();

        verify(view.otherButtonsPanel).setVisible(false);
    }

    @Test
    public void shouldShowOtherButtons() {
        view.showOtherButtons();

        verify(view.otherButtonsPanel).setVisible(true);
    }

    @Test
    public void runButtonShouldBeEnabled() throws Exception {
        reset(run);
        view.setEnableRunButton(true);

        verify(run).setEnable();
        verify(run, never()).setDisable();
    }

    @Test
    public void runButtonShouldBeDisabled() throws Exception {
        reset(run);
        view.setEnableRunButton(false);

        verify(run, never()).setEnable();
        verify(run).setDisable();
    }

    @Test
    public void reRunButtonShouldBeEnabled() throws Exception {
        reset(reRun);
        view.setEnableReRunButton(true);

        verify(reRun).setEnable();
        verify(reRun, never()).setDisable();
    }

    @Test
    public void reRunButtonShouldBeDisabled() throws Exception {
        reset(reRun);
        view.setEnableReRunButton(false);

        verify(reRun, never()).setEnable();
        verify(reRun).setDisable();
    }

    @Test
    public void stopButtonShouldBeEnabled() throws Exception {
        reset(stop);
        view.setEnableStopButton(true);

        verify(stop).setEnable();
        verify(stop, never()).setDisable();
    }

    @Test
    public void stopButtonShouldBeDisabled() throws Exception {
        reset(stop);
        view.setEnableStopButton(false);

        verify(stop, never()).setEnable();
        verify(stop).setDisable();
    }

    @Test
    public void logsButtonShouldBeEnabled() throws Exception {
        reset(logs);
        view.setEnableLogsButton(true);

        verify(logs).setEnable();
        verify(logs, never()).setDisable();
    }

    @Test
    public void logsButtonShouldBeDisabled() throws Exception {
        reset(logs);
        view.setEnableLogsButton(false);

        verify(logs, never()).setEnable();
        verify(logs).setDisable();
    }

}