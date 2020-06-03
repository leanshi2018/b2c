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
 * Entity - 会员关系状态表
 * 
 * @author dzm
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RD_MM_RELATION")
public class RdMmRelation implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 会员编号 */
	@Column(name = "MM_CODE" )
	private String mmCode;
	
	/** 会员级别:参考级别体系 */
	@Column(name = "RANK" )
	private Integer rank;
	
	/** 当前计算后级别 */
	@Column(name = "RANK_NOW" )
	private Integer rankNow;

	/** 升级业务期:YYYYMM */
	@Column(name = "RANK_PERIOD" )
	private String rankPeriod;
	
	/** 累计按零售价购买额:用于判断是否可 以升VIP会员 */
	@Column(name = "A_RETAIL" )
	private BigDecimal aRetail;

	/** 用户已购买PPV的总计:用于判断是否可 以升级 */
	@Column(name = "A_PPV" )
	private BigDecimal aPpv;
	
	/** 登录密码:加密保存 */
	@Column(name = "LOGIN_PWD" )
	private String loginPwd;
	
	/** 是否初始密码:
0，登录时需修改密码
 1，不需修改 */
	@Column(name = "PWD_INIT_YN" )
	private Integer pwdInitYn;
	
	/** 会员状态:0正常1冻结2注销 */
	@Column(name = "MM_STATUS" )
	private Integer mmStatus;
	
	/** 会员积分状态:0正常1冻结2未激活 */
	@Column(name = "MM_POINT_STATUS" )
	private Integer mmPointStatus;
	
	/** 状态变更业务期：YYYYMM */
	@Column(name = "STATUS_PERIOD" )
	private String statusPeriod;
	
	/** 推荐人编号 */
	@Column(name = "SPONSOR_CODE" )
	private String sponsorCode;
	
	/** 推荐人姓名 */
	@Column(name = "SPONSOR_NAME" )
	private String sponsorName;
	
	/** 放置人编号 */
	@Column(name = "PLACEMENT_CODE" )
	private String placementCode;
	
	/** 旧系统转来推荐人:
0：临时状态
1：永久状态 */
	@Column(name = "RA_SPONSOR_STATUS" )
	private Integer raSponsorStatus;
	
	/** 关联公司绑定状态：
0:未绑定
1：已绑定 */
	@Column(name = "RA_STATUS" )
	private Integer raStatus;
	
	/** 老系统会员开店状态0：未开店1：已开店 */
	@Column(name = "RA_SHOP_YN" )
	private Integer raShopYn;
	
	/** 关联公司会员号 */
	@Column(name = "RA_CODE" )
	private String raCode;
	
	/** 关联公司昵称 */
	@Column(name = "RA_NICK_NAME" )
	private String raNickName;
	
	/** 绑定日期 */
	@Column(name = "RA_BINDING_DATE" )
	private Date raBindingDate;

	/** 是否获得过新晋vip奖励  0:没有获得(默认) 1:已经获得 */
	@Column(name = "IS_VIP" )
	private Integer isVip;

	/** 新会员注册老会员注册标识 新会员：1 老会员：2 */
	@Column(name = "N_O_FLAG" )
	private Integer nOFlag;

	/** 用户累计购买额 */
	@Column(name = "A_TOTAL" )
	private BigDecimal aTotal;

	/** 是否弹窗级别解释 */
	@Column(name = "popup_flag" )
	private Integer popupFlag;

	/** 最近下单时间 */
	@Column(name = "last_pay_time" )
	private Date lastPayTime;
}
