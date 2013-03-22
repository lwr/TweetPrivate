<%--
  ~ Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
  --%>

<%@ tag pageEncoding="UTF-8" language="java" %>
<%@ variable name-given="tp" variable-class="solocompany.app.twp.TweetPrivate" scope="AT_END" %>
<%@ include file="/WEB-INF/include/prelude.jspf" %>
<%-- restore token from cookie --%>
<c:set var="token" value='${cookie.tp_current_token.value}' />
<c:if test="${token > ''}">
    <c:set var="checksum" value='${cookie.tp_current_token_checksum.value}' />
    <%
        tc.setCurrentTokenWithChecksum(
                (String) jspContext.getAttribute("token"),
                (String) jspContext.getAttribute("checksum"));
        if (tc.getCurrentToken() != null) {
            jspContext.setAttribute("tp", tc.getCurrentToken().getTweetPrivate());
        }
    %>
</c:if>
