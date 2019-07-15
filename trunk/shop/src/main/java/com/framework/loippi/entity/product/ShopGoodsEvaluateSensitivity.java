package com.framework.loippi.entity.product;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 评价敏感词
 * 
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_GOODS_EVALUATE_SENSITIVITY")
public class ShopGoodsEvaluateSensitivity implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/**  */
	@Column(id = true, name = "id", updatable = false)
	private Long id;
	
	/** 状态（1为启用，0为禁用） */
	@Column(name = "status" )
	private Integer status;
	
	/** 敏感词 */
	@Column(name = "sensitivity" )
	private String sensitivity;
	
	/** 更新时间 */
	@Column(name = "updatetime" )
	private java.util.Date updatetime;
	/**
	 * 查询相关参数
	 */
	private String keyWordId;
	private String searchStartTime;
	private String searchEndTime;
	
}
