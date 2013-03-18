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

    volatile OAuthTool myToken;

    final Properties tokenMap = new Properties();


    @NotNull
    public AccessToken getMyTwitterToken() {
        return getTwitterToken(getMyTwitter().getAccessToken());
    }


    @NotNull
    public AccessToken getTwitterToken(String accessToken) {
        if (tokenMap.isEmpty()) {
            loadAccessTokens();
            tokenMap.put(getMyTwitter().getAccessToken(), getMyTwitter().getAccessSecret());
        }
        return new AccessToken(this, accessToken, tokenMap.getProperty(accessToken));
    }


    private void loadAccessTokens() {
        try {
            InputStream in = new FileInputStream(tokenDataFile);
            try {
                tokenMap.load(in);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            // ignore;
        }
    }


    @NotNull
    public OAuthTool getMyTwitter() {
        if (myToken == null) {
            myToken = retrieveMyTwitter(new File(tokenDataFile, "../app_key.properties"));
        }
        return myToken;
    }


    @NotNull
    private OAuthTool retrieveMyTwitter(File consumerKeyFile) {
        String packageName = AccessTokenManager.class.getPackage().getName();
        try {
            java.lang.reflect.Field apiField = Class.forName(packageName + ".MyTwitterAPI").getDeclaredField("OAUTH");
            return (OAuthTool) apiField.get(null);
        } catch (Exception e) {
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
