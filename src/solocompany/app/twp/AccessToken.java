/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.app.twp;

import solocompany.oauth.LightweightTwitterAPI;
import solocompany.oauth.OAuthTool;

/**
 * AccessToken.
 *
 * @author <a href="mailto:lwr@coremail.cn">William Leung</a>
 */
public class AccessToken {

    private final AccessTokenManager manager;

    private final String accessToken;
    private final String accessSecret;


    public AccessToken(AccessTokenManager manager, String accessToken, String accessSecret) {
        this.manager = manager;
        this.accessToken = accessToken;
        this.accessSecret = accessSecret;
    }


    public TweetPrivate getTweetPrivate() {
        return new TweetPrivate(getTwitterAPI());
    }


    public LightweightTwitterAPI getTwitterAPI() {
        return new LightweightTwitterAPI(getOAuthTool());
    }


    public OAuthTool getOAuthTool() {
        return manager.getMyTwitter().getAnotherToken(accessToken, accessSecret);
    }
}
