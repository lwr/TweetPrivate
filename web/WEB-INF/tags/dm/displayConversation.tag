<%@ tag pageEncoding="UTF-8" language="java" %>
<%@ include file="/WEB-INF/include/prelude.jspf" %>
<%@ attribute name="id" type="java.lang.Long" required="true" %>
<%--
  ~ Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
  --%>

<%
    java.util.HashMap<String, Object> userInfoMap = new java.util.HashMap<String, Object>();
    solocompany.app.twp.TweetPrivate tp = tc.getCurrentToken().getTweetPrivate();
    userInfoMap.put("in", tp.getConversationStats().get(String.valueOf(id)).normalize());
    userInfoMap.put("out", tp.getProfile().normalize());
%>
<jsp:useBean id="user_avatar" class="java.util.HashMap" />
<jsp:useBean id="user_info" class="java.util.HashMap" />
<c:forEach items="<%= userInfoMap.entrySet() %>" var="entry">
    <c:set var="item" value="${entry.value}" />
    <c:set var="nameX" value="${fn:escapeXml(item['name'])}" />
    <c:set target="${user_avatar}" property="${entry.key}">
        <img class="avatar" src="${item['profile_image_url_https']}" alt="${nameX}" />
    </c:set>
    <c:set target="${user_info}" property="${entry.key}">
        <span class="fullname">${nameX}</span>
        <span class="username">
            <s>@</s>
            <b>${item['screen_name']}</b>
        </span>
    </c:set>
</c:forEach>

<div class="body_parent dm_conversion">
    <c:forEach items="<%= tp.getConversation(id).normalize() %>" var="record">
        ${user_avatar[record.type]}
        <p class="tweet-head">
            ${user_info[record.type]}
            <span class="date">
                <c:set var="date" value="${record.date}" />
                <fmt:formatDate type="both" dateStyle="long"
                                value='<%= new java.util.Date((Long) jspContext.getAttribute("date"))%>' />
            </span>
        </p>
        <p class="tweet-text">${fn:escapeXml(record.text)}</p>
    </c:forEach>
</div>
