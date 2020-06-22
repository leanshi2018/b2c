package com.framework.loippi.result.selfMention;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelfMentionOrderStatistics {
    /**
     * 订单数
     */
    private Integer orderNum;

    /**
     * 销售额
     */
    private BigDecimal orderIncome;
}
