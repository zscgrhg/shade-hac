package org.apache.http.client.fluent;

import java.util.List;

public interface DiscoveryClient {
    List<RealServer> seek(String name);
}
