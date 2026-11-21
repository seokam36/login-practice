package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 앱의 시작점. main을 실행하면 스프링이 이 패키지(com.example.demo) 아래를 훑어서 @Service, @RestController 등을 찾아 준비한다.
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
