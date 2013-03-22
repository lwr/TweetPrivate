/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.app.twp;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import solocompany.oauth.TwitterConfig;

import java.io.*;
import java.util.*;

/**
 * AccessTokenManager.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class AccessTokenManager {


    File tokenDataFile = TwitterConfig.getConfigFile("token.properties");
    Writer tokenDateOS;

    volatile TwitterConfig config;

    private Map<String, AccessToken> tokenMap = null;


    @NotNull
    public TwitterConfig getConfig() {
        if (config == null) {
            config = TwitterConfig.getInstance();
        }
        return config;
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
}
