package com.bank.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping(value="/user",produces = "text/json;charset=utf-8")
public class UserController extends BaseController{
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@RequestMapping("/login")
	@ResponseBody
	public String login(String username,String pwd){
		
		logger.info("run into controller login method! ");	
		logger.info("thread name:"+Thread.currentThread().getName());
		
		JSONObject resultJson = new JSONObject();
		Map<String, String> loginResult = new HashMap<String, String>();
		loginResult.put("username", username);
		loginResult.put("age", "22");
		loginResult.put("sex", "girl");
		
		resultJson.put("code", 200);
		resultJson.put("msg", "登录成功");
		resultJson.put("result", loginResult);
		
		return JSONObject.toJSONString(resultJson);
	}
	
	@RequestMapping("/loginout")
	@ResponseBody
	public String loginout(String username,String pwd){
		
		logger.info("run into controller loginout method! ");
		logger.info("thread name:"+Thread.currentThread().getName());
		
		JSONObject resultJson = new JSONObject();
		Map<String, String> loginResult = new HashMap<String, String>();
		loginResult.put("username", username);
		loginResult.put("age", "24");
		loginResult.put("sex", "boy");
		
		resultJson.put("code", 200);
		resultJson.put("msg", "登录成功");
		resultJson.put("result", loginResult);
		
		return JSONObject.toJSONString(resultJson);
	}
}
	
