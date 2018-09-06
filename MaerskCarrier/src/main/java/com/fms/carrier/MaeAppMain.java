package com.fms.carrier;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fms.carrier.util.AppUtils;

@SpringBootApplication
@EnableScheduling
public class MaeAppMain {
	
	@Value("${carrier.url}")
	private String carrierUrl;

	public static void main(String[] args) {
		SpringApplication.run(MaeAppMain.class, args);
	}
	
	@PreDestroy
	public void stop() {
		AppUtils.getDriverInstance(carrierUrl).quit();
	}
}
