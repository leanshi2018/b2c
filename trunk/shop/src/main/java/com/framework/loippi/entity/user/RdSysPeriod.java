package com.framework.loippi.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * Entity - 业务周期
 * 
 * @author dzm
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RD_SYS_PERIOD")
public class RdSysPeriod implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 当前业绩期，YYYYMM */
	@Column(name = "PERIOD_CODE" )
	private String periodCode;
	
	/** 前一业绩期 */
	@Column(name = "PRE_PERIOD" )
	private String prePeriod;
	
	/** 下一业绩期 */
	@Column(name = "NEXT_PERIOD" )
	private String nextPeriod;
	
	/** 业绩状态
0：未开始
1：已开始
2：外部关闭补录中
3：已关闭 */
	@Column(name = "SALES_STATUS" )
	private Integer salesStatus;
	
	/** 奖金状态
0：未开始
1：计算中
2：临时发布核对中
3：正式发布 */
	@Column(name = "CAL_STATUS" )
	private Integer calStatus;
	
	/** 业绩开始日期 */
	@Column(name = "BEGIN_DATE" )
	private Date beginDate;
	
	/** 业绩结束日期
（两业绩期间不允许重叠，可以有空档） */
	@Column(name = "END_DATE" )
	private Date endDate;
	
	/** 计算次数 */
	@Column(name = "CAL_TIMES" )
	private Integer calTimes;
	
	/** 发放状态
0：未发出
1：已发出 */
	@Column(name = "BONUS_STATUS" )
	private Integer bonusStatus;

	/** pv与人民币之间的比例 */
	@Column(name = "PV_PROPORTION" )
	private Double pvProportion;

}
