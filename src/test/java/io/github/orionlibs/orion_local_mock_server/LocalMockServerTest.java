package io.github.orionlibs.orion_local_mock_server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.orionlibs.orion_local_mock_server.utils.MockAPI;
import okhttp3.Headers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_METHOD)
public class LocalMockServerTest
{
    private LocalMockServer server;


    @AfterEach
    public void teardown() throws ServerShutdownException
    {
        server.shutdown();
    }


    @Test
    void test_LocalMockServer() throws Exception
    {
        String url1ToCall = "http://127.0.0.1:8081";
        int port1 = 8081;
        String responseBody = "{\"status\" : \"OK\"}";
        server = new LocalMockServer(url1ToCall, port1, responseBody);
        assertEquals(responseBody, MockAPI.testEndpoint(server.getBaseURL()));
    }


    @Test
    void test_LocalMockServer_queryParams() throws Exception
    {
        String url1ToCall = "http://127.0.0.1:8081?param1=value1&param2=value2";
        int port1 = 8081;
        String responseBody = "{\"status\" : \"OK\"}";
        server = new LocalMockServer(url1ToCall, port1, responseBody);
        assertEquals(responseBody, MockAPI.testEndpoint(server.getBaseURL()));
        server.assertParamValue("param1", "value1");
        server.assertParamValue("param2", "value2");
        assertEquals("/", server.getURI());
    }


    @Test
    void test_LocalMockServer_requestHeaders() throws Exception
    {
        String url1ToCall = "http://127.0.0.1:8081";
        int port1 = 8081;
        String responseContentType = "application/json";
        String responseBody = "{\"status\" : \"OK\"}";
        server = new LocalMockServer(url1ToCall, port1, responseBody);
        Headers headers = new Headers.Builder()
                        .add("Content-Type", responseContentType)
                        .add("header1", "headerValue1")
                        .add("header2", "headerValue2")
                        .build();
        assertEquals(responseBody, MockAPI.testEndpoint(server.getBaseURL(), headers));
        server.assertHeaderValue("Content-Type", responseContentType);
        server.assertHeaderValue("header1", "headerValue1");
        server.assertHeaderValue("header2", "headerValue2");
        assertEquals("/", server.getURI());
    }
}
