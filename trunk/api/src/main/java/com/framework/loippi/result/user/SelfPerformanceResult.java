package com.framework.loippi.result.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.framework.loippi.entity.user.MemberQualification;
import com.framework.loippi.entity.user.RdBonusMaster;
import com.framework.loippi.entity.user.RdMmBasicInfo;

/**
 * @author :ldq
 * @date:2019/8/14
 * @description:dubbo com.framework.loippi.result.user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelfPerformanceResult {

	/**
	 * 会员编号
	 */
	private String mmCode;
	/**
	 * 会员昵称
	 */
	private String mmNickName;
	/**
	 * 当期会员级别  RANK_AC
	 */
	private String rankStr;
	/**
	 * 当期购买MI值
	 */
	private BigDecimal bugMi;
	/**
	 * 小组MI值
	 */
	private BigDecimal g7pvMi;
	/**
	 * 整组MI值
	 */
	private BigDecimal npvMi;
	/**
	 * 新晋VIP人数
	 */
	private int addVIPNumber;
	/**
	 * 当期零售利润  retail_profit  state=1  profits
	 */
	private BigDecimal profits1;
	/**
	 * 新推VIP积分  rd_bonus_master.bonus_new_vip
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
	/**
	 * 上一周期  RANK_AC
	 */
	private List<String> periodCodeList;
	/**
	 * 当期待发放零售利润   retail_profit  state=2  profits
	 */
	private BigDecimal profits2;
	/**
	 * 显示页面类型 1当期未关闭  2当期已关闭
	 */
	private int lookType;

	public static SelfPerformanceResult build1(RdMmBasicInfo profile, MemberQualification qualification, BigDecimal profits1, RdBonusMaster bonusMaster,List<String> periodCodeList) {
		Optional<RdMmBasicInfo> optionalBasicInfo = Optional.ofNullable(profile);
		Optional<RdBonusMaster> optionalBonusMaster = Optional.ofNullable(bonusMaster);
		SelfPerformanceResult result = new SelfPerformanceResult();
		result.setMmNickName(optionalBasicInfo.map(RdMmBasicInfo::getMmNickName).orElse(""));
		result.setProfits1(profits1);
		result.setLookType(2);
		BigDecimal bugMi = new BigDecimal("0.00");
		BigDecimal g7pvMi = new BigDecimal("0.00");
		BigDecimal npvMi = new BigDecimal("0.00");
		int addVIPNumber = 0;
		String rankStr = "";
		if (qualification!=null){
			bugMi = qualification.getPpv();
			if (bugMi==null){
				bugMi = new BigDecimal("0.00");
			}
			g7pvMi = qualification.getG7pv();
			if (g7pvMi==null){
				g7pvMi = new BigDecimal("0.00");
			}
			npvMi = qualification.getNpv();
			if (npvMi==null){
				npvMi = new BigDecimal("0.00");
			}
			addVIPNumber = qualification.getDdNewVIPNumber();
			int rankAc = qualification.getRankAc();
			if (rankAc==0){
				rankStr = "普通会员";
			}else if (rankAc==1){
				rankStr = "VIP会员";
			}else if (rankAc==2){
				rankStr = "代理会员";
			}else if (rankAc==3){
				rankStr = "初级代理店";
			}else if (rankAc==4){
				rankStr = "一级代理店";
			}else if (rankAc==5){
				rankStr = "二级代理店";
			}else if (rankAc==6){
				rankStr = "三级代理店";
			}else if (rankAc==7){
				rankStr = "旗舰店";
			}else if (rankAc==8){
				rankStr = "高级旗舰店";
			}else {
				rankStr = "超级旗舰店";
			}
		}
		result.setBugMi(bugMi);
		result.setG7pvMi(g7pvMi);
		result.setNpvMi(npvMi);
		result.setAddVIPNumber(addVIPNumber);
		result.setRankStr(rankStr);

		BigDecimal bonusDevp1 = new BigDecimal("0.00");
		BigDecimal bonusDevp2 = new BigDecimal("0.00");
		BigDecimal bonusDevpShare = new BigDecimal("0.00");
		BigDecimal bonusLdSum = new BigDecimal("0.00");
		BigDecimal bonusSum = new BigDecimal("0.00");
		if (bonusMaster!=null){
			bonusDevp1 = bonusMaster.getBonusDevp1();
			if (bonusDevp1==null){
				bonusDevp1 = new BigDecimal("0.00");
			}
			bonusDevp2 = bonusMaster.getBonusDevp2();
			if (bonusDevp2==null){
				bonusDevp2 = new BigDecimal("0.00");
			}
			bonusDevpShare = bonusMaster.getBonusDevpShare();
			if (bonusDevpShare==null){
				bonusDevpShare = new BigDecimal("0.00");
			}
			bonusLdSum = bonusMaster.getBonusLdSum();
			if (bonusLdSum==null){
				bonusLdSum = new BigDecimal("0.00");
			}
			bonusSum = bonusMaster.getBonusSum();
			if (bonusSum==null){
				bonusSum = new BigDecimal("0.00");
			}
		}
		result.setBonusDevp1(bonusDevp1);
		result.setBonusDevp2(bonusDevp2);
		result.setBonusDevpShare(bonusDevpShare);
		result.setBonusLdSum(bonusLdSum);
		result.setBonusSum(bonusSum);

		return result;
	}

	public static SelfPerformanceResult build2(RdMmBasicInfo profile, MemberQualification qualification, BigDecimal profits1, BigDecimal profits2,List<String> periodCodeList) {
		Optional<RdMmBasicInfo> optional = Optional.ofNullable(profile);
		SelfPerformanceResult result = new SelfPerformanceResult();
		result.setMmNickName(optional.map(RdMmBasicInfo::getMmNickName).orElse(""));
		result.setProfits1(profits1);
		result.setProfits2(profits2);
		result.setLookType(1);
		BigDecimal bugMi = new BigDecimal("0.00");
		BigDecimal g7pvMi = new BigDecimal("0.00");
		BigDecimal npvMi = new BigDecimal("0.00");
		int addVIPNumber = 0;
		String rankStr = "";
		if (qualification!=null){
			bugMi = qualification.getPpv();
			if (bugMi==null){
				bugMi = new BigDecimal("0.00");
			}
			g7pvMi = qualification.getG7pv();
			if (g7pvMi==null){
				g7pvMi = new BigDecimal("0.00");
			}
			npvMi = qualification.getNpv();
			if (npvMi==null){
				npvMi = new BigDecimal("0.00");
			}
			addVIPNumber = qualification.getDdNewVIPNumber();
			int rankAc = qualification.getRankAc();
			if (rankAc==0){
				rankStr = "普通会员";
			}else if (rankAc==1){
				rankStr = "VIP会员";
			}else if (rankAc==2){
				rankStr = "代理会员";
			}else if (rankAc==3){
				rankStr = "初级代理店";
			}else if (rankAc==4){
				rankStr = "一级代理店";
			}else if (rankAc==5){
				rankStr = "二级代理店";
			}else if (rankAc==6){
				rankStr = "三级代理店";
			}else if (rankAc==7){
				rankStr = "旗舰店";
			}else if (rankAc==8){
				rankStr = "高级旗舰店";
			}else {
				rankStr = "超级旗舰店";
			}
		}
		result.setBugMi(bugMi);
		result.setG7pvMi(g7pvMi);
		result.setNpvMi(npvMi);
		result.setAddVIPNumber(addVIPNumber);
		result.setRankStr(rankStr);
		return result;
	}

}
