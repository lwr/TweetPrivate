<%--
  ~ Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
  --%>

<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/include/pageDefaults.jspf" %>
<ROOT:oauth />
<html>
<head>
    <title>TweetPrivate Project Home</title>
</head>
<body>
<c:choose>
    <c:when test="${tc.currentToken != null}">
        <c:set var="tp" value="${tc.currentToken.tweetPrivate}" />
        <c:set var="profile" value="${tp.profile.map}" />
        <img src="${profile.profile_image_url_https}" alt="${profile.screen_name}"
             style="width: 48px; height: 48px; vertical-align: text-bottom;" />
        <span>
            Welcome ${fn:escapeXml(profile.name)}
            <a href="https://twitter.com/${profile.screen_name}">@${profile.screen_name}</a>
            (from ${fn:escapeXml(profile.location)})
            <a href="?action=update_status">update now</a>
        </span>
        <p>
            Last tweet: ${fn:escapeXml(profile.status.text)}
            via ${profile.status.source}
            at ${profile.status.created_at}
        </p>
        <%--
        <pre><%= Variant.wrap(profile).getDebugInfo() %></pre>
        --%>
        <ul>
            <li>
                <a href="dm/index.jsp">My Direct Messages</a>
            </li>
            <li>
                <a href="?action=logout">Logout</a>
            </li>
        </ul>
    </c:when>
    <c:otherwise>
        <p>
            You are not sign in.
        </p>
        <ul>
            <li>
                <a href="?action=login_twitter">Sign in with Twitter</a>
            </li>
        </ul>
    </c:otherwise>
</c:choose>
</body>
</html>