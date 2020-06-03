package com.framework.loippi.result.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import com.framework.loippi.entity.user.MemberQualification;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.vo.order.OrderSumPpv;

/**
 * Result - 分销用户资料信息显示
 *
 * @author Loippi team
 * @version 2.0
 * @description 用户资料
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubordinateUserInformationResult {

    //头像
    private String avatar;
    //昵称
    private String nickname;
    //会员等级名称
    private String memberGradeName;
    /**
     * 会员头像颜色 0.灰色 1.橙色 2.红色 3.白色
     */
    private Integer memberAvatarColour;
    /**
     * 与推荐人的绑定关系
     */
    private Integer raSpoStatus;
    /**
     * 加入时间
     */
    //private Date createTime;
    private String createTime;

    //累计购货额
    private BigDecimal totalMoney;
    /**
     * 当前周期购货额
     */
    private BigDecimal periodMoney;
    /**
     * 零售购买额（零售类型已支付且没有退款的订单）
     */
    private BigDecimal retailMoney;
    /**
     * 待发放零售利润
     */
    private BigDecimal retailProfitsNoPay;
    /**
     * 已发放零售利润
     */
    private BigDecimal retailProfits;
    /**
     * 当期获得PV值（MI值）
     */
    private BigDecimal periodPv;
    /**
     * 当期小组MI值
     */
    private BigDecimal periodGroupPv;
    /**
     * 当期整组MI值
     */
    private BigDecimal periodTeamPv;
    /**
     * 整组新vip人数
     */
    private Integer netNewVipNum;
    /**
     * 会员编号
     */
    private String mCode;
    /**
     * 推荐人编号
     */
    private String sponsorCode;
    /**
     * 周期
     */
    private String periodCode;

    //是否在线 0不在线 1在线
    private Integer isOnline;
    //当月购买PV
    private BigDecimal monthPpv;
    //当期购货额
    private BigDecimal monthMoney;
    //新晋vip人数  当期直邀VIP人数
    private Integer ddNewVIPNumber;
    //累计直邀VIP人数
    private Integer ddRank1Number;
    private BigDecimal appvFinal;//个人累计PV
    private Integer ddAcNumber;//直接推荐复消合格人数----直邀代理人数(当期活跃)
    private Integer ddRank2Number;//直接推荐代理的人数，代理级别为2  ---- 直邀代理人数(累计直邀)
    private Integer netAcNumber;//团队重复消费合格人数 ---- 整组重消活跃人数
    private String rankRecordHigh;//历史最高级别

    private BigDecimal serviceMi;//服务mi值
    private BigDecimal serviceCoefficient;//服务系数
    private BigDecimal coachMi;//辅导mi值
    private BigDecimal coachCoefficient;//辅导系数
    private BigDecimal shareMi;//分红mi值
    private BigDecimal shareCoefficient;//分红系数


    public static SubordinateUserInformationResult build(RdMmBasicInfo profile, RdRanks shopMemberGrade, OrderSumPpv orderSumMonthlyPpv, OrderSumPpv orderSumAccumulatedPpv) {
        Optional<RdMmBasicInfo> optional = Optional.ofNullable(profile);
        SubordinateUserInformationResult result = new SubordinateUserInformationResult();
        result.setAvatar(optional.map(RdMmBasicInfo::getMmAvatar).orElse(""));
        result.setNickname(optional.map(RdMmBasicInfo::getMmNickName).orElse(""));
        if (shopMemberGrade!=null){
            if(shopMemberGrade.getRankId()==2){
                result.setMemberGradeName("VIP会员");
            }else {
                result.setMemberGradeName(shopMemberGrade.getRankName());
            }
        }else{
            result.setMemberGradeName("用户");
        }

        result.setIsOnline(0);
        if (orderSumAccumulatedPpv!=null){
            result.setTotalMoney(Optional.ofNullable(orderSumAccumulatedPpv.getTotalmoney()).orElse(BigDecimal.ZERO));
        }else{
            result.setTotalMoney(BigDecimal.ZERO);
        }
        if (orderSumMonthlyPpv!=null && shopMemberGrade.getRankClass()!=0){
            result.setMonthMoney(Optional.ofNullable(orderSumMonthlyPpv.getTotalmoney()).orElse(BigDecimal.ZERO));
            result.setMonthPpv(Optional.ofNullable(orderSumMonthlyPpv.getTotalPpv()).orElse(BigDecimal.ZERO));
        }else{
            result.setMonthMoney(BigDecimal.ZERO);
            result.setMonthPpv(BigDecimal.ZERO);
        }

        return result;
    }

    public static SubordinateUserInformationResult build2(RdMmBasicInfo profile, RdRanks shopMemberGrade, OrderSumPpv periodSumPpv, OrderSumPpv orderSumAccumulatedPpv,Integer raSponsorStatus) {
        Optional<RdMmBasicInfo> optional = Optional.ofNullable(profile);
        SubordinateUserInformationResult result = new SubordinateUserInformationResult();
        result.setAvatar(optional.map(RdMmBasicInfo::getMmAvatar).orElse(""));
        result.setNickname(optional.map(RdMmBasicInfo::getMmNickName).orElse(""));
        if (shopMemberGrade!=null){
            if(shopMemberGrade.getRankId()==2){
                result.setMemberGradeName("VIP会员");
            }else {
                result.setMemberGradeName(shopMemberGrade.getRankName());
            }
        }else{
            result.setMemberGradeName("用户");
        }

        result.setIsOnline(0);
        if (orderSumAccumulatedPpv!=null){
            result.setTotalMoney(Optional.ofNullable(orderSumAccumulatedPpv.getTotalmoney()).orElse(BigDecimal.ZERO));
        }else{
            result.setTotalMoney(BigDecimal.ZERO);
        }
        if (periodSumPpv!=null){
            result.setPeriodMoney(Optional.ofNullable(periodSumPpv.getTotalmoney()).orElse(BigDecimal.ZERO));
            result.setPeriodPv(Optional.ofNullable(periodSumPpv.getTotalPpv()).orElse(BigDecimal.ZERO));
        }else{
            result.setPeriodMoney(BigDecimal.ZERO);
            result.setPeriodPv(BigDecimal.ZERO);
        }
        result.setRaSpoStatus(raSponsorStatus);
        return result;
    }

    public static SubordinateUserInformationResult build3(MemberQualification memberQualification, RdMmBasicInfo rdMmBasicInfo, RdMmRelation rdMmRelation, OrderSumPpv periodSumPpv, RdRanks shopMemberGrade,BigDecimal retail,BigDecimal pay,BigDecimal nopay) {
        SubordinateUserInformationResult result = new SubordinateUserInformationResult();
        result.setAvatar(Optional.ofNullable(rdMmBasicInfo.getMmAvatar()).orElse(""));//设置头像
        result.setNickname(Optional.ofNullable(rdMmBasicInfo.getMmNickName()).orElse(""));//设置昵称
        if(shopMemberGrade.getRankId()==2){
            result.setMemberGradeName("VIP会员");//设置会员级别
        }else {
            result.setMemberGradeName(Optional.ofNullable(shopMemberGrade.getRankName()).orElse(""));//设置会员级别
        }
        result.setRaSpoStatus(Optional.ofNullable(rdMmRelation.getRaSponsorStatus()).orElse(null));//设置会员状态 永久 间接
        Date creationDate = rdMmBasicInfo.getCreationDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String formatStr = format.format(creationDate);
        result.setCreateTime(formatStr);//设置会员加入时间
        result.setTotalMoney(Optional.ofNullable(rdMmRelation.getATotal()).orElse(BigDecimal.ZERO));//设置累计购货额
        Optional<OrderSumPpv> optional = Optional.ofNullable(periodSumPpv);
        result.setPeriodMoney(optional.map(OrderSumPpv::getTotalmoney).orElse(BigDecimal.ZERO));
        //result.setPeriodMoney(Optional.ofNullable(periodSumPpv.getTotalmoney()).orElse(BigDecimal.ZERO));//设置当期累计购货额
        result.setRetailMoney(retail);//设置零售订单总金额
        result.setRetailProfitsNoPay(nopay);//设置未发放的零售利润
        result.setRetailProfits(pay);//设置已发放的零售利润
        //result.setPeriodPv(Optional.ofNullable(memberQualification.getPpv()).orElse(BigDecimal.ZERO));//设置当期会员获得PV值
        result.setPeriodPv(optional.map(OrderSumPpv::getTotalPpv).orElse(BigDecimal.ZERO));//设置当期会员获得PV值
        result.setMCode(Optional.ofNullable(rdMmBasicInfo.getMmCode()).orElse(""));//设置会员编号
        result.setSponsorCode(Optional.ofNullable(memberQualification.getSponsorCode()).orElse(""));//设置当周期推荐人
        result.setPeriodCode(Optional.ofNullable(memberQualification.getPeriodCode()).orElse(""));//设置统计周期
        result.setPeriodGroupPv(Optional.ofNullable(memberQualification.getG7pv()).orElse(BigDecimal.ZERO));//设置小组MI
        result.setPeriodTeamPv(Optional.ofNullable(memberQualification.getNpv()).orElse(BigDecimal.ZERO));//设置整组MI
        result.setNetNewVipNum(Optional.ofNullable(memberQualification.getNetNewVipNumber()).orElse(0));//设置整组新晋vip人数
        result.setDdNewVIPNumber(Optional.ofNullable(memberQualification.getDdNewVIPNumber()).orElse(0));//当期直邀VIP人数
        result.setDdRank1Number(Optional.ofNullable(memberQualification.getDdRank1Number()).orElse(0));//累计直邀VIP人数
        result.setAppvFinal(Optional.ofNullable(memberQualification.getAppvFinal()).orElse(BigDecimal.ZERO));//个人累计PV
        result.setDdAcNumber(Optional.ofNullable(memberQualification.getDdAcNumber()).orElse(0));//直接推荐复消合格人数----直邀代理人数(当期活跃)
        result.setDdRank2Number(Optional.ofNullable(memberQualification.getDdRank2Number()).orElse(0));//直接推荐代理的人数，代理级别为2  ---- 直邀代理人数(累计直邀)
        result.setNetAcNumber(Optional.ofNullable(memberQualification.getNetAcNumber()).orElse(0));//团队重复消费合格人数 ---- 整组重消活跃人数
        result.setMonthPpv(Optional.ofNullable(memberQualification.getPpv()).orElse(BigDecimal.ZERO));//ppv
        result.setMonthMoney(Optional.ofNullable(memberQualification.getRetail()).orElse(BigDecimal.ZERO));//购买额

        if (rdMmRelation.getRank()!=0){
            if (memberQualification.getPpvqualified()==null || memberQualification.getHPpvQualified()==null){
                result.setMemberAvatarColour(0);
            }else {
                if (memberQualification.getPpvqualified()!=1 && memberQualification.getHPpvQualified()!=1){
                    result.setMemberAvatarColour(0);
                }else{
                    result.setMemberAvatarColour(0);
                    if (memberQualification.getPpvqualified()==1){
                        result.setMemberAvatarColour(1);
                    }
                    if (memberQualification.getHPpvQualified()==1){
                        result.setMemberAvatarColour(2);
                    }
                }
            }
        }else {
            result.setMemberAvatarColour(3);
        }

        if (memberQualification.getRankRecordHigh()==0){
            result.setRankRecordHigh("普通会员");
        }else if (memberQualification.getRankRecordHigh()==1){
            result.setRankRecordHigh("VIP会员");
        }else if (memberQualification.getRankRecordHigh()==2){
            result.setRankRecordHigh("VIP会员");
        }else if (memberQualification.getRankRecordHigh()==3){
            result.setRankRecordHigh("初级代理店");
        }else if (memberQualification.getRankRecordHigh()==4){
            result.setRankRecordHigh("一级代理店");
        }else if (memberQualification.getRankRecordHigh()==5){
            result.setRankRecordHigh("二级代理店");
        }else if (memberQualification.getRankRecordHigh()==6){
            result.setRankRecordHigh("三级代理店");
        }else if (memberQualification.getRankRecordHigh()==7){
            result.setRankRecordHigh("旗舰店");
        }else if (memberQualification.getRankRecordHigh()==8){
            result.setRankRecordHigh("高级旗舰店");
        }else if (memberQualification.getRankRecordHigh()==9){
            result.setRankRecordHigh("超级旗舰店");
        }else {
            result.setRankRecordHigh("普通会员");
        }
        //TODO
        result.setServiceMi(Optional.ofNullable(memberQualification.getDev1PvBase()).orElse(BigDecimal.ZERO));
        result.setServiceCoefficient(Optional.ofNullable(memberQualification.getDev1Rate()).orElse(BigDecimal.ZERO));
        result.setCoachMi(Optional.ofNullable(memberQualification.getDev2PvBase()).orElse(BigDecimal.ZERO));
        result.setCoachCoefficient(Optional.ofNullable(memberQualification.getDev2Rate()).orElse(BigDecimal.ZERO));
        result.setShareMi(Optional.ofNullable(memberQualification.getDevSharePvBase()).orElse(BigDecimal.ZERO));
        result.setShareCoefficient(Optional.ofNullable(memberQualification.getDevShareRate()).orElse(BigDecimal.ZERO));
        return result;
    }

    public static SubordinateUserInformationResult build4(RdMmBasicInfo rdMmBasicInfo, RdMmRelation rdMmRelation, OrderSumPpv periodSumPpv, RdRanks shopMemberGrade, BigDecimal retail, BigDecimal pay, BigDecimal nopay, String periodStr) {
        SubordinateUserInformationResult result = new SubordinateUserInformationResult();
        result.setAvatar(Optional.ofNullable(rdMmBasicInfo.getMmAvatar()).orElse(""));//设置头像
        result.setNickname(Optional.ofNullable(rdMmBasicInfo.getMmNickName()).orElse(""));//设置昵称
        if(shopMemberGrade.getRankId()==2){
            result.setMemberGradeName("VIP会员");//设置会员级别
        }else {
            result.setMemberGradeName(Optional.ofNullable(shopMemberGrade.getRankName()).orElse(""));//设置会员级别
        }
        result.setRaSpoStatus(Optional.ofNullable(rdMmRelation.getRaSponsorStatus()).orElse(null));//设置会员状态 永久 间接
        Date creationDate = rdMmBasicInfo.getCreationDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String formatStr = format.format(creationDate);
        result.setCreateTime(formatStr);//设置会员加入时间
        result.setTotalMoney(Optional.ofNullable(rdMmRelation.getATotal()).orElse(BigDecimal.ZERO));//设置累计购货额
        Optional<OrderSumPpv> optional = Optional.ofNullable(periodSumPpv);
        result.setPeriodMoney(optional.map(OrderSumPpv::getTotalmoney).orElse(BigDecimal.ZERO));
        //result.setPeriodMoney(Optional.ofNullable(periodSumPpv.getTotalmoney()).orElse(BigDecimal.ZERO));//设置当期累计购货额
        result.setRetailMoney(retail);//设置零售订单总金额
        result.setRetailProfitsNoPay(nopay);//设置未发放的零售利润
        result.setRetailProfits(pay);//设置已发放的零售利润
        //result.setPeriodPv(Optional.ofNullable(memberQualification.getPpv()).orElse(BigDecimal.ZERO));//设置当期会员获得PV值
        result.setPeriodPv(optional.map(OrderSumPpv::getTotalPpv).orElse(BigDecimal.ZERO));//设置当期会员获得PV值
        result.setMCode(Optional.ofNullable(rdMmBasicInfo.getMmCode()).orElse(""));//设置会员编号
        result.setSponsorCode(Optional.ofNullable(rdMmRelation.getSponsorCode()).orElse(""));//设置当周期推荐人
        result.setPeriodCode(periodStr);//设置统计周期
        result.setPeriodGroupPv(BigDecimal.ZERO);//设置小组MI
        result.setPeriodTeamPv(BigDecimal.ZERO);//设置整组MI
        result.setNetNewVipNum(0);//设置整组新晋vip人数
        result.setDdNewVIPNumber(0);//当期直邀VIP人数
        result.setDdRank1Number(0);//累计直邀VIP人数
        result.setAppvFinal(BigDecimal.ZERO);//个人累计PV
        result.setDdAcNumber(0);//直接推荐复消合格人数----直邀代理人数(当期活跃)
        result.setDdRank2Number(0);//直接推荐代理的人数，代理级别为2  ---- 直邀代理人数(累计直邀)
        result.setNetAcNumber(0);//团队重复消费合格人数 ---- 整组重消活跃人数
        result.setMemberAvatarColour(3);
        result.setRankRecordHigh("普通会员");
        result.setMonthPpv(BigDecimal.ZERO);//ppv
        result.setMonthMoney(BigDecimal.ZERO);//购买额
        //TODO
        result.setServiceMi(new BigDecimal("0.00"));
        result.setServiceCoefficient(new BigDecimal("0"));
        result.setCoachMi(new BigDecimal("0.00"));
        result.setCoachCoefficient(new BigDecimal("0"));
        result.setShareMi(new BigDecimal("0.00"));
        result.setShareCoefficient(new BigDecimal("0"));
        return result;
    }
}

