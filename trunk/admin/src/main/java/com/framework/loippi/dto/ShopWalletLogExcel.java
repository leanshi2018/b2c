package com.framework.loippi.dto;

import com.framework.loippi.entity.user.ShopMemberPaymentTally;
import com.framework.loippi.utils.Dateutil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by longbh on 2017/12/1.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopWalletLogExcel {

    public ShopWalletLogExcel(ShopMemberPaymentTally cash) {
        this.paymentSn = cash.getPaymentSn();
//        this.bizSn = cash.getBizSn();
        this.paymentAmount = cash.getPaymentAmount();
        this.buyerName = cash.getBuyerName();
        if (cash.getPaymentAmount().compareTo(new BigDecimal(0)) > 0) {
            this.paymentDirect = "收入";
        } else {
            this.paymentDirect = "支出";
        }
        this.paymentName = cash.getPaymentName();
        if ("'weixinMobilePaymentPlugin'".equals(this.paymentName)) {
            this.paymentDesc = "微信";
        } else if ("weixinH5PaymentPlugin".equals(this.paymentName)) {
            this.paymentDesc = "微信公众号";
        } else if ("waletPaymentPlugin".equals(this.paymentName)) {
            this.paymentDesc = "钱包支付";
        } else if ("unionpayMobilePaymentPlugin".equals(this.paymentName)) {
            this.paymentDesc = "银联支付";
        } else if ("alipayMobilePaymentPlugin".equals(this.paymentName)) {
            this.paymentDesc = "支付宝";
        } else if ("YL".equals(this.paymentName)) {
            this.paymentDesc = "银联支付";
        }
        this.createTime = Dateutil.getFormatDate(cash.getCreateTime(), "");
    }

    /**
     * 记录唯一标示
     */
    private String paymentSn;

    /**
     * 会员名称
     */
    private String bizSn;

    /**
     * 金额
     */
    private BigDecimal paymentAmount;

    private String buyerName;

    private String paymentDirect;

    private String paymentName;

    private String paymentDesc;

    private String createTime;

}
