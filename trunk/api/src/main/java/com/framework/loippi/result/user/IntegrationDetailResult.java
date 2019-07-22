package com.framework.loippi.result.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import com.cloopen.rest.sdk.utils.DateUtil;
import com.framework.loippi.entity.integration.RdMmIntegralRule;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.entity.user.RdMmBasicInfo;

/**
 *
 *
 * @author Loippi team
 * @version 2.0
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationDetailResult {

    /** 交易流水号 */
    private Integer transNumber;

    /** 交易类型代码
     BA:奖金转入
     RC:充值
     RB:公司补发奖金
     WD:取现
     SP:转购物积分
     RR:归还欠款
     BT:奖励积分转换
     TF:他人转入
     OT:订单退款
     OP:订单支付
     TT:转给他人
     PC:购买商品并评论
     EG:换购商品 */
    private String transTypeCode;
    /** 交易类型代码
     BA:奖金转入
     RC:充值
     RB:公司补发奖金
     WD:取现
     SP:转购物积分
     RR:归还欠款
     BT:奖励积分转换
     TF:他人转入
     OT:订单退款
     OP:订单支付
     TT:转给他人
     PC:购买商品并评论
     EG:换购商品 */
    private String transTypeName;

    /** 交易金额 */
    private BigDecimal amount;

    /** 交易后余额 */
    private BigDecimal blanceAfter;

   /** 交易前余额 */
    private BigDecimal blanceBefore;

    /** 交易时间 */
    private String transDate;
    /** 交易状态
     -2：拒绝授权
     -1：已取消
     1：新单（保存状态）
     2：已申请
     3：已授权 */
    private Integer status;

    /** 对方会员编号（如果是会员） */
    private String trMmCode;
    /** 对方会员名称（如果是会员） */
    private String trMmName;
    /** 与交易有关的订单信息 */
    private Long trOrderOid;
    /** 授权人 */
    private String autohrizeBy;
    /** 授权时间 */
    private String autohrizeTime;
    /** 比例 */
    private int proportion=0;
    /** 税费 */
    private int tax=0;
    /** 提现金额 */
    private BigDecimal withdrawnAmount=new BigDecimal("0");
    /** 1奖励积分 2购物积分 3换购积分 */
    private int type;
    /** 授权说明（同意或不同意的理由） */
    private String autohrizeDesc;
    public static IntegrationDetailResult build(RdMmAccountLog rdMmAccountLog, RdMmBasicInfo shopMember, RdMmIntegralRule rdMmIntegralRule, Integer type) {
        IntegrationDetailResult integrationListResult=new IntegrationDetailResult();

//             Optional.ofNullable(rdMmIntegralRule.getBonusPointShopping()).orElse(0)
                 integrationListResult.setAutohrizeDesc(Optional.ofNullable(rdMmAccountLog.getAutohrizeDesc()).orElse(""));
                 integrationListResult.setAmount(Optional.ofNullable(rdMmAccountLog.getAmount()).orElse(new BigDecimal(0)));
                 integrationListResult.setBlanceAfter(Optional.ofNullable(rdMmAccountLog.getBlanceAfter()).orElse(new BigDecimal(0)));
                 integrationListResult.setBlanceBefore(Optional.ofNullable(rdMmAccountLog.getBlanceBefore()).orElse(new BigDecimal(0)));
                 integrationListResult.setTransDate(Optional.ofNullable(DateUtil.dateToStr(rdMmAccountLog.getTransDate(),"yyyy-MM-dd HH:mm:ss")).orElse(""));
                 integrationListResult.setTransNumber(Optional.ofNullable(rdMmAccountLog.getTransNumber()).orElse(0));
                 integrationListResult.setTransTypeCode(Optional.ofNullable(rdMmAccountLog.getTransTypeCode()).orElse(""));
                 integrationListResult.setStatus(Optional.ofNullable(rdMmAccountLog.getStatus()).orElse(0));
                 integrationListResult.setTrMmCode(Optional.ofNullable(rdMmAccountLog.getTrMmCode()).orElse(""));
                 if (rdMmAccountLog.getTrMmCode()!=null){
                     integrationListResult.setTrMmName(shopMember.getMmNickName());
                 }else{
                     integrationListResult.setTrMmName("");
                 }
                 integrationListResult.setTrOrderOid( Optional.ofNullable(rdMmAccountLog.getTrOrderOid()).orElse(0L));
                 integrationListResult.setAutohrizeBy(Optional.ofNullable(rdMmAccountLog.getAutohrizeBy()).orElse(""));
                 integrationListResult.setAutohrizeTime(Optional.ofNullable(DateUtil.dateToStr(rdMmAccountLog.getAutohrizeTime(),"yyyy-MM-dd HH:mm:ss")).orElse(""));
                 integrationListResult.setType(type);
                 if (rdMmIntegralRule!=null){
                     if (type==1){ //奖励积分
                         Double proportions =Optional.ofNullable(rdMmIntegralRule.getBonusPointShopping()).orElse(0)*0.01;
                         integrationListResult.setProportion(proportions.intValue());
                         integrationListResult.setTax(Optional.ofNullable(rdMmIntegralRule.getBonusPointWd()).orElse(0));
                         integrationListResult.setWithdrawnAmount(integrationListResult.getAmount().subtract(Optional.ofNullable(rdMmAccountLog.getAmount()).orElse(new BigDecimal(0)).multiply(BigDecimal.valueOf(Optional.ofNullable(rdMmIntegralRule.getBonusPointWd()).orElse(0)*0.01))).setScale(2, RoundingMode.HALF_UP));
                     }
                     if (type==2){//购物积分
                         Double proportions =Optional.ofNullable(rdMmIntegralRule.getShoppingPointSr()).orElse(0)*0.01;
                         integrationListResult.setProportion(proportions.intValue());
                     }
                 }
        String transTypeCode=rdMmAccountLog.getTransTypeCode();
        String transTypeName="";
        if (type==1){
            if ("SP".equals(transTypeCode)){
                transTypeName="积分转出";
            }else if ("WD".equals(transTypeCode)){
                transTypeName="提现";
            }else if ("BA".equals(transTypeCode)){
                transTypeName="奖金转入";
            }else if ("RB".equals(transTypeCode)){
                transTypeName="公司补发奖金";
            }else if ("RC".equals(transTypeCode)){
                transTypeName="购买商品并评论";
            }else if ("RR".equals(transTypeCode)){
                transTypeName="归还欠款";
            }else if ("MUB".equals(transTypeCode)){
                transTypeName="补偿奖励积分";
            }else if ("MDB".equals(transTypeCode)){
                transTypeName="补扣奖励积分";
            }
        }else if (type==2) {
            if ("BT".equals(transTypeCode)) {
                transTypeName = "奖励积分转换";
            } else if ("OP".equals(transTypeCode)) {
                transTypeName = "购物";
            }else if ("OT".equals(transTypeCode)) {
                transTypeName = "订单退还";
            }else if ("TF".equals(transTypeCode)) {
                transTypeName = "他人转入";
            }else if ("MUW".equals(transTypeCode)) {
                transTypeName = "补偿购物积分";
            }else if ("MDW".equals(transTypeCode)) {
                transTypeName = "补扣购物积分";
            }else if ("TT".equals(transTypeCode)) {
                transTypeName = "转给他人";
            }
        }else if (type==3) {
            if ("PC".equals(transTypeCode)) {
                transTypeName = "评价";
            } else if ("EG".equals(transTypeCode)) {
                transTypeName = "积分换购";
            }else if ("OT".equals(transTypeCode)) {
                transTypeName = "订单退还";
            }else if ("MUR".equals(transTypeCode)) {
                transTypeName = "补偿换购积分";
            }else if ("MDR".equals(transTypeCode)) {
                transTypeName = "补扣换购积分";
            }
        }

        integrationListResult.setTransTypeName(transTypeName);
        return integrationListResult;
    }

}
