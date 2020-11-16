package com.framework.loippi.service;

import com.framework.loippi.enus.ShunFengOperation;

/**
 * @author :ldq
 * @date:2020/11/3
 * @description:dubbo com.framework.loippi.service
 */
public interface ShunFengJsonExpressService {
	String shunFengOperationProcessor(Object requestContent, ShunFengOperation creteOrder);
}
