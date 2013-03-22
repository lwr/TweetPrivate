<%--
  ~ Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
  --%>

<%@ tag pageEncoding="UTF-8" language="java" %>
<%@ include file="/WEB-INF/include/prelude.jspf" %>
<c:if test="${tp == null}">
    <c:redirect url="/" />
</c:if>
