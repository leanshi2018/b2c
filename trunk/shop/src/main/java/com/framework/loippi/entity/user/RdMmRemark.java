package com.framework.loippi.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * @author :ldq
 * @date:2020/12/3
 * @description:dubbo com.framework.loippi.entity.user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shop_gift_activity")
public class RdMmRemark implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;
	/**
	 * 主键id索引
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	/**
	 * 会员编号
	 */
	private String mmCode;
	/**
	 * 分销会员编号
	 */
	private String spCode;
	/**
	 * 备注名称
	 */
	private String remarkName;
}
