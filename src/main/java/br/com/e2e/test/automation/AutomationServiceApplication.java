package br.com.e2e.test.automation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class AutomationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutomationServiceApplication.class, args);
	}

}
