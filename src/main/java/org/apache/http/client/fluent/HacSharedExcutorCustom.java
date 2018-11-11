package org.apache.http.client.fluent;

import org.apache.http.Consts;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.ManagedNHttpClientConnectionFactory;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.client.util.HttpAsyncClientUtils;
import org.apache.http.nio.conn.ManagedNHttpClientConnection;
import org.apache.http.nio.conn.NHttpConnectionFactory;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;

import java.nio.charset.CodingErrorAction;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HacSharedExcutorCustom implements HacExecutorCustom {
    public static volatile CloseableHttpAsyncClient hac;
    public static volatile ExecutorService service;

    public CloseableHttpAsyncClient createHac() {
        return custom();
    }

    public ExecutorService createExecutorService() {
        return simpleExecutor();
    }

    private synchronized static ExecutorService simpleExecutor() {
        if (service != null) {
            return service;
        }
        service = Executors.newFixedThreadPool(8);
        return service;
    }

    private synchronized static CloseableHttpAsyncClient custom() {
        if (hac != null) {
            return hac;
        }
        NHttpConnectionFactory<ManagedNHttpClientConnection> connFactory = new ManagedNHttpClientConnectionFactory();


        ConnectingIOReactor ioReactor = ioReactor();


        // Create a connection manager with custom configuration.
        PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(
                ioReactor, connFactory);


        // Create connection configuration
        ConnectionConfig connectionConfig = ConnectionConfig
                .custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(Consts.UTF_8)
                .build();
        // Configure the connection manager to use connection configuration either
        // by default or for a specific host.
        connManager.setDefaultConnectionConfig(connectionConfig);

        // Configure total max or per route limits for persistent connections
        // that can be kept in the pool or leased by the connection manager.
        connManager.setMaxTotal(100);
        connManager.setDefaultMaxPerRoute(10);

        // Use custom cookie store if necessary.

        // Create global request configuration
        RequestConfig defaultRequestConfig = RequestConfig
                .custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .setExpectContinueEnabled(true)
                .build();

        // Create an HttpClient with the given custom dependencies and configuration.
        hac = HttpAsyncClients
                .custom()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
        hac.start();
        return hac;
    }

    private static ConnectingIOReactor ioReactor() {
        IOReactorConfig ioReactorConfig = IOReactorConfig
                .custom()
                .setIoThreadCount(Runtime
                        .getRuntime()
                        .availableProcessors())
                .setConnectTimeout(6000)
                .setSoTimeout(60000)
                .build();
        try {
            return new DefaultConnectingIOReactor(ioReactorConfig);
        } catch (IOReactorException e) {
            throw new RuntimeException(e);
        }
    }
}
