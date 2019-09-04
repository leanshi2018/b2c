package com.framework.loippi.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * @author :ldq
 * @date:2019/9/4
 * @description:dubbo com.framework.loippi.entity.user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RD_MM_BANK_DISCERN")
public class RdMmBankDiscern  implements GenericEntity {

	/**  */
	@Column(id = true, name = "ID", updatable = false)
	private Integer id;

	/** 会员编号 */
	@Column(name = "MM_CODE" )
	private String mmCode;

	/** 识别日期 */
	@Column(name = "DISCERN_DATE" )
	private Date discernDate;

	/** 当天识别次数 */
	@Column(name = "NUM_TIMES" )
	private Integer numTimes;
}
