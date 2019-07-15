package com.framework.loippi.dto;

import com.framework.loippi.entity.walet.ShopWalletRecharge;
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
public class ShopWalletRechargeExcel {

    public ShopWalletRechargeExcel(ShopWalletRecharge cash) {
        this.pdrSn = cash.getPdrSn();
        this.pdrMemberName = cash.getPdrMemberName();
        this.pdrAmount = cash.getPdrAmount();
        this.pdrPaymentName = cash.getPdrPaymentName();
        this.pdrPaymentTime = Dateutil.getFormatDate(cash.getPdrPaymentTime(), "");
    }

    /**
     * 记录唯一标示
     */
    private String pdrSn;

    /**
     * 会员名称
     */
    private String pdrMemberName;

    private BigDecimal pdrAmount;

    private String pdrPaymentName;

    private String pdrPaymentTime;

}
