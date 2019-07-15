package com.framework.loippi.entity.product;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Entity - 运费规则表
 * 
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_GOODS_FREIGHT_RULE")
public class ShopGoodsFreightRule implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 运费规则id */
	@Column(id = true, name = "id", updatable = false)
	private Long id;
	
	/** 用户等级id */
	@Column(name = "member_grade_id" )
	private Long memberGradeId;
	
	/** 用户等级名称 */
	@Column(name = "member_grade_name" )
	private String memberGradeName;
	
	/** 最大订单金额 */
	@Column(name = "maximum_order_amount" )
	private BigDecimal maximumOrderAmount;
	
	/** 最小订单金额 */
	@Column(name = "minimum_order_amount" )
	private BigDecimal minimumOrderAmount;
	
	/** 优惠金额 */
	@Column(name = "preferential" )
	private BigDecimal preferential;
	
	/** 创建时间 */
	@Column(name = "create_time" )
	private Date createTime;
	
	/** 修改时间 */
	@Column(name = "update_time" )
	private Date updateTime;

	private BigDecimal goodsTotalAmount;

	private BigDecimal maxIsNull;

	public List<ShopGoodsFreightRule> shopGoodsFreightRules;
	
}
