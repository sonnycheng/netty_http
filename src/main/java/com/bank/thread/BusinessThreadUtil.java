package com.bank.thread;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class BusinessThreadUtil {

    private static final ExecutorService executor = new ThreadPoolExecutor(
    		2, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(100000));//CPU核数4-10倍

    public static void doBusiness(ChannelHandlerContext ctx, Object msg, byte[] content) {
        //异步线程池处理  	
        executor.submit( () -> {
            if (msg instanceof HttpRequest) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Thread.currentThread().setName("buessness-thread");
                System.out.println(Thread.currentThread().getId());
                HttpRequest req = (HttpRequest) msg;
                boolean keepAlive = isKeepAlive(req);
                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(content));
                response.headers().set("Content-Type", "text/plain");
                // response.headers().setInt("Content-Length", response.content().readableBytes());
                if (!keepAlive) {
                    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set("Connection", "keep-alive");
                    ctx.writeAndFlush(response);
                }
            }
        });
    }
}
