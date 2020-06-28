package com.plarium.logreader.unit;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.plarium.logreader.processing.JsonValidationProcess;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class JsonValidationProcessTest {

    @Test
    public void shouldGroupLogs() throws JsonProcessingException {
        JsonValidationProcess jsonValidationProcess = new JsonValidationProcess(mock(Path.class), 1);
        ArrayNode validatedArrayNode = jsonValidationProcess.process(listOfValidLogs());
        assertEquals(3, validatedArrayNode.size());
        assertEquals("eventlog1", validatedArrayNode.get(0).get("type").asText());
        assertEquals("log message 1", validatedArrayNode.get(0).get("message").asText());
        assertEquals("eventlog2", validatedArrayNode.get(1).get("type").asText());
        assertEquals("log message 2", validatedArrayNode.get(1).get("message").asText());
        assertEquals("eventlog3", validatedArrayNode.get(2).get("type").asText());
        assertEquals("log message 3", validatedArrayNode.get(2).get("message").asText());
    }

    private static List<String> listOfValidLogs() {
        return Arrays.asList(
                "{\"type\": \"eventlog1\", \"message\": \"log message 1\"}",
                "{\"type\": \"eventlog2\", \"message\": \"log message 2\"}",
                "{\"type\": \"eventlog3\", \"message\": \"log message 3\"}"
        );
    }

    @Test
    public void shouldFilterLogs() {
        JsonValidationProcess jsonValidationProcess = new JsonValidationProcess(mock(Path.class), 1);
        ArrayNode validatedArrayNode = jsonValidationProcess.process(listOfLogsWithoutType());
        assertEquals(2, validatedArrayNode.size());
        assertEquals("eventlog1", validatedArrayNode.get(0).get("type").asText());
        assertEquals("log message 1", validatedArrayNode.get(0).get("message").asText());
        assertEquals("eventlog5", validatedArrayNode.get(1).get("type").asText());
        assertEquals("log message 5", validatedArrayNode.get(1).get("message").asText());
    }

    private static List<String> listOfLogsWithoutType() {
        return Arrays.asList(
                "{\"type\": \"eventlog1\", \"message\": \"log message 1\"}",
                "{\"type\": null, \"message\": \"log message 2\"}",
                "{\"type\": \"\", \"message\": \"log message 3\"}",
                "{ \"message\": \"log message 4\"}",
                "{\"type\": \"eventlog5\", \"message\": \"log message 5\"}"
        );
    }

}