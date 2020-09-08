package de.stergioulas.address;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@EnableEurekaClient
@Log4j2
public class AddressServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AddressServiceApplication.class, args);
	}


	@RestController
	class AddressController {

		@Autowired
		Environment env;

		@GetMapping("/addresses")
		List<String> customers() {
			log.info("addresses endpoint ->");
			return List.of("Address1", env.getProperty("server.port"));
		}
	}

}
