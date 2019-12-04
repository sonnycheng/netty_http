package com.bank.httpserver.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.DispatcherServlet;

import com.bank.nettyserver.handler.HttpProtocolHandler;
import com.bank.nettyserver.handler.InterceptorHandler;
import com.bank.pojo.NettyConfig;
import com.bank.utils.Constants;


@Configuration
public class NettyHttpServer implements ApplicationListener<ApplicationStartedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(NettyHttpServer.class);
    
    @Autowired
    private  NettyConfig nettyConfig;   
   
    @Override
    public void onApplicationEvent(@NonNull ApplicationStartedEvent event) {
    	
    	DispatcherServlet servlet = WebContextInit.getDispatcherServlet(event.getApplicationContext());      	   
    	WebContextInit.getUrlSet(servlet.getWebApplicationContext());
    	
    	int port = nettyConfig.getPort();
    	logger.info("Netty config host:"+nettyConfig.getHost()+",config port:"+port+"\n" +
    			",worker thread:"+nettyConfig.getWorkerCount()+ ",business thread:"+nettyConfig.getBusinessCount());
    	
    	EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup(nettyConfig.getWorkerCount());
		// 业务线程池
		EventExecutorGroup businessGroup = new DefaultEventExecutorGroup(nettyConfig.getBusinessCount());
		
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			
			// 绑定两个线程组    
			bootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, nettyConfig.getBacklog())
					.childOption(ChannelOption.SO_KEEPALIVE, true)	
					.childHandler(new ChannelInitializer<SocketChannel>(){	
			            @Override
			            public void initChannel(SocketChannel ch) {
			            	// 添加请求消息解码器
			            	ch.pipeline().addLast("decoder", new HttpRequestDecoder());
			        		// 添加响应解码器
			            	ch.pipeline().addLast("encoder", new HttpResponseEncoder());
			        		//HttpObjectAggregator的作用 将多个消息转换为单一的FullHttpRequest或者FullHttpResponse
			            	ch.pipeline().addLast("aggregator", new HttpObjectAggregator(2147483647));
			        		// 支持异步发送大的码流(大的文件传输),但不占用过多的内存，防止java内存溢出
			            	ch.pipeline().addLast("chunkedWriter", new ChunkedWriteHandler());
			            	ch.pipeline().addLast("deflater", new HttpContentCompressor());
			            	ch.pipeline().addLast("interceptor", new InterceptorHandler());
			            	ch.pipeline().addLast(businessGroup,"handler", new HttpProtocolHandler(servlet));
			            }
	        });

            ChannelFuture channelFuture = bootstrap.bind(new InetSocketAddress(nettyConfig.getHost(), port)).sync().addListener(future -> {
            String logBanner = "\n\n" +
                    "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n" +
                    "*                                                                                   *\n" +
                    "*                                                                                   *\n" +
                    "*                   Netty Http Server started on port {}.                           *\n" +
                    "*                                                                                   *\n" +
                    "*                                                                                   *\n" +
                    "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n";
            logger.info(logBanner, port);
            });  
            
			// 等待服务器 socket 关闭 。在这个例子中，这不会发生，但你可以优雅地关闭你的服务器。
            channelFuture.channel().closeFuture().sync();
    
		}catch (Exception e) {
			logger.error("NettySever start fail",e);
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}  
    
  }   
}