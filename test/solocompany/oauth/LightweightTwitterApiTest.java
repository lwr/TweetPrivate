/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.oauth;

import org.junit.Ignore;
import org.junit.Test;
import solocompany.json.JSONParser;
import solocompany.utils.URLUtils;
import solocompany.var.Variant;

import java.io.*;
import java.lang.reflect.Field;


// CS:AllowPrintln

/**
 * LightweightTwitterApiTest.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class LightweightTwitterApiTest {


    protected LightweightTwitterAPI api;


    public LightweightTwitterApiTest() {
        String packageName = LightweightTwitterApiTest.class.getPackage().getName();
        try {
            Field apiField = Class.forName(packageName + ".MyTwitterAPI").getDeclaredField("OAUTH");
            this.api = new LightweightTwitterAPI((OAuthTool) apiField.get(null));
        } catch (Exception e) {
            throw new RuntimeException(""
                    + "Twitter api keys not found"
                    + "\n"
                    + "==== To make this simple test worked, you should defined your api like this: ====\n"
                    + "package " + packageName + ";\n"
                    + "\n"
                    + "public class MyTwitterAPI {\n"
                    + "\n"
                    + "    public static OAuthTool OAUTH = OAuthTool(\n"
                    + "            consumerKey,        // Your twitter app key\n"
                    + "            consumerSecret,     // Your twitter app secret\n"
                    + "            accessToken,        // Your twitter access token\n"
                    + "            accessSecret        // your twitter access secret\n"
                    + "    );\n"
                    + "}\n"
                    + "", e);
        }
    }


    public String jsonInvokeFMT(String type, String body) throws IOException {
        return jsonInvoke(type, body).getDebugInfo();
    }


    public Variant jsonInvoke(String type, String body) throws IOException {
        return new JSONParser().parseJson(api.jsonAPI(type, body));
    }


    @Ignore
    @Test
    public void testAPI10_LowLevel() throws Exception {
        System.out.println("1/help/test 1 : " + api.invokeAPI("1/help/test.json",         /**/ ""));
        System.out.println("1/help/test 2 : " + api.invokeAPI("1/help/test.json?foo=123", /**/ ""));
        System.out.println("1/help/test 3 : " + api.invokeAPI("1/help/test.json",         /**/ "bar=456"));
        System.out.println("1/help/test 4 : " + api.invokeAPI("1/help/test.json?foo=123", /**/ "bar=456"));
    }


    @Ignore
    @Test
    public void testAPI11_LowLevel() throws Exception {
        System.out.println("1.1/configuration : " + api.invokeAPI("1.1/help/configuration.json",     /**/ ""));
        System.out.println("1.1/languages     : " + api.invokeAPI("1.1/help/languages.json?foo=123", /**/ ""));
        System.out.println("1.1/privacy       : " + api.invokeAPI("1.1/help/privacy.json",           /**/ "bar=456"));
        System.out.println("1.1/tos           : " + api.invokeAPI("1.1/help/tos.json?foo=123",       /**/ "bar=456"));
    }


    @Ignore
    @Test
    public void testAPI10_HighLevel() throws Exception {
        System.out.println("1/help/test 1 : " + jsonInvoke("1/help/test",         /**/ ""));
        System.out.println("1/help/test 2 : " + jsonInvoke("1/help/test?foo=123", /**/ ""));
        System.out.println("1/help/test 3 : " + jsonInvoke("1/help/test",         /**/ "bar=456"));
        System.out.println("1/help/test 4 : " + jsonInvoke("1/help/test?foo=123", /**/ "bar=456"));
    }


    @Ignore
    @Test
    public void testAPI11_HighLevel() throws Exception {
        System.out.println("== 1.1/configuration ==\n" + jsonInvokeFMT("1.1/help/configuration",     /**/ ""));
        System.out.println("== 1.1/languages     ==\n" + jsonInvokeFMT("1.1/help/languages?foo=123", /**/ ""));
        System.out.println("== 1.1/privacy       ==\n" + jsonInvokeFMT("1.1/help/privacy",           /**/ "bar=456"));
        System.out.println("== 1.1/tos           ==\n" + jsonInvokeFMT("1.1/help/tos?foo=123",       /**/ "bar=456"));
    }


    @Ignore
    @Test
    public void firstPost() throws Exception {
        System.out.println("Tweet result: " + newTweet("Hello world!"));
    }


    public String newTweet(String msg) throws IOException {
        return jsonInvokeFMT("1.1/statuses/update", "status=" + URLUtils.simpleEncode(msg));
    }
}
