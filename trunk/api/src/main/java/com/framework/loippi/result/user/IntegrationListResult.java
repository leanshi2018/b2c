package com.framework.loippi.result.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.framework.loippi.entity.user.RdMmAccountLog;

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
     CF:自动自提失败退还积分
     AWD:自动自提积分预扣减
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
             HashMap<Long, RdMmAccountLog> mapAWD = new HashMap<>();
             HashMap<Long, RdMmAccountLog> mapCF = new HashMap<>();
             for (RdMmAccountLog item:rdMmAccountLogList) {
                 String transTypeCode=item.getTransTypeCode();
                 Long orderOid = item.getTrOrderOid();
                 if("AWD".equals(transTypeCode) && item.getStatus()==3){//如果自动提现相关的记录
                     if(orderOid!=null){
                         RdMmAccountLog rdMmAccountLog = mapAWD.get(orderOid);
                         if(rdMmAccountLog!=null){//判断map集合中是否有关于该订单的AWD日志
                            //比较两条记录的时间先后
                             if(item.getTransDate().getTime()>rdMmAccountLog.getTransDate().getTime()){
                                 mapAWD.put(orderOid,item);//替代之前的一条记录
                                 //去除userIntegrationListResultList中根据rdMmAccountLog生成的一条记录
                                 for (IntegrationListResult integrationListResult : userIntegrationListResultList) {
                                     if(integrationListResult.getTransNumber().equals(rdMmAccountLog.getTransNumber())){
                                         userIntegrationListResultList.remove(integrationListResult);
                                     }
                                 }
                             }else {//使用之前有的记录
                                 continue;
                             }
                         }else {
                             mapAWD.put(orderOid,item);
                         }
                     }
                 }
                 if("CF".equals(transTypeCode) && item.getStatus()==3&&"多次跳转微信小程序退还用户奖励积分".equals(item.getAutohrizeDesc())){//如果自动提现相关的记录
                     if(orderOid!=null){
                         RdMmAccountLog rdMmAccountLog = mapCF.get(orderOid);
                         if(rdMmAccountLog!=null){//判断map集合中是否有关于该订单的AWD日志
                             //比较两条记录的时间先后
                             if(item.getTransDate().getTime()>rdMmAccountLog.getTransDate().getTime()){
                                 mapCF.put(orderOid,item);//替代之前的一条记录
                                 //去除userIntegrationListResultList中根据rdMmAccountLog生成的一条记录
                                 for (IntegrationListResult integrationListResult : userIntegrationListResultList) {
                                     if(integrationListResult.getTransNumber().equals(rdMmAccountLog.getTransNumber())){
                                         userIntegrationListResultList.remove(integrationListResult);
                                     }
                                 }
                             }else {//使用之前有的记录
                                 continue;
                             }
                         }else {
                             mapCF.put(orderOid,item);
                         }
                     }
                 }
                 IntegrationListResult integrationListResult=new IntegrationListResult();
                 integrationListResult.setBlanceAfter(item.getBlanceAfter());
                 integrationListResult.setTransDate(item.getTransDate());
                 integrationListResult.setTransNumber(item.getTransNumber());
                 integrationListResult.setTransTypeCode(item.getTransTypeCode());
                 String transTypeName="";
                 String symbol="-";
                 if (type==1){
                     //奖励积分
                     if ("SP".equals(transTypeCode) && item.getStatus()==3){
                         transTypeName="转出";
                     }else if ("WD".equals(transTypeCode) && item.getStatus()==2){
                         transTypeName="提现-审核中，未到账";
                     }else if ("WD".equals(transTypeCode) && item.getStatus()==3){
                         transTypeName="提现-";
                     }else if ("WD".equals(transTypeCode) && item.getStatus()==-2){
                         transTypeName="提现-提现失败，已全额退款";
                     }else if ("WD".equals(transTypeCode) && item.getStatus()==4){
                         transTypeName="提现-成功";
                     }else if ("BA".equals(transTypeCode) && item.getStatus()==3){
                         transTypeName="奖金转入";
                         symbol="+";
                     }else if ("RB".equals(transTypeCode) && item.getStatus()==3){
                         transTypeName="公司补发奖金";
                         symbol="+";
                     }else if ("RC".equals(transTypeCode) && item.getStatus()==3){
                         transTypeName="购买商品并评论";
                         symbol="+";
                     }else if ("RR".equals(transTypeCode) && item.getStatus()==3){
                         transTypeName="归还欠款";
                     }else if ("MUB".equals(transTypeCode) && item.getStatus()==3){
                         transTypeName="补偿奖励积分";
                         symbol="+";
                     }else if ("MDB".equals(transTypeCode) && item.getStatus()==3){
                         transTypeName="补扣奖励积分";
                     }
                     else if ("AWD".equals(transTypeCode) && item.getStatus()==3){
                         transTypeName="自动提现预扣减积分成功";
                     }
                     else if ("CF".equals(transTypeCode) && item.getStatus()==3){
                         symbol="+";
                         transTypeName="自动提现失败积分退还";
                     }
                 }else if (type==2) {
                     //购物积分
                     if ("BT".equals(transTypeCode) && item.getStatus()==3) {
                         transTypeName = "奖励积分转换";
                         symbol="+";
                     } else if ("TT".equals(transTypeCode) && item.getStatus()==3) {
                         transTypeName = "转出";
                     } else if ("OP".equals(transTypeCode) && item.getStatus()==3) {
                         transTypeName = "购物";
                     }else if ("OT".equals(transTypeCode) && item.getStatus()==3) {
                         transTypeName = "订单退还";
                         symbol="+";
                     }else if ("TF".equals(transTypeCode) && item.getStatus()==3) {
                         transTypeName = "他人转入";
                         symbol="+";
                     }else if ("MUW".equals(transTypeCode) && item.getStatus()==3) {
                         transTypeName = "补偿购物积分";
                         symbol="+";
                     }else if ("MDW".equals(transTypeCode) && item.getStatus()==3) {
                         transTypeName = "补扣购物积分";
                     }
                 }else if (type==3) {
                     //换购积分
                     if ("PC".equals(transTypeCode) && item.getStatus()==3) {
                         transTypeName = "评价";
                         symbol="+";
                     } else if ("EG".equals(transTypeCode) && item.getStatus()==3) {
                         transTypeName = "换购";
                     }else if ("OT".equals(transTypeCode) && item.getStatus()==3) {
                         transTypeName = "订单退还";
                         symbol="+";
                     }else if ("MUR".equals(transTypeCode) && item.getStatus()==3) {
                         transTypeName = "补偿换购积分";
                         symbol="+";
                     }else if ("MDR".equals(transTypeCode) && item.getStatus()==3) {
                         transTypeName = "补扣换购积分";
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
