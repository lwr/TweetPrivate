/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.utils;

import org.junit.Ignore;
import org.junit.Test;

import javax.net.ssl.HttpsURLConnection;
import java.net.*;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

/**
 * SSLHelperTest.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class SSLHelperTest {

    @Test
    @Ignore
    public void testSelfSignedCert() throws Exception {
        HttpsURLConnection sConn = (HttpsURLConnection) new URL("https://127.0.0.1:8443/favicon.ico").openConnection();
        sConn.setSSLSocketFactory(SSLHelper.getDummySSLSocketFactory());
        sConn.setHostnameVerifier(SSLHelper.getDummyHostnameVerifier());
        sConn.connect();

        assertEquals(200, sConn.getResponseCode());
        assertEquals("OK", sConn.getResponseMessage());
        assertEquals("HTTP/1.1 200 OK", sConn.getHeaderField(null));
        assertEquals("image/x-icon", sConn.getContentType());

        System.out.println(HexDump.dump(
                ByteBuffer.wrap(IOUtils.allBytes(sConn.getInputStream())), 0x2000));
    }
}
