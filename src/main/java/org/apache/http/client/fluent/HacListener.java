package org.apache.http.client.fluent;

import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;

public class HacListener implements FutureCallback<HttpResponse> {
    public void completed(HttpResponse response) {

    }

    public void failed(Exception ex) {

    }

    public void cancelled() {

    }
}
