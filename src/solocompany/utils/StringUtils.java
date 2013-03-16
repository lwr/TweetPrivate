/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.utils;

/**
 * StringUtils.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class StringUtils {


    private StringUtils() {}


    public static boolean startsWithIgnoreCase(String s1, int offset, String s2) {
        return s1.regionMatches(true, offset, s2, 0, s2.length());
    }
}
