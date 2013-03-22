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

    boolean saveData = false;
    boolean dataLoaded = false;
    VarObject data = new VarObject();


    public TweetPrivate(LightweightTwitterAPI api) {
        this.api = api;
    }


    private File getDataFile() throws IOException {
        return new File(System.getProperty("user.home"), ".TwitterData/" + getUserId() + ".json");
    }


    public long getUserId() throws IOException {
        if (!data.containsKey("profile")) {
            // try to retrieve id from token
            String token = api.getAccessToken();
            int i = token.indexOf('-');
            if (i > 0) {
                try {
                    return Long.parseLong(token.substring(0, i));
                } catch (NumberFormatException e) {
                    // ignore;
                }
            }
            updateProfile();
        }
        return data.get("profile").getLong("id");
    }


    public void updateProfile() throws IOException {
        data.put("profile", new JSONParser().parseJson(api.jsonAPI("1.1/account/verify_credentials", "")).getMap());
        saveData();
    }


    public VarObject getProfile() throws IOException {
        if (!loadData("profile").isObjectType()) {
            updateProfile();
        }
        return data.get("profile").getMap();
    }


    private Variant loadData(String key) throws IOException {
        if (!data.containsKey(key)) {
            loadData();
        }
        return data.get(key);
    }


    void loadData() throws IOException {
        if (getDataFile().isFile()) {
            saveData = true;
            InputStream in = new FileInputStream(getDataFile());
            try {
                VarObject map = new JSONParser().parseJson(in).getMap();
                map.putAll(data);
                data = map;
            } finally {
                in.close();
            }
        }
        dataLoaded = true;
    }


    void saveData() throws IOException {
        if (!dataLoaded) {
            loadData();
        }
        if (saveData) {
            System.out.println("Writing to: " + getDataFile());
            Writer os = new OutputStreamWriter(new FileOutputStream(getDataFile()), "UTF-8");
            try {
                new JSONWriter(os).writeVariant(data);
            } finally {
                os.close();
            }
        }
    }


    public void downloadDirectMessages(PrintStream out) throws Exception {
        data.put("inbox", fetchList("1.1/direct_messages", out));
        data.put("outbox", fetchList("1.1/direct_messages/sent", out));
        saveData();
    }


    private VarArray fetchList(String apiPrefix, PrintStream out) throws IOException {
        VarArray result = new VarArray();
        long nextId = -1;
        while (true) {
            String url = apiPrefix + "?count=200";
            if (nextId > 0) {
                url = url + "&" + "max_id=" + nextId;
            }

            out.println("Fetching data from twitter api: " + url);
            VarArray a = new JSONParser().parseArray(api.jsonAPI(url, ""));
            if (a.isEmpty()) {
                break;
            }
            result.addAll(a);
            nextId = a.get(a.size() - 1).getLong("id") - 1;
        }
        return result;
    }


    public VarObject getConversationStats() throws IOException {
        VarObject m = new VarObject();
        for (Variant item : loadData("inbox").asList()) {
            String id = item.get("sender_id").toString();
            VarObject stat = (VarObject) m.getOrNull(id);
            if (stat == null) {
                stat = initialStat(item.get("sender"));
                m.put(id, stat);
            }
            updateLast(stat, item, "inCount", "inLast");
        }

        for (Variant item : loadData("outbox").asList()) {
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

        for (Variant item : loadData("inbox").asList()) {
            if (item.getLong("sender_id") != id) {
                continue;
            }
            VarObject record = new VarObject();
            record.put("type", "in");
            record.put("date", parseDate(item.getString("created_at")));
            record.put("text", item.get("text"));
            a.add(record);
        }

        for (Variant item : loadData("outbox").asList()) {
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
