package com.unclezs.novel.core.analyzer;

import com.unclezs.novel.core.request.PhantomJs;
import com.unclezs.novel.core.utils.StringUtil;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

/**
 * @author blog.unclezs.com
 * @since 2020/12/24 11:35
 */
public class PhantomJsTest {
    private static String exePath = "/Users/zhanghongguo/coder/uncle-novel/novel-analyzer/lib/phantomjs";
    private static String jsBasePath = "/Users/zhanghongguo/coder/uncle-novel/novel-analyzer/lib/%s.js";
    public static final String URL = "http://httpbin.org/get";

    @Test
    public void testArguments() throws IOException, URISyntaxException {
        System.out.println(PhantomJs.content(URL));
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println(PhantomJs.class.getResource("/script/spider.js").toURI().toString());
    }

    // 调用phantomjs程序，并传入js文件，并通过流拿回需要的数据。
    public static String html(String url, String userAgent, String cookie, String referer) throws IOException {
        return executePhantomJs("spider", url);
    }

    /**
     * 执行phantomJS
     *
     * @param script 脚本名称
     * @param args   参数
     */
    public static String executePhantomJs(String script, String... args) throws IOException {
        String scriptPath = String.format(jsBasePath, script);
        StringBuilder command = new StringBuilder();
        command.append(exePath).append(StringUtil.EMPTY).append(scriptPath);
        for (String arg : args) {
            command.append(StringUtil.EMPTY).append(arg);
        }
        System.out.println(command);
        Process process = Runtime.getRuntime().exec(command.toString());
        InputStream is = process.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder buffer = new StringBuilder();
        String tmp;
        while ((tmp = br.readLine()) != null) {
            buffer.append(tmp).append(StringUtil.NEW_LINE);
        }
        return buffer.toString();
    }
}
