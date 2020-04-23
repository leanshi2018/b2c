package com.framework.loippi.entity.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * @author :ldq
 * @date:2020/4/21
 * @description:dubbo com.framework.loippi.entity.common
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shop_auto_ship")
public class ShopAutoShip implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 索引ID */
	private Long id;
	/** 是否自动发货 0.否 1.是 */
	private Integer shipStatus;

}
