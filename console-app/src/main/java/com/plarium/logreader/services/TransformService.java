package com.plarium.logreader.services;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.plarium.logreader.processing.JsonValidationProcess;
import com.plarium.logreader.processing.SendingProcess;
import com.plarium.logreader.processing.TypeGroupingProcess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
                try (FileInputStream fileInputStream = new FileInputStream(path.toString()); Scanner scanner = new Scanner(fileInputStream)) {
                    int linesCounter = 0;
                    List<String> batch = new ArrayList<>();
                    boolean processResult = true;
                    while (scanner.hasNextLine()) {
                        if (linesCounter % batchSize == 0) {
                            processResult &= process(batch, path, apiUrl, linesCounter);
                            batch.clear();
                        }
                        String line = scanner.nextLine();
                        batch.add(line);
                        System.out.println(line);
                        linesCounter++;
                    }
                    // process logs at the end of file if size is less than batchSize
                    processResult &= process(batch, path, apiUrl, linesCounter);
                    // if any batch of file failed we put file back to queue for reprocessing, web service is idempotent
                    if (!processResult) {
                        newFilesQueue.put(path);
                        continue;
                    }
                    removeTransformedFile(path);
                } catch (IOException | InterruptedException e) {
                    logger.severe(String.format("error occurred while sending file: %s with message: %s", path.toString(), e.getMessage()));
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
        File file = new File(path.toUri());
        if (file.delete()) {
            logger.info(String.format("File %s deleted successfully", path.toString()));
        } else {
            logger.severe(String.format("Failed to delete the file: %s", path.toString()));
        }
    }

    /**
     * Processing chain
     *
     * @param batch        Batch of log lines
     * @param path         path of file being processed
     * @param apiUrl       url of web service to be sent at
     * @param linesCounter line number
     * @return result of processing a batch
     */
    private boolean process(List<String> batch, Path path, String apiUrl, int linesCounter) {
        JsonValidationProcess jsonValidationProcess = new JsonValidationProcess(path, linesCounter);
        TypeGroupingProcess typeGroupingProcess = new TypeGroupingProcess();
        SendingProcess sendingProcess = new SendingProcess(apiUrl);
        ArrayNode validatedLines = jsonValidationProcess.process(batch);
        Map<String, ArrayNode> groupedLines = typeGroupingProcess.process(validatedLines);
        return sendingProcess.process(groupedLines);
    }
}
