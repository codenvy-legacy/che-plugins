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
package org.eclipse.che.ide.extension.machine.client.command.valueproviders;

import com.google.inject.Inject;

import org.eclipse.che.ide.util.loging.Log;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implementation for {@link CommandValueProviderRegistry}.
 *
 * @author Artem Zatsarynnyy
 */
public class CommandValueProviderRegistryImpl implements CommandValueProviderRegistry {

    private final Map<String, CommandValueProvider> valueProviders;

    public CommandValueProviderRegistryImpl() {
        this.valueProviders = new HashMap<>();
    }

    @Inject(optional = true)
    private void register(Set<CommandValueProvider> valueProviders) {
        for (CommandValueProvider provider : valueProviders) {
            final String key = provider.getKey();
            if (this.valueProviders.containsKey(key)) {
                Log.warn(CommandValueProviderRegistryImpl.class, "Value provider for key " + key + " is already registered.");
            } else {
                this.valueProviders.put(key, provider);
            }
        }
    }

    @Nonnull
    @Override
    public String getValue(String key) {
        final CommandValueProvider commandValueProvider = valueProviders.get(key);
        if (commandValueProvider != null) {
            return commandValueProvider.getValue();
        }

        return "";
    }

    @Override
    public Set<String> getKeys() {
        return valueProviders.keySet();
    }
}
