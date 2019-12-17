package com.framework.loippi.entity.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * @author :ldq
 * @date:2019/12/13
 * @description:dubbo com.framework.loippi.entity.common
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shop_common_express_notarea")
public class ShopCommonExpressNotArea implements GenericEntity {
	private static final long serialVersionUID = 5081846432919091193L;

	/**
	 * 索引ID
	 */
	private Long id;

	/**
	 * 快递ID
	 */
	private Long expressId;
	/**
	 * 地区ID
	 */
	private Long areaId;
	/**
	 * 地区名称
	 */
	private String areaName;
	/**
	 * 地区深度，从1开始
	 */
	private Integer areaDeep;
}
