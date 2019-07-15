package com.framework.loippi.result.user;

import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.mybatis.ext.annotation.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.*;

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
public class IntegrationListResult {

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

    /** 交易金额 */ //后台判断添加+-符号
    private String amount;

    /** 交易后余额 */
    private BigDecimal blanceAfter;

    /** 交易时间 */
    private Date transDate;

    public static List<IntegrationListResult> build(List<RdMmAccountLog> rdMmAccountLogList,Integer type) {
        List<IntegrationListResult> userIntegrationListResultList=new ArrayList<>();
         if (rdMmAccountLogList!=null && rdMmAccountLogList.size()>0){
             for (RdMmAccountLog item:rdMmAccountLogList) {
                 IntegrationListResult integrationListResult=new IntegrationListResult();
                 integrationListResult.setBlanceAfter(item.getBlanceAfter());
                 integrationListResult.setTransDate(item.getTransDate());
                 integrationListResult.setTransNumber(item.getTransNumber());
                 integrationListResult.setTransTypeCode(item.getTransTypeCode());
                 String transTypeCode=item.getTransTypeCode();
                 String transTypeName="";
                 String symbol="-";
                 if (type==1){
                     if ("SP".equals(transTypeCode)){
                         transTypeName="转出";
                     }else if ("WD".equals(transTypeCode) && item.getStatus()==2){
                         transTypeName="提现-审核中";
                     }else if ("WD".equals(transTypeCode) && item.getStatus()==3){
                         transTypeName="提现-已通过";
                     }else if ("WD".equals(transTypeCode) && item.getStatus()==-2){
                         transTypeName="提现-已拒绝";
                     }else if ("WD".equals(transTypeCode) && item.getStatus()==4){
                         transTypeName="提现-成功";
                     }
                 }else if (type==2) {
                     if ("BT".equals(transTypeCode)) {
                         transTypeName = "奖励积分转换";
                         symbol="+";
                     } else if ("TT".equals(transTypeCode)) {
                         transTypeName = "转出";
                     } else if ("OP".equals(transTypeCode)) {
                         transTypeName = "购物";
                     }else if ("OT".equals(transTypeCode)) {
                         transTypeName = "订单退还";
                         symbol="+";
                     }else if ("TF".equals(transTypeCode)) {
                         transTypeName = "他人转入";
                         symbol="+";
                     }
                 }else if (type==3) {
                     if ("PC".equals(transTypeCode)) {
                         transTypeName = "评价";
                         symbol="+";
                     } else if ("EG".equals(transTypeCode)) {
                         transTypeName = "换购";
                     }else if ("OT".equals(transTypeCode)) {
                         transTypeName = "订单退还";
                         symbol="+";
                     }
                 }
                 integrationListResult.setTransTypeName(transTypeName);
                 // TODO: 2019/1/18 名称 符号
                 integrationListResult.setAmount(symbol+item.getAmount());
                 userIntegrationListResultList.add(integrationListResult);
             }
         }
        return userIntegrationListResultList;
    }

}
