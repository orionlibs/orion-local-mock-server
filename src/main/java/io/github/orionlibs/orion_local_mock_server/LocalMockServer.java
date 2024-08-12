package io.github.orionlibs.orion_local_mock_server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

/** Local test mock server for unit tests. */
public class LocalMockServer
{
    private final MockWebServer server;
    private final MockResponse response;
    private HttpUrl baseURL;
    private RecordedRequest request;
    private List<NameValuePair> params;
    private Headers requestHeaders;
    private List<NameValuePair> requestHeadersPairs;


    public LocalMockServer(String responseBody) throws IOException
    {
        this.server = new MockWebServer();
        this.response = new MockResponse();
        response.setHeader("Content-Type", "application/json");
        response.setBody(responseBody);
    }


    public LocalMockServer(String baseURL, int port, String responseBody) throws IOException
    {
        this(responseBody);
        prepareServer(baseURL, port);
    }


    private void prepareServer(String baseURL, int port) throws IOException
    {
        server.enqueue(response);
        server.start(port);
        this.baseURL = server.url(baseURL);
    }


    /**
     * Call this after your test is supposed to have already simulated the API endpoint call.
     * You can call this method, for example, in the @BeforeEach method of your JUnit 5 test class.
     * @throws ServerShutdownException
     */
    public void shutdown() throws ServerShutdownException
    {
        try
        {
            server.shutdown();
        }
        catch(IOException e)
        {
            throw new ServerShutdownException(e);
        }
    }


    private List<NameValuePair> parseQueryParamsFromQueryString(String queryString) throws URISyntaxException
    {
        String[] tokens = queryString.split("\\s", -1);
        String url = tokens[1];
        return URLEncodedUtils.parse(new URI(url), Charset.forName("UTF-8"));
    }


    private void extractRequest() throws InterruptedException
    {
        if(this.request == null)
        {
            this.request = server.takeRequest();
        }
    }


    private List<NameValuePair> extractRequestParams() throws InterruptedException, URISyntaxException
    {
        extractRequest();
        return parseQueryParamsFromQueryString(request.getRequestLine());
    }


    /**
     * Asserts if the given request parameter has the expected value.
     * @param paramName
     * @param expectedParamValue
     * @throws URISyntaxException
     * @throws InterruptedException
     */
    public void assertParamValue(String paramName, String expectedParamValue) throws URISyntaxException, InterruptedException
    {
        if(params == null)
        {
            params = extractRequestParams();
        }
        boolean paramFound = false;
        for(NameValuePair pair : params)
        {
            if(pair.getName().equals(paramName))
            {
                paramFound = true;
                assertEquals(expectedParamValue, pair.getValue());
            }
        }
        assertTrue(paramFound);
    }


    private void extractRequestHeaders() throws InterruptedException
    {
        extractRequest();
        if(this.requestHeaders == null)
        {
            this.requestHeaders = request.getHeaders();
            ;
        }
    }


    private List<NameValuePair> extractRequestHeadersPairs() throws InterruptedException
    {
        extractRequestHeaders();
        List<NameValuePair> list = new ArrayList<>();
        requestHeaders.forEach(h -> list.add(new BasicNameValuePair(h.getFirst(), h.getSecond())));
        return list;
    }


    /**
     * Asserts if the given request header has the expected value.
     * @param headerName
     * @param expectedHeaderValue
     * @throws URISyntaxException
     * @throws InterruptedException
     */
    public void assertHeaderValue(String headerName, String expectedHeaderValue) throws URISyntaxException, InterruptedException
    {
        if(requestHeadersPairs == null)
        {
            requestHeadersPairs = extractRequestHeadersPairs();
        }
        boolean headerFound = false;
        for(NameValuePair pair : requestHeadersPairs)
        {
            if(pair.getName().equals(headerName))
            {
                headerFound = true;
                assertEquals(expectedHeaderValue, pair.getValue());
            }
        }
        assertTrue(headerFound);
    }


    /**
     * Returns the URI part of the URL. For example if the server processes
     * http://127.0.0.1:8080/hello?param1=value1 then this method will return /hello
     * @return
     * @throws InterruptedException
     */
    public String getURI() throws InterruptedException
    {
        extractRequest();
        return request.getPath().split("\\?", -1)[0];
    }


    public HttpUrl getBaseURL()
    {
        return baseURL;
    }
}
