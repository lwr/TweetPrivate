/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.utils;

import java.util.*;

public final class URLUtils {

    private URLUtils() {}


    /**
     * 把URL格式查询字符串解析为KV对.
     * 如果有多值只保留第一个.
     * <p/>
     * <b>注意:</b> 所有返回值均未经decode
     *
     * @param qs 输入，要解析的字符串
     * @param m  用于接收解析结果的KV对.
     */
    public static void parseParameters(String qs, Map<? super String, ? super String> m) {
        String[] ss = qs.split("&");
        for (int i = ss.length - 1; i >= 0; i--) { // 反向遍历，使得先出现的值优先
            String s = ss[i];
            if (s != null && s.length() > 0) {
                int j = s.indexOf('=');
                if (j >= 0) {
                    m.put(s.substring(0, j), s.substring(j + 1));
                } else {
                    m.put(s, "");
                }
            }
        }
    }

    private static final String[] SPECIAL_CHARS = new String[0x80];

    static {
        for (int c = 0; c < SPECIAL_CHARS.length; c++) {
            if (c < 0x10) {
                SPECIAL_CHARS[c] = "%0" + Integer.toHexString(c).toUpperCase();
            } else if (c >= '0' && c <= '9') { // SAFE: digits
            } else if (c >= 'A' && c <= 'Z') { // SAFE: upper alphas
            } else if (c >= 'a' && c <= 'z') { // SAFE: lower alphas
            } else if (c == '_' || c == '-' || c == '.' || c == '*' || c == '@') { // special characters
            } else { // assert (c >= 0x10) && (c <= 0xFF)
                SPECIAL_CHARS[c] = "%" + Integer.toHexString(c).toUpperCase();
            }
        }
        // SPECIAL_CHARS[' '] = "+";
    }

    /**
     * Simply translates a string into <code>application/x-www-form-urlencoded
     * </code> format. Unlike the implementation in {@link java.net.URLEncoder},
     * this implementation keep all unicode characters unchanged.
     *
     * @see java.net.URLEncoder#encode(String, String)
     */
    public static String simpleEncode(String s) {
        StringBuilder sb = null;
        for (int i = 0; i < s.length(); i++) {
            char c0 = s.charAt(i);
            if (c0 >= SPECIAL_CHARS.length) {
                if (sb != null) {
                    sb.append(c0);
                }
            } else {
                String c1 = SPECIAL_CHARS[c0];
                if (c1 != null) {
                    if (sb == null) {
                        sb = new StringBuilder(s.length() + c1.length());
                        sb.append(s.substring(0, i));
                    }
                    sb.append(c1);
                } else {
                    if (sb != null) {
                        sb.append(c0);
                    }
                }
            }
        }
        return (sb != null) ? sb.toString() : s;
    }
}
