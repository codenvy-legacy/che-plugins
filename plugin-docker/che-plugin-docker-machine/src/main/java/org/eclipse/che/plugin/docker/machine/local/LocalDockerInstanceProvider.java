package org.eclipse.che.plugin.docker.machine.local;

import org.eclipse.che.api.core.util.LineConsumer;
import org.eclipse.che.api.machine.server.exception.MachineException;
import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.plugin.docker.client.*;
import org.eclipse.che.plugin.docker.client.parser.DockerImageIdentifier;
import org.eclipse.che.plugin.docker.client.parser.DockerImageIdentifierParser;
import org.eclipse.che.plugin.docker.machine.*;
import org.eclipse.che.plugin.docker.machine.node.WorkspaceFolderPathProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.Set;

/**
 * Implementation that helps avoiding machine creation failure if docker is offline but needed base image is cached.
 *
 * @author Alexander Garagatyi
 */
public class LocalDockerInstanceProvider extends DockerInstanceProvider {
    private static final Logger LOG = LoggerFactory.getLogger(LocalDockerInstanceProvider.class);

    private final DockerConnector docker;

    @Inject
    public LocalDockerInstanceProvider(DockerConnector docker,
                                       DockerConnectorConfiguration dockerConnectorConfiguration,
                                       DockerMachineFactory dockerMachineFactory,
                                       DockerInstanceStopDetector dockerInstanceStopDetector,
                                       @Named("machine.docker.dev_machine.machine_servers") Set<ServerConf> devMachineServers,
                                       @Named("machine.docker.machine_servers") Set<ServerConf> allMachinesServers,
                                       @Named("machine.docker.dev_machine.machine_volumes") Set<String> devMachineSystemVolumes,
                                       @Named("machine.docker.machine_volumes") Set<String> allMachinesSystemVolumes,
                                       @Nullable @Named("machine.docker.machine_extra_hosts") String allMachinesExtraHosts,
                                       WorkspaceFolderPathProvider workspaceFolderPathProvider,
                                       @Named("che.machine.projects.internal.storage") String projectFolderPath,
                                       @Named("machine.docker.pull_image") boolean doForcePullOnBuild,
                                       @Named("machine.docker.dev_machine.machine_env") Set<String> devMachineEnvVariables,
                                       @Named("machine.docker.machine_env") Set<String> allMachinesEnvVariables)
            throws IOException {
        super(docker,
                dockerConnectorConfiguration,
                dockerMachineFactory,
                dockerInstanceStopDetector,
                devMachineServers,
                allMachinesServers,
                devMachineSystemVolumes,
                allMachinesSystemVolumes,
                allMachinesExtraHosts,
                workspaceFolderPathProvider,
                projectFolderPath,
                doForcePullOnBuild,
                devMachineEnvVariables,
                allMachinesEnvVariables);
        this.docker = docker;
    }

    @Override
    protected void buildImage(Dockerfile dockerfile,
                              LineConsumer creationLogsOutput,
                              String imageName,
                              boolean doForcePullOnBuild)
            throws MachineException {

        // If force pull of base image is not disabled ensure that image build won't fail if needed layers are cached
        // but update of them fails due to network outage.
        // To do that do pull manually if needed and do not force docker to do pull itself.
        if (doForcePullOnBuild) {
            try {
                pullImage(dockerfile.getImages().get(0).getFrom(), creationLogsOutput);
            } catch (IOException | DockerFileException | InterruptedException ignored) {
            }
        }
        super.buildImage(dockerfile, creationLogsOutput, imageName, false);
    }

    private void pullImage(String image, final LineConsumer creationLogsOutput)
            throws DockerFileException, IOException, InterruptedException {

        DockerImageIdentifier imageIdentifier = DockerImageIdentifierParser.parse(image);
        final ProgressLineFormatterImpl progressLineFormatter = new ProgressLineFormatterImpl();
        docker.pull(imageIdentifier.getRepository(),
                imageIdentifier.getTag(),
                imageIdentifier.getRegistry(),
                currentProgressStatus -> {
                    try {
                        creationLogsOutput.writeLine(progressLineFormatter.format(currentProgressStatus));
                    } catch (IOException e) {
                        LOG.error(e.getLocalizedMessage(), e);
                    }
                });
    }
}
