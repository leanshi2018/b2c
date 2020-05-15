package com.framework.loippi.pojo.common;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 查询对应类型订单数量以及总金额
 */
@Data
public class CensusVo {
    /**
     * 订单数量
     */
    private Integer orderNum;
    /**
     * 总金额
     */
    private BigDecimal amountTotal;
}
