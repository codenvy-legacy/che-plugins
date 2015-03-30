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
package org.eclipse.che.ide.ext.runner.client.tabs.console.panel;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

/**
 * The builder that simplifies work flow of using {@link SimpleHtmlSanitizer} class for avoiding hackers attacks.
 *
 * @author Andrey Plotnikov
 */
public class MessageBuilder {

    private final Set<MessageType> types;
    private       String           message;

    @Inject
    public MessageBuilder() {
        types = EnumSet.noneOf(MessageType.class);
        message = "";
    }

    /**
     * The type of message that needs to be applied for message. If someone performs it a few times it will contain all values of type and
     * will apply all styles.
     *
     * @param type
     *         type that needs to apply
     * @return an instance of {@link MessageBuilder}
     */
    @Nonnull
    public MessageBuilder type(@Nonnull MessageType type) {
        types.add(type);
        return this;
    }

    /**
     * The message that needs to be contained inside {@link SafeHtml} message. If someone performs it a few times it will contain the last
     * value.
     *
     * @param message
     *         message that needs to show
     * @return an instance of {@link MessageBuilder}
     */
    @Nonnull
    public MessageBuilder message(@Nonnull String message) {
        this.message = message;
        return this;
    }

    /** @return an instance of {@link SafeHtml} with all given information */
    @Nonnull
    public SafeHtml build() {
        SafeHtmlBuilder builder = new SafeHtmlBuilder().appendHtmlConstant("<pre style='margin:0px;'>");
        StringBuilder prefixes = new StringBuilder();

        for (Iterator<MessageType> iterator = types.iterator(); iterator.hasNext(); ) {
            MessageType type = iterator.next();
            String prefix = type.getPrefix();

            builder.appendHtmlConstant("[<span style='color:" + type.getColor() + ";'>")
                   .appendHtmlConstant("<b>" + prefix.replaceAll("[\\[\\]]", "") + "</b></span>]");

            prefixes.append(prefix);

            if (iterator.hasNext()) {
                prefixes.append(' ');
            }
        }

        builder.append(SimpleHtmlSanitizer.sanitizeHtml(message.substring(prefixes.length())));

        return builder.appendHtmlConstant("</pre>")
                      .toSafeHtml();
    }

}