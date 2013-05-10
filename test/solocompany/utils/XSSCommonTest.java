/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * XSSCommonTest.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class XSSCommonTest {

    @Test
    public void testSubstringAfterIgnoreCase() {
        assertEquals("Bc", XSSCommon.substringAfterIgnoreCase("aBc", "A"));
        assertEquals("", XSSCommon.substringAfterIgnoreCase("abc", "ABC"));
        assertEquals("", XSSCommon.substringAfterIgnoreCase("a\tb\tc", "ABC"));
        assertEquals("d", XSSCommon.substringAfterIgnoreCase("a\tb\n\rcd", "ABC"));
        assertEquals(null, XSSCommon.substringAfterIgnoreCase("abc", "C"));
    }


    @Test
    public void testStartsWithIgnoreCase() {
        assertEquals(true, XSSCommon.startsWithIgnoreCase("aBc", "A"));
        assertEquals(true, XSSCommon.startsWithIgnoreCase("aBc", 0, "A"));
        assertEquals(true, XSSCommon.startsWithIgnoreCase("|aBc|", 1, "A"));
        assertEquals(true, XSSCommon.startsWithIgnoreCase("abc", "ABC"));
        assertEquals(true, XSSCommon.startsWithIgnoreCase("abc", 0, "ABC"));
        assertEquals(true, XSSCommon.startsWithIgnoreCase("|abc|", 1, "ABC"));
        assertEquals(true, XSSCommon.startsWithIgnoreCase("a\tb\tc", "ABC"));
        assertEquals(true, XSSCommon.startsWithIgnoreCase("a\tb\tc", 0, "ABC"));
        assertEquals(true, XSSCommon.startsWithIgnoreCase("|a\tb\tc|", 1, "ABC"));
        assertEquals(true, XSSCommon.startsWithIgnoreCase("a\tb\n\rcd", "ABC"));
        assertEquals(true, XSSCommon.startsWithIgnoreCase("a\tb\n\rcd", 0, "ABC"));
        assertEquals(true, XSSCommon.startsWithIgnoreCase("|a\tb\n\rcd|", 1, "ABC"));
        assertEquals(false, XSSCommon.startsWithIgnoreCase("abc", "C"));
        assertEquals(false, XSSCommon.startsWithIgnoreCase("abc", 0, "C"));
        assertEquals(false, XSSCommon.startsWithIgnoreCase("|abc|", 1, "C"));
        assertEquals(true, XSSCommon.startsWithIgnoreCase("abc", 2, "C"));
        assertEquals(true, XSSCommon.startsWithIgnoreCase("|abc|", 3, "C"));
    }


}
