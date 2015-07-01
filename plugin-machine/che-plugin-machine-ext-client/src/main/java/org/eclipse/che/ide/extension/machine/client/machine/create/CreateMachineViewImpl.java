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
package org.eclipse.che.ide.extension.machine.client.machine.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.ui.window.Window;

/**
 * The implementation of {@link CreateMachineView}.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class CreateMachineViewImpl extends Window implements CreateMachineView {

    private static final CreateMachineViewImplUiBinder UI_BINDER = GWT.create(CreateMachineViewImplUiBinder.class);

    @UiField(provided = true)
    MachineLocalizationConstant localizationConstant;

    @UiField
    TextBox machineName;
    @UiField
    TextBox recipeURL;
    @UiField
    Label   errorHint;

    private ActionDelegate delegate;
    private Button         createButton;
    private Button         replaceButton;
    private Button         cancelButton;

    @Inject
    public CreateMachineViewImpl(MachineLocalizationConstant localizationConstant) {
        this.localizationConstant = localizationConstant;

        setWidget(UI_BINDER.createAndBindUi(this));
        setTitle(localizationConstant.viewCreateMachineTitle());

        machineName.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                delegate.onNameChanged();
            }
        });

        recipeURL.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                delegate.onRecipeUrlChanged();
            }
        });

        createFooterButtons();
    }

    private void createFooterButtons() {
        createButton = createButton(localizationConstant.viewCreateMachineButtonCreate(), "window-create-machine-create",
                                    new ClickHandler() {
                                        @Override
                                        public void onClick(ClickEvent event) {
                                            delegate.onCreateClicked();
                                        }
                                    });

        replaceButton = createButton(localizationConstant.viewCreateMachineButtonReplace(), "window-create-machine-replace",
                                     new ClickHandler() {
                                         @Override
                                         public void onClick(ClickEvent event) {
                                             delegate.onReplaceDevMachineClicked();
                                         }
                                     });

        cancelButton = createButton(localizationConstant.cancelButton(), "window-create-machine-cancel",
                                    new ClickHandler() {
                                        @Override
                                        public void onClick(ClickEvent event) {
                                            delegate.onCancelClicked();
                                        }
                                    });

        getFooter().add(createButton);
        getFooter().add(replaceButton);
        getFooter().add(cancelButton);
    }

    @Override
    public void show() {
        super.show();

        new Timer() {
            @Override
            public void run() {
                machineName.setFocus(true);
            }
        }.schedule(300);
    }

    @Override
    public void close() {
        hide();
    }

    @Override
    public String getMachineName() {
        return machineName.getValue();
    }

    @Override
    public void setMachineName(String name) {
        machineName.setValue(name);
    }

    @Override
    public String getRecipeURL() {
        return recipeURL.getValue();
    }

    @Override
    public void setRecipeURL(String url) {
        recipeURL.setValue(url);
        recipeURL.setTitle(url);
    }

    @Override
    public void setErrorHint(boolean show) {
        errorHint.setVisible(show);
    }

    @Override
    public void setCreateButtonState(boolean enabled) {
        createButton.setEnabled(enabled);
    }

    @Override
    public void setReplaceButtonState(boolean enabled) {
        replaceButton.setEnabled(enabled);
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    interface CreateMachineViewImplUiBinder extends UiBinder<Widget, CreateMachineViewImpl> {
    }
}
