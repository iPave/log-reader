package com.plarium.logreader;


import com.plarium.logreader.services.FileWatcherService;
import com.plarium.logreader.services.TransformService;
import com.plarium.logreader.utils.EnvUtils;

import javax.naming.ConfigurationException;
import java.nio.file.Path;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class FileReaderConsoleApplication {

    public static void main(String[] args) throws ConfigurationException {
        String path = EnvUtils.getRequiredEnvString(System.getenv(), "PATH_DIRECTORY");
        String apiUrl = EnvUtils.getRequiredEnvString(System.getenv(), "API_URI");
        int batchSize = EnvUtils.getEnvInt(System.getenv(), "BATCH_SIZE", 100);
        int nThreads = EnvUtils.getEnvInt(System.getenv(), "N_THREADS", 10);
        BlockingDeque<Path> newFilesQueue = new LinkedBlockingDeque<>();
        FileWatcherService fileWatcherService = new FileWatcherService(path, newFilesQueue);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(fileWatcherService);
        ExecutorService transformExecutorService = Executors.newFixedThreadPool(nThreads);
        for (int i = 0; i < nThreads; i++) {
            transformExecutorService.execute(new TransformService(newFilesQueue, apiUrl, batchSize));
        }
        //todo initial process to send already existing files to queue
    }
}
