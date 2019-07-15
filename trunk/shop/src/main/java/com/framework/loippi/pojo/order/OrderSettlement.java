package com.framework.loippi.pojo.order;

import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * 功能： 订单结算超类
 * 类名：OrderSettlement
 * 日期：2017/11/22  19:35
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
@Data
public class OrderSettlement {
    /**
     * 全部商品总金额
     */
    private BigDecimal goodsAmount;

    /**
     * 全部订单需付款金额
     */
    private BigDecimal orderAmount;

    private List<OrderVo> orderVoList = Lists.newArrayList();
}
