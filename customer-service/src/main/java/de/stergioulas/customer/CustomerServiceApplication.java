package de.stergioulas.customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
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
@RequiredArgsConstructor
public class CustomerServiceApplication implements CommandLineRunner {


    @Autowired
    Environment env;

    @Autowired
    Binding binding;

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }


    @Override
    public void run(String... args) throws Exception {
        if (env.acceptsProfiles(Profiles.of("rabbit"))) {

        }
    }


    @FeignClient("address-service")
    interface AddressClient {
        @RequestMapping(method = RequestMethod.GET, value = "/addresses")
        List<String> getAddresses();
    }

    @RestController
    @RequiredArgsConstructor
    class CustomerController {

        private final RestTemplate restTemplate;
        private final AddressClient client;

        // simple endpoint
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

// without feign
//        @GetMapping("/customers/addresses")
//        Object addresses() {
//            log.info("Addresses endpoint ->");
//            return restTemplate.getForObject("http://address-service/addresses", Object.class) without feign
//
//        }
    }

    @RequiredArgsConstructor
    @Profile("rabbit")
    @RestController
    class RabbitConfig {

        private final RabbitTemplate rabbitTemplate;
        private String queueName = "my-queue";
        private String exchange = "my-exchange";
        private String routingKey = "#routing.key";

        @Bean
        Queue queue() {
            // Creating a queue.
            return new Queue(queueName, false);
        }

        @Bean
        TopicExchange topicExchange() {
            // Creating a topic exchange.
            return new TopicExchange(exchange);
        }

        @Bean
        Binding binding(final Queue queue, final TopicExchange topicExchange) {
            // Binding the queue to the topic with a routing key.
            return BindingBuilder.bind(queue).to(topicExchange).with(routingKey);
        }

        @GetMapping("/customers/send")
        public void sendToRabbit() {
            rabbitTemplate.convertAndSend(exchange, routingKey, "message");
        }
    }
}
