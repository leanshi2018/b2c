//package com.framework.loippi.entity.user;
//
//import com.framework.loippi.mybatis.eitity.GenericEntity;
//import com.framework.loippi.mybatis.ext.annotation.Column;
//import com.framework.loippi.mybatis.ext.annotation.Table;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
///**
// * Entity - 买家地址信息表
// *
// * @author zijing
// * @version 2.0
// */
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "SHOP_MEMBER_ADDRESS")
//public class ShopMemberAddress implements GenericEntity {
//
//	private static final long serialVersionUID = 5081846432919091193L;
//
//	/** 地址ID */
//	@Column(id = true, name = "id", updatable = false)
//	private Long id;
//
//	/** 会员ID */
//	@Column(name = "member_id" )
//	private Long memberId;
//
//	/** 会员姓名 */
//	@Column(name = "true_name" )
//	private String trueName;
//
//	/** 省级id */
//	@Column(name = "province_id" )
//	private Long provinceId;
//
//	/** 市级ID */
//	@Column(name = "city_id" )
//	private Long cityId;
//
//	/** 地区ID */
//	@Column(name = "area_id" )
//	private Long areaId;
//
//	/** 地区内容 */
//	@Column(name = "area_info" )
//	private String areaInfo;
//
//	/** 地址 */
//	@Column(name = "address" )
//	private String address;
//
//	/** 座机电话 */
//	@Column(name = "tel_phone" )
//	private String telPhone;
//
//	/** 手机电话 */
//	@Column(name = "mob_phone" )
//	private String mobPhone;
//
//	/** 是否为默认地址1:是0:否 */
//	@Column(name = "is_default" )
//	private Integer isDefault;
//
//	/** 邮编 */
//	@Column(name = "zip_code" )
//	private String zipCode;
//
//}
