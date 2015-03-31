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

package org.eclipse.che.plugin.tour.client.html;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Florent Benoit
 */
public class TestCustomImage {

    private CustomImage customImage;

    @BeforeClass
    public void init() {
        this.customImage = new CustomImage();
    }

    @Test
    public void checkImageNoWidth() {

        String text = "hello this is a test ![a picture of my dog](my dog.jpg)";
        assertEquals(sanitize(text), "hello this is a test <img src='my%20dog.jpg' alt='a picture of my dog' />");
    }

    @Test
    public void checkImageWidthHeight() {

        String text = "![](./pic/pic1s.png = 250x30)";
        assertEquals( sanitize(text), "<img src='./pic/pic1s.png' alt='' width='250' height='30' />");
    }

    @Test
    public void checkImageWidthHeightNoSpaces() {

        String text = "![](./pic/pic1s.png=250x30)";
        String changed = sanitize(text);
        assertEquals(changed, "<img src='./pic/pic1s.png' alt='' width='250' height='30' />");
    }

    @Test
    public void checkImageWidthNoHeight() {

        String text = "![](./pic/pic1s.png=250x)";
        assertEquals(sanitize(text), "<img src='./pic/pic1s.png' alt='' width='250' />");
    }

    @Test
    public void checkImageJavaScript() {

        String text = "![](javascript:alert('XSS')";
        assertEquals(sanitize(text), "<img src='#' alt='' />");
    }

    protected String sanitize(String text) {
        return customImage.addImages(text);
    }
}
