package com.framework.loippi.pojo.common;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 会员指标包装数据
 */
@Data
public class MemIndicatorVo {
    /**
     * 购买次数
     */
    private Integer buyNum;

    /**
     * 会员编号
     */
    private String mCode;
    /**
     * 购买总金额
     */
    private BigDecimal moneyTotal;
    /**
     * 购买总pv
     */
    private BigDecimal ppvTotal;
    /**
     * 周期
     */
    private String periodCode;
}
