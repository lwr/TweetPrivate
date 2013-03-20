/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * HexTest.
 *
 * @author <a href="mailto:lwr@coremail.cn">William Leung</a>
 */
public class HexTest {

    @Test
    public void testGeneral() {
        int numRuns = 10;
        int numBytes = 1000;
        java.util.Random rnd = new java.util.Random();
        for (int i = 0; i < numRuns; i++) {
            for (int j = 0; j < numBytes; j++) {
                byte[] src = new byte[j];
                rnd.nextBytes(src);
                Assert.assertTrue(Arrays.equals(src, Hex.hexToBytes(Hex.bytesToHexL(src))));
                Assert.assertTrue(Arrays.equals(src, Hex.hexToBytes(Hex.bytesToHexU(src))));
            }
        }
    }
}
