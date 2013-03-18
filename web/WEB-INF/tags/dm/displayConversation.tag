<%--
  ~ Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
  --%>
<%@ tag pageEncoding="UTF-8" language="java" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<p>
    <a href='?id='>Back</a>
</p>
<%@ include file="../accessTokenManager.jspf" %>
<%
    long id;
    try {
        id = Long.parseLong(request.getParameter("id"));
    } catch (NumberFormatException e) {
        out.println(e);
        return;
    }

    java.util.HashMap<String, Object> userInfoMap = new java.util.HashMap<String, Object>();
    userInfoMap.put("in", tp.getConversationStats().get(String.valueOf(id)).normalize());
    userInfoMap.put("out", tp.getMyInfo().normalize());
%>
<jsp:useBean id="user_info" class="java.util.HashMap" />
<c:forEach items="<%= userInfoMap.entrySet() %>" var="entry">
    <c:set var="item" value="${entry.value}" />
    <c:set var="nameX" value="${fn:escapeXml(item['name'])}" />
    <c:set target="${user_info}" property="${entry.key}">
        <img class="avatar" src="${item['profile_image_url_https']}" alt="${nameX}" />
        <span class="fullname">${nameX}</span>
        <span class="username">
            <s>@</s>
            <b>${item['screen_name']}</b>
        </span>
    </c:set>
</c:forEach>

<div class="body_parent">
    <c:forEach items="<%= tp.getConversation(id).normalize() %>" var="record">
        ${user_info[record.type]}
        <span class="date">
            <c:set var="date" value="${record.date}" />
            <fmt:formatDate type="both" dateStyle="long"
                            value='<%= new java.util.Date((Long) jspContext.getAttribute("date"))%>' />
        </span>
        <p class="tweet-text">${fn:escapeXml(record.text)}</p>
    </c:forEach>
</div>
