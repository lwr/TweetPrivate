/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.app.twp;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import solocompany.var.VarObject;
import solocompany.var.Variant;

import java.io.*;
import java.util.*;

/**
 * TweetPrivateTest.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class TweetPrivateTest {

    TweetPrivate tp = new AccessTokenManager().getMyToken().getTweetPrivate();
    PrintStream out = System.out;


    @Before
    public void setUp() throws Exception {
        tp.saveData = true;
    }


    @Test
    @Ignore
    public void downloadDirectMessages() throws Exception {
        tp.downloadDirectMessages(out);
        dumpDirectMessagesStats();
    }


    @Test
    @Ignore
    public void updateProfile() throws Exception {
        tp.updateProfile();
        dumpProfile();
    }


    @Test
    @Ignore
    public void dumpProfile() throws Exception {
        out.println(tp.getProfile().getDebugInfo());
    }


    @Test
    @Ignore
    public void dumpDirectMessagesInboxOneRecord() throws Exception {
        tp.loadData();
        out.println(tp.data.get("inbox").get(0).getDebugInfo());
    }


    @Test
    @Ignore
    public void dumpDirectMessagesOutboxOneRecord() throws Exception {
        tp.loadData();
        out.println(tp.data.get("outbox").get(0).getDebugInfo());
    }


    @Test
    @Ignore
    public void dumpDirectMessagesStats() throws Exception {
        VarObject stats = tp.getConversationStats();
        String format = "%11s  %-16s  %7s  %8s  %s";
        out.println(String.format(format, "id", "screen_name", "inCount", "outCount", "name"));
        for (Map.Entry<String, Variant> e : stats.entrySet()) {
            Variant item = e.getValue();
            out.print(String.format(format,
                    e.getKey(),
                    item.getString("screen_name"),
                    item.getInt("inCount"),
                    item.getInt("outCount"),
                    item.getString("name")
            ));
            out.println();
        }
    }


    @Test
    @Ignore
    public void dumpDirectMessages() throws Exception {
        Map.Entry<String, Variant> e = tp.getConversationStats().entrySet().iterator().next();
        long id = Long.parseLong(e.getKey());
        System.out.println("Conversion with " + e.getValue().get("name") + " @" + e.getValue().get("screen_name"));
        System.out.println();
        displayConversation(id);
    }


    private String formatDate(long d) {
        return new java.sql.Timestamp(d).toString();
    }


    private void displayConversation(long id) throws Exception {
        for (Variant record : tp.getConversation(id).asList()) {
            out.println(formatDate(record.getLong("date")) + " - " + record.get("type"));
            out.println("  " + record.get("text"));
        }
    }
}
