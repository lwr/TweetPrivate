/*
 * Copyright (c) 2012 Mailtech.cn, Ltd. All Rights Reserved.
 */

package solocompany.json;

import org.junit.Test;

import java.io.*;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * JSONParserTest.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class JSONParserTest {

    @Test
    public void testParseStringSimplest() throws Exception {
        assertEquals(null, new JSONParser().parseJson("null").normalize());
        assertEquals(Collections.emptyMap(), new JSONParser().parseJson("{}").normalize());
        assertEquals(Collections.emptyList(), new JSONParser().parseJson("[]").normalize());

        try {
            new JSONParser().parseJson("");
            fail("JSONException not thrown");
        } catch (JSONException e) {
            assertEquals("Missing value at 0 [character 1 line 1]", e.getMessage());
        }
    }


    @Test
    public void testParseWithStream1() throws Exception {
        String s1 = ""
                + "{\n"
                + "    \"expand\": \"widgets\",\n"
                + "    \"self\": \"http://www.example.com/jira/rest/api/resource/KEY-1\",\n"
                + "    \"widgets\": {\n"
                + "        \"widgets\": [],\n"
                + "        \"size\": 5\n"
                + "    },"
                + "    foo : '中文'\n"
                + "}"
                + "";

        @SuppressWarnings("unchecked") List<Map<String, ?>> mList = (List) Arrays.asList(
                new JSONParser().parseJson(s1).normalize(),
                new JSONParser().parseObject(s1).normalize(),
                new JSONParser().parseJson(new StringReader(s1)).normalize(),
                new JSONParser().parseJson(new ByteArrayInputStream(s1.getBytes("UTF-8"))).normalize()
        );

        for (Map<String, ?> m : mList) {
            assertEquals(4, m.size());
            assertEquals("widgets", m.get("expand"));
            assertEquals("http://www.example.com/jira/rest/api/resource/KEY-1", m.get("self"));
            assertEquals(2, ((Map) m.get("widgets")).size());
            assertEquals(Collections.emptyList(), ((Map) m.get("widgets")).get("widgets"));
            assertEquals(5, ((Map) m.get("widgets")).get("size"));
            assertEquals("中文", m.get("foo"));
        }
    }


    @Test
    public void testParseWithStream2() throws Exception {
        String s2 = ""
                + "[\n"
                + "    'foo' , '中文'\n"
                + "]"
                + "";

        @SuppressWarnings("unchecked") List<List<String>> arrList = (List) Arrays.asList(
                new JSONParser().parseJson(s2).normalize(),
                new JSONParser().parseArray(s2).normalize(),
                new JSONParser().parseJson(new StringReader(s2)).normalize(),
                new JSONParser().parseJson(new ByteArrayInputStream(s2.getBytes("UTF-8"))).normalize()
        );

        for (List<String> arr : arrList) {
            assertEquals("foo", arr.get(0));
            assertEquals("中文", arr.get(1));
        }
    }
}
