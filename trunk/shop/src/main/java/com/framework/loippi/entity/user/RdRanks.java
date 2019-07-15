package com.framework.loippi.entity.user;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 会员等级
 * 
 * @author dzm
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RD_RANKS")
public class RdRanks implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** ID */
	@Column(name = "RANK_ID" )
	private Integer rankId;
	
	/** 级别名称 */
	@Column(name = "RANK_NAME" )
	private String rankName;
	
	/** 级别简称 */
	@Column(name = "RANK_SHORT_NAME" )
	private String rankShortName;
	
	/** 级别序号 */
	@Column(name = "RANK_ORDER" )
	private Integer rankOrder;
	
	/** 级别的等级
0：无级别
1：VIP级
2：代理级
3：店级
4：旗舰店级 */
	@Column(name = "RANK_CLASS" )
	private Integer rankClass;
	
	/** 是否固定
0：不固定，可升降
1：固定，只升不降 */
	@Column(name = "STATIC_YN" )
	private Integer staticYn;
	
	/** 前置级别（要想达到本级别，至少要先达到的级别） */
	@Column(name = "R_PRE_RANK" )
	private Integer rPreRank;
	
	/** 当月重消PV要求 */
	@Column(name = "ACTIVE_PV" )
	private Integer activePv;
	
	/** 级别可保持的月数 */
	@Column(name = "RANK_KEEP_MONTHS" )
	private Integer rankKeepMonths;
	
}
