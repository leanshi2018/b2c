package com.framework.loippi.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付宝退款所需实体
 *
 * @类名称：AliPayRefund
 * @类描述：
 * @修改备注：
 */
@Data
@ToString
public class AliPayRefund implements Serializable {
    /**
     * 交易号（如2015102821001004900005578936）
     */
    private String tradeNo;
    /**
     * 退款金额
     */
    private BigDecimal refundAmount;
    /**
     * 退款理由 “退款理由”长度不能大于256字节，“退款理由”中不能有“^”、“|”、“$”、“#”等影响detail_data格式的特殊字符；
     */
    private String rRefundReason;
    /**
     * 退款当天日期 格式 2007-10-01 13:13:13
     */
    private String refundDate;
    /**
     * 退款笔数 最大支持1000笔
     */
    private Integer refundAmountNum;
    /**
     * 批次号 退款日期（8位）+流水号（3～24位）不可重复 如（201511130002）
     */
    private String batchNumber;
    /**
     * 单笔数据集 单条数据
     * 原付款支付宝交易号^退款总金额^退款理由
     * 格式为：第一笔交易退款数据集#第二笔交易退款数据集#第三笔交易退款数据集…#第N笔交易退款数据集
     * 2015102821001004900005578936^0.01^协商退款#2015101621001004650091502622^0.01^协商退款
     */
    private String detaildata;

    public void setDetaildata(String tradeNo, BigDecimal refundAmount, String rRefundReason) {
        if ("".equals(rRefundReason)) {
            rRefundReason = "协商退款";
        }
        this.detaildata = tradeNo + "^" + refundAmount + "^" + rRefundReason;
    }

    public String getDetaildata() {
        return detaildata;
    }

    /**
     * 退款成功后回调路径
     */
    private String notifyurl;

    /**
     * 批次号
     */
    private String batchNo;
//	public static void main(String[] args) {
//		String str="2010031906272929^80^SUCCESS$jax_chuanhang@alipay.com^2088101003147483^0.01^SUCCESS";
//		System.out.println("hahh"+str.substring(0, str.indexOf("^")));
//		
//	}
}
