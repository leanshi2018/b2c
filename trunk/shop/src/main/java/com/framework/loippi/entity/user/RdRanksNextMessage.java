package com.framework.loippi.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * @author :ldq
 * @date:2020/5/21
 * @description:dubbo com.framework.loippi.entity.user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_ranks_next_message")
public class RdRanksNextMessage implements GenericEntity {
	private static final long serialVersionUID = 5081846432919091193L;

	private Long id;

	private Integer rank;//等级标识

	private String message;//升下一级说明

}
