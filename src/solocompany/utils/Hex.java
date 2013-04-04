/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.utils;

/**
 * Static methods for translating hex strings to byte arrays and vice-versa.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class Hex {

    private Hex() {}

    /**
     * Translates the specified byte array into a hex string in upper case
     */
    public static String bytesToHexU(byte[] a) {
        return bytesToHex(a, HEX_DIGITS_UC);

    }

    /**
     * Translates the specified byte array into a hex string in lower case
     */
    public static String bytesToHexL(byte[] a) {
        return bytesToHex(a, HEX_DIGITS_LC);

    }

    private static String bytesToHex(byte[] a, char[] hexDigits) {
        char[] buf = new char[a.length * 2];

        for (int i = 0, j = 0; i < a.length; i++) {
            byte aByte = a[i];


            buf[j++] = hexDigits[(aByte >> 4) & 0xF];
            buf[j++] = hexDigits[aByte & 0xF];
        }

        return new String(buf);
    }


    /**
     * Translates the specified hex string into a byte array.
     *
     * @throws IllegalArgumentException if <tt>s</tt> is not a valid hex
     *                                  string.
     */
    public static byte[] hexToBytes(String s) {
        int l = s.length();

        if ((l & 1) != 0) {
            throw new IllegalArgumentException("String length must be an even number!");
        }

        byte[] a = new byte[l >> 1];

        for (int i = 0, j = 0; i < a.length; i++) {

            int b1 = hexToInt(s.charAt(j++));
            int b2 = hexToInt(s.charAt(j++));

            a[i] = (byte) ((b1 << 4) | b2);
        }

        return a;
    }


    /**
     * Translates the specified hex character to integer.
     *
     * @throws IllegalArgumentException if c is not a standard hex character {0~9|a~f|A~F}.
     */
    private static int hexToInt(char c) {
        if (c >= '0' && c <= '9') {
            return (c - '0');
        } else if (c >= 'A' && c <= 'F') {
            return (c - 'A') + 10;
        } else if (c >= 'a' && c <= 'f') {
            return (c - 'a') + 10;
        } else {
            throw new IllegalArgumentException("Illegal hex character " + c);
        }
    }

    private static final char[] HEX_DIGITS_UC = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    private static final char[] HEX_DIGITS_LC = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };
}
