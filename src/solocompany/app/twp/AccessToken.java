/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.app.twp;

import solocompany.json.JSONParser;
import solocompany.oauth.LightweightTwitterAPI;
import solocompany.utils.Hex;
import solocompany.var.VarObject;

import java.io.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * AccessToken.
 *
 * @author <a href="mailto:lwr@coremail.cn">William Leung</a>
 */
public class AccessToken {

    final transient AccessTokenManager manager;

    final String token;
    final String secret;

    private volatile transient LightweightTwitterAPI api;


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


    public TweetPrivate getTweetPrivate() {
        return new TweetPrivate(getTwitterAPI());
    }


    public LightweightTwitterAPI getTwitterAPI() {
        if (api == null) {
            api = new LightweightTwitterAPI(manager.getMyTwitter().getAnotherToken(token, secret));
        }
        return api;
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


    VarObject profile;

    public Map<String, Object> getProfile() throws IOException {
        if (profile == null) {
            profile = new JSONParser().parseJson(getTwitterAPI().jsonAPI("1.1/account/verify_credentials", "")).getMap();
        }
        return profile.normalize();
    }
}
