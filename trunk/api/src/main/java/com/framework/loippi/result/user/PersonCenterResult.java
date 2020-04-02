package com.framework.loippi.result.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.framework.loippi.consts.OrderState;
import com.framework.loippi.entity.user.RdMmBank;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.vo.order.CountOrderStatusVo;

/**
 * Result - 用户资料
 *
 * @author Loippi team
 * @version 2.0
 * @description 用户资料
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors
public class PersonCenterResult {

    /***
     * 昵称
     */
    private String nickname;

    /***
     * 头像
     */
    private String avatar;

    /***
     * 性别(0:女，1:男)
     */
    private Integer sex;

    /**
     * 等级
     */
    private Integer grade;

    //等级名称
    private String gradeName;

    /**
     * id
     */
    private Integer userId;


    /**
     * 未读消息总数
     */
    private Integer messageNum;
    /**
     * 未读提醒消息数
     */
    private Integer messageRemindNum;
    /**
     * 未读订单消息数
     */
    private Integer messageOrderNum;
    /**
     * 未读留言消息数
     */
    private Integer messageLeaveNum;

    /**
     * 未付款订单数
     */
    private Integer noPaymentNum=0;
    /**
     * 代发货订单数
     */
    private Integer unfilledNum=0;
    /**
     * 代收货订单数
     */
    private Integer notReceivingNum=0;
    /**
     * 代评价订单数
     */
    private Integer evaluationNum=0;
    /**
     * 心愿单数
     */
    private Integer favoritesNum=0;
    /**
     * 足迹
     */
    private Integer browseNum=0;
    /**
     * 是否设置支付密码 0 没有 1有
     */
    private Integer isPaymentPasswd=0;
    //是否显示ppv 0 不显示 1 显示
    private Integer lookPpv;
    //是否显示vip价格 0 不显示 1 显示
    private Integer lookVip;
    //当前周期
    private String period;
    //周期结束时间
    private String endDate;
    //本期已达成MI
    private BigDecimal periodMi;
    //加密后密码
    private String pwd;

    //是否绑定银行卡  0:未绑定  1：已绑定（该银行卡绑定是指手动提现银行卡绑定状态，与通联银行卡绑定无关）
    private Integer badingBankFlag;

    //是否进行通联支付实名制认证 0：未实名制认证 1：已实名制认证
    private Integer trueNameFlag;

    //0:未签约 1：已签约  通联支付签约状态
    private Integer allInPayContractStatus;

    //0:未绑定通联支付手机号 1：已绑定 2：已解绑（解绑后未绑定新的手机号）  用户是否绑定了通联支付手机号码
    private Integer allInPayPhoneStatus;


    public static PersonCenterResult build(RdMmBasicInfo profile, RdRanks shopMemberGrade, List<RdMmBank> banks) {
        Optional<RdMmBasicInfo> optional = Optional.ofNullable(profile);
        PersonCenterResult result = new PersonCenterResult();
        result.setAvatar(optional.map(RdMmBasicInfo::getMmAvatar).orElse(""));
        result.setNickname(optional.map(RdMmBasicInfo::getMmNickName).orElse(""));
        result.setSex(optional.map(RdMmBasicInfo::getGender).orElse(0));
        result.setGrade(shopMemberGrade.getRankId());
        result.setGradeName(shopMemberGrade.getRankName());
        result.setUserId(profile.getMmId());
        result.setMessageNum(0);
        result.setLookPpv(0);
        result.setLookVip(0);
        if (shopMemberGrade!=null) {
            if (shopMemberGrade.getRankClass()>0){
                result.setLookPpv(1);
                result.setLookVip(1);
            }
        }
        if(banks!=null&&banks.size()>0){
            result.setBadingBankFlag(1);
        }else {
            result.setBadingBankFlag(0);
        }
        result.setAllInPayContractStatus(optional.map(RdMmBasicInfo::getAllInContractStatus).orElse(0));
        result.setAllInPayPhoneStatus(optional.map(RdMmBasicInfo::getAllInPayPhoneStatus).orElse(0));
        result.setTrueNameFlag(optional.map(RdMmBasicInfo::getWhetherTrueName).orElse(0));
        return result;
    }

    public static PersonCenterResult build2(PersonCenterResult result, List<CountOrderStatusVo> countOrderStatusVoList) {
        if (countOrderStatusVoList != null && countOrderStatusVoList.size() > 0) {
            for (CountOrderStatusVo item : countOrderStatusVoList) {
                //待收款
                if (item.getOrderStatus() == OrderState.ORDER_STATE_NO_PATMENT) {
                    result.setNoPaymentNum(item.getTotal());
                    continue;
                }
                //待发货
                if (item.getOrderStatus() == OrderState.ORDER_STATE_UNFILLED) {
                    result.setUnfilledNum(item.getTotal());
                    continue;
                }
                //待收货
                if (item.getOrderStatus() == OrderState.ORDER_STATE_NOT_RECEIVING) {
                    result.setNotReceivingNum(item.getTotal());
                    continue;
                }
                //待评价
                if (item.getOrderStatus() == OrderState.ORDER_STATE_FINISH && item.getEvaluationStatusType() == 0) {
                    result.setEvaluationNum(item.getTotal());
                    continue;
                }
            }
        }
        return result;
    }
}

