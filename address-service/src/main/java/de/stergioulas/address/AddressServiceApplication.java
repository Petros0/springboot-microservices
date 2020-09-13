package de.stergioulas.address;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
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
			log.info("Addresses endpoint ->");
			return List.of("450  Leisure Lane", "1673  Gerald L. Bates Drive");
		}
	}

	@Component
	@Profile("rabbit")
	public class RabbitMqListener {

		private String queueName = "my-queue";
		private String exchange = "my-exchange";
		private String routingKey = "#routing.key";


		@RabbitListener(queues = "my-queue")
		public void listen(String message) {
			String msg = new String(message);
			System.out.println("Received a new message...");
			System.out.println(msg);
		}

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
	}

}
