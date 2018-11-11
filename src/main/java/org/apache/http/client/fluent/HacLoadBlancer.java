package org.apache.http.client.fluent;

import java.util.List;

public interface HacLoadBlancer {
    HacRealServer select(List<HacRealServer> realRealServers);
}
