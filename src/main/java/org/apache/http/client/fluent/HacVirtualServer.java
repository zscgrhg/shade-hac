package org.apache.http.client.fluent;

import org.slf4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HacVirtualServer {
    private final String name;
    private final HacDiscoveryClient discoveryClient;
    private final HacLoadBlancer loadBlancer;
    private final HacExecutor hacExecutor;

    private volatile List<HacRealServer> realRealServers = new ArrayList<HacRealServer>();

    HacVirtualServer(String name, HacDiscoveryClient discoveryClient, HacLoadBlancer loadBlancer, HacExecutor hacExecutor) {
        this.name = name;
        this.discoveryClient = discoveryClient;
        this.loadBlancer = loadBlancer;
        this.hacExecutor = hacExecutor;
    }

    public void initRealServers() {
        refreshRealServers();
    }

    public void refreshRealServers() {
        List<HacRealServer> realServers = discoveryClient.seek(name);
        if (realServers == null || realServers.isEmpty()) {
            return;
        }
        this.realRealServers = new ArrayList<HacRealServer>(realServers);
    }

    public void remove(HacRealServer realServer) {
        List<HacRealServer> copy = new ArrayList<HacRealServer>(realRealServers);
        copy.remove(realServer);
        this.realRealServers = copy;
    }


    public class ServerFailedListener extends HacListener implements HacSyncListener {
        final Logger LOGGER = HacLoggerBuddy.of(ServerFailedListener.class);
        private final HacRealServer watched;

        public ServerFailedListener(HacRealServer watched) {
            this.watched = watched;
        }

        @Override
        public void failed(Exception ex) {
            if (ex instanceof ConnectException
                    || ex instanceof UnknownHostException) {
                remove(watched);
                LOGGER.debug(" remove failed server: " + watched.getAddress());
            }
        }
    }

    public HcRequestBuilder PATH(String path) {
        return new HcRequestBuilder(path);
    }

    public class HcRequestBuilder {
        private final String path;
        private final Map<String, String> queryParams = new HashMap<String, String>();

        public HcRequestBuilder(String path) {
            this.path = path;
        }

        public HcRequestBuilder addParam(String name, String value) {
            queryParams.put(name, value);
            return this;
        }

        public HcRequestBuilder addParams(Map<String, String> pm) {
            queryParams.putAll(pm);
            return this;
        }

        private URI expand(HacRealServer realServer) {


            StringBuilder sb = new StringBuilder(realServer.resolve(path));
            char c = '?';
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                sb
                        .append(c)
                        .append(encode(entry.getKey()))
                        .append('=')
                        .append(encode(entry.getValue()));
                c = '&';
            }

            return URI
                    .create(sb.toString())
                    .normalize();
        }

        private String encode(String str) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        private HacRealServer select() {
            List<HacRealServer> copy = realRealServers;
            if (copy == null || copy.isEmpty()) {
                throw new RuntimeException("server of " + name + " is down");
            }
            HacRealServer realServer = loadBlancer.select(copy);
            return realServer;
        }

        public HacRequest Get() {
            HacRealServer select = select();
            return HacRequest
                    .Get(hacExecutor, expand(select))
                    .addListener(new ServerFailedListener(select));
        }

        public HacRequest Post() {
            HacRealServer select = select();
            return HacRequest
                    .Post(hacExecutor, expand(select))
                    .addListener(new ServerFailedListener(select));
        }
    }
}
