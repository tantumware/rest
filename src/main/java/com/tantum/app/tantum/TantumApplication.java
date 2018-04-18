package com.tantum.app.tantum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = { "com.tantum" })
@SpringBootApplication
public class TantumApplication {

	public static void main(String[] args) {
		SpringApplication.run(TantumApplication.class, args);
	}
}
