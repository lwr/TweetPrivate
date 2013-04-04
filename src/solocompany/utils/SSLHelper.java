/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.utils;

import java.io.*;
import java.net.*;

/**
 * SSLHelper.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class SSLHelper {


    private SSLHelper() {}


    public static javax.net.ssl.SSLSocketFactory getDummySSLSocketFactory() {
        return DummySSLSocketFactory.getDefault();
    }


    public static javax.net.ssl.HostnameVerifier getDummyHostnameVerifier() {
        return new javax.net.ssl.HostnameVerifier() {
            @Override
            public boolean verify(String hostName, javax.net.ssl.SSLSession sslSession) {
                return true;
            }
        };
    }


    public static class DummySSLSocketFactory extends CustomSSLSocketFactory {

        @Override
        javax.net.ssl.TrustManager[] initTrustManager() {
            // 构造虚构的 TrustManager
            return new javax.net.ssl.TrustManager[]{new javax.net.ssl.X509TrustManager() {

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {}

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {}
            }};
        }


        @Override
        protected Socket configSocket(Socket socket) {
            if (socket instanceof javax.net.ssl.SSLSocket) {
                // Disable TLS for Sun Java security package issue: http://bugs.sun.com/view_bug.do?bug_id=4815023
                // 'bad_record_mac' error for self signed cert
                ((javax.net.ssl.SSLSocket) socket).setEnabledProtocols(new String[]{"SSLv2Hello", "SSLv3"});
            }
            return socket;
        }


        public static javax.net.ssl.SSLSocketFactory getDefault() {
            return new DummySSLSocketFactory();
        }
    }


    abstract static class CustomSSLSocketFactory extends javax.net.ssl.SSLSocketFactory {

        private javax.net.ssl.SSLSocketFactory impl;

        CustomSSLSocketFactory() {
            try {
                javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("SSL");
                sslContext.init(null, initTrustManager(), null);
                this.impl = sslContext.getSocketFactory();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        abstract javax.net.ssl.TrustManager[] initTrustManager() throws Exception;

        @Override
        public String[] getDefaultCipherSuites() {
            return impl.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return impl.getSupportedCipherSuites();
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
            return configSocket(impl.createSocket(socket, host, port, autoClose));
        }

        @Override
        public Socket createSocket() throws IOException {
            return configSocket(impl.createSocket());
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException {
            return configSocket(impl.createSocket(host, port));
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
            return configSocket(impl.createSocket(host, port, localHost, localPort));
        }

        @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
            return configSocket(impl.createSocket(host, port));
        }

        @Override
        public Socket createSocket(InetAddress host, int port, InetAddress localHost, int localPort) throws IOException {
            return configSocket(impl.createSocket(host, port, localHost, localPort));
        }

        protected Socket configSocket(Socket socket) {
            return socket;
        }
    }
}
