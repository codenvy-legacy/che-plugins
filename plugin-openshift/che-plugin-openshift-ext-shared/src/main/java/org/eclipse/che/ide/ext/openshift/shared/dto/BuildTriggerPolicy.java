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
package org.eclipse.che.ide.ext.openshift.shared.dto;

import org.eclipse.che.dto.shared.DTO;

@DTO
public interface BuildTriggerPolicy {
    ImageChangeTrigger getImageChange();

    void setImageChange(ImageChangeTrigger imageChange);

    BuildTriggerPolicy withImageChange(ImageChangeTrigger imageChange);

    WebHookTrigger getGithub();

    void setGithub(WebHookTrigger github);

    BuildTriggerPolicy withGithub(WebHookTrigger github);

    String getType();

    void setType(String type);

    BuildTriggerPolicy withType(String type);

    WebHookTrigger getGeneric();

    void setGeneric(WebHookTrigger generic);

    BuildTriggerPolicy withGeneric(WebHookTrigger generic);

}
