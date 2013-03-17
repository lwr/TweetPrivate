/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.app.twp;

import solocompany.commons.collections.MapFieldComparator;
import solocompany.json.JSONParser;
import solocompany.json.JSONWriter;
import solocompany.oauth.LightweightTwitterAPI;
import solocompany.var.VarArray;
import solocompany.var.VarObject;
import solocompany.var.Variant;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * TweetPrivate.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class TweetPrivate {


    protected LightweightTwitterAPI api;


    File inbox = new File(System.getProperty("user.home"), "Downloads/TwitterData/direct messages/inbox.json");
    File outbox = new File(System.getProperty("user.home"), "Downloads/TwitterData/direct messages/outbox.json");


    public TweetPrivate(LightweightTwitterAPI api) {
        this.api = api;
    }


    public void downloadDirectMessages() throws Exception {
        downloadTo("1.1/direct_messages", inbox);
        downloadTo("1.1/direct_messages/sent", outbox);
    }


    public void downloadTo(String apiPrefix, File file) throws IOException {
        if (!file.getParentFile().isDirectory() && !file.getParentFile().mkdirs()) {
            throw new IOException("Make dir failed: " + file.getParent());
        }

        VarArray result = new VarArray();
        long nextId = -1;
        while (true) {
            String url = apiPrefix + "?count=200";
            if (nextId > 0) {
                url = url + "&" + "max_id=" + nextId;
            }

            System.out.println("Fetching data from twitter api: " + url);
            VarArray a = new JSONParser().parseArray(api.jsonAPI(url, ""));
            if (a.isEmpty()) {
                break;
            }
            result.addAll(a);
            nextId = a.get(a.size() - 1).getLong("id") - 1;
        }

        System.out.println("Writing to: " + file);
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        try {
            new JSONWriter(writer).writeVariant(result);
        } finally {
            writer.close();
        }
    }


    public Variant getMyInfo() throws IOException {
        return new JSONParser().parseJson(new FileInputStream(inbox)).get(0).get("recipient");
    }


    public VarObject getConversationStats() throws IOException {
        VarObject m = new VarObject();
        for (Variant item : new JSONParser().parseJson(new FileInputStream(inbox)).asList()) {
            String id = item.get("sender_id").toString();
            VarObject stat = (VarObject) m.getOrNull(id);
            if (stat == null) {
                stat = initialStat(item.get("sender"));
                m.put(id, stat);
            }
            updateLast(stat, item, "inCount", "inLast");
        }

        for (Variant item : new JSONParser().parseJson(new FileInputStream(outbox)).asList()) {
            String id = item.get("recipient_id").toString();
            VarObject stat = (VarObject) m.getOrNull(id);
            if (stat == null) {
                stat = initialStat(item.get("recipient"));
                m.put(id, stat);
            }
            updateLast(stat, item, "outCount", "outLast");
        }
        return m;
    }

    private VarObject initialStat(Variant recipient) {
        VarObject stat = recipient.getMap().copyMap();
        stat.put("inCount", 0);
        stat.put("outCount", 0);
        return stat;
    }


    private void updateLast(VarObject stat, Variant item, String keyForCount, String keyForDate) {
        stat.put(keyForCount, stat.getInt(keyForCount) + 1);
        stat.put(keyForDate, Math.max(stat.getLong(keyForDate), parseDate(item.getString("created_at"))));
    }


    public VarArray getConversation(long id) throws IOException {
        List<Map<String, Object>> list = VarArray.newNormalizeList();
        VarArray a = (VarArray) Variant.wrap(list);

        for (Variant item : new JSONParser().parseJson(new FileInputStream(inbox)).asList()) {
            if (item.getLong("sender_id") != id) {
                continue;
            }
            VarObject record = new VarObject();
            record.put("type", "in");
            record.put("date", parseDate(item.getString("created_at")));
            record.put("text", item.get("text"));
            a.add(record);
        }

        for (Variant item : new JSONParser().parseJson(new FileInputStream(outbox)).asList()) {
            if (item.getLong("recipient_id") != id) {
                continue;
            }
            VarObject record = new VarObject();
            record.put("type", "out");
            record.put("date", parseDate(item.getString("created_at")));
            record.put("text", item.get("text"));
            a.add(record);
        }

        Collections.sort(list, new MapFieldComparator<String, Object>("date"));
        return a;
    }


    DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yy" + "yy", Locale.ENGLISH);

    private long parseDate(String d) {
        try {
            return dateFormat.parse(d).getTime();
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date: " + d);
        }
    }
}
