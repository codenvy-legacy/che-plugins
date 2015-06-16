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
package org.eclipse.che.ide.extension.machine.client.command.edit;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.machine.gwt.client.CommandServiceClient;
import org.eclipse.che.api.machine.shared.dto.CommandDescriptor;
import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.command.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.CommandType;
import org.eclipse.che.ide.extension.machine.client.command.CommandTypeRegistry;
import org.eclipse.che.ide.extension.machine.client.command.ConfigurationPage;
import org.eclipse.che.ide.extension.machine.client.command.ConfigurationPage.DirtyStateListener;
import org.eclipse.che.ide.ui.dialogs.ConfirmCallback;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.ui.dialogs.choice.ChoiceDialog;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmDialog;
import org.eclipse.che.ide.util.UUID;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Presenter for managing command configurations.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class EditConfigurationsPresenter implements EditConfigurationsView.ActionDelegate {

    private final EditConfigurationsView      view;
    private final CommandServiceClient        commandServiceClient;
    private final CommandTypeRegistry         commandTypeRegistry;
    private final DialogFactory               dialogFactory;
    private final MachineLocalizationConstant localizationConstant;

    private final Set<ConfigurationsChangedListener> configurationsChangedListeners;

    private ConfigurationPage<CommandConfiguration> editedPage;
    /** Command that being edited. */
    private CommandConfiguration                    editedCommand;
    /** Name of the edited command before editing. */
    private String                                  editedCommandOriginName;

    @Inject
    protected EditConfigurationsPresenter(EditConfigurationsView view,
                                          CommandServiceClient commandServiceClient,
                                          CommandTypeRegistry commandTypeRegistry,
                                          DialogFactory dialogFactory,
                                          MachineLocalizationConstant localizationConstant) {
        this.view = view;
        this.view.setDelegate(this);
        this.commandServiceClient = commandServiceClient;
        this.commandTypeRegistry = commandTypeRegistry;
        this.dialogFactory = dialogFactory;
        this.localizationConstant = localizationConstant;

        configurationsChangedListeners = new HashSet<>();
    }

    @Override
    public void onOkClicked() {
        final CommandConfiguration selectedConfiguration = view.getSelectedConfiguration();
        if (!isViewModified() || selectedConfiguration == null) {
            view.close();
            return;
        }

        commandServiceClient.updateCommand(selectedConfiguration.getId(),
                                           selectedConfiguration.getName(),
                                           selectedConfiguration.toCommandLine()).then(new Operation<CommandDescriptor>() {
            @Override
            public void apply(CommandDescriptor arg) throws OperationException {
                view.close();
                fireConfigurationsChanged();
            }
        });
    }

    @Override
    public void onApplyClicked() {
        final CommandConfiguration selectedConfiguration = view.getSelectedConfiguration();
        if (selectedConfiguration != null) {
            commandServiceClient.updateCommand(selectedConfiguration.getId(),
                                               selectedConfiguration.getName(),
                                               selectedConfiguration.toCommandLine()).then(new Operation<CommandDescriptor>() {
                @Override
                public void apply(CommandDescriptor arg) throws OperationException {
                    fetchCommands(arg.getId());
                    fireConfigurationsChanged();
                }
            });
        }
    }

    @Override
    public void onCancelClicked() {
        view.close();
    }

    @Override
    public void onAddClicked() {
        final CommandType selectedType = view.getSelectedCommandType();
        if (selectedType == null) {
            return;
        }

        if (!isViewModified()) {
            createCommand(selectedType);
            return;
        }

        final ConfirmCallback saveCallback = new ConfirmCallback() {
            @Override
            public void accepted() {
                commandServiceClient.updateCommand(editedCommand.getId(),
                                                   editedCommand.getName(),
                                                   editedCommand.toCommandLine()).then(new Operation<CommandDescriptor>() {
                    @Override
                    public void apply(CommandDescriptor arg) throws OperationException {
                        createCommand(selectedType);
                    }
                });
            }
        };

        final ConfirmCallback discardCallback = new ConfirmCallback() {
            @Override
            public void accepted() {
                createCommand(selectedType);
            }
        };

        final ChoiceDialog dialog = dialogFactory.createChoiceDialog(
                localizationConstant.editConfigurationsSaveChangesTitle(),
                localizationConstant.editConfigurationsSaveChangesConfirmation(editedCommand.getName()),
                localizationConstant.editConfigurationsSaveChangesSave(),
                localizationConstant.editConfigurationsSaveChangesDiscard(),
                saveCallback,
                discardCallback);
        dialog.show();
    }

    private void createCommand(CommandType type) {
        final String name = type.getDisplayName() + " (" + UUID.uuid(2, 10) + ')';
        final Promise<CommandDescriptor> commandPromise = commandServiceClient.createCommand(name,
                                                                                             type.getCommandTemplate(),
                                                                                             type.getId());
        commandPromise.then(new Operation<CommandDescriptor>() {
            @Override
            public void apply(CommandDescriptor arg) throws OperationException {
                fetchCommands(arg.getId());
                fireConfigurationsChanged();
            }
        });
    }

    @Override
    public void onRemoveClicked() {
        final CommandConfiguration selectedConfiguration = view.getSelectedConfiguration();
        if (selectedConfiguration == null) {
            return;
        }

        final ConfirmCallback confirmCallback = new ConfirmCallback() {
            @Override
            public void accepted() {
                commandServiceClient.removeCommand(selectedConfiguration.getId()).then(new Operation<Void>() {
                    @Override
                    public void apply(Void arg) throws OperationException {
                        fetchCommands(null);
                        fireConfigurationsChanged();
                    }
                });
            }
        };

        final ConfirmDialog confirmDialog = dialogFactory.createConfirmDialog(
                "",
                localizationConstant.editConfigurationsRemoveConfirmation(selectedConfiguration.getName()),
                confirmCallback,
                null);
        confirmDialog.show();
    }

    @Override
    public void onCommandTypeSelected(CommandType type) {
        view.setAddButtonState(true);
        view.setRemoveButtonState(false);

        if (!isViewModified()) {
            resetEditedCommand();
            return;
        }

        final ConfirmCallback saveCallback = new ConfirmCallback() {
            @Override
            public void accepted() {
                commandServiceClient.updateCommand(editedCommand.getId(),
                                                   editedCommand.getName(),
                                                   editedCommand.toCommandLine()).then(new Operation<CommandDescriptor>() {
                    @Override
                    public void apply(CommandDescriptor arg) throws OperationException {
                        fetchCommands(null);
                        fireConfigurationsChanged();
                    }
                });
            }
        };

        final ConfirmCallback discardCallback = new ConfirmCallback() {
            @Override
            public void accepted() {
                fetchCommands(null);
            }
        };

        final ChoiceDialog dialog = dialogFactory.createChoiceDialog(
                localizationConstant.editConfigurationsSaveChangesTitle(),
                localizationConstant.editConfigurationsSaveChangesConfirmation(editedCommand.getName()),
                localizationConstant.editConfigurationsSaveChangesSave(),
                localizationConstant.editConfigurationsSaveChangesDiscard(),
                saveCallback,
                discardCallback);
        dialog.show();
    }

    private void resetEditedCommand() {
        editedCommand = null;
        editedCommandOriginName = null;
        editedPage = null;

        view.setConfigurationName("");
        view.clearCommandConfigurationsDisplayContainer();
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public void onConfigurationSelected(final CommandConfiguration configuration) {
        if (!isViewModified()) {
            handleCommandSelection(configuration);
            return;
        }

        final ConfirmCallback saveCallback = new ConfirmCallback() {
            @Override
            public void accepted() {
                commandServiceClient.updateCommand(editedCommand.getId(),
                                                   editedCommand.getName(),
                                                   editedCommand.toCommandLine()).then(new Operation<CommandDescriptor>() {
                    @Override
                    public void apply(CommandDescriptor arg) throws OperationException {
                        fetchCommands(configuration.getId());
                        fireConfigurationsChanged();
                        handleCommandSelection(configuration);
                    }
                });
            }
        };

        final ConfirmCallback discardCallback = new ConfirmCallback() {
            @Override
            public void accepted() {
                fetchCommands(configuration.getId());
                handleCommandSelection(configuration);
            }
        };

        final ChoiceDialog dialog = dialogFactory.createChoiceDialog(
                localizationConstant.editConfigurationsSaveChangesTitle(),
                localizationConstant.editConfigurationsSaveChangesConfirmation(editedCommand.getName()),
                localizationConstant.editConfigurationsSaveChangesSave(),
                localizationConstant.editConfigurationsSaveChangesDiscard(),
                saveCallback,
                discardCallback);
        dialog.show();
    }

    private void handleCommandSelection(CommandConfiguration configuration) {
        editedCommand = configuration;
        editedCommandOriginName = configuration.getName();

        view.setAddButtonState(true);
        view.setRemoveButtonState(true);
        view.setConfigurationName(configuration.getName());

        final Collection<ConfigurationPage<? extends CommandConfiguration>> pages = configuration.getType().getConfigurationPages();
        for (ConfigurationPage<? extends CommandConfiguration> page : pages) {
            final ConfigurationPage<CommandConfiguration> p = ((ConfigurationPage<CommandConfiguration>)page);

            editedPage = p;

            p.setDirtyStateListener(new DirtyStateListener() {
                @Override
                public void onDirtyStateChanged() {
                    view.setCancelButtonState(isViewModified());
                    view.setApplyButtonState(isViewModified());
                }
            });
            p.resetFrom(configuration);
            p.go(view.getCommandConfigurationsDisplayContainer());

            // TODO: for now show only first page but need to show all pages
            break;
        }

        // TODO: check dirty state and save prev command
    }

    @Override
    public void onNameChanged() {
        final CommandConfiguration selectedConfiguration = view.getSelectedConfiguration();
        if (selectedConfiguration != null) {
            selectedConfiguration.setName(view.getConfigurationName());

            view.setCancelButtonState(isViewModified());
            view.setApplyButtonState(isViewModified());
        }
    }

    /** Show dialog. */
    public void show() {
        fetchCommands(null);
        view.show();
    }

    /**
     * Fetch commands from server and update view.
     *
     * @param commandToSelect
     *         ID of the command to select
     */
    private void fetchCommands(@Nullable final String commandToSelect) {
        resetEditedCommand();
        view.setAddButtonState(false);
        view.setRemoveButtonState(false);
        view.setCancelButtonState(false);
        view.setApplyButtonState(false);

        commandServiceClient.getCommands().then(new Function<List<CommandDescriptor>, List<CommandConfiguration>>() {
            @Override
            public List<CommandConfiguration> apply(List<CommandDescriptor> arg) throws FunctionException {
                final List<CommandConfiguration> configurationList = new ArrayList<>();

                for (CommandDescriptor descriptor : arg) {
                    final CommandType type = commandTypeRegistry.getCommandTypeById(descriptor.getType());
                    // skip command if it's type isn't registered
                    if (type != null) {
                        configurationList.add(type.getConfigurationFactory().createFromCommandDescriptor(descriptor));
                    }
                }

                return configurationList;
            }
        }).then(new Operation<List<CommandConfiguration>>() {
            @Override
            public void apply(List<CommandConfiguration> commandConfigurations) throws OperationException {
                view.setData(commandTypeRegistry.getCommandTypes(), commandConfigurations);

                if (commandToSelect != null) {
                    view.selectCommand(commandToSelect);
                }
            }
        }).catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                dialogFactory.createMessageDialog("Error", arg.toString(), null).show();
            }
        });
    }

    private boolean isViewModified() {
        if (editedCommand == null || editedPage == null) {
            return false;
        }
        return editedPage.isDirty() || !editedCommandOriginName.equals(view.getConfigurationName());
    }

    private void fireConfigurationsChanged() {
        for (ConfigurationsChangedListener listener : configurationsChangedListeners) {
            listener.onConfigurationsChanged();
        }
    }

    public void addConfigurationsChangedListener(ConfigurationsChangedListener listener) {
        configurationsChangedListeners.add(listener);
    }

    public void removeConfigurationsChangedListener(ConfigurationsChangedListener listener) {
        configurationsChangedListeners.remove(listener);
    }

    /** Listener that will be called when command configurations changed. */
    public interface ConfigurationsChangedListener {
        void onConfigurationsChanged();
    }
}
