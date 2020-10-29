package com.framework.loippi.utils.wechat.h5.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author :ldq
 * @date:2020/10/28
 * @description:dubbo com.framework.loippi.utils.wechat.h5.pojo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignResult {

	private String appId;
	private String appsecret;
	private Long timestamp;
	private String nonceStr;
	private String signature;
	private String sign;
	private String ticket;
}
