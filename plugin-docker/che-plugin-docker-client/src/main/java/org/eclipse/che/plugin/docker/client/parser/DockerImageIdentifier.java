package org.eclipse.che.plugin.docker.client.parser;

/**
 * Describes specific docker image.
 * <p>
 * Includes:
 * <ul>
 *     <li>registry:</li>
 *     <ul>
 *         <li>registry host</li>
 *         <li>registry port</li>
 *     </ul>
 *     <li>repository:</li>
 *     <ul>
 *         <li>user space</li>
 *         <li>image name</li>
 *     </ul>
 *     <li>tag</li>
 *     <li>digest</li>
 * </ul>
 * Example:
 * <br>garagatyi/my_image
 * <br>ubuntu
 * <br>ubuntu:14.04
 * <br>my_private_registry:15800/my_image1:latest
 * <br>my_private_registry:15800/my_image1@sha256:6b019df8c73bb42e606225ef935760b9c428521eba4ad2519ef3ff4cdb3dbd69
 *
 * @author Alexander Garaagtyi
 */
public class DockerImageIdentifier {
    private final String  registryHost;
    private final Integer registryPort;
    private final String  userSpace;
    private final String  imageName;
    private final String  tag;
    private final String  digest;

    public DockerImageIdentifier(String registryHost,
                                 Integer registryPort,
                                 String userSpace,
                                 String imageName,
                                 String tag,
                                 String digest) {
        this.registryHost = registryHost;
        this.registryPort = registryPort;
        this.userSpace = userSpace;
        this.imageName = imageName;
        this.tag = tag;
        this.digest = digest;
    }

    public static DockerImageIdentifierBuilder builder() {
        return new DockerImageIdentifierBuilder();
    }

    public String getRegistryHost() {
        return registryHost;
    }

    public int getRegistryPort() {
        return registryPort;
    }

    public String getRegistry() {
        if (registryHost == null && registryPort == null) {
            return null;
        }
        return registryHost + ':' + registryPort;
    }

    public String getUserSpace() {
        return userSpace;
    }

    public String getImageName() {
        return imageName;
    }

    public String getRepository() {
        return userSpace != null ? userSpace + '/' + imageName : imageName;
    }

    public String getTag() {
        return tag;
    }

    public String getDigest() {
        return digest;
    }

    public static class DockerImageIdentifierBuilder {
        private String  registryHost;
        private Integer registryPort;
        private String  userSpace;
        private String  imageName;
        private String  tag;
        private String  digest;

        public DockerImageIdentifier build() {
            return new DockerImageIdentifier(registryHost, registryPort, userSpace, imageName, tag, digest);
        }

        public DockerImageIdentifierBuilder setRegistryHost(String registryHost) {
            this.registryHost = registryHost;
            return this;
        }

        public DockerImageIdentifierBuilder setRegistryPort(Integer registryPort) {
            this.registryPort = registryPort;
            return this;
        }

        public DockerImageIdentifierBuilder setUserSpace(String userSpace) {
            this.userSpace = userSpace;
            return this;
        }

        public DockerImageIdentifierBuilder setImageName(String imageName) {
            this.imageName = imageName;
            return this;
        }

        public DockerImageIdentifierBuilder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public DockerImageIdentifierBuilder setDigest(String digest) {
            this.digest = digest;
            return this;
        }

        private DockerImageIdentifierBuilder() {}
    }
}
