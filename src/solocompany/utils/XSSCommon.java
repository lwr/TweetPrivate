/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.utils;

/**
 * 某些针对 XSS 的处理工具类.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class XSSCommon {

    private XSSCommon() {
    }


    /**
     * 忽略控制字符后, 判断是否以某字符串开始.
     * <p/>
     * 注: 忽略控制字符是为了对付安全漏洞
     *
     * @param source 源字符串
     * @param prefix 要匹配的前缀字符串
     * @return 如果测试成功, 返回 true, 否则, 返回 false;
     */
    public static boolean startsWithIgnoreCase(String source, String prefix) {
        return findPrefixEndIgnoreCase(source, 0, prefix) >= 0;
    }


    /**
     * 忽略控制字符后, 判断是否以某字符串开始.
     * <p/>
     * 注: 忽略控制字符是为了对付安全漏洞
     *
     * @param source       源字符串
     * @param sourceOffset 源字符串的起始位置
     * @param prefix       要匹配的前缀字符串
     * @return 如果测试成功, 返回 true, 否则, 返回 false;
     */
    public static boolean startsWithIgnoreCase(String source, int sourceOffset, String prefix) {
        return findPrefixEndIgnoreCase(source, sourceOffset, prefix) >= 0;
    }


    /**
     * 忽略控制字符后, 判断是否以某字符串开始, 并返回匹配后的截取部分.
     * <p/>
     * 注: 忽略控制字符是为了对付IE的安全漏洞
     *
     * @param source 源字符串
     * @param prefix 要匹配的前缀字符串
     * @return 如果测试成功, 返回截取后的字符串; 否则, 返回 null;
     */
    public static String substringAfterIgnoreCase(String source, String prefix) {
        int offset = findPrefixEndIgnoreCase(source, 0, prefix);
        return (offset == -1) ? null : source.substring(offset);
    }


    private static int findPrefixEndIgnoreCase(String source, int sourceOffset, String prefix) {
        int sourceLength = source.length();
        int targetLength = prefix.length();

        if (sourceLength < targetLength) {
            return -1;
        }

        int targetOffset = 0;
        char targetChar = Character.toUpperCase(prefix.charAt(targetOffset));

        for (; sourceOffset < sourceLength; sourceOffset++) {
            char c = source.charAt(sourceOffset);
            if (c < ' ') {
                // 忽略控制字符
                continue;
            }

            if (Character.toUpperCase(c) != targetChar) {
                break;
            }

            targetOffset++;
            if (targetOffset == targetLength) {
                return sourceOffset + 1;
            }

            targetChar = Character.toUpperCase(prefix.charAt(targetOffset));
        }
        return -1;
    }
}
