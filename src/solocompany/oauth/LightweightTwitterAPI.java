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


    public LightweightTwitterAPI(OAuthTool oAuthTool) {
        this.oAuthTool = oAuthTool;
    }


    /*
        # dig 的结果
        ;; ANSWER SECTION:
        api.twitter.com.    4       IN      A       199.59.149.232
        api.twitter.com.    4       IN      A       199.59.150.9
        api.twitter.com.    4       IN      A       199.59.148.20

        # Java 不支持 Socks-hostname 必须要修改 hosts 才能防 DNS 污染
        sudo vi /etc/hosts

        # 加入这行
        199.59.149.232    api.twitter.com
    */


    private void configProxy() {
        final List<Proxy> proxyList = Collections.singletonList(new Proxy(Proxy.Type.SOCKS,
                new InetSocketAddress("127.0.0.1", 7070)));

        ProxySelector.setDefault(new ProxySelector() {
            @Override
            public List<Proxy> select(URI uri) {
                return proxyList;
            }

            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
            }
        });
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
        ProxySelector proxySelector = ProxySelector.getDefault();
        configProxy();
        try {
            return oAuthTool.httpRequest(new URL("https://api.twitter.com/" + type), body);
        } finally {
            ProxySelector.setDefault(proxySelector);
        }
    }
}
