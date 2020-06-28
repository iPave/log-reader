package com.plarium.logreader.unit;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.plarium.logreader.processing.SendingProcess;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SendingProcessTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldSuccessfullySendBatchesToWebService() throws JsonProcessingException {
        stubFor(post(urlEqualTo("/save"))
                .withHeader("Accept", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("<response>Ok</response>")));
        SendingProcess sendingProcess = new SendingProcess("http://localhost:8089/save");
        ArrayNode nodeOfLogs = (ArrayNode) objectMapper.readTree(listOfValidLogs());
        boolean result = sendingProcess.process(nodeOfLogs);
        assertTrue(result);

        verify(postRequestedFor(urlMatching("/save"))
                .withRequestBody(containing(listOfValidLogs()))
                .withHeader("Content-Type", matching("application/json")));
    }

    @Test
    public void shouldFailSendingBatchesToWebService() throws JsonProcessingException {
        stubFor(post(urlEqualTo("/save"))
                .withHeader("Accept", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("<response>Server error</response>")));
        SendingProcess sendingProcess = new SendingProcess("http://localhost:8089/save");
        ArrayNode nodeOfLogs = (ArrayNode) objectMapper.readTree(listOfValidLogs());
        boolean result = sendingProcess.process(nodeOfLogs);
        assertFalse(result);

        verify(postRequestedFor(urlMatching("/save"))
                .withRequestBody(containing(listOfValidLogs()))
                .withHeader("Content-Type", matching("application/json")));
    }

    private static String listOfValidLogs() {
        return "[{\"type\":\"eventlog1\",\"message\":\"log message 1\"}," +
                "{\"type\":\"eventlog2\",\"message\":\"log message 2\"}," +
                "{\"type\":\"eventlog3\",\"message\":\"log message 3\"}]";
    }
}