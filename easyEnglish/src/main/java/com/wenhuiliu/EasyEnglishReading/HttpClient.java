package com.wenhuiliu.EasyEnglishReading;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
//import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


/**
 * A simple HTTP client that prints out the content of the HTTP response to
 * {@link System#out} to test {@link HttpServer}.
 *
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 */
public class HttpClient {
    //	正则表达式
    private static final String WORD_PATTERN = "<div class=\"trans-container\">.*<div id=\"webTrans\" class=\"trans-wrapper trans-tab\">" ;

    private static final String WORD_ATTR_PATTERN = "<li>(.*)</li>";
    private static final Pattern wordPattern = Pattern.compile(WORD_PATTERN, Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern wordAttrPattern = Pattern.compile(WORD_ATTR_PATTERN);

    //	定义存储网页内容的容器
    StringBuilder body;
    //	定义存储网址的变量
    String url;
    public HttpClient(String url){
        this.url = url;
        this.body = new StringBuilder();
    }

    //	获取网页内容
    public String getResponse(){
        URI uri = null;
        try {
            uri = new URI(this.url);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//		判断网址头部分(协议，主见IP，端口)
        String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
        String host = uri.getHost() == null ? "localhost" : uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            if (scheme.equalsIgnoreCase("http")) {
                port = 80;
            } else if (scheme.equalsIgnoreCase("https")) {
                port = 443;
            }
        }
//		只支持HTTP协议
        if (!scheme.equalsIgnoreCase("http")
                && !scheme.equalsIgnoreCase("https")) {
            System.err.println("Only HTTP(S) is supported.");
            return null;
        }


        boolean ssl = scheme.equalsIgnoreCase("https");
//		 Configure the client.
        ClientBootstrap bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));
//		 Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new HttpClientPipelineFactory(ssl, this.body));
//		 Start the connection attempt.
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host,
                port));
//		 Wait until the connection attempt succeeds or fails.
        Channel channel = future.awaitUninterruptibly().getChannel();
        if (!future.isSuccess()) {
            future.getCause().printStackTrace();
            bootstrap.releaseExternalResources();
            return null;
        }
//		 Prepare the HTTP request.(采用get方法提交)
        HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
                HttpMethod.GET, uri.toASCIIString());
        request.setHeader(HttpHeaders.Names.HOST, host);
        request.setHeader(HttpHeaders.Names.CONNECTION,
                HttpHeaders.Values.CLOSE);
        request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING,
                HttpHeaders.Values.GZIP);
        request.setHeader(HttpHeaders.Names.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0");
//        request.setHeader(HttpHeaders.Names.ACCEPT_LANGUAGE, HttpHeaders.Values.);
//		 Set some example cookies.本项目不需要缓存
//		CookieEncoder httpCookieEncoder = new CookieEncoder(false);
//		httpCookieEncoder.addCookie("my-cookie", "foo");
//		httpCookieEncoder.addCookie("another-cookie", "bar");
//		request.setHeader(HttpHeaders.Names.COOKIE, httpCookieEncoder.encode());

//		 Send the HTTP request.
        channel.write(request);
//		 Wait for the server to close the connection.
        channel.getCloseFuture().awaitUninterruptibly();
//		 Shut down executor threads to exit.
        bootstrap.releaseExternalResources();
//		返回结果为网页内容
        return this.body.toString();
    }

    //	获取单词意思
    public String getMean(String body) {
//		把每个单词的意思放到容器
        StringBuffer words = new StringBuffer();
        Matcher word = wordPattern.matcher(body);
        String mean = null;

        while (word.find()){
            Matcher attrs = wordAttrPattern.matcher(word.group());
            while (attrs.find()){
                words.append(attrs.group(1)+";");
            }
        }

        mean = words.toString();
        return mean;
    }

    public static String ClientRun(String word){
        HttpClient httpcli = new HttpClient("http://dict.youdao.com/search?len=eng&q="+word+"&keyfrom=dict.top");
        String mean = httpcli.getMean(httpcli.getResponse());
        return mean;
    }
}

