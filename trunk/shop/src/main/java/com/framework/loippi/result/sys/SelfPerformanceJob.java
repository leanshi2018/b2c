package com.framework.loippi.result.sys;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author :ldq
 * @date:2020/5/14
 * @description:dubbo com.framework.loippi.job
 */
@Data
public class SelfPerformanceJob {

	/**
	 * 统计周期
	 */
	private String periodCode;
	/**
	 * 会员编号
	 */
	private String mmCode;
	/**
	 * 会员姓名
	 */
	private String mName;
	/**
	 * 推荐人编号
	 */
	private String sponsorCode;
	/**
	 * 推荐人姓名
	 */
	private String sponsorName;
	/**
	 * 当期会员的状态
	 */
	private Integer mStatus;
	/**
	 * 当月个人消费是否合格\r\n0：不合格\r\n1：合格
	 */
	private Integer ppvQualified;
	/**
	 * 当期会员级别 rd_mm_relation RANK
	 */
	private Integer rankStr;
	/**
	 * 当期预估会员级别 RANK_P0
	 */
	private Integer rankP0Str;
	/**
	 * 历史最高级别
	 */
	private Integer rankRecordHighStr;
	/**
	 * 累计购货额 rd_mm_relation A_TOTAL
	 */
	private BigDecimal totalMoney;
	/**
	 * 当前周期购货额
	 */
	//private BigDecimal periodMoney;
	/**
	 * 个人零售购买额
	 */
	private BigDecimal retail;
	/**
	 * 当期购买MI值  ppv
	 */
	private BigDecimal bugMi;
	/**
	 * 期末个人累计MI值  appvFinal
	 */
	private BigDecimal bugMiFinal;
	/**
	 * 小组MI值
	 */
	private BigDecimal g7pvMi;
	/**
	 * 整组MI值
	 */
	private BigDecimal npvMi;
	/**
	 * 新晋VIP人数  当期直邀VIP人数
	 */
	private int addVIPNumber;
	/**
	 * 新晋VIP人数  累计直邀VIP人数
	 */
	private Integer ddRank1Number;
	/**
	 * 网络新vip人数 当期整组新vip人数
	 */
	private Integer netNewVipNumber;
	/**
	 * 直接推荐复消合格人数 （直销代理人数-当期活跃）
	 */
	private Integer ddAcNumber;
	/**
	 * 直接推荐代理的人数，代理级别为2（直销代理人数-累积直邀）
	 */
	private Integer ddRank2Number;
	/**
	 * 团队重复消费合格人数   整组重消合格人数
	 */
	private Integer netAcNumber;
	/**
	 * 当期新推VIP积分  rd_bonus_master.bonus_new_vip
	 */
	private BigDecimal bonusNewVIP;
	/**
	 * 当期服务积分 rd_bonus_master.bonus_devp_1
	 */
	private BigDecimal bonusDevp1;
	/**
	 * 当期辅导积分  bonus_devp_2
	 */
	private BigDecimal bonusDevp2;
	/**
	 * 当期分红积分  bonus_devp_share
	 */
	private BigDecimal bonusDevpShare;
	/**
	 * 当期领导积分  bonus_ld_sum
	 */
	private BigDecimal bonusLdSum;
	/**
	 * 当期总积分  bonus_sum
	 */
	private BigDecimal bonusSum;

	//前端暂不显示

	/**
	 * 个人消费合格
	 */
	private Integer hPpvQualified;
	/**
	 * 下级4级会员网络条数  直邀一级代理店数
	 */
	private Integer dlRank4Number;
	/**
	 * 下级5级会员网络条数  直邀二级代理店数
	 */
	private Integer dlRank5Number;
	/**
	 * 下属3级代理店网络数  直邀三级代理店数
	 */
	private Integer dlRank6Number;
	/**
	 * 下属旗舰店网络数  直邀旗舰店数
	 */
	private Integer dlRank7Number;
	/**
	 * 下属高级旗舰店网络数  直邀高级旗舰店数
	 */
	private Integer dlRank8Number;


}
