package com.plarium.logreader.services;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.BlockingDeque;
import java.util.logging.Logger;

public class FileWatcherService implements Runnable {

    private static final Logger logger = Logger.getLogger(TransformService.class.getName());

    private Path path;

    private BlockingDeque<Path> newFilesQueue;

    public FileWatcherService(String path, BlockingDeque<Path> newFilesQueue) {
        this.path = Paths.get(path);
        this.newFilesQueue = newFilesQueue;
    }

    /**
     * Watcher service puts events about created files to the queue
     */
    @Override
    public void run() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    logger.info(String.format("Event of kind: %s, File affected: %s", event.kind(), event.context()));
                    newFilesQueue.put(path.resolve((Path) event.context()));
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
        }
    }
}
