<%--
  ~ Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
  --%>

<%@ tag pageEncoding="UTF-8" language="java" %>
<%@ include file="/WEB-INF/include/prelude.jspf" %>
<c:if test="${param.action == 'dm_download_temp' || param.action == 'dm_download_save'}">
    <p>
        Message downloading...
    </p>
    <blockquote>
        <pre><%
            if ("dm_download_save".equals(request.getParameter("action"))) {
                tp.setSaveData();
            }
            //noinspection UnhandledExceptionInJSP
            tp.downloadDirectMessages(new java.io.PrintWriter(out, true));
        %></pre>
    </blockquote>
    <p>
        Download finished: totally
            ${fn:length(tp.normalizedData.inbox)} income messages,
            ${fn:length(tp.normalizedData.outbox)} sent messages
    </p>
    <p>
        <a href="index.jsp">Refresh now</a>
    </p>
    <%
        //noinspection ConstantConditions
        if (tp != null) { // always true, just hack IDEA for badly annoying warnings
            //noinspection UnhandledExceptionInJSP
            throw new SkipPageException();
        }
    %>
</c:if>
<c:if test="${tp.normalizedData.inbox == null}">
    <p>
        Your direct messages are not downloaded yet.
    </p>
    <ul>
        <li>
            <a href="?action=dm_download_temp">Download temporary</a>
        </li>
        <li>
            <a href="?action=dm_download_save">Download and save to server</a>
        </li>
    </ul>
</c:if>
<div class="body_parent">
    <c:forEach items="${tp.conversationStats.map}" var="entry">
        <c:set var="id" value="${entry.key}" />
        <c:set var="item" value="${entry.value}" />
        <c:set var="nameX" value="${fn:escapeXml(item['name'])}" />
        <% solocompany.var.Variant x =
                solocompany.var.Variant.wrap(jspContext.getAttribute("item"));%>
        <div class="content">
            <img class="avatar" src="${item['profile_image_url_https']}" alt="${nameX}" />
            <p class="tweet-head">
                <span class="fullname">${nameX}</span>
                <span class="username">
                    <s>@</s>
                    <b>${item['screen_name']}</b>
                </span>
                <span class="date">
                    <% long lastDate = Math.max(x.getLong("inLast"), x.getLong("outLast")); %>
                    <fmt:formatDate type="both" dateStyle="long" value='<%= new java.util.Date(lastDate) %>' />
                </span>
            </p>
            <p class="tweet-text-normal">
                <a href="?id=${id}">
                    共收到 <%= x.getInt("inCount") %> 条,
                    发出 <%= x.getInt("outCount") %> 条,
                </a>
            </p>
        </div>
    </c:forEach>
</div>
