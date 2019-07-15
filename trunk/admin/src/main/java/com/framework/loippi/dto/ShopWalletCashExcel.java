package com.framework.loippi.dto;

import com.framework.loippi.entity.walet.ShopWalletCash;
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
public class ShopWalletCashExcel {

    public ShopWalletCashExcel(ShopWalletCash cash) {
        this.pdcSn = cash.getPdcSn();
        this.pdcMemberName = cash.getPdcMemberName();
        this.pdcAmount = cash.getPdcAmount();
        this.pdcBankUser = cash.getPdcBankUser();
        this.pdcBankName = cash.getPdcBankName();
        this.pdcBankNo = cash.getPdcBankNo();
        this.createTime = Dateutil.getFormatDate(cash.getCreateTime(), "");
        this.pdcPaymentTime = Dateutil.getFormatDate(cash.getPdcPaymentTime(), "");
        if ("0".equals(cash.getPdcPaymentState())) {
            this.pdcPaymentState = "申请中";
        } else if ("1".equals(cash.getPdcPaymentState())) {
            this.pdcPaymentState = "支付完成";
        } else {
            this.pdcPaymentState = "提现失败";
        }
    }

    /**
     * 记录唯一标示
     */
    private String pdcSn;

    /**
     * 会员名称
     */
    private String pdcMemberName;

    /**
     * 金额
     */
    private BigDecimal pdcAmount;

    private String pdcBankUser;

    /**
     * 收款银行
     */
    private String pdcBankName;

    /**
     * 收款账号
     */
    private String pdcBankNo;

    /**
     * 添加时间
     */
    private String createTime;

    /**
     * 提现支付状态 0提现时申请中   1支付完成   2-提现失败
     */
    private String pdcPaymentState;

    /**
     * 付款时间
     */
    private String pdcPaymentTime;

}
