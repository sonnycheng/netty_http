package com.bank.nettyserver.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@ChannelHandler.Sharable
@Component
/**
 *   handler拦截器，可以验证访问请求的权限
 */
public class InterceptorHandler extends ChannelInboundHandlerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(InterceptorHandler.class);
	
    @Override
    public void channelRead(ChannelHandlerContext context, Object msg)   {
    	
    	logger.info("InterceptorHandler thread name:"+Thread.currentThread().getName());
    	
        if (!isPassed(context, (FullHttpRequest) msg)){         
            context.pipeline().close();            
        }else{
        	context.fireChannelRead(msg);
        	return;
        }

        ReferenceCountUtil.release(msg);
        // context.writeAndFlush(NettyHttpResponse.make(HttpResponseStatus.UNAUTHORIZED)).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 验证合法性
     * @param request
     * @return
     */
    private boolean isPassed(ChannelHandlerContext context, FullHttpRequest request){
    	String visitIp = getIPString(context);
    	logger.info("visitIp:"+visitIp);
    	 /**
    	if("127.0.0.1".equals(visitIp))
    		return false;
    	else
    	**/	
    	return true;
    }
    
    
    /**
     * 获取client的ip
     *
     * @param ctx
     * @return
     */
    public String getIPString(ChannelHandlerContext ctx) {
        String ipString = "";
        String socketString = ctx.channel().remoteAddress().toString();
        int colonAt = socketString.indexOf(":");
        ipString = socketString.substring(1, colonAt);
        return ipString;
    }
}
