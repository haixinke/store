package com.igen.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.igen.store"})
@EnableAutoConfiguration
@ServletComponentScan
public class IgenStoreApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(IgenStoreApiApplication.class, args);
	}
}
