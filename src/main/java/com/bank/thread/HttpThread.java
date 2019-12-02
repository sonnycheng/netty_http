package com.bank.thread;

import java.io.IOException;

import javax.servlet.ServletException;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.DispatcherServlet;

public class HttpThread implements Runnable{
	
	private DispatcherServlet servlet;
	private MockHttpServletRequest servletRequest;
	private MockHttpServletResponse servletResponse;
	
	public HttpThread(DispatcherServlet servlet,
			MockHttpServletRequest servletRequest,
			MockHttpServletResponse servletResponse) {
		super();
		this.servlet = servlet;
		this.servletRequest = servletRequest;
		this.servletResponse = servletResponse;
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			servlet.service(servletRequest, servletResponse);
		} catch (ServletException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}

}
