package org.apache.http.client.fluent;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class HacVirtualServerKeeper {
    public static final Logger LOGGER = HacLoggerBuddy.of(HacVirtualServerKeeper.class);
    private static final Map<String, HacVirtualServer> SERVERS = new ConcurrentHashMap<String, HacVirtualServer>();
    private static final Set<HacCloseable> closeables = new HashSet<HacCloseable>();

    static {
        Runtime
                .getRuntime()
                .addShutdownHook(new Thread(new Runnable() {
                    public void run() {
                        for (HacCloseable closeable : closeables) {
                            tryClose(closeable);
                        }
                    }
                }));
    }

    public synchronized static HacVirtualServer create(final String name,
                                                       HacDiscoveryClient discoveryClient,
                                                       HacLoadBlancer loadBlancer,
                                                       HacExecutorCustom haec) {
        if (SERVERS.containsKey(name)) {
            return SERVERS.get(name);
        } else {
            final CloseableHttpAsyncClient hac = haec.createHac();
            final ExecutorService executorService = haec.createExecutorService();
            closeables.add(new HacCloseable() {
                public boolean isOpen() {
                    return hac.isRunning();
                }

                public void close() throws IOException {
                    LOGGER.debug("close hac of server[" + name + "]");
                    hac.close();
                }
            });
            closeables.add(new HacCloseable() {
                public boolean isOpen() {
                    return !executorService.isShutdown();
                }

                public void close() throws IOException {
                    LOGGER.debug("close executor of server[" + name + "]");
                    executorService.shutdown();
                }
            });
            HacVirtualServer created = new HacVirtualServer(name, discoveryClient,
                    loadBlancer,
                    new HacExecutor(hac, executorService));
            SERVERS.put(name, created);
            return created;
        }
    }

    public synchronized static HacVirtualServer create(String name, HacDiscoveryClient discoveryClient) {
        return create(name, discoveryClient,
                new HacSimpleLB(),
                new HacSharedExcutorCustom());
    }

    private static void tryClose(HacCloseable closeable) {
        if (closeable.isOpen()) {
            try {
                closeable.close();
            } catch (Exception e) {

            }
        }
    }


}
