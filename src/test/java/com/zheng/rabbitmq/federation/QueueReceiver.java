package com.zheng.rabbitmq.federation;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.zheng.rabbitmq.Constants;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Author zhenglian
 * @Date 2018/4/24 16:56
 */
public class QueueReceiver {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost(Constants.HOST);
        factory.setHost(Constants.UPSTREAM_HOST);
        factory.setPort(Constants.PORT);
        factory.setVirtualHost(Constants.V_HOST);
        factory.setUsername(Constants.USER);
        factory.setPassword(Constants.PASSWORD);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String federationQueue = "federationQueue";
        channel.queueDeclare(federationQueue, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                System.out.println(" [x] Received '" + message + "'");
            }
        };
        channel.basicConsume(federationQueue, false, consumer);
    }
}
