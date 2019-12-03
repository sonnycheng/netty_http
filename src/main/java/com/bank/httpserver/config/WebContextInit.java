package com.bank.httpserver.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class WebContextInit implements ApplicationContextAware{
	
	private static final Logger logger = LoggerFactory.getLogger(WebContextInit.class);
	
	public static Set<String>  urlSet;
		
	public static DispatcherServlet getDispatcherServlet(ApplicationContext context){

    	XmlWebApplicationContext mvcContext = new XmlWebApplicationContext();
    	// classpath:
		mvcContext.setConfigLocation("classpath:spring-servlet.xml");
		mvcContext.setParent(context);
		

		MockServletConfig servletConfig = new MockServletConfig(mvcContext.getServletContext(), "dispatcherServlet");
		DispatcherServlet dispatcherServlet = new DispatcherServlet(mvcContext);		
		
		try {
			dispatcherServlet.init(servletConfig);
			
		} catch (ServletException e) {
			e.printStackTrace();
		}
		return dispatcherServlet;
	}
	
	public static Set<String> getControllerPath(ApplicationContext context) {
		RequestMappingHandlerMapping mapping = context.getBean(RequestMappingHandlerMapping.class);
		// 获取url与类和方法的对应信息
		Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		Set<String>  set = new HashSet<String>();
		for (Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
			Map<String, String> map1 = new HashMap<String, String>();
			RequestMappingInfo info = m.getKey();  
            HandlerMethod method = m.getValue();  
            PatternsRequestCondition p = info.getPatternsCondition();  
            for (String url : p.getPatterns()) { 
            	logger.info("url:"+url);
            	int num = StringUtils.countMatches(url, "/");
                if(num>2){
               	   url = url.substring(0, url.lastIndexOf("/"));
               	   logger.info("url2:"+url);
                }
            	map1.put("url", url);
            	set.add(url);
            	
            }  
            map1.put("className", method.getMethod().getDeclaringClass().getName()); // 类名  
            // logger.info("className:"+method.getMethod().getDeclaringClass().getName());
            map1.put("method", method.getMethod().getName()); // 方法名 
            // logger.info("method:"+method.getMethod().getName());
            
            RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
            for (RequestMethod requestMethod : methodsCondition.getMethods()) {
            	map1.put("type", requestMethod.toString());
			}
			
            list.add(map1);
		}
		
		urlSet = set;
		return set;
	}
	
	public static DispatcherServlet getMockDispatcherServlet(ApplicationContext context)  {
	  	
	    AnnotationConfigWebApplicationContext annotationConfigWebApplicationContext = new AnnotationConfigWebApplicationContext();
	    annotationConfigWebApplicationContext.setConfigLocation("spring-servlet.xml");
	    
	    MockServletContext mockServletContext = new MockServletContext();	 
        MockServletConfig mockServletConfig = new MockServletConfig(mockServletContext);
  
        annotationConfigWebApplicationContext.setServletConfig(mockServletConfig);
        annotationConfigWebApplicationContext.register(WebContextInit.class);
        // init and start DispatcherServlet
        DispatcherServlet dispatcherServlet = new DispatcherServlet(annotationConfigWebApplicationContext);       
        RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping();
        
        try {
			dispatcherServlet.init(mockServletConfig);
		} catch (ServletException e) {			
			e.printStackTrace();
		}
           
		return dispatcherServlet;
	}
    
     // 添加路径映射和过滤器映射
    private static void initServerConfig(ApplicationContext ServerContext) {
    	/**
        ServerContext.setFilter(ServerContext.MAPPING_ALL, BaseFilter.class);
        ServerContext.setFilter("/template", TemplateFilter.class);
        ServerContext.setAction(ServerContext.MAPPING_ALL, DefaultIndexAction.class);
        ServerContext.setAction("/template", TemplateAction.class);
        ServerContext.setAction("/files", FileAction.class);
        ServerContext.setROOT("root");
        ServerContext.setPORT(8090);
        **/
    }


	public static Set<String> getUrlSet(ApplicationContext context) {
		if(urlSet==null){
			urlSet = getControllerPath(context);
		}
		return urlSet;
	}

	public static void setUrlSet(Set<String> urlSet) {
		WebContextInit.urlSet = urlSet;
	}

	@Override
    public void setApplicationContext(ApplicationContext applicationContext) {
    	/**
        Map<String, Object> handlers =  applicationContext.getBeansWithAnnotation(NettyHttpHandler.class);
        for (Map.Entry<String, Object> entry : handlers.entrySet()) {
            Object handler = entry.getValue();
            Path path = Path.make(handler.getClass().getAnnotation(NettyHttpHandler.class));
            if (functionHandlerMap.containsKey(path)){
                LOGGER.error("IFunctionHandler has duplicated :" + path.toString(),new IllegalPathDuplicatedException());
                System.exit(0);
            }
            functionHandlerMap.put(path, (IFunctionHandler) handler);
        }
        **/
    }
	

}
