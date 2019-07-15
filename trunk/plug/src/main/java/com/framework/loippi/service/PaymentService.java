package com.framework.loippi.service;

import com.framework.loippi.entity.AliPayRefund;
import com.framework.loippi.entity.PayCommon;

/**
 * 支付反馈接口,用于在业务逻辑继承并实现支付结束后流程
 * Created by longbh on 2017/7/29.
 */
public interface PaymentService {

    /**
     * 示例数据
     * AliPayRefund aliPayRefund = new AliPayRefund();
     * aliPayRefund.setRefundAmountNum(1);
     * aliPayRefund.setDetaildata("2015101421001004650090828422", BigDecimal.valueOf(0.01), "协商退款");
     *
     * @param bizId
     * @return
     */
    AliPayRefund updateFrontBack(Long bizId);

    /**
     * 预支付
     *
     * @param no
     * @param type 类型
     * @return
     */
    PayCommon prePay(String no);

    /**
     * 退款回调,status:1-正常 0-不正常
     *
     * @param batchNo
     * @param status
     */
    public void updateReturnBack(Integer status, String message, String batchNo);

    /**
     * 支付回调
     *
     * @param sn
     * @param batchNo
     * @param plug  eg:alipay
     * @param totalFee
     */
    void updatePayBack(String sn, String batchNo, String plug, String totalFee);

    /**
     * 支付失败 如果使用积分支付则全部返回,否则无变化
     * @param sn
     */
    void updatePayfailBack(String sn);
}
