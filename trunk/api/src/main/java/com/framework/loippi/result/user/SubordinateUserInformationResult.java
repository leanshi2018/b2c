package com.framework.loippi.result.user;

import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.vo.order.OrderSumPpv;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    //是否在线 0不在线 1在线
    private Integer isOnline;
    //累计购货额
    private BigDecimal totalMoney;
    //当月购货额
    private BigDecimal monthMoney;
    //当月购买PV
    private BigDecimal monthPpv;

    /**
     * 与推荐人的绑定关系
     */
    private Integer raSpoStatus;
    /**
     * 当前周期购货额
     */
    private BigDecimal periodMoney;
    /**
     * 当月获得PV值（MI值）
     */
    private BigDecimal periodPv;
    /**
     * 零售购买额（零售类型已支付且没有退款的订单）
     */
    private BigDecimal retailMoney;
    /**
     * 已获零售利润
     */
    private BigDecimal retailProfits;




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
        if (periodSumPpv!=null && shopMemberGrade.getRankClass()!=0){
            result.setPeriodMoney(Optional.ofNullable(periodSumPpv.getTotalmoney()).orElse(BigDecimal.ZERO));
            result.setPeriodPv(Optional.ofNullable(periodSumPpv.getTotalPpv()).orElse(BigDecimal.ZERO));
        }else{
            result.setMonthMoney(BigDecimal.ZERO);
            result.setMonthPpv(BigDecimal.ZERO);
        }
        result.setRaSpoStatus(raSponsorStatus);
        return result;
    }

}

