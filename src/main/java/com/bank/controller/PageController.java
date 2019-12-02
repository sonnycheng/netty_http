package com.bank.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value="/user3",produces = "text/json;charset=utf-8")
public class PageController extends BaseController{
	
	private static final Logger logger = LoggerFactory.getLogger(PageController.class);
	
	@RequestMapping("/thymeleafPage")
	public ModelAndView  getDymicPage(){
						
		ModelAndView mv = new ModelAndView();		
		mv.addObject("name", "三国人物");	
		mv.addObject("array", new String[]{"曹操", "刘备", "孙权", "汉献帝"});		
		mv.setViewName("test");
		 		
		return mv;
	}
	
	

	@RequestMapping("/page")
	public ModelAndView  redirect(String username,String pwd){
						
		ModelAndView mv = new ModelAndView();
		// mv.addObject("key", "netty");		
		mv.setViewName("redirect:/hello.html");
		 
		return mv;
	}
	
	@RequestMapping("/forward")
	public ModelAndView  forward(String username,String pwd){
		
		logger.info("run into controller forward method! ");				
		ModelAndView mv = new ModelAndView();
		mv.addObject("name", "test");		
		mv.setViewName("forward:/user/login");
		 
		return mv;
	}
	
	@RequestMapping("/forwardtest")
	public ModelAndView  forwardtest(String username,String pwd){
		
		logger.info("run into controller forward test method! ");				
		ModelAndView mv = new ModelAndView();
		mv.addObject("name", "test");		
		mv.setViewName("forward:/user/loginout");
		 
		return mv;
	}
}
	
