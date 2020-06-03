package com.framework.loippi.pojo.common;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 查询对应会员类型及交易总金额
 */
@Data
public class MemCensusVo {
    /**
     * 会员数
     */
    private Long num;
    /**
     * 总金额
     */
    private BigDecimal amountTotal;
}
