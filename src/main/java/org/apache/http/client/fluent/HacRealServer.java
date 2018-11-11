package org.apache.http.client.fluent;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicLong;

@EqualsAndHashCode(of = {"host", "port"})
@ToString
public class HacRealServer {
    public static final AtomicLong COUNTER = new AtomicLong(0);
    private final long mark = COUNTER.incrementAndGet();
    private final String schema;
    private final String host;
    private final int port;
    private final String ctxPath;
    private final String healthEndpoint;
    private final String address;
    private final boolean enableSSL;

    public HacRealServer(String host, int port, String ctxPath, String healthEndpoint, boolean enableSSL) {
        this.host = host;
        this.port = port;
        this.address = host + ":" + port;
        this.ctxPath = ctxPath;
        this.healthEndpoint = healthEndpoint;
        this.enableSSL = enableSSL;
        if (enableSSL) {
            this.schema = "https://";
        } else {
            this.schema = "http://";
        }
    }

    public String resolve(String path) {
        return schema + address + "/" + HacStringBuddy.trimBeginSlash(ctxPath + "/" + path);
    }

    public String getAddress() {
        return address;
    }

}
