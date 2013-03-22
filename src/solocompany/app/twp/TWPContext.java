/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.app.twp;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * RequestBean.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class TWPContext {

    ServletContext servletContext;
    HttpServletRequest request;

    AccessTokenManager accessTokenManager;
    AccessToken currentToken;


    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }


    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }


    public AccessTokenManager getAccessTokenManager() {
        if (accessTokenManager == null) {
            accessTokenManager = (AccessTokenManager) servletContext.getAttribute(AccessTokenManager.class.getName());
            if (accessTokenManager == null) {
                synchronized (AccessTokenManager.class) {
                    accessTokenManager = (AccessTokenManager) servletContext.getAttribute(AccessTokenManager.class.getName());
                    if (accessTokenManager == null) {
                        accessTokenManager = new AccessTokenManager();
                        servletContext.setAttribute(AccessTokenManager.class.getName(), accessTokenManager);
                    }
                }
            }
        }
        return accessTokenManager;
    }


    public AccessToken getCurrentToken() {
        return currentToken;
    }


    public void setCurrentToken(AccessToken currentToken) {
        this.currentToken = currentToken;
    }


    public boolean setCurrentTokenWithChecksum(String token, String checksum) {
        if (token != null && token.length() > 0) {
            AccessToken accessToken = getAccessTokenManager().getToken(token);
            if (accessToken.hasSecret() && accessToken.getChecksum().equals(checksum)) {
                currentToken = accessToken;
                return true;
            }
        }
        return false;
    }
}
