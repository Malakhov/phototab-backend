package com.tabs.phototab.util.http;

import sun.net.www.http.HttpClient;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;

public class HttpTimeoutURLConnection extends sun.net.www.protocol.http.HttpURLConnection {

    public HttpTimeoutURLConnection(URL u, HttpTimeoutHandler handler, int iSoTimeout)
            throws IOException {
        super(u, handler);
        HttpTimeoutClient.setSoTimeout(iSoTimeout);
    }

    public void connect() throws IOException {
        if (connected) {
            return;
        }

        try {
            if ("http".equals(url.getProtocol())) // && !failedOnce <-PRIVATE
            {
// for safety's sake, as reported by KLGroup
                synchronized (url) {
                    http = HttpTimeoutClient.getNew(url);
                }
            } else {
                if (handler instanceof HttpTimeoutHandler) {
                    http = new HttpTimeoutClient(super.url, ((HttpTimeoutHandler) handler).getProxy(), ((HttpTimeoutHandler) handler).getProxyPort());
                } else {
                    throw new IOException("HttpTimeoutHandler expected");
                }
            }

            ps = (PrintStream) http.getOutputStream();
        } catch (IOException e) {
            throw e;
        }

        connected = true;
    }

    protected HttpClient getNewClient(URL url)
            throws IOException {
        HttpTimeoutClient httpTimeoutClient = new HttpTimeoutClient(url, (String) null, -1);
        return httpTimeoutClient;
    }

    protected HttpClient getProxiedClient(URL url, String s, int i)
            throws IOException {
        HttpTimeoutClient httpTimeoutClient = new HttpTimeoutClient(url, s, i);
        return httpTimeoutClient;
    }
}
