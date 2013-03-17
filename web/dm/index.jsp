<%--
  ~ Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
  --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="dm" tagdir="/WEB-INF/tags/dm" %>
<html>
<head>
    <title>TweetPrivate PProject - Direct Messages</title>
    <link rel="stylesheet" href="css.css" type="text/css" />
</head>
<body>
<%
    if (request.getParameter("id") == null || request.getParameter("id").isEmpty()) {
%>
<dm:displayConversationStats />
<%
} else {
%>
<dm:displayConversation />
<%
    }
%>
</body>
</html>