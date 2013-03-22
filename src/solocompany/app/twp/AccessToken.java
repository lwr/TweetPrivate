/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.app.twp;

import solocompany.oauth.LightweightTwitterAPI;
import solocompany.utils.Hex;

import java.security.MessageDigest;

/**
 * AccessToken.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class AccessToken {

    final transient AccessTokenManager manager;

    final String token;
    final String secret;

    private volatile transient TweetPrivate tp;


    public AccessToken(AccessTokenManager manager, String token, String secret) {
        this.manager = manager;
        this.token = token;
        this.secret = secret;
    }


    public String getToken() {
        return token;
    }


    public boolean hasSecret() {
        return secret.length() > 0;
    }


    public void revoke() {
        manager.revokeToken(token);
    }


    public LightweightTwitterAPI getTwitterAPI() {
        return getTweetPrivate().api;
    }


    public TweetPrivate getTweetPrivate() {
        if (tp == null) {
            tp = new TweetPrivate(manager.getConfig().getAPI(token, secret));
        }
        return tp;
    }


    public String getChecksum() {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update("TRRLdk3s".getBytes("ISO-8859-1"));
            md5.update(token.getBytes("ISO-8859-1"));
            md5.update(secret.getBytes("ISO-8859-1"));
            return Hex.bytesToHexL(md5.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
