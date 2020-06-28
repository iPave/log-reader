package com.plarium.logreader.services;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingDeque;
import java.util.logging.Logger;

public class InitialFilesParsingService {

    private static final Logger logger = Logger.getLogger(InitialFilesParsingService.class.getName());

    private Path path;

    private BlockingDeque<Path> newFilesQueue;

    public InitialFilesParsingService(String path, BlockingDeque<Path> newFilesQueue) {
        this.path = Paths.get(path);
        this.newFilesQueue = newFilesQueue;
    }

    /**
     * Initial parsing service puts all previously existing files to the queue
     */
    public void parse() {
        try {
            searchForFiles(path.toFile(), newFilesQueue);
        } catch (InterruptedException e) {
            logger.severe(String.format("Exception occurred while parsing directory: %s", e.getMessage()));
        }
    }

    /**
     * Recursive function for searching all existing files
     *
     * @param folder folder for search
     */
    private void searchForFiles(final File folder, BlockingDeque<Path> newFilesQueue) throws InterruptedException {
        final File[] files = folder.listFiles();
        if (files != null) {
            for (final File fileEntry : files) {
                if (fileEntry.isDirectory()) {
                    searchForFiles(fileEntry, newFilesQueue);
                } else {
                    newFilesQueue.put(fileEntry.toPath());
                }
            }
        }
    }
}
