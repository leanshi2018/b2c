package com.framework.loippi.service.wechat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author :ldq
 * @date:2020/3/24
 * @description:dubbo com.framework.loippi.service.wechat
 */
public interface WechatAppletsService {
	String notifyCheck(HttpServletRequest request, HttpServletResponse response, String sn);
}
