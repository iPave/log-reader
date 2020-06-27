package com.plarium.filereader.processing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.HashMap;
import java.util.Map;

public class TypeGroupingProcess implements Process<ArrayNode, Map<String, ArrayNode>> {

    private final ObjectMapper mapper = new ObjectMapper();

    private static final String LOG_TYPE_STRING = "type";

    @Override
    public Map<String, ArrayNode> process(ArrayNode input) {
        Map<String, ArrayNode> logGroupedByType = new HashMap<>();
        for (JsonNode log : input) {
            String logType = log.get(LOG_TYPE_STRING).textValue();
            ArrayNode groupedList = logGroupedByType.get(logType);
            if (groupedList == null) {
                groupedList = mapper.createArrayNode();
            }
            groupedList.add(log);
            logGroupedByType.put(logType, groupedList);
        }
        return logGroupedByType;
    }
}
