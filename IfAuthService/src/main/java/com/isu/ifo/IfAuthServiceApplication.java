package com.isu.ifo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
 
 
@SpringBootApplication
public class IfAuthServiceApplication extends SpringBootServletInitializer
{

	public static void main(String[] args) {
		SpringApplication.run(IfAuthServiceApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		// TODO Auto-generated method stub
		return builder.sources(IfAuthServiceApplication.class);
	}
}
