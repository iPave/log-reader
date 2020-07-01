package com.plarium.logreader.processing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;

import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class JsonValidationProcess implements Process<List<String>, ArrayNode> {

    private static final Logger logger = Logger.getLogger(JsonValidationProcess.class.getName());

    private final ObjectMapper mapper = new ObjectMapper();

    private static final String LOG_TYPE_STRING = "type";

    private final Path path;

    private final int startLinePosition;

    public JsonValidationProcess(Path path, int startLinePosition) {
        this.path = path;
        this.startLinePosition = startLinePosition;
    }

    @Override
    public ArrayNode process(List<String> input) {
        ArrayNode validatedLines = mapper.createArrayNode();
        int currentLinePosition = startLinePosition - 1;
        for (String line : input) {
            currentLinePosition++;
            try {
                if (line.isEmpty()) continue;
                JsonNode jsonObjectLine = mapper.readTree(line);
                JsonNode objectType = jsonObjectLine.get(LOG_TYPE_STRING);
                if (objectType == null || objectType == NullNode.getInstance() || objectType.textValue().isEmpty()) {
                    logger.severe(String.format("Log object has no object type at file: %s at line: %d", path.toString(), currentLinePosition));
                    continue;
                }
                validatedLines.add(jsonObjectLine);
            } catch (JsonProcessingException e) {
                logger.severe(String.format("Invalid json object in file: %s at line: %d with message: %s", path.toString(), currentLinePosition, e.getMessage()));
            }
        }
        return validatedLines;
    }
}
