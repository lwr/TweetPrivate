<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/include/pageDefaults.jspf" %>

<%--
  ~ Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
  --%>

<html>
<head>
    <title>TweetPrivate PProject - Direct Messages</title>
    <link rel="stylesheet" href="css.css" type="text/css" />
</head>
<body>
<p>
    <%
        long id;
        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            id = 0;
        }
    %>
    <a href='../'>Back to home</a>
    <c:choose>
        <c:when test="<%= id > 0 %>">
            <a href='index.jsp'>Back to summary</a>
            <dm:displayConversation id="<%= id %>" />
        </c:when>
        <c:otherwise>
            <dm:displayConversationStats />
        </c:otherwise>
    </c:choose>
</p>
</body>
</html>