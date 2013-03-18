<%--
  ~ Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
  --%>
<%@ tag pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../accessTokenManager.jspf" %>
<div class="body_parent">
    <c:forEach items="<%= tp.getConversationStats().normalize() %>" var="entry">
        <c:set var="id" value="${entry.key}" />
        <c:set var="item" value="${entry.value}" />
        <c:set var="nameX" value="${fn:escapeXml(item['name'])}" />
        <% solocompany.var.Variant x =
                solocompany.var.Variant.wrap(jspContext.getAttribute("item"));%>
        <div class="content">
            <img class="avatar" src="${item['profile_image_url_https']}" alt="${nameX}" />
            <span class="fullname">${nameX}</span>
            <span class="username">
                <s>@</s>
                <b>${item['screen_name']}</b>
            </span>
            <span class="date">
                <% long lastDate = Math.max(x.getLong("inLast"), x.getLong("outLast")); %>
                <fmt:formatDate type="both" dateStyle="long" value='<%= new java.util.Date(lastDate) %>' />
            </span>
            <a href="?id=${id}">
                <p class="tweet-text-normal">
                    共收到 <%= x.getInt("inCount") %> 条,
                    发出 <%= x.getInt("outCount") %> 条,
                </p>
            </a>
        </div>
    </c:forEach>
</div>
