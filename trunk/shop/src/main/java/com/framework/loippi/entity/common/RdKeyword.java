package com.framework.loippi.entity.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * 搜索关键词
 * @author :ldq
 * @date:2019/12/31
 * @description:dubbo com.framework.loippi.entity.common
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_keyword")
public class RdKeyword implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 索引ID */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	/** 关键词 */
	private String keyword;
	/** 排序 */
	private Integer sort;
}
