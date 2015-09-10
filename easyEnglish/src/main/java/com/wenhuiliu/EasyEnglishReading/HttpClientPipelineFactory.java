package com.wenhuiliu.EasyEnglishReading;


import static org.jboss.netty.channel.Channels.*;

//import javax.net.ssl.SSLEngine;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
//import org.jboss.netty.example.securechat.SecureChatSslContextFactory;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
//import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;

/**
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 *
 * @version $Rev: 2226 $, $Date: 2010-03-31 11:26:51 +0900 (Wed, 31 Mar 2010) $
 */
public class HttpClientPipelineFactory implements ChannelPipelineFactory {
    private final boolean ssl;
    public StringBuilder content;
    public HttpClientPipelineFactory(boolean ssl, StringBuilder content) {
        this.ssl = ssl;
        this.content = content;
    }

    public ChannelPipeline getPipeline() throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = pipeline();
        // Enable HTTPS if necessary.
        if (ssl) {
//			SSLEngine engine = SecureChatSslContextFactory.getClientContext()
//					.createSSLEngine();
//			engine.setUseClientMode(true);
//			pipeline.addLast("ssl", new SslHandler(engine));
        }
        pipeline.addLast("decoder", new HttpResponseDecoder());
        pipeline.addLast("encoder", new HttpRequestEncoder());
        //pipeline.addLast("codec", new HttpClientCodec());
        // Remove the following line if you don't want automatic content
        // decompression.
        pipeline.addLast("inflater", new HttpContentDecompressor());
        // Uncomment the following line if you don't want to handle HttpChunks.
        // pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
        pipeline.addLast("handler", new HttpResponseHandler(content));
        return pipeline;
    }
}