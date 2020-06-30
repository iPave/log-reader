package com.plarium.logreader.services;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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
            registerRecursive(path, watchService);
            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    logger.info(String.format("Event of kind: %s, File affected: %s", event.kind(), event.context()));
                    newFilesQueue.put(path.resolve((Path) event.context()));
                }
                key.reset();
            }
        } catch (IOException e) {
            logger.severe(String.format("Exception occurred while watching directory: %s", e.getMessage()));
        } catch (InterruptedException e) {
            logger.severe(String.format("Exception occurred, thread was interrupted with message: %s", e.getMessage()));
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Registers all subfolders of root for watch service
     *
     * @param root         the root directory path
     * @param watchService WatchService
     * @throws IOException if IO problems occur
     */
    private void registerRecursive(final Path root, WatchService watchService) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
