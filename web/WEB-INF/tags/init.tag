<%--
  ~ Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
  --%>

<%@ tag pageEncoding="UTF-8" language="java" %>
<%@ tag import="solocompany.app.twp.TWPContext" %>
<%@ variable name-given="tp" variable-class="solocompany.app.twp.TweetPrivate" scope="AT_END" %>
<%
    TWPContext tc = (TWPContext) request.getAttribute("tc");
    if (tc == null) {
        tc = new TWPContext();
        tc.setRequest(request);
        tc.setServletContext(application);
        request.setAttribute("tc", tc);
    }
    if (tc.getCurrentToken() != null) {
        jspContext.setAttribute("tp", tc.getCurrentToken().getTweetPrivate());
    }
%>
