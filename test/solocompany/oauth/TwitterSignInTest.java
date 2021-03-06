/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.oauth;

import org.junit.Ignore;
import org.junit.Test;
import solocompany.json.JSONParser;
import solocompany.utils.URLUtils;
import solocompany.var.VarObject;

import java.io.*;

/**
 * TwitterSignInTest.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class TwitterSignInTest {

    public static void main(String[] args) throws IOException {
        TwitterConfig config = TwitterConfig.getInstance();
        LightweightTwitterAPI api = config.getAPI("", "");

        // 第一步，通过 request_token 获取临时 token，回掉 oob 或者是定制 url （如果是定制 url 需要授权域名）
        VarObject m1 = new VarObject();
        URLUtils.parseParameters(api.invokeAPI("oauth/request_token", "", "oauth_callback=oob"), m1.normalize());
        System.out.println("== auth/request_token ==\n" + m1.getDebugInfo());

        // 第二步，访问 Twitter API 官方网站获取授权
        //         如果是 oob 方式，则显示一个授权校验码
        //         如果是 url 方式，则直接回掉 url 同时传递校验码参数
        //                url?oauth_token=***&oauth_verifier=***
        System.out.println("Please visit this page to finish authorization");
        System.out.println("https://api.twitter.com/oauth/authenticate?oauth_token=" + m1.getString("oauth_token"));

        System.out.println("Please enter the PIN: ");
        String pin = new BufferedReader(new InputStreamReader(System.in, "ISO-8859-1")).readLine();
        System.out.println("The PIN is " + pin);

        // 第三步，通过 access_token 获取真正的授权 token 及 secret
        api = config.getAPI(m1.getString("oauth_token"), m1.getString("oauth_token_secret"));
        VarObject m2 = new VarObject();
        URLUtils.parseParameters(api.invokeAPI("oauth/access_token", "oauth_verifier=" + pin), m2.normalize());
        System.out.println("== oauth/access_token ==\n" + m2.getDebugInfo());

        // 最后，校验得到的 token 和 secret 是否可用
        api = config.getAPI(m2.getString("oauth_token"), m2.getString("oauth_token_secret"));
        System.out.println("== 1.1/account/verify_credentials ==\n"
                + new JSONParser().parseJson(api.jsonAPI("1.1/account/verify_credentials", "")).getDebugInfo());

    }


    @Ignore
    @Test
    public void testAuthCallback() throws Exception {
        TwitterConfig config = TwitterConfig.getInstance();
        LightweightTwitterAPI api = config.getAPI("", "");

        // 第一步，通过 request_token 获取临时 token，定制 url
        VarObject m1 = new VarObject();
        URLUtils.parseParameters(api.invokeAPI("oauth/request_token", "", "oauth_callback="
                + URLUtils.simpleEncode("http://127.0.0.1/auth/twitter/callback")), m1.normalize());
        System.out.println("== auth/request_token ==\n" + m1.getDebugInfo());

        // 第二步，访问 Twitter API 官方网站获取授权
        //         如果是 oob 方式，则显示一个授权校验码
        //         如果是 url 方式，则直接回掉 url 同时传递校验码参数
        //                url?oauth_token=***&oauth_verifier=***
        System.out.println("Please visit this page to finish authorization");
        System.out.println("https://api.twitter.com/oauth/authenticate?oauth_token=" + m1.getString("oauth_token"));

    }
}
