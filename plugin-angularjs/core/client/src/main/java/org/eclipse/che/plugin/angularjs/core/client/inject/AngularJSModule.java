/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.angularjs.core.client.inject;

import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.ide.api.project.type.wizard.ProjectWizardRegistrar;
import org.eclipse.che.ide.ext.web.html.editor.HTMLCodeAssistProcessor;
import org.eclipse.che.ide.ext.web.js.editor.JsCodeAssistProcessor;
import org.eclipse.che.plugin.angularjs.core.client.editor.AngularJSHtmlCodeAssistProcessor;
import org.eclipse.che.plugin.angularjs.core.client.javascript.JavaScriptCodeAssistProcessor;
import org.eclipse.che.plugin.angularjs.core.client.wizard.AngularJsProjectWizardRegistrar;
import org.eclipse.che.plugin.angularjs.core.client.wizard.BasicJsProjectWizardRegistrar;
import org.eclipse.che.plugin.angularjs.core.client.wizard.GruntJsProjectWizardRegistrar;
import org.eclipse.che.plugin.angularjs.core.client.wizard.GulpJsProjectWizardRegistrar;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.multibindings.GinMultibinder;

/**
 * Gin Module for injection of AngularJS plugin.
 * 
 * @author Florent Benoit
 */
@ExtensionGinModule
public class AngularJSModule extends AbstractGinModule {

    /** {@inheritDoc} */
    @Override
    protected void configure() {

        // Adds the Auto Edit Strategy (interpolation braces)
        // bind(AutoEditStrategyFactory.class).to(AngularJSInterpolationBraceStrategyFactory.class).in(Singleton.class);
        // GinMultibinder<AutoEditStrategyFactory> binder = GinMultibinder.newSetBinder(binder(), AutoEditStrategyFactory.class);
        // binder.addBinding().to(AngularJSInterpolationBraceStrategyFactory.class);

        // Add HTML completion processors (as being in a set)
        GinMultibinder<HTMLCodeAssistProcessor> binderHtmlProcessors = GinMultibinder.newSetBinder(binder(), HTMLCodeAssistProcessor.class);
        binderHtmlProcessors.addBinding().to(AngularJSHtmlCodeAssistProcessor.class);

        // Add JavaScript completion processors (as being in a set)
        GinMultibinder<JsCodeAssistProcessor> binderJsProcessors = GinMultibinder.newSetBinder(binder(), JsCodeAssistProcessor.class);
        binderJsProcessors.addBinding().to(JavaScriptCodeAssistProcessor.class);

        GinMultibinder<ProjectWizardRegistrar> projectWizardBinder = GinMultibinder.newSetBinder(binder(), ProjectWizardRegistrar.class);
        projectWizardBinder.addBinding().to(AngularJsProjectWizardRegistrar.class);
        projectWizardBinder.addBinding().to(BasicJsProjectWizardRegistrar.class);
        projectWizardBinder.addBinding().to(GulpJsProjectWizardRegistrar.class);
        projectWizardBinder.addBinding().to(GruntJsProjectWizardRegistrar.class);
    }

}
