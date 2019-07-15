package com.framework.loippi.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 网页授权信息
 * @author ihui
 * 
 */
@Data
@ToString
public class WeiXinOauth2Token implements Serializable{

	private static final long serialVersionUID = 2657083907635131384L;
	//网页授权接口调用凭证
	private String accessToken;
	//凭证有效时长
	private Integer expriesIn;
	//用于刷新凭证
	private String refreshToken;
	//用户标示
	private String OpenId;
	//用户授权作用域
	private String scope;
}
