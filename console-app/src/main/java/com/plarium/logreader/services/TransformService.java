package com.plarium.logreader.services;

import com.plarium.logreader.processing.Chain;
import com.plarium.logreader.processing.JsonValidationProcess;
import com.plarium.logreader.processing.SendingProcess;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingDeque;
import java.util.logging.Logger;

public class TransformService implements Runnable {

    private static final Logger logger = Logger.getLogger(TransformService.class.getName());

    private BlockingDeque<Path> newFilesQueue;

    private final String apiUrl;

    private int batchSize;

    public TransformService(BlockingDeque<Path> newFilesQueue, String apiUrl, int batchSize) {
        this.newFilesQueue = newFilesQueue;
        this.apiUrl = apiUrl;
        this.batchSize = batchSize;
    }

    /**
     * Main loop which pools new file events from queue and process them
     */
    @Override
    public void run() {
        while (true) {
            Path path = newFilesQueue.poll();
            if (path != null) {
                System.out.println("THREAD IS " + Thread.currentThread());
                try {
                    FileInputStream fileInputStream = new FileInputStream(path.toString());
                    Scanner scanner = new Scanner(fileInputStream);
                    int linesCounter = 0;
                    List<String> batch = new ArrayList<>();
                    boolean processResult = true;
                    while (scanner.hasNextLine()) {
                        if (linesCounter != 0 & linesCounter % batchSize == 0) {
                            processResult &= process(batch, path, apiUrl, batchSize, linesCounter);
                            batch.clear();
                        }
                        String line = scanner.nextLine();
                        batch.add(line);
                        System.out.println(line);
                        linesCounter++;
                    }
                    // process logs at the end of file if size is less than batchSize
                    processResult &= process(batch, path, apiUrl, batchSize, linesCounter);
                    // if any batch of file failed we put file back to queue for reprocessing, web service is idempotent
                    if (!processResult) {
                        newFilesQueue.put(path);
                        continue;
                    }
                    fileInputStream.close();
                    scanner.close();
                    removeTransformedFile(path);
                } catch (IOException e) {
                    logger.severe(String.format("error occurred while sending file: %s with message: %s", path.toString(), e.getMessage()));
                } catch (InterruptedException e) {
                    logger.severe(String.format("Exception occurred, thread was interrupted with message: %s", e.getMessage()));
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Removes file which've been processed
     *
     * @param path path of file to be deleted after processed
     */
    private void removeTransformedFile(Path path) {
        try {
            Files.delete(path);
            logger.info(String.format("File %s deleted successfully", path.toString()));
        } catch (IOException e) {
            logger.severe(String.format("Failed to delete the file: %s with error; %s", path.toString(), e.getMessage()));
        }
    }

    /**
     * Processing chain
     *
     * @param batch        Batch of log lines
     * @param path         path of file being processed
     * @param apiUrl       url of web service to be sent at
     * @param batchSize    batch size
     * @param linesCounter line number
     * @return result of processing a batch
     */
    private boolean process(List<String> batch, Path path, String apiUrl, int batchSize, int linesCounter) {
        try {
            return (Boolean) Chain.createStart(new JsonValidationProcess(path, linesCounter - batchSize))
                    .append(new SendingProcess(apiUrl))
                    .start(batch);
        } catch (Exception e) {
            return false;
        }

    }
}
