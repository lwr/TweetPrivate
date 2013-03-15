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
        # dig �Ľ��
        ;; ANSWER SECTION:
        api.twitter.com.    4       IN      A       199.59.149.232
        api.twitter.com.    4       IN      A       199.59.150.9
        api.twitter.com.    4       IN      A       199.59.148.20

        # Java ��֧�� Socks-hostname ����Ҫ�޸� hosts ���ܷ� DNS ��Ⱦ
        sudo vi /etc/hosts

        # ��������
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
