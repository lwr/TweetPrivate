/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.oauth;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * A lightweight Twitter API implementation.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class LightweightTwitterAPI {

    final OAuthTool oAuthTool;
    final TwitterConfig config;


    LightweightTwitterAPI(OAuthTool oAuthTool, TwitterConfig config) {
        this.oAuthTool = oAuthTool;
        this.config = config;
    }


    public String getAccessToken() {
        return oAuthTool.getAccessToken();
    }


    public String getAccessSecret() {
        return oAuthTool.getAccessSecret();
    }


    public String jsonAPI(String type, String body) throws IOException {
        String qLeft = type;
        String query = "";
        int qMarkPosition = type.indexOf('?');
        if (qMarkPosition != -1) {
            qLeft = type.substring(0, qMarkPosition);
            query = type.substring(qMarkPosition);
        }
        return invokeAPI(qLeft + (qLeft.endsWith(".json") ? "" : ".json") + query, body);
    }


    public String invokeAPI(String type, String body) throws IOException {
        return invokeAPI(type, body, null);
    }


    public String invokeAPI(String type, String body, String oAuthHeaders) throws IOException {
        ProxySelector proxySelector = null;
        if (config != null && config.proxyList != null) {
            proxySelector = ProxySelector.getDefault();
            ProxySelector.setDefault(getProxySelector(config.proxyList, proxySelector));
        }

        try {
            return oAuthTool.httpRequest(new URL("https://api.twitter.com/" + type), body, oAuthHeaders);
        } finally {
            if (proxySelector != null) {
                ProxySelector.setDefault(proxySelector);
            }
        }
    }


    static ProxySelector getProxySelector(final List<Proxy> proxyList, final ProxySelector fallback) {
        return new ProxySelector() {
            @Override
            public List<Proxy> select(URI uri) {
                if (uri.getHost().equals("api.twitter.com")) {
                    return proxyList;
                }
                return fallback.select(uri);
            }

            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                fallback.connectFailed(uri, sa, ioe);
            }
        };
    }
}
