package com.framework.loippi.rabbitmq;

import org.osgi.service.io.ConnectionFactory;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.io.Serializable;

/**
 * 基于rabbitmq的事件发布
 *
 * @author tangzz
 * @createDate 2016年1月15日
 */
public class Producer extends EndPoint{

    public Producer(String endPointName) throws IOException{
        super(endPointName);
    }

    public void sendMessage(Serializable object) throws IOException {
        channel.basicPublish("",endPointName, null, SerializationUtils.serialize(object));
    }
}