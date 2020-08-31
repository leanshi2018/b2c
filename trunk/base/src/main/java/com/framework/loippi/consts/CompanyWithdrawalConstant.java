package com.framework.loippi.consts;

import java.math.BigDecimal;

public interface CompanyWithdrawalConstant {
    /**
     * 商户提现税费（百分比计算）
     */
    public static final BigDecimal COMPANY_WITHDRAWAL_RATE=new BigDecimal("0.00");

    /**
     * 商户提现最低提现金额
     */
    public static final BigDecimal LOW_ACC=new BigDecimal("5000.00");
}
