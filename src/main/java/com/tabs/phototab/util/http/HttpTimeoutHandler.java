package com.tabs.phototab.util.http;

import java.io.IOException;
import java.net.URL;

public class HttpTimeoutHandler extends sun.net.www.protocol.http.Handler {

    private int iSoTimeout = 0;

    public HttpTimeoutHandler(int iSoTimeout) {
        if (iSoTimeout % 2 != 0) {
            iSoTimeout++;
        }
        this.iSoTimeout = (iSoTimeout / 2);
    }

    protected java.net.URLConnection openConnection(URL u) throws
            IOException {
        return new HttpTimeoutURLConnection(u, this, iSoTimeout);
    }

    protected String getProxy() {
        return proxy;
    }

    protected int getProxyPort() {
        return proxyPort;
    }
}
