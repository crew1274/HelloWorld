package com.example.HelloWorld;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.*;
import java.io.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
@RestController
public class HelloWorldApplication
{
	public static final String exchangeName = "test";

	public static final String queueName = "test";

	public static final String queueNameRPC = "test_rpc";

	public static final String routingKey = "test";

	public static final String routingKeyRPC = "test_rpc";

	public String response = "Hello World! Spring Boot!";

	@RequestMapping("/")
	public String home()
	{
		return response;
	}

//	@Bean
//	Queue queue()
//	{
//		return new Queue(queueName, false);
//	}
//
//	@Bean
//	Queue replyQueue()
//	{
//		return new Queue(queueNameRPC, false);
//	}
//
//	@Bean
//	TopicExchange exchange()
//	{
//		return new TopicExchange(exchangeName);
//	}

//	@Bean
//	Binding binding()
//	{
//		return BindingBuilder.bind(queue()).to(exchange()).with(routingKey);
//	}

//	@Bean
//	public Binding replyBinding()
//	{
//		return BindingBuilder.bind(replyQueue()).to(exchange()).with(routingKeyRPC);
//	}

//	@Bean
//	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
//											 MessageListenerAdapter listenerAdapter)
//	{
//		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//		container.setConnectionFactory(connectionFactory);
//		container.setQueueNames(queueName);
//		container.setMessageListener(listenerAdapter);
//		return container;
//	}
//
//	@Bean
//	MessageListenerAdapter listenerAdapter(HelloWorldApplication this)
//	{
//		return new MessageListenerAdapter(this, "receiveMessage");
//	}

	@Autowired
	RabbitTemplate rabbit;

	@RabbitListener(queues = "test")
	public void receiveMessage(Message message) throws Exception
	{
//      System.out.println("Received: <" + message.getMessageProperties() + ">");
//		System.out.println("Received getReplyTo: <" + message.getMessageProperties().getReplyTo() + ">");
		if(	message.getMessageProperties().getReplyTo() != null &&
				message.getMessageProperties().getHeaders().get("routingKey").toString() != null)
		{
			var props = new MessageProperties();
			props.getHeaders().put("MULE_CORRELATION_ID", message.getMessageProperties().getHeaders().get("MULE_CORRELATION_ID"));
			props.setContentType(message.getMessageProperties().getContentType());
			String s = new String(message.getBody(), StandardCharsets.UTF_8);
			doSomething(s);
			System.out.println("getBody: <" + s + ">");
			Message messageReply = new Message((s + " for rpc").getBytes(), props);
//			System.out.println("routingKey: <" + message.getMessageProperties().getHeaders().get("routingKey").toString() + ">");
			var routingKey= message.getMessageProperties().getHeaders().get("routingKey").toString();
			rabbit.send("test", routingKey, messageReply);
		}
	}

	public void doSomething(String s)
	{
		response = s;
	}

	public static void main(String[] args) throws InterruptedException
	{
		SpringApplication.run(HelloWorldApplication.class, args);
	}
}