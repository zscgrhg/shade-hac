package org.apache.http.client.fluent;

import org.apache.http.*;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

public class HacRequest extends Request {
    final static Logger LOGGER = HacLoggerBuddy.of(HacRequest.class);
    private final InternalHttpRequest internalHttpRequest;
    private final HacListenerChain chain = new HacListenerChain();
    private final HacExecutor hacExecutor;

    HacRequest(HacExecutor hacExecutor, InternalHttpRequest request) {
        super(request);
        this.hacExecutor = hacExecutor;
        this.internalHttpRequest = request;
    }

    public static HacRequest Get(HacExecutor hacExecutor, URI uri) {
        return new HacRequest(hacExecutor, new InternalHttpRequest("GET", uri));
    }

    public static HacRequest Get(HacExecutor hacExecutor, String uri) {
        return new HacRequest(hacExecutor, new InternalHttpRequest("GET", URI.create(uri)));
    }


    public static HacRequest Post(HacExecutor hacExecutor, URI uri) {
        return new HacRequest(hacExecutor, new InternalEntityEnclosingHttpRequest("POST", uri));
    }

    public static HacRequest Post(HacExecutor hacExecutor, String uri) {
        return new HacRequest(hacExecutor, new InternalEntityEnclosingHttpRequest("POST", URI.create(uri)));
    }

    public Future<HttpResponse> aexec() {
        LOGGER.debug(internalHttpRequest
                .getURI()
                .toString());
        Future<HttpResponse> execute = hacExecutor
                .execute(internalHttpRequest, chain);
        return execute;
    }

    public HacRequest addListener(FutureCallback<HttpResponse> listener) {
        if (listener instanceof HacSyncListener) {
            chain.addSync(listener);
        } else {
            chain.addAsync(listener);
        }
        return this;
    }


    @Override
    public HacRequest addHeader(Header header) {
        super.addHeader(header);
        return this;
    }

    @Override
    public HacRequest setHeader(Header header) {
        super.setHeader(header);
        return this;
    }

    @Override
    public HacRequest addHeader(String name, String value) {
        super.addHeader(name, value);
        return this;
    }

    @Override
    public HacRequest setHeader(String name, String value) {
        super.setHeader(name, value);
        return this;
    }

    @Override
    public HacRequest removeHeader(Header header) {
        super.removeHeader(header);
        return this;
    }

    @Override
    public HacRequest removeHeaders(String name) {
        super.removeHeaders(name);
        return this;
    }

    @Override
    public HacRequest setHeaders(Header... headers) {
        super.setHeaders(headers);
        return this;
    }

    @Override
    public HacRequest setCacheControl(String cacheControl) {
        super.setCacheControl(cacheControl);
        return this;
    }

    @Override
    public HacRequest setDate(Date date) {
        super.setDate(date);
        return this;
    }

    @Override
    public HacRequest setIfModifiedSince(Date date) {
        super.setIfModifiedSince(date);
        return this;
    }

    @Override
    public HacRequest setIfUnmodifiedSince(Date date) {
        super.setIfUnmodifiedSince(date);
        return this;
    }

    @Override
    public HacRequest config(String param, Object object) {
        super.config(param, object);
        return this;
    }

    @Override
    public HacRequest removeConfig(String param) {
        super.removeConfig(param);
        return this;
    }

    @Override
    public HacRequest version(HttpVersion version) {
        super.version(version);
        return this;
    }

    @Override
    public HacRequest elementCharset(String charset) {
        super.elementCharset(charset);
        return this;
    }

    @Override
    public HacRequest useExpectContinue() {
        super.useExpectContinue();
        return this;
    }

    @Override
    public HacRequest userAgent(String agent) {
        super.userAgent(agent);
        return this;
    }

    @Override
    public HacRequest socketTimeout(int timeout) {
        super.socketTimeout(timeout);
        return this;
    }

    @Override
    public HacRequest connectTimeout(int timeout) {
        super.connectTimeout(timeout);
        return this;
    }

    @Override
    public HacRequest staleConnectionCheck(boolean b) {
        super.staleConnectionCheck(b);
        return this;
    }

    @Override
    public HacRequest viaProxy(HttpHost proxy) {
        super.viaProxy(proxy);
        return this;
    }

    @Override
    public HacRequest viaProxy(String proxy) {
        super.viaProxy(proxy);
        return this;
    }

    @Override
    public HacRequest body(HttpEntity entity) {
        super.body(entity);
        return this;
    }

    @Override
    public HacRequest bodyForm(Iterable<? extends NameValuePair> formParams, Charset charset) {
        super.bodyForm(formParams, charset);
        return this;
    }

    @Override
    public HacRequest bodyForm(Iterable<? extends NameValuePair> formParams) {
        super.bodyForm(formParams);
        return this;
    }

    @Override
    public HacRequest bodyForm(NameValuePair... formParams) {
        super.bodyForm(formParams);
        return this;
    }

    @Override
    public HacRequest bodyString(String s, ContentType contentType) {
        super.bodyString(s, contentType);
        return this;
    }

    @Override
    public HacRequest bodyFile(File file, ContentType contentType) {
        super.bodyFile(file, contentType);
        return this;
    }

    @Override
    public HacRequest bodyByteArray(byte[] b) {
        super.bodyByteArray(b);
        return this;
    }

    @Override
    public HacRequest bodyByteArray(byte[] b, ContentType contentType) {
        super.bodyByteArray(b, contentType);
        return this;
    }

    @Override
    public HacRequest bodyByteArray(byte[] b, int off, int len) {
        super.bodyByteArray(b, off, len);
        return this;
    }

    @Override
    public HacRequest bodyByteArray(byte[] b, int off, int len, ContentType contentType) {
        super.bodyByteArray(b, off, len, contentType);
        return this;
    }

    @Override
    public HacRequest bodyStream(InputStream instream) {
        super.bodyStream(instream);
        return this;
    }

    @Override
    public HacRequest bodyStream(InputStream instream, ContentType contentType) {
        super.bodyStream(instream, contentType);
        return this;
    }

    private class HacListenerChain extends HacListener {
        private final List<FutureCallback<HttpResponse>> syncTasks = new ArrayList<FutureCallback<HttpResponse>>();
        private final List<FutureCallback<HttpResponse>> asyncTasks = new ArrayList<FutureCallback<HttpResponse>>();


        public void addSync(FutureCallback<HttpResponse> listener) {
            syncTasks.add(listener);
        }

        public void addAsync(FutureCallback<HttpResponse> listener) {
            asyncTasks.add(listener);
        }

        @Override
        public void completed(final HttpResponse result) {
            for (FutureCallback<HttpResponse> hcListener : syncTasks) {
                hcListener.completed(result);
            }
            for (final FutureCallback<HttpResponse> hcListener : asyncTasks) {
                hacExecutor.execute(new Runnable() {
                    public void run() {
                        hcListener.completed(result);
                    }
                });
            }

        }

        @Override
        public void failed(final Exception ex) {
            for (FutureCallback<HttpResponse> hcListener : syncTasks) {
                hcListener.failed(ex);
            }
            for (final FutureCallback<HttpResponse> hcListener : asyncTasks) {
                hacExecutor.execute(new Runnable() {
                    public void run() {
                        hcListener.failed(ex);
                    }
                });
            }
        }

        @Override
        public void cancelled() {
            for (FutureCallback<HttpResponse> hcListener : syncTasks) {
                hcListener.cancelled();
            }
            for (final FutureCallback<HttpResponse> hcListener : asyncTasks) {
                hacExecutor.execute(new Runnable() {
                    public void run() {
                        hcListener.cancelled();
                    }
                });
            }
        }

    }
}
