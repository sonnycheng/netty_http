package com.bank.nettyserver;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.EventExecutorGroup;

import org.springframework.web.servlet.DispatcherServlet;

import com.bank.nettyserver.handler.HttpServerHandler;


public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
	

	private DispatcherServlet servlet;
	
	private EventExecutorGroup businessGroup;

	public HttpServerInitializer(DispatcherServlet servlet,EventExecutorGroup businessGroup) {
		this.servlet = servlet;
		this.businessGroup = businessGroup;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		
		ChannelPipeline pipeline = ch.pipeline();
			
		// 添加请求消息解码器
		pipeline.addLast("decoder", new HttpRequestDecoder());
		// 添加响应解码器
		pipeline.addLast("encoder", new HttpResponseEncoder());
		//HttpObjectAggregator的作用 将多个消息转换为单一的FullHttpRequest或者FullHttpResponse
		pipeline.addLast("aggregator", new HttpObjectAggregator(2147483647));
		// 支持异步发送大的码流(大的文件传输),但不占用过多的内存，防止java内存溢出
		pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
        pipeline.addLast("deflater", new HttpContentCompressor());
        pipeline.addLast(businessGroup, "handler", new HttpServerHandler(servlet));
	}
	
}

	
