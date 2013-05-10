/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.json;

import org.junit.Assert;
import org.junit.Test;
import solocompany.var.VarObject;

import java.net.*;

/**
 * Test JSONWriter.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class JSONWriterTest {


    @Test
    public void testSpecials() throws Exception {
        String s = "";
        for (int ch = 0; ch <= 'z'; ch++) {
            s += (char) ch;
        }

        String afterSpace = ""
                + " !\"#$%&\\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\\\]^_`abcdefghijklmnopqrstuvwxyz";

        StringBuilder sb = new StringBuilder();
        JSONWriter scriptWriter = new JSONWriter(sb);
        scriptWriter.setKeepInvalidChar(false);
        scriptWriter.writeString(s);
        Assert.assertEquals("'\\t\\n\\f\\r" + afterSpace + "'",
                sb.toString());

        sb.setLength(0);
        scriptWriter.setKeepInvalidChar(true);
        scriptWriter.writeString(s);
        Assert.assertEquals(""
                + "'\\0\\1\\2\\3\\4\\5\\6\\7\\u0008\\t\\n\\u000b\\f\\r"
                + "\\u000e\\u000f\\u0010\\u0011\\u0012\\u0013\\u0014\\u0015\\u0016\\u0017\\u0018\\u0019"
                + "\\u001a\\u001b\\u001c\\u001d\\u001e\\u001f"
                + afterSpace + "'",
                sb.toString());
    }


    @Test
    public void testInlineMode() throws Exception {
        StringBuilder sb = new StringBuilder();
        JSONWriter scriptWriter = new JSONWriter(sb);
        scriptWriter.setInlineMode(true);
        scriptWriter.writeString("123<>\"'&abc");
        Assert.assertEquals("'123\\u003c\\u003e\\u0022\\u0027\\u0026abc'", sb.toString());

        sb.setLength(0);
        scriptWriter.writeVariant(VarObject.newFromKvList("a", 1, "b", 2));
        Assert.assertEquals("{'a':1,'b':2}", sb.toString());
    }


    @Test
    public void testEscapeXml() throws Exception {
        StringBuilder sb = new StringBuilder();
        JSONWriter scriptWriter = new JSONWriter(sb);
        scriptWriter.setEscapeXml(true);
        scriptWriter.writeString("123<>\"'&abc");
        Assert.assertEquals("&#39;123&lt;&gt;&quot;\\&#39;&amp;abc&#39;", sb.toString());

        sb.setLength(0);
        scriptWriter.setUseDoubleQuote(true);
        scriptWriter.writeVariant(VarObject.newFromKvList("a", 1, "b", 2));
        Assert.assertEquals("{&quot;a&quot;:1,&quot;b&quot;:2}", sb.toString().replaceAll("\\s", ""));
    }


    @Test
    public void testUNICODE_SEPERATOR() throws Exception {
        StringBuilder sb = new StringBuilder();
        JSONWriter scriptWriter = new JSONWriter(sb);
        scriptWriter.setInlineMode(true);
        scriptWriter.writeVariant(VarObject.newFromKvList("test", "123\u2028456\u2029"));
        Assert.assertEquals("{'test':'123\\u2028456\\u2029'}", sb.toString());
    }


    @Test
    public void testScriptTag() throws Exception {
        StringBuilder sb = new StringBuilder();
        JSONWriter scriptWriter = new JSONWriter(sb);
        scriptWriter.writeString(""
                + "<SCRIPT>var a = 1;</SCRIPT>\n"
                + "<scRipT>var a = 2;</scRipT>\n"
                + "<scRipT>var a = 3;</scRipT >\n"
                + "");
        Assert.assertEquals(""
                + "'"
                + "<SCRIPT>var a = 1;<'+'/SCRIPT>\\n"
                + "<scRipT>var a = 2;<'+'/scRipT>\\n"
                + "<scRipT>var a = 3;<'+'/scRipT >\\n"
                + "'", sb.toString());
    }


    @Test
    public void testScriptTagWithXSS() throws Exception {
        // 利用 \0 注入 XSS
        String s = URLDecoder.decode(""
                + "%27%29;}}%29%28%29;%3C/sc%00R"
                + "ipt%3E%3CscR" + "ipt%3E"
                + "alert%28" + "1" + "%29;"
                + "%3C/sc%00R" + "ipt%3E"
                + "", "UTF8");
        Assert.assertEquals(""
                + "');}})();"
                + "</sc" + '\0' + "R" + "ipt>"
                + "<scR" + "ipt>alert(1);"
                + "</sc" + '\0' + "R" + "ipt>", s);

        StringBuilder sb = new StringBuilder();
        JSONWriter scriptWriter = new JSONWriter(sb);
        scriptWriter.writeString(s);
        Assert.assertEquals(""
                + "'"
                + "\\');}})();"
                + "<'+'/scR" + "ipt>"
                + "<scR" + "ipt>alert(1);<'+'/scR" + "ipt>"
                + "'"
                + "", sb.toString());
    }
}
