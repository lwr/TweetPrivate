/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.utils;

import java.io.*;

/**
 * IOUtils.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class IOUtils {

    public static String allToString(InputStream is, String charsetName) throws IOException {
        if (is instanceof ByteArrayInputStream) {
            byte[] bytes = new byte[is.available()];
            //noinspection ResultOfMethodCallIgnored
            is.read(bytes);
            return new String(bytes, charsetName);
        }

        return allToString(new InputStreamReader(is, charsetName));
    }


    public static String allToString(Reader in) throws IOException {
        StringBuilder buf = new StringBuilder();
        int size = 16384;
        char[] str = new char[size];
        int n;
        while ((n = in.read(str, 0, size)) > 0) {
            buf.append(str, 0, n);
        }
        return buf.toString();
    }


    public static void allBytes(InputStream is, OutputStream os) throws IOException {
        int size = 16384;
        byte[] b = new byte[size];
        int n;
        while ((n = is.read(b, 0, size)) > 0) {
            os.write(b, 0, n);
        }
    }
}
