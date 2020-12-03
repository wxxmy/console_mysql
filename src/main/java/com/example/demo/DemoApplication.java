package com.example.demo;

import ch.qos.logback.classic.ClassicConstants;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan(basePackages = {"com.example.demo.dao"})
public class DemoApplication {

	private static Logger log= null;
	static {
		System.setProperty(ClassicConstants.LOGBACK_CONTEXT_SELECTOR,"com.example.demo.util.CustomDefaultContextSelector");
		log = LoggerFactory.getLogger(DemoApplication.class);
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);
	}

}
