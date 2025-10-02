package com.gateway.service3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@EnableFeignClients
@ComponentScan(basePackages = {"com.gateway.service3", "com.gateway.service3.configuracion"})
public class GatewayService3Application {

	public static void main(String[] args) {
		SpringApplication.run(GatewayService3Application.class, args);
	}

}
