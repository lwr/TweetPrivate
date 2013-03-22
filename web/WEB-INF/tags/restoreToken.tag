<%@ tag pageEncoding="UTF-8" language="java" %>
<%@ attribute name="check" type="java.lang.Boolean" %>
<%@ include file="/WEB-INF/include/prelude.jspf" %>
<%--
  ~ Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
  --%>

<%-- restore token from cookie --%>
<c:set var="token" value='${cookie.tp_current_token.value}' />
<c:if test="${token > ''}">
    <c:set var="checksum" value='${cookie.tp_current_token_checksum.value}' />
    <%
        tc.setCurrentTokenWithChecksum(
                (String) jspContext.getAttribute("token"),
                (String) jspContext.getAttribute("checksum"));
    %>
</c:if>
<c:if test="${check && tc.currentToken == null}">
    <c:redirect url="/" />
</c:if>
