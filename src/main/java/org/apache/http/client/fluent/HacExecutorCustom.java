package org.apache.http.client.fluent;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import java.util.concurrent.ExecutorService;

public interface HacExecutorCustom {

    CloseableHttpAsyncClient createHac();

    ExecutorService createExecutorService();
}
