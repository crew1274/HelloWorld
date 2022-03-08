 package com.example.HelloWorld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.CountDownLatch;


@SpringBootApplication
@RestController
public class HelloWorldApplication
{
	public static final String exchangeName = "test";

	public static final String queueName = "test";

	public static final String queueNameRPC = "test_rpc";

	public static final String routingKey = "test";

	public static final String routingKeyRPC = "test_rpc";

	public static final String response = "Hello World! Spring Boot!";

	@RequestMapping("/")
	public String home()
	{
		return response;
	}

	@Bean
	Queue queue()
	{
		return new Queue(queueName, false);
	}

	@Bean
	Queue replyQueue()
	{
		return new Queue(queueNameRPC, false);
	}

	@Bean
	TopicExchange exchange()
	{
		return new TopicExchange(exchangeName);
	}

	@Bean
	Binding binding(Queue queue, TopicExchange exchange)
	{
		return BindingBuilder.bind(queue).to(exchange).with(routingKey);
	}

	@Bean
	public Binding replyBinding()
	{
		return BindingBuilder.bind(replyQueue()).to(exchange()).with(routingKeyRPC);
	}

	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
											 MessageListenerAdapter listenerAdapter)
	{
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(Receiver receiver)
	{
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}

	public static void main(String[] args) throws InterruptedException
	{
		SpringApplication.run(HelloWorldApplication.class, args);
	}

}

