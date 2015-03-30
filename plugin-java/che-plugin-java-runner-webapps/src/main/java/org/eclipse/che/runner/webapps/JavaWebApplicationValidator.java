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
package org.eclipse.che.runner.webapps;

import org.eclipse.che.api.runner.internal.DeploymentSources;
import org.eclipse.che.api.runner.internal.DeploymentSourcesValidator;

import java.io.IOException;
import java.util.zip.ZipFile;

/**
 * Validator checks that {@link DeploymentSources} is a valid Java web application.
 *
 * @author Artem Zatsarynnyy
 */
public class JavaWebApplicationValidator implements DeploymentSourcesValidator {
    public static final String WEB_XML = "WEB-INF" + java.io.File.separatorChar + "web.xml";

    @Override
    public boolean isValid(DeploymentSources deployment) {
        if (deployment.isZipArchive()) {
            try (ZipFile zip = new ZipFile(deployment.getFile())) {
                String path = WEB_XML.replace("\\", "/");
                return zip.getEntry(path) != null;
            } catch (IOException e) {
                return false;
            }
        }
        return new java.io.File(deployment.getFile(), WEB_XML).exists();
    }
}
