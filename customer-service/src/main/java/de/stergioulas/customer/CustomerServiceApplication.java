package de.stergioulas.customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@Log4j2
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @FeignClient("address-service")
    interface AddressClient {
        @RequestMapping(method = RequestMethod.GET, value = "/addresses")
        List<String> getAddresses();
    }

    @RestController
    @RequiredArgsConstructor
    class CustomerController {

        private final Environment env;
        private final RestTemplate restTemplate;
        private final AddressClient client;

        @GetMapping("/customers")
        List<String> customers() {
            log.info("Customers endpoint ->");
            return List.of("Nyah", "Rhea");
        }

        @GetMapping("/customers/addresses")
        Object addresses() {
            log.info("Addresses endpoint ->");
            return client.getAddresses();
        }
    }
}
