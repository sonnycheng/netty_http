package com.bank.httpserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.bank.httpserver.config.NettyHttpServer;



@SpringBootApplication()
@ComponentScan(basePackages = {"com.bank.controller","com.bank.service","com.bank.pojo"})
@MapperScan("com.bank.dao")
@Import({NettyHttpServer.class})
public class NettyHttpApplication {
	

    public static void main(String[] args) {   
    	 	  		
    	new SpringApplicationBuilder(NettyHttpApplication.class).web(WebApplicationType.NONE).run(args);
    		
    }
}
