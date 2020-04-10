package com.framework.loippi.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * Entity - 会员基础信息
 * 
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RD_MM_BASIC_INFO")
public class RdMmBasicInfo implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/**  */
	@Column(id = true, name = "MM_ID")
	private Integer mmId;
	
	/** 会员编号 */
	@Column(name = "MM_CODE" )
	private String mmCode;
	
	/** 会员姓名 */
	@Column(name = "MM_NAME" )
	private String mmName;
	
	/** 会员昵称 */
	@Column(name = "MM_NICK_NAME" )
	private String mmNickName;

	/** 会员头像 */
	@Column(name = "MM_AVATAR" )
	private String mmAvatar;
	
	/** 证件类型 1.身份证2.护照3.军官证4.回乡证 */
	@Column(name = "ID_TYPE" )
	private Integer idType;
	
	/** 证件号码 */
	@Column(name = "ID_CODE" )
	private String idCode;
	
	/** 性别 0.男 1.女 */
	@Column(name = "GENDER" )
	private Integer gender;
	
	/** 生日 */
	@Column(name = "BIRTHDATE" )
	private java.util.Date birthdate;
	
	/** 电子邮箱 */
	@Column(name = "EMAIL" )
	private String email;
	
	/** 国家地区:国际标准简写 */
	@Column(name = "NATION_ID" )
	private String nationId;
	
	/** 省 */
	@Column(name = "ADD_PROVINCE_ID" )
	private String addProvinceId;
	
	/** 市 */
	@Column(name = "ADD_CITY_ID" )
	private String addCityId;
	
	/** 区县 */
	@Column(name = "ADD_COUNTRY_ID" )
	private String addCountryId;
	
	/** 街镇 */
	@Column(name = "ADD_TOWN_ID" )
	private String addTownId;
	
	/** 详细地址 */
	@Column(name = "ADD_DETIAL" )
	private String addDetial;
	
	/** 邮编 */
	@Column(name = "ADD_POST" )
	private String addPost;
	
	/** 手机:绑定的手机 */
	@Column(name = "MOBILE" )
	private String mobile;
	
	/** 微信号 */
	@Column(name = "WECHAT_CODE" )
	private String wechatCode;
	
	/** QQ号 */
	@Column(name = "QQ_CODE" )
	private String qqCode;
	
	/** Facebook号 */
	@Column(name = "FACEBOOK_CODE" )
	private String facebookCode;
	
	/** 电话 */
	@Column(name = "PHONE" )
	private String phone;
	
	/** 录入来源1：手机APP端2：公司客服3：旧系统导入4：其他 */
	@Column(name = "CREATION_SOURCE" )
	private String creationSource;
	
	/** 来源IP地址 */
	@Column(name = "CREATION_IP" )
	private String creationIp;
	
	/** 录入时间 */
	@Column(name = "CREATION_DATE" )
	private java.util.Date creationDate;
	
	/** 录入人 */
	@Column(name = "CREATION_BY" )
	private String creationBy;
	
	/** 录入业务周期:YYYYMM */
	@Column(name = "CREATION_PERIOD" )
	private String creationPeriod;
	
	/** 最后更新来源 */
	@Column(name = "UPDATE_SOURCE" )
	private String updateSource;
	
	/** 更新来源IP地址 */
	@Column(name = "UPDATE_IP" )
	private String updateIp;
	
	/** 最后更新日期 */
	@Column(name = "UPDATE_DATE" )
	private java.util.Date updateDate;
	
	/** 最后更新人 */
	@Column(name = "UPDATE_BY" )
	private String updateBy;
	
	/** 最后更新业务周期:YYYYMM */
	@Column(name = "UPDATE_PERIOD" )
	private String updatePeriod;

	/** 分享次数 */
	@Column(name = "SHARE_NUM" )
	private Integer shareNum;

	/** 推送状态 1 可以进行推送 2 不可以进行推送 */
	@Column(name = "PUSH_STATUS" )
	private Integer pushStatus;

	/** 关联通联支付id */
	@Column(name = "TONG_LIAN_ID" )
	private String tongLianId;

	/** 通联支付实名制认证标识 0：未认证 1：已通过认证 */
	@Column(name = "WHETHER_TURE_NAME" )
	private Integer whetherTrueName;

	/** 通联支付实名制认证身份证号 */
	@Column(name = "TURE_ID" )
	private String trueId;

	/** 通联支付实名制姓名 */
	@Column(name = "TURE_NAME" )
	private String trueName;

	/** 0:未签约 1：已签约  通联支付签约状态 2.已签约但未开启自动提现*/
	@Column(name = "ALL_IN_CONTRACT_STATUS" )
	private Integer allInContractStatus;

	/** 通联支付签约成功返回签约码*/
	@Column(name = "CONTRACT_NO" )
	private String contractNo;

	/** 0:未绑定通联支付手机号 1：已绑定 2：已解绑（解绑后未绑定新的手机号）*/
	@Column(name = "ALL_IN_PAY_PHONE_STATUS" )
	private Integer allInPayPhoneStatus;

	/** 通联支付绑定的手机号码*/
	@Column(name = "ALL_IN_PAY_PHONE" )
	private String allInPayPhone;

	private String info;
	private String verificationMobile;
	private String verificationNickName;
	private List<String> mmCodes;
}
