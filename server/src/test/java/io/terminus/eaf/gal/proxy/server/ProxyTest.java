package io.terminus.eaf.gal.proxy.server;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 * Created by yuhp@terminus.io on 2017/9/20.
 * Desc:
 */
public class ProxyTest {
    @Test
    public void proxy() throws Exception {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 6666));
        URL url = new URL("http://www.baidu.com");
        HttpURLConnection action = (HttpURLConnection) url.openConnection(proxy);  //使用代理打开网页
        InputStream in = action.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String lin = System.getProperty("line.separator");
        for (String temp = br.readLine(); temp != null; temp = br.readLine()) {
            sb.append(temp + lin);
        }
        br.close();
        in.close();
        System.out.println(sb);
    }
}
