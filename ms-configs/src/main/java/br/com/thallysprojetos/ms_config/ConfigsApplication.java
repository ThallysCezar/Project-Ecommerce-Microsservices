package br.com.thallysprojetos.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ConfigsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigsApplication.class, args);
	}

}
