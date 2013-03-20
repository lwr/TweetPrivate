<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/include/pageDefaults.jspf" %>
<%--
  ~ Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
  --%>

<ROOT:oauth />
<html>
<head>
    <title>TweetPrivate Project Home</title>
</head>
<body>
<c:choose>
    <c:when test="${tc.currentToken != null}">
        <ROOT:displayProfile profile="${tc.currentToken.profile}" />
        <ul>
            <li>
                <a href="dm/index.jsp">Check My Direct Messages</a>
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