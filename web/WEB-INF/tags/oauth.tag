<%--
  ~ Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
  --%>

<%@ tag pageEncoding="UTF-8" language="java" %>
<%@ tag import=" solocompany.app.twp.AccessToken,
                 solocompany.oauth.LightweightTwitterAPI,
                 solocompany.utils.URLUtils,
                 solocompany.var.VarObject" %>
<%@ include file="/WEB-INF/include/prelude.jspf" %>
<%!
    static void setCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie;
        if (value == null) {
            cookie = new Cookie(name, "");
            cookie.setMaxAge(0); // 删除 cookie
        } else {
            cookie = new Cookie(name, value);
        }
        cookie.setPath("/");
        response.addCookie(cookie);
    }
%>
<%
    if ("logout".equals(request.getParameter("action"))) {
        setCookie(response, "tp_current_token", null);
        setCookie(response, "tp_current_token_checksum", null);
        if (tc.getCurrentToken() != null) {
            tc.setCurrentToken(null);
        }

    } else if ("login_twitter".equals(request.getParameter("action"))) {
        // 通过 request_token 获取临时 token，定制 url
        LightweightTwitterAPI api = tc.getAccessTokenManager().getToken("").getTwitterAPI();

        VarObject m = new VarObject();
        String callbackURL = request.getRequestURL().toString();
        URLUtils.parseParameters(api.invokeAPI("oauth/request_token", "",
                "oauth_callback=" + URLUtils.simpleEncode(callbackURL)), m.normalize());

        AccessToken token = tc.getAccessTokenManager().getTemporaryToken(
                m.getString("oauth_token"),
                m.getString("oauth_token_secret"));

        String authURL = "https://api.twitter.com/oauth/authenticate?oauth_token=" + token.getToken();
        response.sendRedirect(authURL);

        //noinspection UnhandledExceptionInJSP
        throw new SkipPageException();

    } else if (request.getParameter("oauth_token") != null && request.getParameter("oauth_verifier") != null) {
        // 通过 access_token 获取真正的授权 token 及 secret
        AccessToken token = tc.getAccessTokenManager().getToken(request.getParameter("oauth_token"));
        if (token.hasSecret()) {
            LightweightTwitterAPI api = token.getTwitterAPI();
            VarObject m = new VarObject();
            URLUtils.parseParameters(api.invokeAPI("oauth/access_token", "oauth_verifier="
                    + request.getParameter("oauth_verifier")), m.normalize());
            token.revoke();
            token = tc.getAccessTokenManager().getToken(
                    m.getString("oauth_token"),
                    m.getString("oauth_token_secret"));

            setCookie(response, "tp_current_token", token.getToken());
            setCookie(response, "tp_current_token_checksum", token.getChecksum());
            response.sendRedirect(request.getContextPath());

            //noinspection UnhandledExceptionInJSP
            throw new SkipPageException();
        }
    } else if ("update_status".equals(request.getParameter("action"))) {
        if (tc.getCurrentToken() != null) {
            tc.getCurrentToken().getTweetPrivate().updateProfile();
        }
    }
    out.clear();
%>
