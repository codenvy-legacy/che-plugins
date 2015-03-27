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
package org.eclipse.che.plugin.angularjs.completion.dto;

import java.util.List;

/**
 * @author Florent Benoit
 */
public class AngularTemplate {

    private Templating templating;

    public AngularTemplate(Templating templating) {
        this.templating = templating;
    }

    public TemplateDotProvider getTemplateProvider(String name) {
        List<TemplateDotProvider> templateDotProviderList = templating.getTemplateDotProviders();
        if (templateDotProviderList != null) {
            for (TemplateDotProvider templateDotProvider : templateDotProviderList) {
                if (name != null && name.equals(templateDotProvider.getName())) {
                    return templateDotProvider;
                }
            }
        }
        return null;
    }


    public Templating getTemplating() {
        return templating;
    }

    public TemplateDotProvider addOrGet(TemplateDotProvider templateDotProvider) {
        TemplateDotProvider existing = getTemplateProvider(templateDotProvider.getName());
        if (existing != null) {
            return existing;
        }
        templating.getTemplateDotProviders().add(templateDotProvider);
        return templateDotProvider;
    }

}
