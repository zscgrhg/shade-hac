package org.apache.http.client.fluent;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import java.util.concurrent.ExecutorService;

public class HacExecutor {
    private final CloseableHttpAsyncClient hac;
    private final ExecutorService EXECUTOR_SERVICE;


    public HacExecutor(CloseableHttpAsyncClient http_async_client, ExecutorService executor_service) {
        hac = http_async_client;
        EXECUTOR_SERVICE = executor_service;
    }

    public CloseableHttpAsyncClient getHac() {
        return hac;
    }


    public void execute(Runnable command) {
        EXECUTOR_SERVICE.execute(command);
    }
}
