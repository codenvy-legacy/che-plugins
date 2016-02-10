package org.eclipse.che.plugin.docker.client.parser;

import org.eclipse.che.plugin.docker.client.DockerFileException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse image entry used in FROM instruction of Dockerfile
 *
 * @author Alexander Garagatyi
 */
public class DockerImageIdentifierParser {
    private static final String  BASE_DOCKER_PATTERN = "[a-z0-9]+(?:[._-][a-z0-9]+)*";
    private static final String  REGISTRY_PATTERN = "(?<registryHost>[^:/]+)(:(?<registryPort>[1-9][0-9]*))?";
    private static final String  REPOSITORY_PATTERN = "((?<userSpace>" + BASE_DOCKER_PATTERN + ")/)?" +
                                                      "(?<imageName>" + BASE_DOCKER_PATTERN + ")";
    private static final String  TAG_PATTERN = "(?<tag>:" + BASE_DOCKER_PATTERN + ")";
    private static final String  DIGEST_PATTERN = "(?<digest>@[a-z-0-9:]+)";
    private static final Pattern IMAGE_FORMAT = Pattern.compile(
            "(" + REGISTRY_PATTERN + ")?" + REPOSITORY_PATTERN + "(" + TAG_PATTERN + "|" + DIGEST_PATTERN + ")?");

    public static DockerImageIdentifier parse(String image) throws DockerFileException {
        if (image == null) {
            throw new IllegalArgumentException("Null argument value is forbidden");
        }
        DockerImageIdentifier.DockerImageIdentifierBuilder builder = DockerImageIdentifier.builder();

        Matcher matcher = IMAGE_FORMAT.matcher(image);
        if (!matcher.matches()) {
            throw new DockerFileException("Provided image reference is invalid");
        }

        builder.setImageName(matcher.group("imageName"))
               .setUserSpace(matcher.group("userSpace"))
               .setRegistryHost(matcher.group("registryHost"))
               .setTag(matcher.group("tag"))
               .setDigest(matcher.group("digest"));
        if (matcher.group("registryPort") != null) {
            builder.setRegistryPort(Integer.parseInt(matcher.group("registryPort")));
        }
        return builder.build();
    }
}
