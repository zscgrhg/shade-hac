package org.apache.http.client.fluent;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class HacExecutor {
    private final CloseableHttpAsyncClient hac;
    private final ExecutorService EXECUTOR_SERVICE;


    public HacExecutor(CloseableHttpAsyncClient http_async_client, ExecutorService executor_service) {
        hac = http_async_client;
        EXECUTOR_SERVICE = executor_service;
    }

    public Future<HttpResponse> execute(
            final HttpUriRequest request,
            final FutureCallback<HttpResponse> callback) {
        return hac.execute(request, callback);
    }


    public void execute(Runnable command) {
        EXECUTOR_SERVICE.execute(command);
    }
}
