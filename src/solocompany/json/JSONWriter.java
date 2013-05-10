/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.json;

import solocompany.utils.XSSCommon;
import solocompany.var.Variant;
import solocompany.var.VariantWriter;

import java.io.*;
import java.util.*;

/**
 * JSONWriter.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class JSONWriter extends VariantWriter {

    /**
     * 是否 inline 方式.
     * <p/>
     * 例如 <code>&lt;span onclick="..."&gt;&lt;/span&gt;</code> 中的onclick代码.
     * 当使用 inline 方式时:
     * <ol>
     * <li>不输出额外的回车符号
     * <li>字符串中的HTML符号<code>[ &lt &gt; &amp; " ' ]</code>使用 UNICODE
     * 编码使得可以满足HTML语法
     * </ol>
     * 字符串的引号不特殊处理, 因此HTML代码应当使用相反的引号定义属性以避免冲突.
     * 比如, 缺省情况下使用单引号, HTML代码就应当使用双引号来定义属性.
     */
    private boolean inlineMode;

    /**
     * 是否使用双引号 " 引用字符串.
     * <p/>
     * 缺省使用单引号 ' 来引用字符串, 除非设置了此属性为 <code>true</code>.
     */
    private boolean useDoubleQuote;


    private boolean compliantMode;


    private boolean escapeXml;


    public JSONWriter(Appendable out) {
        super(out);
    }


    public boolean isInlineMode() {
        return inlineMode;
    }

    public void setInlineMode(boolean inlineMode) {
        this.inlineMode = inlineMode;
    }


    public boolean isUseDoubleQuote() {
        return useDoubleQuote;
    }

    public void setUseDoubleQuote(boolean useDoubleQuote) {
        this.useDoubleQuote = useDoubleQuote;
    }

    public boolean isCompliantMode() {
        return compliantMode;
    }

    public void setCompliantMode(boolean compliantMode) {
        this.compliantMode = compliantMode;
    }

    public boolean isEscapeXml() {
        return escapeXml;
    }

    public void setEscapeXml(boolean escapeXml) {
        this.escapeXml = escapeXml;
    }

    /**
     * Write a statement witch declares a variable.
     * <BlockQuote><code>
     * var variable [ = value ];
     * </code></BlockQuote>
     */
    @SuppressWarnings({"JavaDoc"})
    public void writeVarStatement(String name, Variant value) throws IOException {
        out.append("var ").append(name).append("=");
        writeVariant(value);
        out.append(';');
        writeln();
    }


    private void writeln() throws IOException {
        if (!inlineMode) {
            out.append('\n');
        }
    }


    public void writeVariant(Variant var) throws IOException {
        if (var.isStringType()) {
            writeString(var.getString());

        } else if (var.isObjectType()) {
            out.append('{');
            writeln();
            boolean first = true;
            for (Map.Entry<String, Variant> e : var.varObject().entrySet()) {
                if (first) {
                    first = false;
                } else {
                    out.append(',');
                    writeln();
                }
                writeString(e.getKey());
                out.append(':');
                writeVariant(e.getValue());
            }
            out.append('}');

        } else if (var.isArrayType()) {
            out.append('[');
            boolean first = true;
            for (Variant item : var.varList()) {
                if (first) {
                    first = false;
                } else {
                    out.append(',');
                    writeln();
                }
                writeVariant(item);
            }
            out.append(']');

        } else if (var.isPrimaryType()) {
            out.append(var.toString());

        } else if (var.isNull()) {
            out.append("null");

        } else {
            throw new RuntimeException("Ignoring unsupported var: " + var.getDebugInfo());
        }
    }


    public void writeMap(Map<String, ?> var) throws IOException {
        out.append('{');
        writeln();
        boolean first = true;
        for (Map.Entry<String, ?> entry : var.entrySet()) {
            if (first) {
                first = false;
            } else {
                out.append(',');
                writeln();
            }
            writeString(entry.getKey());
            out.append(':');
            writeVariant(Variant.wrap(entry.getValue()));
        }
        out.append('}');
    }


    @SuppressWarnings("ConstantConditions")
    public void writeString(String s) throws IOException {
        char quoteChar = useDoubleQuote ? '"' : '\'';
        writeSpecial(quoteChar);
        for (int i = 0, length = s.length(); i < length; i++) {
            char ch = s.charAt(i);
            switch (ch) {
            case '\\':
                out.append('\\').append('\\');
                continue;
            case '\t':
                out.append('\\').append('t');
                continue;
            case '\r':
                out.append('\\').append('r');
                continue;
            case '\n':
                out.append('\\').append('n');
                continue;
            case '\f':
                out.append('\\').append('f');
                continue;
            case '\'':
                if (inlineMode) {
                    writeCodeRepresentation(ch);
                } else {
                    if (!useDoubleQuote) {
                        out.append('\\');
                    }
                    writeSpecial(ch);
                }
                continue;
            case '"':
                if (inlineMode) {
                    writeCodeRepresentation(ch);
                } else {
                    if (useDoubleQuote) {
                        out.append('\\');
                    }
                    writeSpecial(ch);
                }
                continue;
            case '<':
                // </SCRIPT 序列需要特殊处理为 ... <' + '/SCRIPT ...
                if (!inlineMode && !escapeXml
                        && XSSCommon.startsWithIgnoreCase(s, i + 1, "/SCRIPT")) {
                    out.append('<');
                    out.append(quoteChar);
                    out.append('+');
                    out.append(quoteChar);
                    continue;
                }
                // else fall-through
            case '>': // fall-through
            case '&':
                if (inlineMode) {
                    writeCodeRepresentation(ch);
                } else {
                    writeSpecial(ch);
                }
                continue;
            case 0x2028: // fall-through
            case 0x2029:
                // CM-11522 JSON 编码的 BUG，需要避免在字符串常量中出现 U+2028 和 U+2029 这两个字符
                writeCodeRepresentation(ch);
                continue;
            default:
                if (ch < 0x20) {
                    if (keepInvalidChar) {
                        writeCodeRepresentation(ch);
                    } // else 跳过UD的bug等引起的非法控制字符
                    continue;
                }
                out.append(ch);
            }
        }
        writeSpecial(quoteChar);
    }


    private void writeSpecial(char ch) throws IOException {
        if (escapeXml) {
            switch (ch) {
            case '<':
                out.append("&lt;");
                return;
            case '>':
                out.append("&gt;");
                return;
            case '&':
                out.append("&amp;");
                return;
            case '"':
                out.append("&quot;");
                return;
            case '\'':
                out.append("&#39;");
                return;
            }
        }
        out.append(ch);
    }


    private void writeCodeRepresentation(char ch) throws IOException {
        String s = Integer.toHexString(ch);
        out.append('\\');
        if (ch < 8) { // '\' 0 - '\' 7
            out.append(s);
        } else { // '\' u0008 - '\' uFFFF
            out.append('u');
            out.append("0000", 0, 4 - s.length());
            out.append(s);
        }
    }
}
