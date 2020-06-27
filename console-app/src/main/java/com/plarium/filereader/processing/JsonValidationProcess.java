package com.plarium.filereader.processing;

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

    private final int linesCounter;

    public JsonValidationProcess(Path path, int linesCounter) {
        this.path = path;
        this.linesCounter = linesCounter;
    }

    @Override
    public ArrayNode process(List<String> input) {
        ArrayNode validatedLines = mapper.createArrayNode();
        int rowNumber = 1;
        for (String line : input) {
            try {
                JsonNode jsonObjectLine = mapper.readTree(line);
                JsonNode objectType = jsonObjectLine.get(LOG_TYPE_STRING);
                if (objectType == null || objectType == NullNode.getInstance() || objectType.textValue().isEmpty()) {
                    logger.severe(String.format("Log object has no object type at file: %s at line: %d", path.toString(), linesCounter));
                    continue;
                }
                validatedLines.add(jsonObjectLine);
                rowNumber++;
            } catch (JsonProcessingException e) {
                logger.severe(String.format("Invalid json object in file: %s at line: %d with message: %s", path.toString(), linesCounter, e.getMessage()));
            }
        }
        return validatedLines;
    }
}
