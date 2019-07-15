package com.framework.loippi.entity.user;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 会员修改记录信息
 * 
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RD_MM_EDIT")
public class RdMmEdit implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/**  */
	@Column(id = true, name = "EID", updatable = false)
	private Integer eid;
	
	/** 会员编号 */
	@Column(name = "MM_CODE" )
	private String mmCode;
	
	/** 原姓名 */
	@Column(name = "MM_NAME_BEFORE" )
	private String mmNameBefore;
	
	/** 新姓名 */
	@Column(name = "MM_NAME_AFTER" )
	private String mmNameAfter;
	
	/** 原证件类型 1.身份证2.护照3.军官证4.回乡证 */
	@Column(name = "ID_TYPE_BEFORE" )
	private Integer idTypeBefore;
	
	/** 新证件类型 1.身份证2.护照3.军官证4.回乡证 */
	@Column(name = "ID_TYPE_AFTER" )
	private Integer idTypeAfter;
	
	/** 原证件号码 */
	@Column(name = "ID_CODE_BEFORE" )
	private String idCodeBefore;
	
	/** 新证件号码 */
	@Column(name = "ID_CODE_AFTER" )
	private String idCodeAfter;
	
	/** 原昵称 */
	@Column(name = "MM_NICK_NAME_BEFORE" )
	private String mmNickNameBefore;
	
	/** 新昵称 */
	@Column(name = "MM_NICK_NAME_AFTER" )
	private String mmNickNameAfter;
	
	/** 原性别 0.男 1.女 */
	@Column(name = "GENDER_BEFORE" )
	private Integer genderBefore;
	
	/** 新性别 0.男 1.女 */
	@Column(name = "GENDER_AFTER" )
	private Integer genderAfter;
	
	/** 原手机号码 */
	@Column(name = "MOBILE_BEFORE" )
	private String mobileBefore;
	
	/** 新手机号码 */
	@Column(name = "MOBILE_AFTER" )
	private String mobileAfter;
	
	/** 原微信号 */
	@Column(name = "WECHAT_CODE_BEFORE" )
	private String wechatCodeBefore;
	
	/** 新微信号 */
	@Column(name = "WECHAT_CODE_AFTER" )
	private String wechatCodeAfter;
	
	/** 原邮箱 */
	@Column(name = "EMAIL_BEFORE" )
	private String emailBefore;
	
	/** 新邮箱 */
	@Column(name = "EMAIL_AFTER" )
	private String emailAfter;
	
	/** 原邮编 */
	@Column(name = "ADD_POST_BEFORE" )
	private String addPostBefore;
	
	/** 新邮编 */
	@Column(name = "ADD_POST_AFTER" )
	private String addPostAfter;
	
	/** 原省 */
	@Column(name = "ADD_PROVINCE_ID_BEFORE" )
	private String addProvinceIdBefore;
	
	/** 新省 */
	@Column(name = "ADD_PROVINCE_ID_AFTER" )
	private String addProvinceIdAfter;
	
	/** 原市 */
	@Column(name = "ADD_CITY_ID_BEFORE" )
	private String addCityIdBefore;
	
	/** 新市 */
	@Column(name = "ADD_CITY_ID_AFTER" )
	private String addCityIdAfter;
	
	/** 原区 */
	@Column(name = "ADD_COUNTRY_ID_BEFORE" )
	private String addCountryIdBefore;
	
	/** 新区 */
	@Column(name = "ADD_COUNTRY_ID_AFTER" )
	private String addCountryIdAfter;
	
	/** 原详细地址 */
	@Column(name = "ADD_DETIAL_BEFORE" )
	private String addDetialBefore;
	
	/** 新详细地址 */
	@Column(name = "ADD_DETIAL_AFTER" )
	private String addDetialAfter;
	
	/** 原银行详细信息 */
	@Column(name = "BANK_DETAIL_BEFORE" )
	private String bankDetailBefore;
	
	/** 新银行详细信息 */
	@Column(name = "BANK_DETAIL_AFTER" )
	private String bankDetailAfter;
	
	/** 原开户名 */
	@Column(name = "ACC_NAME_BEFORE" )
	private String accNameBefore;
	
	/** 新开户名 */
	@Column(name = "ACC_NAME_AFTER" )
	private String accNameAfter;
	
	/** 原账户号码 */
	@Column(name = "ACC_CODE_BEFORE" )
	private String accCodeBefore;
	
	/** 新账户号码 */
	@Column(name = "ACC_CODE_AFTER" )
	private String accCodeAfter;
	
	/** 原默认收款方式 */
	@Column(name = "WITHDRAW_DEFAULT_BEFORE" )
	private String withdrawDefaultBefore;
	
	/** 新默认收款方式 */
	@Column(name = "WITHDRAW_DEFAULT_AFTER" )
	private String withdrawDefaultAfter;
	
	/** 原推荐人编号 */
	@Column(name = "SPONSOR_CODE_BEFORE" )
	private String sponsorCodeBefore;
	
	/** 新推荐人编号 */
	@Column(name = "SPONSOR_CODE_AFTER" )
	private String sponsorCodeAfter;
	
	/** 原推荐人姓名 */
	@Column(name = "SPONSOR_NAME_BEFORE" )
	private String sponsorNameBefore;
	
	/** 新推荐人姓名 */
	@Column(name = "SPONSOR_NAME_AFTER" )
	private String sponsorNameAfter;
	
	/** 原会员级别 */
	@Column(name = "RANK_BEFORE" )
	private Integer rankBefore;
	
	/** 新会员级别 */
	@Column(name = "RANK_AFTER" )
	private Integer rankAfter;
	
	/** 原老会员账号 */
	@Column(name = "RA_CODE_BEFORE" )
	private String raCodeBefore;
	
	/** 新老会员账号 */
	@Column(name = "RA_CODE_AFTER" )
	private String raCodeAfter;
	
	/** 原老会员姓名 */
	@Column(name = "RA_NAME_BEFORE" )
	private String raNameBefore;
	
	/** 新老会员姓名 */
	@Column(name = "RA_NAME_AFTER" )
	private String raNameAfter;
	
	/** 老系统身份证类型 */
	@Column(name = "RA_ID_TYPE_BEFORE" )
	private Integer raIdTypeBefore;
	
	/** 新老系统身份证类型 */
	@Column(name = "RA_ID_TYPE_AFTER" )
	private Integer raIdTypeAfter;
	
	/** 老系统身份证号码 */
	@Column(name = "RA_ID_CODE_BEFORE" )
	private String raIdCodeBefore;
	
	/** 新老系统身份证号码 */
	@Column(name = "RA_ID_CODE_AFTER" )
	private String raIdCodeAfter;
	
	/** 修改人 */
	@Column(name = "UPDATE_BY" )
	private String updateBy;
	
	/** 修改备注 */
	@Column(name = "UPDATE_MEMO" )
	private String updateMemo;
	
	/** 修改类型：0：修改基本信息1.修改敏感信息2.会员更名3.更改推荐人4.更改会员级别5.与老会员绑定 */
	@Column(name = "UPDATE_TYPE" )
	private Integer updateType;
	
	/** 修改时间 */
	@Column(name = "UPDATE_TIME" )
	private java.util.Date updateTime;
	
	/** 图片地址 */
	@Column(name = "UPLOAD_PATH" )
	private String uploadPath;
	
	/** 审核备注 */
	@Column(name = "REVIEW_MEMO" )
	private String reviewMemo;
	
	/** 审核状态 0:待审 1:驳回 2：审核通过 3：无需审核 */
	@Column(name = "REVIEW_STATUS" )
	private Integer reviewStatus;
	
}
