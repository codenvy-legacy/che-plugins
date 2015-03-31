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
 * Test the hyperlinks of wiki syntax
 * @author Florent Benoit
 */
public class TestWikiHyperLinks {


    private WikiHyperLinks wikiHyperLinks;

    @BeforeClass
    public void init() {
        this.wikiHyperLinks = new WikiHyperLinks();
    }

    @Test
    public void checkSimpleLink() {

        String text = "hello this is a test [http://www.codenvy.com]";
        assertEquals(addLinks(text), "hello this is a test <a href=\"http://www.codenvy.com\">http://www.codenvy.com</a>");
    }


    @Test
    public void checkTwoLinks() {

        String text = "hello this is a test [http://www.codenvy.com] and [http://www.codenvy.com]";
        assertEquals(addLinks(text), "hello this is a test <a href=\"http://www.codenvy.com\">http://www.codenvy.com</a> and <a href=\"http://www.codenvy.com\">http://www.codenvy.com</a>");
    }

    @Test
    public void checkLinkWithSimpleTitle() {
        String text = "hello this is a test [http://www.codenvy.com simple]";
        assertEquals(addLinks(text), "hello this is a test <a href=\"http://www.codenvy.com\">simple</a>");
    }

    @Test
    public void checkLinkWithLongTitle() {
        String text = "hello this is a test [http://www.codenvy.com a long title]";
        assertEquals(addLinks(text), "hello this is a test <a href=\"http://www.codenvy.com\">a long title</a>");
    }

    protected String addLinks(String text) {
        return wikiHyperLinks.addLinks(text);
    }
}
