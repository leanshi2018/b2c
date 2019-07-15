package com.framework.loippi.entity.user;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity - 会员推荐表
 * 
 * @author dzm
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RD_NEW_VIP_DETAIL")
public class RdNewVipDetail implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 推荐人会员编号 */
	@Column(name = "REFEREES_CODE" )
	private String refereesCode;
	
	/** 推荐人会员昵称 */
	@Column(name = "REFEREES_NICKNAME" )
	private String refereesNickname;
	
	/** 推荐人会员级别 */
	@Column(name = "REFEREES_RANK" )
	private Integer refereesRank;
	
	/** 推荐时间 */
	@Column(name = "REFEREES_TIME" )
	private Date refereesTime;
	
	/** 被推荐人会员编号 */
	@Column(name = "ELECTION_CODE" )
	private String electionCode;
	
	/** 被推荐人会员昵称 */
	@Column(name = "ELECTION_NICKNAME" )
	private String electionNickname;
	
	/** 推荐好友注册数量 */
	@Column(name = "REFEREES_MEM_NUMBER" )
	private Integer refereesMemNumber;
	
	/** 消费金额 */
	@Column(name = "CONSUMPTION" )
	private BigDecimal consumption;
	
	/** 返佣金额 */
	@Column(name = "NEW_VIP_BONUS" )
	private BigDecimal newVipBonus;
	
	/** 返佣时间 */
	@Column(name = "GET_BONUS_TIME" )
	private Date getBonusTime;
	
}
