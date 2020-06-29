package com.plarium.logreader.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.plarium.logreader.dao.LogDao;
import com.plarium.logreader.dao.StorageLogDaoImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertEquals;

public class StorageLogDaoTest {

    private String fileName;

    private String logType;

    private ArrayNode arrayNode;

    @BeforeEach
    public void init() throws JsonProcessingException {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final String FILE_PATH = "/{0}/{1}/{2}";
        String userDir = System.getProperty("user.home");
        this.logType = "eventlog";
        String filepath = userDir + FILE_PATH;
        ObjectMapper mapper = new ObjectMapper();
        this.arrayNode = (ArrayNode) mapper.readTree(arrayOfLogs());
        String idempotencyKey = DigestUtils.sha1Hex(logType + arrayNode.toString());
        this.fileName = MessageFormat.format(filepath, logType, simpleDateFormat.format(new Date()), idempotencyKey);
    }

    @Test
    public void shouldSaveLog() throws IOException {
        LogDao logDao = new StorageLogDaoImpl();
        logDao.save(logType, arrayNode);
        File file = new File(fileName);
        assertTrue(file.exists());
        List<String> contents = Files.readAllLines(file.toPath());
        for (int i = 0; i < contents.size(); i++) {
            assertTrue(contents.get(i).equals(arrayNode.get(i).toString()));
        }
        file.delete();
    }


    @Test
    public void shouldMakeIdempotentSaveOperation() throws IOException {
        LogDao logDao = new StorageLogDaoImpl();
        logDao.save(logType, arrayNode);
        logDao.save(logType, arrayNode);
        File file = new File(fileName);
        assertTrue(file.exists());
        String directoryPath = file.getParent();
        File directory = new File(directoryPath);
        final File[] files = directory.listFiles();
        assertNotNull(files);
        assertEquals(1, files.length);
        List<String> contents = Files.readAllLines(file.toPath());
        for (int i = 0; i < contents.size(); i++) {
            assertTrue(contents.get(i).equals(arrayNode.get(i).toString()));
        }
        file.delete();
    }

    private String arrayOfLogs() {
        return "[{\"type\": \"eventlog\", \"message\": \"log message 1\"},\n" +
                "{\"type\": \"eventlog\", \"message\": \"log message 2\"},\n" +
                "{\"type\": \"eventlog\", \"message\": \"log message 1\"},\n" +
                "{\"type\": \"eventlog\", \"message\": \"log message 2\"},\n" +
                "{\"type\": \"eventlog\", \"message\": \"log message 1\"},\n" +
                "{\"type\": \"eventlog\", \"message\": \"log message 2\"}]\n";
    }
}
