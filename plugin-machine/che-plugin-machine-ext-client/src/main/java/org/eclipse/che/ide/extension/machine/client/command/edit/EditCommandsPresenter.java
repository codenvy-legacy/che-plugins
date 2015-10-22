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
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.eclipse.che.api.machine.shared.dto.CommandDto;
import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.api.workspace.gwt.client.WorkspaceServiceClient;
import org.eclipse.che.api.workspace.shared.dto.UsersWorkspaceDto;
import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.actions.SelectCommandComboBoxReady;
import org.eclipse.che.ide.extension.machine.client.command.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.CommandConfigurationPage;
import org.eclipse.che.ide.extension.machine.client.command.CommandConfigurationPage.DirtyStateListener;
import org.eclipse.che.ide.extension.machine.client.command.CommandManager;
import org.eclipse.che.ide.extension.machine.client.command.CommandType;
import org.eclipse.che.ide.extension.machine.client.command.CommandTypeRegistry;
import org.eclipse.che.ide.ui.dialogs.ConfirmCallback;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.ui.dialogs.choice.ChoiceDialog;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmDialog;
import org.eclipse.che.ide.util.UUID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Presenter for managing commands.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class EditCommandsPresenter implements EditCommandsView.ActionDelegate {

    private final EditCommandsView                     view;
    private final WorkspaceServiceClient               workspaceServiceClient;
    private final CommandManager                       commandManager;
    private final String                               workspaceId;
    private final DtoFactory                           dtoFactory;
    private final CommandTypeRegistry                  commandTypeRegistry;
    private final DialogFactory                        dialogFactory;
    private final MachineLocalizationConstant          localizationConstant;
    private final Provider<SelectCommandComboBoxReady> selectCommandActionProvider;

    private final Set<ConfigurationChangedListener> configurationChangedListeners;

    private CommandConfigurationPage<CommandConfiguration> editedPage;
    /** Command that being edited. */
    private CommandConfiguration                           editedCommand;
    /** Name of the edited command before editing. */
    private String                                         editedCommandOriginName;

    @Inject
    protected EditCommandsPresenter(EditCommandsView view,
                                    WorkspaceServiceClient workspaceServiceClient,
                                    CommandTypeRegistry commandTypeRegistry,
                                    DialogFactory dialogFactory,
                                    MachineLocalizationConstant localizationConstant,
                                    Provider<SelectCommandComboBoxReady> selectCommandActionProvider,
                                    CommandManager commandManager,
                                    @Named("workspaceId") String workspaceId,
                                    DtoFactory dtoFactory) {
        this.view = view;
        this.workspaceServiceClient = workspaceServiceClient;
        this.commandManager = commandManager;
        this.workspaceId = workspaceId;
        this.dtoFactory = dtoFactory;
        this.commandTypeRegistry = commandTypeRegistry;
        this.dialogFactory = dialogFactory;
        this.localizationConstant = localizationConstant;
        this.selectCommandActionProvider = selectCommandActionProvider;
        this.view.setDelegate(this);

        configurationChangedListeners = new HashSet<>();
    }

    @Override
    public void onOkClicked() {
        final CommandConfiguration selectedConfiguration = view.getSelectedConfiguration();
        if (!isViewModified() || selectedConfiguration == null) {
            if (selectedConfiguration != null) {
                selectCommandOnToolbar(selectedConfiguration);
            }
            view.close();
            return;
        }

        updateCommand(selectedConfiguration).then(new Operation<UsersWorkspaceDto>() {
            @Override
            public void apply(UsersWorkspaceDto arg) throws OperationException {
                view.close();
                fireConfigurationUpdated(selectedConfiguration);
            }
        });
    }

    private void selectCommandOnToolbar(CommandConfiguration commandToSelect) {
        selectCommandActionProvider.get().setSelectedCommand(commandToSelect);
    }

    @Override
    public void onApplyClicked() {
        final CommandConfiguration selectedConfiguration = view.getSelectedConfiguration();
        if (selectedConfiguration == null) {
            return;
        }

        updateCommand(selectedConfiguration).then(new Operation<UsersWorkspaceDto>() {
            @Override
            public void apply(UsersWorkspaceDto arg) throws OperationException {
                fetchCommands(arg.getName());
                fireConfigurationUpdated(selectedConfiguration);
            }
        });
    }

    private Promise<UsersWorkspaceDto> updateCommand(CommandConfiguration selectedConfiguration) {
        final CommandDto commandDto = dtoFactory.createDto(CommandDto.class)
                                                .withName(selectedConfiguration.getName())
                                                .withCommandLine(selectedConfiguration.toCommandLine())
                                                .withType(selectedConfiguration.getType().getId());

        if (editedCommandOriginName.equals(selectedConfiguration.getName())) {
            return workspaceServiceClient.updateCommand(workspaceId, commandDto);
        } else {
            return workspaceServiceClient.deleteCommand(workspaceId, editedCommandOriginName)
                                         .thenPromise(new Function<UsersWorkspaceDto, Promise<UsersWorkspaceDto>>() {
                                             @Override
                                             public Promise<UsersWorkspaceDto> apply(UsersWorkspaceDto arg) throws FunctionException {
                                                 return workspaceServiceClient.addCommand(workspaceId, commandDto);
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
                updateCommand(editedCommand).then(new Operation<UsersWorkspaceDto>() {
                    @Override
                    public void apply(UsersWorkspaceDto arg) throws OperationException {
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
                localizationConstant.editCommandsSaveChangesTitle(),
                localizationConstant.editCommandsSaveChangesConfirmation(editedCommand.getName()),
                localizationConstant.editCommandsSaveChangesSave(),
                localizationConstant.editCommandsSaveChangesDiscard(),
                saveCallback,
                discardCallback);
        dialog.show();
    }

    private void createCommand(CommandType type) {
        final String name = type.getDisplayName() + " (" + UUID.uuid(2, 10) + ')';
        final CommandDto commandDto = dtoFactory.createDto(CommandDto.class)
                                                .withName(name)
                                                .withCommandLine(type.getCommandTemplate())
                                                .withType(type.getId());
        workspaceServiceClient.addCommand(workspaceId, commandDto).then(new Operation<UsersWorkspaceDto>() {
            @Override
            public void apply(UsersWorkspaceDto arg) throws OperationException {
                fetchCommands(arg.getName());

                final CommandType type = commandTypeRegistry.getCommandTypeById(commandDto.getType());
                fireConfigurationAdded(type.getConfigurationFactory().createFromDto(commandDto));
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
                workspaceServiceClient.deleteCommand(workspaceId, selectedConfiguration.getName()).then(new Operation<UsersWorkspaceDto>() {
                    @Override
                    public void apply(UsersWorkspaceDto arg) throws OperationException {
                        fetchCommands(null);
                        fireConfigurationRemoved(selectedConfiguration);
                    }
                });
            }
        };

        final ConfirmDialog confirmDialog = dialogFactory.createConfirmDialog(
                "",
                localizationConstant.editCommandsRemoveConfirmation(selectedConfiguration.getName()),
                confirmCallback,
                null);
        confirmDialog.show();
    }

    @Override
    public void onExecuteClicked() {
        final CommandConfiguration selectedConfiguration = view.getSelectedConfiguration();
        if (selectedConfiguration == null) {
            return;
        }

        if (isViewModified()) {
            dialogFactory.createMessageDialog("", localizationConstant.editCommandsExecuteMessage(), null).show();
            return;
        }

        commandManager.execute(selectedConfiguration);
        view.close();
    }

    @Override
    public void onCommandTypeSelected(CommandType type) {
        view.setAddButtonState(true);
        view.setRemoveButtonState(false);
        view.setExecuteButtonState(false);

        if (!isViewModified()) {
            reset();
            return;
        }

        final ConfirmCallback saveCallback = new ConfirmCallback() {
            @Override
            public void accepted() {
                updateCommand(editedCommand).then(new Operation<UsersWorkspaceDto>() {
                    @Override
                    public void apply(UsersWorkspaceDto arg) throws OperationException {
                        fetchCommands(null);
                        fireConfigurationUpdated(editedCommand);
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
                localizationConstant.editCommandsSaveChangesTitle(),
                localizationConstant.editCommandsSaveChangesConfirmation(editedCommand.getName()),
                localizationConstant.editCommandsSaveChangesSave(),
                localizationConstant.editCommandsSaveChangesDiscard(),
                saveCallback,
                discardCallback);
        dialog.show();
    }

    private void reset() {
        editedCommand = null;
        editedCommandOriginName = null;
        editedPage = null;

        view.setConfigurationName("");
        view.clearCommandConfigurationsDisplayContainer();
    }

    @Override
    public void onConfigurationSelected(final CommandConfiguration configuration) {
        if (!isViewModified()) {
            handleCommandSelection(configuration);
            return;
        }

        final ConfirmCallback saveCallback = new ConfirmCallback() {
            @Override
            public void accepted() {
                updateCommand(editedCommand).then(new Operation<UsersWorkspaceDto>() {
                    @Override
                    public void apply(UsersWorkspaceDto arg) throws OperationException {
                        fetchCommands(configuration.getName());
                        fireConfigurationUpdated(editedCommand);
                        handleCommandSelection(configuration);
                    }
                });
            }
        };

        final ConfirmCallback discardCallback = new ConfirmCallback() {
            @Override
            public void accepted() {
                fetchCommands(configuration.getName());
                handleCommandSelection(configuration);
            }
        };

        final ChoiceDialog dialog = dialogFactory.createChoiceDialog(
                localizationConstant.editCommandsSaveChangesTitle(),
                localizationConstant.editCommandsSaveChangesConfirmation(editedCommand.getName()),
                localizationConstant.editCommandsSaveChangesSave(),
                localizationConstant.editCommandsSaveChangesDiscard(),
                saveCallback,
                discardCallback);
        dialog.show();
    }

    private void handleCommandSelection(CommandConfiguration configuration) {
        editedCommand = configuration;
        editedCommandOriginName = configuration.getName();

        view.setAddButtonState(true);
        view.setRemoveButtonState(true);
        view.setExecuteButtonState(true);
        view.setConfigurationName(configuration.getName());

        final Collection<CommandConfigurationPage<? extends CommandConfiguration>> pages = configuration.getType().getConfigurationPages();
        for (CommandConfigurationPage<? extends CommandConfiguration> page : pages) {
            final CommandConfigurationPage<CommandConfiguration> p = ((CommandConfigurationPage<CommandConfiguration>)page);

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

            // TODO: for now only the 1'st page is showing but need to show all the pages
            break;
        }
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
     * @param commandNameToSelect
     *         name of the command to select
     */
    private void fetchCommands(@Nullable final String commandNameToSelect) {
        reset();

        view.setAddButtonState(false);
        view.setRemoveButtonState(false);
        view.setExecuteButtonState(false);
        view.setCancelButtonState(false);
        view.setApplyButtonState(false);

        workspaceServiceClient.getCommands(workspaceId).then(new Function<List<CommandDto>, List<CommandConfiguration>>() {
            @Override
            public List<CommandConfiguration> apply(List<CommandDto> arg) throws FunctionException {
                final List<CommandConfiguration> configurationList = new ArrayList<>();

                for (CommandDto descriptor : arg) {
                    final CommandType type = commandTypeRegistry.getCommandTypeById(descriptor.getType());
                    // skip command if it's type isn't registered
                    if (type != null) {
                        configurationList.add(type.getConfigurationFactory().createFromDto(descriptor));
                    }
                }

                return configurationList;
            }
        }).then(new Operation<List<CommandConfiguration>>() {
            @Override
            public void apply(List<CommandConfiguration> commandConfigurations) throws OperationException {
                view.setData(commandTypeRegistry.getCommandTypes(), commandConfigurations);

                if (commandNameToSelect != null) {
                    view.selectCommand(commandNameToSelect);
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

    private void fireConfigurationAdded(CommandConfiguration command) {
        for (ConfigurationChangedListener listener : configurationChangedListeners) {
            listener.onConfigurationAdded(command);
        }
    }

    private void fireConfigurationRemoved(CommandConfiguration command) {
        for (ConfigurationChangedListener listener : configurationChangedListeners) {
            listener.onConfigurationRemoved(command);
        }
    }

    private void fireConfigurationUpdated(CommandConfiguration command) {
        for (ConfigurationChangedListener listener : configurationChangedListeners) {
            listener.onConfigurationsUpdated(command);
        }
    }

    public void addConfigurationsChangedListener(ConfigurationChangedListener listener) {
        configurationChangedListeners.add(listener);
    }

    public void removeConfigurationsChangedListener(ConfigurationChangedListener listener) {
        configurationChangedListeners.remove(listener);
    }

    /** Listener that will be called when command configuration changed. */
    public interface ConfigurationChangedListener {
        void onConfigurationAdded(CommandConfiguration command);

        void onConfigurationRemoved(CommandConfiguration command);

        void onConfigurationsUpdated(CommandConfiguration command);
    }
}
