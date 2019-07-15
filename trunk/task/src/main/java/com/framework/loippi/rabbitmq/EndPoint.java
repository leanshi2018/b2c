package com.framework.loippi.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 基于rabbitmq的事件发布
 *
 * @author tangzz
 * @createDate 2016年1月15日
 */
public class EndPoint {

    protected Channel channel;
    protected Connection connection;
    protected String endPointName;

    public EndPoint(String endpointName) throws IOException{
        this.endPointName = endpointName;

        //Create a connection factory
        ConnectionFactory factory = new ConnectionFactory();

        //hostname of your rabbitmq server
        factory.setHost("47.106.134.89");
        factory.setPort(3306);
        factory.setUsername("guest");
        factory.setPassword("guest");

        //getting a connection
        try {
            connection = factory.newConnection();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        //creating a channel
        channel = connection.createChannel();

        //declaring a queue for this channel. If queue does not exist,
        //it will be created on the server.
        channel.queueDeclare(endpointName, false, false, false, null);
    }


    /**
     * 关闭channel和connection。并非必须，因为隐含是自动调用的。
     * @throws IOException
     */
    public void close() throws IOException {
        try {
            this.channel.close();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        this.connection.close();
    }

}
