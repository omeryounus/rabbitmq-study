package com.zheng.rabbitmq.tracing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.zheng.rabbitmq.Constants;

import java.nio.charset.StandardCharsets;

/**
 * 需要通过rabbitmqctl 将对应的vhost开启消息跟踪
 * rabbitmqctl trace_on
 * 如此在这个虚拟主机上的发送的消息都会被发布到amq.rabbitmq.trace exchange中
 * @Author zhenglian
 * @Date 2018/4/24 16:45
 */
public class TraceSender {
    
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Constants.HOST);
        factory.setPort(Constants.PORT);
        factory.setVirtualHost(Constants.V_HOST);
        factory.setUsername(Constants.USER);
        factory.setPassword(Constants.PASSWORD);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(Constants.QUEUE_NAME, false, false, false, null);
        String message = "Hello Trace!";
        channel.basicPublish("", Constants.QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
        System.out.println(" [x] Sent '" + message + "'");

        channel.close();
        connection.close();
    }
}
