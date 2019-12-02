package com.bank.utils;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

public class ThymeleafUtil {
	       
    public static String handlerThymeleafPage(String viewName, HttpServletRequest req,HttpServletResponse resp) throws IOException {
    	ServletContextTemplateResolver resolver = new ServletContextTemplateResolver(req.getServletContext());
        //模板所在目录，相对于当前classloader的classpath。
        resolver.setPrefix("/templates/");
        //模板文件后缀
        resolver.setSuffix(".html");
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
        
        //构造上下文(Model)
        Context context = new Context();       
                
        Enumeration<String> elem = req.getAttributeNames();
		while(elem.hasMoreElements()){
			   String elem2 = elem.nextElement();
			   if(!elem2.startsWith("org.springframework")&& !elem2.startsWith("java.")){			    
			     context.setVariable(elem2, req.getAttribute(elem2));
			   }
	    }
 
        //渲染模板
        // FileInputStream input = new FileInputStream(new File(path));
        
        // engine.process("example",context,writer);
        //这个example.html 放在resources 下面.这样机会生成一个result.html文件,结果都已经放进去了.
        // Map<String,String> map = new HashMap<String,String>();
        // map.put("name", "test");
        // WebContext ctx = new WebContext(req,resp,req.getServletContext(),req.getLocale());
     // 手动渲染
         // String content = HttpServerHandler.stream2String(input,"UTF-8");
         // String html = engine.process(content, context);  
         String html = engine.process(viewName, context); 
       
         return html;
  
    }
}

