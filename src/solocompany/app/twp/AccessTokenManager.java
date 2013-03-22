/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.app.twp;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import solocompany.oauth.OAuthTool;

import java.io.*;
import java.util.*;

/**
 * AccessTokenManager.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class AccessTokenManager {


    File tokenDataFile = new File(System.getProperty("user.home"), ".TwitterData/token.properties");
    Writer tokenDateOS;

    volatile OAuthTool myTwitter;

    private Map<String, AccessToken> tokenMap = null;


    @NotNull
    public AccessToken getMyToken() {
        return getToken(getMyTwitter().getAccessToken(), getMyTwitter().getAccessSecret());
    }


    @NotNull
    public AccessToken getToken(@NotNull String token) {
        return doGetToken(false, token, null);
    }


    @NotNull
    public AccessToken getToken(@NotNull String token, @NotNull String secret) {
        return doGetToken(false, token, secret);
    }


    @NotNull
    public AccessToken getTemporaryToken(@NotNull String token, @NotNull String secret) {
        return doGetToken(true, token, secret);
    }


    private AccessToken doGetToken(boolean temporary, @NotNull String token, @Nullable String secret) {
        if (tokenMap == null) {
            loadTokens();
        }

        AccessToken result = tokenMap.get(token);
        if ((result != null)
                && (secret == null || result.secret.equals(secret))) {
            // return from cache
            return result;

        } else if (secret == null) {
            // return a dummy
            return new AccessToken(this, token, "");

        } else {
            // add to cache (replace the token)
            result = new AccessToken(this, token, secret);
            tokenMap.put(token, result);
            if (!temporary) {
                appendToken(token, secret);
            }
            return result;
        }
    }


    void revokeToken(String token) {
        if (tokenMap != null) {
            tokenMap.remove(token);
        }
    }


    private synchronized void appendToken(String token, String secret) {
        try {
            if (tokenDateOS == null) {
                //noinspection ResultOfMethodCallIgnored
                tokenDataFile.getParentFile().mkdirs();
                tokenDateOS = new OutputStreamWriter(new FileOutputStream(tokenDataFile), "ISO-8859-1");
            }
            tokenDateOS.write(token + ' ' + secret + '\n');
            tokenDateOS.flush();
        } catch (IOException e) {
            PrintStream out = System.err;
            out.println("Write token failed: " + token);
            e.printStackTrace(out);
        }
    }


    private synchronized void loadTokens() {
        if (tokenMap != null) {
            return;
        }

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

        Map<String, AccessToken> tokenMap = new HashMap<String, AccessToken>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            AccessToken token = new AccessToken(this, (String) entry.getKey(), (String) entry.getValue());
            tokenMap.put(token.getToken(), token);
        }
        tokenMap.put("", new AccessToken(this, "", ""));
        this.tokenMap = Collections.synchronizedMap(tokenMap);
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
