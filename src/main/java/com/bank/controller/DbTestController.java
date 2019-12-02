package com.bank.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.bank.nettyserver.handler.HttpServerHandler;
import com.bank.pojo.User;
import com.bank.service.IUserService;
import com.bank.utils.StringUtil;


@Controller
@RequestMapping(value="/db",produces = "text/json;charset=utf-8")
public class DbTestController extends BaseController{
	
	private static final Logger logger = LoggerFactory.getLogger(DbTestController.class);
		
	@Autowired
    private IUserService userService;

	@RequestMapping("/test/{id}")
	@ResponseBody
	public String test(@PathVariable(name = "id") String id, @RequestParam(name = "name") String name){
				
		logger.info("id:"+id+",name:"+name);
				
		String threadName = Thread.currentThread().getName();
				
		User user = new User();
		user.setName(name);
		user.setPassword("password"+id);
		user.setIdentifyType(Integer.parseInt(id));
		user.setIdentifyNumber(threadName);
				
		boolean isInsert = userService.save(user);
				
		JSONObject resultJson = new JSONObject();
			
		resultJson.put("code", 200);
		resultJson.put("id", user.getId());
		resultJson.put("msg", isInsert);
		
		return JSONObject.toJSONString(resultJson);
	}
	
	@RequestMapping("/test2")
	@ResponseBody
	public String test2(MockHttpServletRequest request){
		
		 logger.info("run into controller db test2 method! ");	
		 String id= request.getParameter("id");
		 System.out.println("id="+id);
		 System.out.println("name="+request.getParameter("name"));
			
		 String threadName = Thread.currentThread().getName();
			
		 JSONObject resultJson = new JSONObject();
			
		 resultJson.put("code", 200);
		 resultJson.put("id", id);
		 resultJson.put("msg", "访问成功");
		
		 return JSONObject.toJSONString(resultJson);
	}
	
	@PostMapping(value= "/posttest")
	@ResponseBody
	public String posttest(@RequestBody String body){
		logger.info("run into controller db posttest method: "+body);
		 	
		Map<String,String>  map = StringUtil.getMap(body);
		map.forEach((k, v) -> System.out.println(k + " -> " +  v ));	
						
		/**
		User user = new User();
		user.setName("opq");
		user.setPassword("password456");
		user.setIdentifyType(8);
		user.setIdentifyNumber(threadName);
		boolean isInsert = userService.save(user);
		**/
		
		JSONObject resultJson = new JSONObject();
			
		resultJson.put("code", 200);
		// resultJson.put("id", user.getId());
		resultJson.put("msg", "登录成功");
		
		return JSONObject.toJSONString(resultJson);
	}
	
	@PostMapping(value= "/postForm")
	@ResponseBody
	public String postForm(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password){
		
		logger.info("db postForm method,username:"+username+",:"+password);
			
		/**
		User user = new User();
		user.setName("opq");
		user.setPassword("password456");
		user.setIdentifyType(8);
		user.setIdentifyNumber(threadName);
		boolean isInsert = userService.save(user);
		**/
		
		JSONObject resultJson = new JSONObject();
			
		resultJson.put("code", 200);
		// resultJson.put("id", user.getId());
		resultJson.put("msg", "登录成功");
		
		return JSONObject.toJSONString(resultJson);
	}
}
	
