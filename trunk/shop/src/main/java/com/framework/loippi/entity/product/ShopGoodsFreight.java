package com.framework.loippi.entity.product;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity - 地区运费表
 * 
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_GOODS_FREIGHT")
public class ShopGoodsFreight implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 地区运费id */
	@Column(id = true, name = "id", updatable = false)
	private Long id;
	
	/** 地区id(只到省级别) */
	@Column(name = "area_id" )
	private Long areaId;

	/** 地区名称(只到省级别) */
	@Column(name = "area_name" )
	private String areaName;

	/** 首重运费 */
	@Column(name = "first_weight" )
	private BigDecimal firstWeight;
	
	/** 次重运费 */
	@Column(name = "additional_weight" )
	private BigDecimal additionalWeight;
	
	/** 创建时间 */
	@Column(name = "create_time" )
	private Date createTime;
	
	/** 修改时间 */
	@Column(name = "update_time" )
	private Date updateTime;
	
	/** 备注 */
	@Column(name = "remarks" )
	private String remarks;

    //用于搜索
	private String areaNameLike;
	
}
