package io.github.orionlibs.orion_local_mock_server.utils;

import java.io.IOException;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * It represents an API endpoint. It could be a Spring MVC Controller class that has controller methods.
 * This class is used by the test class of this library as a mock API endpoint
 */
public class MockAPI
{
    public static String testEndpoint(HttpUrl apiUrl) throws IOException
    {
        return testEndpoint(apiUrl, null);
    }


    public static String testEndpoint(HttpUrl apiUrl, Headers headers) throws IOException
    {
        OkHttpClient client = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(apiUrl);
        if(headers != null)
        {
            requestBuilder.headers(headers);
        }
        Request request = requestBuilder.build();
        try(Response response = client.newCall(request).execute())
        {
            return response.body().string();
        }
    }
}
