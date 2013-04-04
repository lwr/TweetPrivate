/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.utils;

import org.junit.Ignore;
import org.junit.Test;

import java.io.*;

/**
 * IOUtilsTest.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class IOUtilsTest {

    @Test
    @Ignore
    public void testFileToString() throws Exception {
        File file = new File(System.getProperty("user.home"), ".TwitterData/app_key.properties");
        System.out.println(IOUtils.fileToString(file, "UTF-8"));
    }

}
