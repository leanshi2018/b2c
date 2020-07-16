package com.framework.loippi.entity.travel;

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
 * @date:2020/7/7
 * @description:dubbo com.framework.loippi.entity.common
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_tourism_compliance")
public class RdTourismCompliance implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 索引ID */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	/** 会员编号 */
	private String mmCode;
	/** 一级奖励考核： 0不合格 ，1合格，2已发放 */
	private Integer oneQualify;
	/** 一级奖励发放时间 */
	private Date oneGrantTime;
	/** 二级奖励考核： 0不合格 ，1合格，2已发放 */
	private Integer twoQualify;
	/** 二级奖励发放时间 */
	private Date twoGrantTime;
	/** 三级奖励考核： 0不合格 ，1合格，2已发放 */
	private Integer threeQualify;
	/** 三级奖励发放时间 */
	private Date threeGrantTime;
	/** 保留资格： 0无 ，1保留一级奖励 ，2保留二级奖励，3保留三级奖励 */
	private Integer keepQualify;

}
