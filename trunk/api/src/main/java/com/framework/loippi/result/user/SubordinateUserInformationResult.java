package com.framework.loippi.result.user;

import com.framework.loippi.entity.user.MemberQualification;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.vo.order.OrderSumPpv;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

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



    public static SubordinateUserInformationResult build(RdMmBasicInfo profile, RdRanks shopMemberGrade, OrderSumPpv orderSumMonthlyPpv, OrderSumPpv orderSumAccumulatedPpv) {
        Optional<RdMmBasicInfo> optional = Optional.ofNullable(profile);
        SubordinateUserInformationResult result = new SubordinateUserInformationResult();
        result.setAvatar(optional.map(RdMmBasicInfo::getMmAvatar).orElse(""));
        result.setNickname(optional.map(RdMmBasicInfo::getMmNickName).orElse(""));
        if (shopMemberGrade!=null){
            result.setMemberGradeName(shopMemberGrade.getRankName());
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
            result.setMemberGradeName(shopMemberGrade.getRankName());
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
        result.setMemberGradeName(Optional.ofNullable(shopMemberGrade.getRankName()).orElse(""));//设置会员级别
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
        return result;
    }

    public static SubordinateUserInformationResult build4(RdMmBasicInfo rdMmBasicInfo, RdMmRelation rdMmRelation, OrderSumPpv periodSumPpv, RdRanks shopMemberGrade, BigDecimal retail, BigDecimal pay, BigDecimal nopay, String periodStr) {
        SubordinateUserInformationResult result = new SubordinateUserInformationResult();
        result.setAvatar(Optional.ofNullable(rdMmBasicInfo.getMmAvatar()).orElse(""));//设置头像
        result.setNickname(Optional.ofNullable(rdMmBasicInfo.getMmNickName()).orElse(""));//设置昵称
        result.setMemberGradeName(Optional.ofNullable(shopMemberGrade.getRankName()).orElse(""));//设置会员级别
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
        return result;
    }
}

