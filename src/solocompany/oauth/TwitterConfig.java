/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.oauth;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * TwitterConfig.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class TwitterConfig {

    protected OAuthTool oAuth;
    protected List<Proxy> proxyList;

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

        proxyList = Collections.singletonList(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 7070)));
    */


    public TwitterConfig() {
    }


    public static File getAppKeyProperties() {
        return getConfigFile("app_key.properties");
    }


    public static File getConfigFile(String name) {
        return new File(System.getProperty("user.home"), ".TwitterData/" + name);
    }


    public LightweightTwitterAPI getAPI() {
        return new LightweightTwitterAPI(oAuth, this);
    }


    public LightweightTwitterAPI getAPI(String token, String secret) {
        return new LightweightTwitterAPI(oAuth.getAnotherToken(token, secret), this);
    }


    @NotNull
    public static TwitterConfig getInstance() {
        TwitterConfig config = new TwitterConfig();
        String message = "Twitter api keys not found";
        Throwable t = null;
        String packageName = TwitterConfig.class.getPackage().getName();
        try {
            config = (TwitterConfig) Class.forName(packageName + ".MyTwitterConfig").newInstance();
        } catch (Exception e) {
            t = (e instanceof ClassNotFoundException) ? null : e;
            try {
                config.loadFromProperties();
            } catch (Exception e1) {
                message = "Load " + getAppKeyProperties() + " failed";
                t = e1;
            }
        }

        if (config.oAuth == null) {
            System.err.println(""
                    + "==== You should defined your api like this: ====\n"
                    + "package " + packageName + ";\n"
                    + "\n"
                    + "public class MyTwitterConfig extends TwitterConfig {\n"
                    + "\n"
                    + "    public MyTwitterConfig() {\n"
                    + "        String consumerKey = ?,          // Your twitter app key\n"
                    + "        String consumerSecret = ?,       // Your twitter app secret\n"
                    + "        String accessToken = ?,          // Your twitter access token\n"
                    + "        String accessSecret = ?,         // your twitter access secret\n"
                    + "        oAuth = new OAuthTool(consumerKey, consumerSecret, accessToken, accessSecret);\n"
                    + "        proxyList = ?                    // Your proxy definition\n"
                    + "    }\n"
                    + "}\n"
                    + "\n"
                    + "==== Or use a properties files like this: ====\n"
                    + "" + getAppKeyProperties() + "\n"
                    + "\n"
                    + "consumerKey      = Your twitter app key\n"
                    + "consumerSecret   = Your twitter app secret\n"
                    + "accessToken      = Your twitter access token\n"
                    + "accessSecret     = Your twitter access secret\n"
                    + "proxyType        = http|socks|direct|system\n"
                    + "proxyHost        = 127.0.0.1:8080\n"
                    + "\n"
                    + ""
            );
            throw new RuntimeException(message, t);
        }
        return config;
    }


    private void loadFromProperties() throws Exception {
        if (!getAppKeyProperties().isFile()) {
            return;
        }
        Properties properties = new Properties();
        InputStream in = new FileInputStream(getAppKeyProperties());
        try {
            properties.load(in);
        } finally {
            in.close();
        }


        final String consumerKey = properties.getProperty("consumerKey");
        final String consumerSecret = properties.getProperty("consumerSecret");
        if (consumerKey != null && consumerSecret != null) {
            this.oAuth = new OAuthTool(consumerKey, consumerSecret,
                    properties.getProperty("accessToken"),
                    properties.getProperty("accessSecret"));
        }


        String proxyType = properties.getProperty("proxyType");
        String proxyHost = properties.getProperty("proxyHost");
        if ("default".equalsIgnoreCase(proxyType) || "system".equalsIgnoreCase(proxyType)) {
            this.proxyList = null;

        } else if ("none".equalsIgnoreCase(proxyType) || "direct".equalsIgnoreCase(proxyType)) {
            this.proxyList = Collections.singletonList(Proxy.NO_PROXY);

        } else if (proxyHost != null && proxyHost.indexOf(':') > 0) {
            Proxy.Type type = (proxyType == null || proxyType.length() == 0)
                    ? Proxy.Type.HTTP : Proxy.Type.valueOf(proxyType.toUpperCase());
            this.proxyList = Collections.singletonList(new Proxy(type, new InetSocketAddress(
                    proxyHost.substring(0, proxyHost.indexOf(':')),
                    Integer.parseInt(proxyHost.substring(proxyHost.indexOf(':') + 1))
            )));
        }
    }
}
