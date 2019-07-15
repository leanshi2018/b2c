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
//import java.util.Date;
//
///**
// * Entity - 会员等级管理
// *
// * @author dzm
// * @version 2.0
// */
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "SHOP_MEMBER_GRADE")
//public class ShopMemberGrade implements GenericEntity {
//
//	private static final long serialVersionUID = 5081846432919091193L;
//
//	/** 索引id */
//	@Column(id = true, name = "id", updatable = false)
//	private Long id;
//
//	/** 等级名称 */
//	@Column(name = "grade_name" )
//	private String gradeName;
//
//	/** pv值 */
//	@Column(name = "ppv" )
//	private Integer ppv;
//
//	/** 低消额度 */
//	@Column(name = "offset_amount" )
//	private java.math.BigDecimal offsetAmount;
//
//	/** 1 零售 2会员价 3pv值 4 pv大单价 多个用逗号隔开 */
//	@Column(name = "look_goods_info" )
//	private String lookGoodsInfo;
//
//	/** 会员等级 */
//	@Column(name = "level" )
//	private Integer level;
//
//	/** 可选择的订单类型id集合 ,号隔开 */
//	@Column(name = "order_discount_ids" )
//	private String orderDiscountIds;
//
//	/** 备注 */
//	@Column(name = "remarks" )
//	private String remarks;
//	/** 创建时间 */
//	@Column(name = "create_time" )
//	private Date createTime;
//
//	/** 修改时间 */
//	@Column(name = "update_time" )
//	private Date updateTime;
//
//	/** 人数 */
//	@Column(name = "number" )
//	private Integer number;
//}
