package com.plarium.logreader.dao;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Repository;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

@Repository
public class StorageLogDaoImpl implements LogDao {

    private static final Logger logger = Logger.getLogger(StorageLogDaoImpl.class.getName());

    private static final String DATA_DIR = "/{0}/{1}/";

    private static final String FILE_PATH = "/{0}/{1}/{2}";

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void save(String logType, ArrayNode logs) throws IOException {
        //not the best idea to save to root home directory, maybe its better to create special user for the execution of service
        String userDir = System.getProperty("user.home");
        String idempotencyKey = DigestUtils.sha1Hex(logType + logs.toString());
        String logDir = userDir + DATA_DIR;
        String filepath = userDir + FILE_PATH;
        String fileDirName = MessageFormat.format(logDir, logType, simpleDateFormat.format(new Date()));
        String fileName = MessageFormat.format(filepath, logType, simpleDateFormat.format(new Date()), idempotencyKey);
        Path directoryPath = Paths.get(fileDirName);
        Path logFilePath = Paths.get(fileName);
        if (Files.exists(logFilePath)) {
            return;
        }
        if (Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }
        Files.createFile(logFilePath);
        FileWriter out = new FileWriter(logFilePath.toString());
        for (int i = 0; i < logs.size(); i++) {
            out.append(logs.get(i).toString()).append(System.lineSeparator());
        }
        out.close();
        logger.severe(String.format("Batch Log saved to file : %s ", fileName));

    }
}
