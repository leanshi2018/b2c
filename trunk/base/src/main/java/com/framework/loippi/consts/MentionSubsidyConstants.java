package com.framework.loippi.consts;

import java.math.BigDecimal;

public interface MentionSubsidyConstants {
    /**
     * 月销售额大于10万补贴系数
     */
    public static final BigDecimal SUBSIDY_MORE_TEN=new BigDecimal("4.00");
    /**
     * 月销售额在5万到10万之间补贴系数
     */
    public static final BigDecimal SUBSIDY_BETWEEN_FIVE_TEN=new BigDecimal("3.50");
    /**
     * 月销售额在1万到5万之间补贴系数
     */
    public static final BigDecimal SUBSIDY_BETWEEN_ONE_FIVE=new BigDecimal("3.00");
}
