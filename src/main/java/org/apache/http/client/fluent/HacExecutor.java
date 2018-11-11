package org.apache.http.client.fluent;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class HacExecutor {
    private final CloseableHttpAsyncClient asyncClient;
    private final ExecutorService executorService;


    public HacExecutor(CloseableHttpAsyncClient hac, ExecutorService service) {
        asyncClient = hac;
        executorService = service;
    }

    public Future<HttpResponse> execute(
            final HttpUriRequest request,
            final FutureCallback<HttpResponse> callback) {
        return asyncClient.execute(request, callback);
    }


    public void execute(Runnable command) {
        executorService.execute(command);
    }
}
