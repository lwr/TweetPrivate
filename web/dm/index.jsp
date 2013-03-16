<%--
  ~ Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
  --%>
<%@ page contentType="text/html;charset=UTF-8" language="java"
         import="solocompany.app.twp.TweetPrivate,
                 solocompany.var.VarArray,
                 solocompany.var.VarObject,
                 solocompany.var.Variant,
                 java.io.*,
                 java.util.*"
        %>
<html>
<head>
    <title>TweetPrivate PProject - Direct Messages</title>
</head>
<body>
<%!
    private String formatDate(long d) {
        return new java.sql.Timestamp(d).toString();
    }


    public void displayConversationStats(VarObject stats, JspWriter out) throws IOException {
        String format = "%11s  %-16s  %7s  %8s  %s";
        out.println(String.format(format, "id", "screen_name", "inCount", "outCount", "name"));
        for (Map.Entry<String, Variant> e : stats.entrySet()) {
            Variant item = e.getValue();
            out.print("<a href=\'?id=");
            out.print(e.getKey());
            out.print("\'>");
            out.print(String.format(format,
                    e.getKey(),
                    item.getString("screen_name"),
                    item.getInt("inCount"),
                    item.getInt("outCount"),
                    item.getString("name")
            ));
            out.print("</a>");
            out.println();
        }
    }


    private void displayConversation(VarArray a, JspWriter out) throws IOException {
        for (Variant record : a.asList()) {
            out.println(formatDate(record.getLong("date")) + " - " + record.get("sender"));
            out.println("  " + record.get("text"));
        }
    }


    private void run(HttpServletRequest request, JspWriter out) throws IOException {
        TweetPrivate tp = new TweetPrivate(null);
        if (request.getParameter("id") == null || request.getParameter("id").isEmpty()) {
            displayConversationStats(tp.getConversationStats(), out);
        } else {
            out.print("<a href=\'?id=\'>Back</a>");
            out.println();
            long id;
            try {
                id = Long.parseLong(request.getParameter("id"));
            } catch (NumberFormatException e) {
                out.println(e);
                return;
            }
            displayConversation(tp.getConversation(id), out);
        }
    }
%>
<pre><% run(request, out); %></pre>
</body>
</html>