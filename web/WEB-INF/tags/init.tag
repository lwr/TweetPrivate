<%@ tag pageEncoding="UTF-8" language="java" %>
<%@ tag import="solocompany.app.twp.TWPContext" %>
<%--
  ~ Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
  --%>

<%
    TWPContext context = (TWPContext) request.getAttribute("tc");
    if (context == null) {
        context = new TWPContext();
        context.setRequest(request);
        context.setServletContext(application);
        request.setAttribute("tc", context);
    }
%>