package com.framework.loippi.consts;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 通过网也授权获取用户信息
 * @author ihui
 * 
 */
@Data
@ToString
public class SNSUserINfo implements Serializable{
	private static final long serialVersionUID = 8390686586830673035L;
    //用户标示
	private String openId;
	//用户昵称
	private String nickname;
	//性别 (1男性，2女性 ，0未知)
	private Integer sex;
	//国家
	private String country;
	//省份
	private String province;
	//城市
	private String city;
	//用户头像链接
	private String headImgUrl;
	//用户特权信息
	private List<String> privilegeList;
	//如果 用户绑定用户将公众号绑定到微信开放平台帐号后，才会出现该字段
	private String unionid;
	
}
