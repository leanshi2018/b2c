package com.framework.loippi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WxJSApiResult {

	private String appId;

	private String timestamp;

	private String nonceStr;

	private String signature;

}
