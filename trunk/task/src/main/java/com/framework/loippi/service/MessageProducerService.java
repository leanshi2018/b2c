package com.framework.loippi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 *
 * @author tangzz
 * @createDate 2016年1月15日
 */
public interface MessageProducerService {

    public void sendMessage(Object message);
}  