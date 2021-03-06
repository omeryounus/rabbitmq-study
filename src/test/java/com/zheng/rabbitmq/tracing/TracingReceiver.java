package com.zheng.rabbitmq.tracing;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.zheng.rabbitmq.Constants;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 对于这种发布订阅模型，需要消费者首先注册，目前创建的是非持久化的订阅
 *
 * @Author zhenglian
 * @Date 2018/4/25 23:53
 */
public class TracingReceiver {
    private static final String ROUTING_KEY = "#";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Constants.HOST);
        factory.setPort(Constants.PORT);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(Constants.EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true,
                false, true, null);
        // 这里没有指定queueName，由broker提供channel范围内唯一的名称
        String queueName = channel.queueDeclare().getQueue();
        System.out.println("queue name: " + queueName);
        // 将queue与exchange进行绑定，从而获取exchange中的内容
        channel.queueBind(queueName, Constants.EXCHANGE_NAME, ROUTING_KEY);

        System.out.println(" [*] 1 Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                Map<String, Object> headers = properties.getHeaders();
                for (Map.Entry<String, Object> entry : headers.entrySet()) {
                    System.out.println(entry.getKey() + ":" + entry.getValue());
                }
                System.out.println("routing key: " + envelope.getRoutingKey());
                System.out.println(" [x] Received '" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}
