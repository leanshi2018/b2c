package com.framework.loippi.entity.user;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 会员地址表
 * 
 * @author dzm
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RD_MM_ADD_INFO")
public class RdMmAddInfo implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** id */
	@Column(id = true, name = "AID")
	private Integer aid;
	
	/** 会员编号 */
	@Column(name = "MM_CODE" )
	private String mmCode;
	
	/** 收货人姓名 */
	@Column(name = "CONSIGNEE_NAME" )
	private String consigneeName;
	
	/** 手机 */
	@Column(name = "MOBILE" )
	private String mobile;
	
	/** 电话 */
	@Column(name = "PHONE" )
	private String phone;
	
	/** 默认地址 */
	@Column(name = "DEFAULTADD" )
	private Integer defaultadd;
	
	/** 有效状态 */
	@Column(name = "VALID" )
	private Integer valid;
	
	/** 省 */
	@Column(name = "ADD_PROVINCE_CODE" )
	private String addProvinceCode;
	
	/** 市 */
	@Column(name = "ADD_CITY_CODE" )
	private String addCityCode;
	
	/** 区县 */
	@Column(name = "ADD_COUNTRY_CODE" )
	private String addCountryCode;
	
	/** 街镇 */
	@Column(name = "ADD_TOWN_CODE" )
	private String addTownCode;
	
	/** 详细地址 */
	@Column(name = "ADD_DETIAL" )
	private String addDetial;
	
	/** 邮编 */
	@Column(name = "ADD_POST" )
	private String addPost;
	
}
