/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.oauth;

import solocompany.utils.Base64;
import solocompany.utils.IOUtils;
import solocompany.utils.URLUtils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * OAuthUtil.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class OAuthTool {

    final String consumerKey;
    final String consumerSecret;
    final String accessToken;
    final String accessSecret;


    public OAuthTool(String consumerKey, String consumerSecret, String accessToken, String accessSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.accessToken = accessToken;
        this.accessSecret = accessSecret;
    }


    public OAuthTool getAnotherToken(String accessToken, String accessSecret) {
        return new OAuthTool(consumerKey, consumerSecret, accessToken, accessSecret);
    }


    public String getAccessToken() {
        return accessToken;
    }


    public String getAccessSecret() {
        return accessSecret;
    }


    public String httpRequest(URL url, String parametersForPost) throws IOException {
        HttpURLConnection conn = setupConnection((HttpURLConnection) url.openConnection(), parametersForPost);
        try {
            return IOUtils.allToString(conn.getInputStream(), "UTF-8");
        } catch (IOException e) {
            IOUtils.allBytes(conn.getErrorStream(), System.err);
            throw e;
        }
    }


    public HttpURLConnection setupConnection(HttpURLConnection conn, String parametersForPost) throws IOException {
        if (parametersForPost != null && parametersForPost.length() > 0) {
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
        }

        String authorizationHeader = getAuthorizationHeader(conn.getRequestMethod(), conn.getURL(), parametersForPost);
        conn.setRequestProperty("Authorization", authorizationHeader);

        if (parametersForPost != null && parametersForPost.length() > 0) {
            conn.getOutputStream().write(parametersForPost.getBytes("UTF-8"));
            conn.getOutputStream().flush();
        }
        return conn;
    }


    public String getAuthorizationHeader(String method, URL url, String parametersForPost) {
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        String nonce = Long.toHexString(System.nanoTime());

        String baseString = getBaseString(url, method, parametersForPost, nonce, timestamp);
        String signature = getSignature(baseString);

        return "OAuth "
                + "oauth_consumer_key=\"" + consumerKey + "\", "
                + "oauth_nonce=\"" + nonce + "\", "
                + "oauth_signature=\"" + percentEncode(signature) + "\", "
                + "oauth_signature_method=\"" + SIGNATURE_METHOD + "\", "
                + "oauth_timestamp=\"" + timestamp + "\", "
                + "oauth_token=\"" + accessToken + "\", "
                + "oauth_version=\"1.0\""
                + "";
    }


    private String getBaseString(URL url, String method, String parametersForPost, String nonce, String timestamp) {
        HashMap<String, String> rawParameterMap = new HashMap<String, String>();
        rawParameterMap.put("oauth_consumer_key", consumerKey);
        rawParameterMap.put("oauth_nonce", nonce);
        rawParameterMap.put("oauth_signature_method", SIGNATURE_METHOD);
        rawParameterMap.put("oauth_timestamp", timestamp);
        rawParameterMap.put("oauth_token", accessToken);
        rawParameterMap.put("oauth_version", "1.0");

        if (url.getQuery() != null) {
            URLUtils.parseParameters(url.getQuery(), rawParameterMap);
        }

        if (parametersForPost != null) {
            URLUtils.parseParameters(parametersForPost, rawParameterMap);
        }

        return percentEncode(method.toUpperCase())
                + '&' + percentEncode(normalizeUrl(url))
                + '&' + percentEncode(normalizeParameters(rawParameterMap));
    }


    protected static String normalizeUrl(URL url) {
        String scheme = url.getProtocol().toLowerCase();
        String authority = url.getAuthority().toLowerCase();
        boolean dropPort = (scheme.equals("http") && url.getPort() == 80)
                || (scheme.equals("https") && url.getPort() == 443);
        if (dropPort) {
            // find the last : in the authority
            int index = authority.lastIndexOf(":");
            if (index >= 0) {
                authority = authority.substring(0, index);
            }
        }
        String path = url.getPath();
        if (path == null || path.length() <= 0) {
            path = "/"; // conforms to RFC 2616 section 3.2.2
        }
        // we know that there is no query and no fragment here.
        return scheme + "://" + authority + path;
    }


    private String normalizeParameters(Map<String, String> rawParameterMap) {
        String[] keys = rawParameterMap.keySet().toArray(new String[rawParameterMap.size()]);
        Arrays.sort(keys);
        StringBuilder builder = new StringBuilder();
        for (String key : keys) {
            builder.append('&');
            builder.append(key);
            builder.append('=');
            builder.append(rawParameterMap.get(key));
        }
        return builder.substring(1);
    }


    private static String percentEncode(String s) {
        return (s != null) ? URLUtils.simpleEncode(s) : "";
    }


    private String getSignature(String baseString) {
        try {
            return Base64.bytesToBase64(computeSignature(baseString));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static final String SIGNATURE_METHOD = "HMAC-SHA1";
    private static final String ENCODING = "ISO-8859-1";
    private static final String MAC_NAME = "HmacSHA1";
    private SecretKey key;


    // Now only support HMAC-SHA1
    private byte[] computeSignature(String baseString) throws Exception {
        SecretKey key;
        synchronized (this) {
            if (this.key == null) {
                String keyString = percentEncode(consumerSecret) + '&' + percentEncode(accessSecret);
                this.key = new SecretKeySpec(keyString.getBytes(ENCODING), MAC_NAME);
            }
            key = this.key;
        }

        Mac mac = Mac.getInstance(MAC_NAME);
        mac.init(key);
        return mac.doFinal(baseString.getBytes(ENCODING));
    }
}
