package org.apache.http.client.fluent;

import java.util.List;

public interface HacDiscoveryClient {
    List<HacRealServer> seek(String name);
}
