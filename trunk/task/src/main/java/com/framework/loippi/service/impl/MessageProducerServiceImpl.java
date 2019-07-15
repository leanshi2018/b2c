package com.framework.loippi.service.impl;

import com.framework.loippi.service.MessageProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 基于redis的事件发布
 *
 * @author tangzz
 * @createDate 2016年1月15日
 */
@Service
public class MessageProducerServiceImpl implements MessageProducerService {

    private Logger logger = LoggerFactory.getLogger(MessageProducerServiceImpl.class);

    @Resource
    private AmqpTemplate amqpTemplate;

    public void sendMessage(Object message){
        logger.info("to send message:{}",message);
        System.err.println("to send message:{}"+message);
        amqpTemplate.convertAndSend("queueTestKey",message);
    }
}  