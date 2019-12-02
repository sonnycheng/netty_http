package com.bank.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.stat.DruidStatManagerFacade;

/**
 * @author : feng
 * @description: DruidStatController
 * @date : 2019-05-13 11:42
 * @version: : 1.0
 */
@RestController
public class DruidStatController {

    
    @GetMapping("/druid/stat")
    public Object druidStat(){
    	
    	// /druid/index.html, 官方监控页面打不开
    	
    	List<Map<String,Object>>  list =  DruidStatManagerFacade.getInstance().getDataSourceStatDataList();
    	System.out.println("list size:"+list.size());
    	
    	for(int i=0;i<list.size();i++){
	        Map<String,Object> map = list.get(i);
	        System.out.println("==========================");
	        for(Map.Entry<String, Object> entry : map.entrySet()){
	    	    String mapKey = entry.getKey();
	    	    Object mapValue = entry.getValue();
	    	    if(mapValue!=null){
	    	        System.out.println("====: "+mapKey+":"+mapValue.toString());
	    	    }else{
	    	    	System.out.println("====: "+mapKey);
	    	    }
	    	}
    	}
        return null;
    }

   
}
