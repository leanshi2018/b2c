package com.framework.loippi.entity.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * @author :ldq
 * @date:2020/8/21
 * @description:dubbo com.framework.loippi.entity.common
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shop_product_recommendation")
public class ShopProductRecommendation implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/**
	 * 推荐页id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	/**
	 * 推荐页名称
	 */
	private String recommendationName;

	/**
	 * 推荐页图片路径
	 */
	private String pictureUrl;

	/**
	 * 状态
	 * 0 禁用 1开启
	 */
	private Integer auditStatus;

	/**
	 * 创建时间
	 */
	private Date createTime;

}
