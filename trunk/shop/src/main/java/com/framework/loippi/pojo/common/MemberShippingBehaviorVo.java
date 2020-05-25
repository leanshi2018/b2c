package com.framework.loippi.pojo.common;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 统计新老会员购买行为包装类
 */
@Data
public class MemberShippingBehaviorVo {
    /**
     * 会员编号
     */
    private String mmCode;
    /**
     * 订单数
     */
    private Integer orderNum;
}
