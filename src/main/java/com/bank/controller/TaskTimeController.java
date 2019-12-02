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
@RequestMapping(value="/user2",produces = "text/json;charset=utf-8")
public class TaskTimeController extends BaseController{
	
	private static final Logger logger = LoggerFactory.getLogger(TaskTimeController.class);

	@RequestMapping("/taketime")
	@ResponseBody
	public String taketime(String username,String pwd){
		
		logger.info("taketime thread name:"+Thread.currentThread().getName());
		
		try {
			Thread.sleep(1000*30);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		
		JSONObject resultJson = new JSONObject();
		Map<String, String> loginResult = new HashMap<String, String>();
		loginResult.put("username", username);
		loginResult.put("age", "24");
		loginResult.put("sex", "girl");
		
		resultJson.put("code", 200);
		resultJson.put("msg", "登录成功");
		resultJson.put("result", loginResult);
		
		return JSONObject.toJSONString(resultJson);
	}
}
	
