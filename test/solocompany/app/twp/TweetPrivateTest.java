/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.app.twp;

import org.junit.Ignore;
import org.junit.Test;
import solocompany.oauth.LightweightTwitterApiTest;

/**
 * TweetPrivateTest.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class TweetPrivateTest extends LightweightTwitterApiTest {


    @Test
    @Ignore
    public void downloadDirectMessages() throws Exception {
        new TweetPrivate(api).downloadDirectMessages();
    }
}
