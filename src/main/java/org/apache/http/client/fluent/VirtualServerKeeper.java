package org.apache.http.client.fluent;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class VirtualServerKeeper {
    public static final Logger LOGGER = LoggerBuddy.of(VirtualServerKeeper.class);
    private static final Map<String, VirtualServer> SERVERS = new ConcurrentHashMap<String, VirtualServer>();
    private static final Set<CloseableObject> closeables = new HashSet<CloseableObject>();

    public synchronized static VirtualServer create(final String name,
                                                    DiscoveryClient discoveryClient,
                                                    LoadBlancer loadBlancer,
                                                    HacExecutorCustom haec) {
        if (SERVERS.containsKey(name)) {
            return SERVERS.get(name);
        } else {
            final CloseableHttpAsyncClient hac = haec.createHac();
            final ExecutorService executorService = haec.createExecutorService();
            closeables.add(new CloseableObject() {
                public boolean isOpen() {
                    return hac.isRunning();
                }

                public void close() throws IOException {
                    LOGGER.debug("close hac of " + name);
                    hac.close();
                }
            });
            closeables.add(new CloseableObject() {
                public boolean isOpen() {
                    return !executorService.isShutdown();
                }

                public void close() throws IOException {
                    LOGGER.debug("close executor of " + name);
                    executorService.shutdown();
                }
            });
            VirtualServer created = new VirtualServer(name, discoveryClient,
                    loadBlancer,
                    new HacExecutor(hac, executorService));
            SERVERS.put(name, created);
            return created;
        }
    }

    public synchronized static VirtualServer create(String name, DiscoveryClient discoveryClient) {
        return create(name, discoveryClient,
                new SimpleLB(),
                new SharedHacCustom());
    }

    private static void tryClose(CloseableObject closeable) {
        if (closeable.isOpen()) {
            try {
                closeable.close();
            } catch (Exception e) {

            }
        }
    }

    static {
        Runtime
                .getRuntime()
                .addShutdownHook(new Thread(new Runnable() {
                    public void run() {
                        for (CloseableObject closeable : closeables) {
                            tryClose(closeable);
                        }
                    }
                }));
    }


}
