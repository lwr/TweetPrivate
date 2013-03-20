<%@ tag pageEncoding="UTF-8" language="java" %>
<%@ include file="/WEB-INF/include/prelude.jspf" %>
<%@ attribute name="profile" type="java.util.Map" %>
<%--
  ~ Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
  --%>

<c:if test="<%= profile != null %>">
    <p>
        <img src="${profile.profile_image_url_https}" alt="${profile.screen_name}">
        You are now authorized as ${fn:escapeXml(profile.name)}
        <a href="https://twitter.com/${profile.screen_name}">
            @${profile.screen_name}
        </a>
    </p>
    <p>
        Location: ${fn:escapeXml(profile.location)}
    </p>
    <p>
        Last tweet: ${fn:escapeXml(profile.status.text)}
        via ${profile.status.source}
        at ${profile.status.created_at}
    </p>
    <%--
    <pre><%= Variant.wrap(profile).getDebugInfo() %></pre>
    --%>
</c:if>
