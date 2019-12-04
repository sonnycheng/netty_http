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
		resultJson.put("msg", "耗时正常返回");
		resultJson.put("result", loginResult);
		
		return JSONObject.toJSONString(resultJson);
	}
	
	@RequestMapping("/takelongtime")
	@ResponseBody
	public String takelongtime(String username,String pwd){
			
		try {
			Thread.sleep(1000*60);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		
		JSONObject resultJson = new JSONObject();
		Map<String, String> loginResult = new HashMap<String, String>();
		loginResult.put("username", username);
		loginResult.put("age", "24");
		loginResult.put("sex", "girl");
		
		resultJson.put("code", 200);
		resultJson.put("msg", "1分钟耗时正常返回");
		resultJson.put("result", loginResult);
		
		return JSONObject.toJSONString(resultJson);
	}
}
	
