/**
 * Copyright (c) 2008 Mailtech.cn, Ltd. All Rights Reserved.
 */

package solocompany.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Base64Test.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class Base64Test {


    @Test
    public void testGeneral() {
        int numRuns = 10;
        int numBytes = 1000;
        java.util.Random rnd = new java.util.Random();
        for (int i = 0; i < numRuns; i++) {
            for (int j = 0; j < numBytes; j++) {
                byte[] src = new byte[j];
                rnd.nextBytes(src);

                Assert.assertTrue(Arrays.equals(src,
                        Base64.base64ToBytes(Base64.bytesToBase64(src))));
            }
        }
    }
}
