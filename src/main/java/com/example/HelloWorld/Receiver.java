package com.example.HelloWorld;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class Receiver {
    
    private final RabbitTemplate rabbitTemplate = new RabbitTemplate();

    private CountDownLatch latch = new CountDownLatch(1);

    public void receiveMessage(String message)
    {
        System.out.println("Received <" + message + ">");
        latch.countDown();
        rabbitTemplate.convertAndSend("test_rpc", "test_rpc", message);
    }

    public CountDownLatch getLatch()
    {
        return latch;
    }

}
