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

package org.eclipse.che.plugin.yeoman.builder;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates a zip by using nio/filesystem of JDK7
 * @author Florent Benoit
 */
public final class ZipHelper {

    /**
     * Zip the current directory in the given filename
     * @param outputFile the Output file (zip to build)
     * @param inputDirectory the directory to zip
     * @param timeAfter the time after which we should keep files (generated one)
     */
    public static void zip(final Path outputFile, final Path inputDirectory, final long timeAfter) throws IOException {
        // Create zip file

        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        // delete if it may exists
        Files.deleteIfExists(outputFile);

        // create file system
        URI zipURI = URI.create(String.format("jar:file:%s", outputFile.toString()));
        try (FileSystem zipfs = FileSystems.newFileSystem(zipURI, env)) {

            Files.walkFileTree(inputDirectory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    // Check for files
                    if (attrs.isRegularFile() && (attrs.creationTime().toMillis() > timeAfter)) {
                        // compute path to use inside the zip
                        Path entry = inputDirectory.relativize(file);
                        Path zipPath = zipfs.getPath(entry.toString());

                        // create folders
                        Path parent = zipPath.getParent();
                        if (parent != null) {
                            try {
                                Files.createDirectories(parent);
                            } catch (FileAlreadyExistsException ignore) {

                            }
                        }
                        Files.copy(file, zipPath);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

        }
    }


}
