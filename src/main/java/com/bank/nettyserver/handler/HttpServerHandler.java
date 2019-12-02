package com.bank.nettyserver.handler;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import com.bank.httpserver.config.WebContextInit;
import com.bank.pojo.NettyConfig;
import com.bank.thread.HttpThread;
import com.bank.utils.Constants;
import com.bank.utils.StringUtil;
import com.bank.utils.ThymeleafUtil;

//@ChannelHandler.Sharable
@Component
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);
    
    @Autowired
    private  NettyConfig nettyConfig;
              
    private final DispatcherServlet servlet;
    private final ServletContext servletContext;
        
    public HttpServerHandler(DispatcherServlet servlet) {
		this.servlet = servlet;
		this.servletContext = servlet.getServletConfig().getServletContext();
	}

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

       
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {
    	    	
    	 String uri = fullHttpRequest.getUri();
    	 logger.info("uri:"+uri);
         if(uri.equals(Constants.NO_HANDER_URL)){
         	 return;
         }                   
         
         Set<String>  urlSet =WebContextInit.getUrlSet(servlet.getWebApplicationContext());
         int num = StringUtils.countMatches(uri, "/");
         if(num>2){
        	 uri = uri.substring(0, uri.lastIndexOf("/"));
         }

         if(!urlSet.contains(uri)){
        	 logger.info("not in serrvice! ");
        	 String newUri= Constants.NO_PAGE;
        	 handerStaticPage(ctx, fullHttpRequest, newUri);
         }
               
        // 处理静态页面
        if(uri.endsWith(".html")||uri.endsWith(".css")||uri.endsWith(".js")||uri.endsWith(".jpg")){
        	handerStaticPage(ctx, fullHttpRequest, uri);
        	
        }else{
    	
	    	boolean flag = HttpMethod.POST.equals(fullHttpRequest.getMethod())
	                || HttpMethod.GET.equals(fullHttpRequest.getMethod());
	        	        
	        if(flag && ctx.channel().isActive()){
	            //HTTP请求 GET/POST
	            MockHttpServletResponse servletResponse = new MockHttpServletResponse();	                			
	            MockHttpServletRequest servletRequest = assembleHttpRequest(ctx, fullHttpRequest);
	            	          	            	          	            	           
	            // 耗时任务 
	            ctx.executor().submit(new HttpThread(servlet,servletRequest,servletResponse){
	            	@Override
	        	    public void run() {
	            		
	            		logger.info("thread name:"+Thread.currentThread().getName());	        	       
	            		try {         				            			
	            			servlet.service(servletRequest, servletResponse);	            				            			
	            			HttpResponseStatus status = HttpResponseStatus.valueOf(servletResponse.getStatus());		            			            			
	                        String forwardUrl = servletResponse.getForwardedUrl();
	                        String result = servletResponse.getContentAsString();                        
	                        result = StringUtils.isEmpty(result)?"":result;
	                        
	                        FullHttpResponse response = null;
	                        
	                        // forward 	                     	                        	                    	                        
	                        if(null!=forwardUrl&&(!"default".equals(forwardUrl))){
	                        	
	                        	if(forwardUrl.endsWith(".html")){
	                        		// 返回动态 页面
	                        			                        		
	                        		String viewName = forwardUrl.substring(forwardUrl.lastIndexOf("/")+1, forwardUrl.indexOf(".html"));	  
	                        		String result2 =  ThymeleafUtil.handlerThymeleafPage(viewName,servletRequest,servletResponse);
		                        	response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,Unpooled.copiedBuffer(result2,CharsetUtil.UTF_8));
		                            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");	                        		
	                        		
	                        	}else{
		                            // forward to action
			                        servletRequest.setRequestURI(forwardUrl);
			        	            servletRequest.setServletPath(forwardUrl);			        	           
			        	
			                        MockHttpServletResponse servletResponse2 = new MockHttpServletResponse();
			                        servlet.service(servletRequest, servletResponse2);
			                    	HttpResponseStatus status2 = HttpResponseStatus.valueOf(servletResponse.getStatus());	                        
			                        String result2 = servletResponse2.getContentAsString();                        
			                        result2 = StringUtils.isEmpty(result2)?"":result2;
			                        response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status2,Unpooled.copiedBuffer(result2,CharsetUtil.UTF_8));
			                        response.headers().set("Content-Type", "text/json;charset=UTF-8");
			                        response.headers().set("Content-Length", Integer.valueOf(response.content().readableBytes()));
	                        	}
	                        }else if(status.equals(HttpResponseStatus.FOUND)) {
	                        	//  重定向
	                        	String newUri = Constants.URL_AHEAD + nettyConfig.getHost()+":"+ nettyConfig.getPort().toString()+servletResponse.getRedirectedUrl();
	                        	logger.info("newUri:"+newUri);
	                            response.headers().set(HttpHeaders.Names.LOCATION, newUri);           		                        
	                        }else {	                        	
	                        	
	                        	// 返回 json 数据	                        	
	                        	response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,Unpooled.copiedBuffer(result,CharsetUtil.UTF_8));
	                        	response.headers().set("Content-Type", "text/json;charset=UTF-8");
	                        	response.headers().set("Content-Length", Integer.valueOf(response.content().readableBytes()));
	                        }
	                        		                    
	                        
	                        response.headers().set("Access-Control-Allow-Origin", "*");
	                        response.headers().set("Access-Control-Allow-Headers", "Content-Type,Content-Length, Authorization, Accept,X-Requested-With,X-File-Name");
	                        response.headers().set("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS");                       
	                        response.headers().set("Connection", "keep-alive");
	                                                                                                             
	                        ChannelFuture writeFuture = ctx.writeAndFlush(response);
	                        writeFuture.addListener(ChannelFutureListener.CLOSE);
	            		} catch (ServletException e) {			
	            			e.printStackTrace();
	            		} catch (IOException e) {			
	            			e.printStackTrace();
	            		} finally{
	            			 ReferenceCountUtil.release(fullHttpRequest);
	            		}
	        	    	 
	        	    }
	        	}, null);	           	         	            
	        }
        }        
    }

	private MockHttpServletRequest assembleHttpRequest(ChannelHandlerContext ctx,FullHttpRequest fullHttpRequest)
			   throws UnsupportedEncodingException {
		
		MockHttpServletRequest servletRequest =new MockHttpServletRequest(servletContext);
		// headers
		for (String name : fullHttpRequest.headers().names()) {
		    for (String value : fullHttpRequest.headers().getAll(name)) {
		        servletRequest.addHeader(name, value);
		    }
		}
        
		String uri = fullHttpRequest.getUri();
		uri = new String(uri.getBytes("ISO8859-1"), "UTF-8");
		uri = URLDecoder.decode(uri, "UTF-8");
		UriComponents uriComponents = UriComponentsBuilder.fromUriString(uri).build();
		String path = uriComponents.getPath();
		path = URLDecoder.decode(path, "UTF-8");
		servletRequest.setRequestURI(path);
		servletRequest.setServletPath(path);
		servletRequest.setMethod(fullHttpRequest.getMethod().name());

		if (uriComponents.getScheme() != null) {
		    servletRequest.setScheme(uriComponents.getScheme());
		}
		if (uriComponents.getHost() != null) {
		    servletRequest.setServerName(uriComponents.getHost());
		}
		if (uriComponents.getPort() != -1) {
		    servletRequest.setServerPort(uriComponents.getPort());
		}

		String  contentType = fullHttpRequest.headers().get("Content-Type");
        
        Map<String, List<String>> uriAttributes =  null;
        if((Constants.FORM_CONTENT_TYPE).equals(contentType)){
        	
			String jsonStr = fullHttpRequest.content().toString(StandardCharsets.UTF_8);
			QueryStringDecoder queryDecoder = new QueryStringDecoder(jsonStr, false);
			uriAttributes = queryDecoder.parameters();
			for (Map.Entry<String, List<String>> attr : uriAttributes.entrySet()) {
				for (String attrVal : attr.getValue()) {
			        System.out.println(attr.getKey()+"="+attrVal);
			    }
			}
        }
			
		ByteBuf content = fullHttpRequest.content();
		content.readerIndex(0);
		byte[] data = new byte[content.readableBytes()];
		content.readBytes(data);
		servletRequest.setContent(data);
		
		if (uriComponents.getQuery() != null) {
		    String query = UriUtils.decode(uriComponents.getQuery(),"UTF-8");
		    servletRequest.setQueryString(query);
		}
		
		Map<String, String>  parammap = getRequestParams(ctx,fullHttpRequest);
		if(parammap!=null&&parammap.size()>0){
			for (String key : parammap.keySet()) {
		        servletRequest.addParameter(UriUtils.decode(key,"UTF-8"), UriUtils.decode(parammap.get(key) == null ? "": parammap.get(key), "UTF-8"));
		    }
		}
		
		if(uriAttributes!=null&&uriAttributes.size()>0){
			for (String key : uriAttributes.keySet()) {
		        servletRequest.addParameter(UriUtils.decode(key,"UTF-8"), UriUtils.decode(uriAttributes.get(key) == null ? "": (uriAttributes.get(key)).toString(), "UTF-8"));
		    }
		}
		
		return servletRequest;
	}

	
	private void handerStaticPage(ChannelHandlerContext ctx,FullHttpRequest fullHttpRequest, String url) throws FileNotFoundException {
		String path = Constants.PROJECT_LOCATION + url;
		logger.info("path:"+path);
		File html = new File(path);
		                   
		String result = StringUtil.stream2String(new FileInputStream(html), "UTF-8");
		ByteBuf content = Unpooled.copiedBuffer(result, CharsetUtil.UTF_8);
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
		response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");            
		// response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/x-javascript");
		// response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/css; charset=UTF-8");
		            
		response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, result.length());
		response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		
		ctx.write(response);          
		// ctx.write(new ChunkedNioFile(file.getChannel()));
		    
		if (content != null) {
		    response.headers().set("Content_Length", response.content().readableBytes());
		}
        
		// 写入文件尾部
		ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);  
		HttpRequest req = (HttpRequest) fullHttpRequest;
		if (!isKeepAlive(req)) {
		    future.addListener(ChannelFutureListener.CLOSE);
		}     	 
	}   
  
    /**
     * 获取post请求、get请求的参数保存到map中
     */
    private Map<String, String> getRequestParams(ChannelHandlerContext ctx, HttpRequest req){
    	
        Map<String, String>requestParams=new HashMap<String, String>();
        //  处理get请求 
        if (req.getMethod() == HttpMethod.GET) {
            QueryStringDecoder decoder = new QueryStringDecoder(req.getUri());  
            Map<String, List<String>> parame = decoder.parameters();  
            Iterator<Entry<String, List<String>>> iterator = parame.entrySet().iterator();
            while(iterator.hasNext()){
                Entry<String, List<String>> next = iterator.next();
                requestParams.put(next.getKey(), next.getValue().get(0));
            }
        }
        // 处理 POST 请求 
        if (req.getMethod() == HttpMethod.POST) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(  
                    new DefaultHttpDataFactory(false), req);  
            List<InterfaceHttpData> postData = decoder.getBodyHttpDatas(); //
            for(InterfaceHttpData data:postData){
                if (data.getHttpDataType() == HttpDataType.Attribute) {  
                    MemoryAttribute attribute = (MemoryAttribute) data;                     
                    requestParams.put(attribute.getName(), attribute.getValue());
                }
            }
        }
        return requestParams;
    }
    
    /**
     * 活跃的、有效的通道
     * 第一次连接成功后进入的方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.info("tcp client " + getRemoteAddress(ctx) + " connect success");       
    }

    /**
     * 不活动的通道
     * 连接丢失后执行的方法（client端可据此实现断线重连）
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
    }

    /**
     * 异常处理
     *
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        //发生异常，关闭连接
        logger.error("引擎 {} 的通道发生异常，即将断开连接", getRemoteAddress(ctx));
        ctx.close();//再次建议close
    }

    /**
     * 心跳机制，超时处理
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        String socketString = ctx.channel().remoteAddress().toString();
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
            	logger.info("Client: " + socketString + " READER_IDLE 读超时");
                ctx.disconnect();//断开
            } else if (event.state() == IdleState.WRITER_IDLE) {
            	logger.info("Client: " + socketString + " WRITER_IDLE 写超时");
                ctx.disconnect();
            } else if (event.state() == IdleState.ALL_IDLE) {
            	logger.info("Client: " + socketString + " ALL_IDLE 总超时");
                ctx.disconnect();
            }
        }
    }

    /**
     * 获取client对象：ip+port
     *
     * @param ctx
     * @return
     */
    public String getRemoteAddress(ChannelHandlerContext ctx) {
        String socketString = "";
        socketString = ctx.channel().remoteAddress().toString();
        return socketString;
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
