package com.plarium.logreader.unit;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.plarium.logreader.processing.TypeGroupingProcess;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TypeGroupingProcessTest {

    @Test
    public void shouldGroupLogs() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = (ArrayNode) mapper.readTree(arrayOfLogs());
        TypeGroupingProcess typeGroupingProcess = new TypeGroupingProcess();
        Map<String, ArrayNode> groupedLogs = typeGroupingProcess.process(arrayNode);
        assertEquals(3, groupedLogs.size());
        assertEquals(2, groupedLogs.get("eventlog").size());
        assertEquals(2, groupedLogs.get("eventlog1").size());
        assertEquals(2, groupedLogs.get("eventlog2").size());
    }

    private String arrayOfLogs() {
        return "[{\"type\": \"eventlog\", \"message\": \"log message 1\"},\n" +
                "{\"type\": \"eventlog\", \"message\": \"log message 2\"},\n" +
                "{\"type\": \"eventlog1\", \"message\": \"log message 1\"},\n" +
                "{\"type\": \"eventlog1\", \"message\": \"log message 2\"},\n" +
                "{\"type\": \"eventlog2\", \"message\": \"log message 1\"},\n" +
                "{\"type\": \"eventlog2\", \"message\": \"log message 2\"}]\n";
    }
}
