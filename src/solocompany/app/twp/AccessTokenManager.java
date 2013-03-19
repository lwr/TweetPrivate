/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.app.twp;

import org.jetbrains.annotations.NotNull;
import solocompany.oauth.OAuthTool;

import java.io.*;
import java.util.*;

/**
 * AccessTokenManager.
 *
 * @author <a href="mailto:lwr@coremail.cn">William Leung</a>
 */
public class AccessTokenManager {


    File tokenDataFile = new File(System.getProperty("user.home"), ".TwitterData/token.data");

    volatile OAuthTool myTwitter;

    final Map<String, AccessToken> tokenMap = new HashMap<String, AccessToken>();


    @NotNull
    public AccessToken getMyToken() {
        return getToken(getMyTwitter().getAccessToken());
    }


    @NotNull
    public AccessToken getToken(String accessToken) {
        if (tokenMap.isEmpty()) {
            loadTokens();
        }
        AccessToken result = tokenMap.get(accessToken);
        if (result != null) {
            return result;
        }
        return newToken(accessToken, null, false);
    }


    public AccessToken getToken(String accessToken, String secret) {
        if (tokenMap.isEmpty()) {
            loadTokens();
        }
        return newToken(accessToken, secret, true);
    }


    private AccessToken newToken(String accessToken, String secret, boolean putToCache) {
        AccessToken result = new AccessToken(this, accessToken, secret);
        if (putToCache) {
            tokenMap.put(accessToken, result);
        }
        return result;
    }


    private void loadTokens() {
        Properties properties = new Properties();
        try {
            InputStream in = new FileInputStream(tokenDataFile);
            try {
                properties.load(in);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            // ignore;
        }
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            newToken((String) entry.getKey(), (String) entry.getValue(), true);
        }

        newToken(getMyTwitter().getAccessToken(), getMyTwitter().getAccessSecret(), true);
        newToken("", "", true);
    }


    @NotNull
    OAuthTool getMyTwitter() {
        if (myTwitter == null) {
            myTwitter = retrieveMyTwitter();
        }
        return myTwitter;
    }


    @NotNull
    private OAuthTool retrieveMyTwitter() {
        String packageName = AccessTokenManager.class.getPackage().getName();
        try {
            java.lang.reflect.Field apiField = Class.forName(packageName + ".MyTwitterAPI").getDeclaredField("OAUTH");
            return (OAuthTool) apiField.get(null);
        } catch (Exception e) {
            File consumerKeyFile = new File(tokenDataFile, "../app_key.properties");
            Properties properties = new Properties();
            try {
                InputStream in = new FileInputStream(consumerKeyFile);
                try {
                    properties.load(in);
                } finally {
                    in.close();
                }
            } catch (IOException e1) {
                // ignore;
            }

            final String consumerKey = properties.getProperty("consumerKey");
            final String consumerSecret = properties.getProperty("consumerSecret");
            if (consumerKey != null && consumerSecret != null) {
                return new OAuthTool(consumerKey, consumerSecret,
                        properties.getProperty("accessToken"),
                        properties.getProperty("accessSecret"));
            }


            throw new RuntimeException(""
                    + "Twitter api keys not found"
                    + "\n"
                    + "==== To make this simple test worked, you should defined your api like this: ====\n"
                    + "package " + packageName + ";\n"
                    + "\n"
                    + "public class MyTwitterAPI {\n"
                    + "\n"
                    + "    public static OAuthTool OAUTH = OAuthTool(\n"
                    + "            consumerKey,        // Your twitter app key\n"
                    + "            consumerSecret,     // Your twitter app secret\n"
                    + "            accessToken,        // Your twitter access token\n"
                    + "            accessSecret        // your twitter access secret\n"
                    + "    );\n"
                    + "}\n"
                    + "", e);
        }
    }
}
