package org.apache.http.client.fluent;

import java.util.List;

public interface LoadBlancer {
    RealServer select(List<RealServer> realRealServers);
}
