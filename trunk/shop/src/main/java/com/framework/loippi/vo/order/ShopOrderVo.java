package com.framework.loippi.vo.order;

import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderAddress;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.order.ShopOrderLog;

import com.framework.loippi.mybatis.ext.annotation.Column;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 功能： 订单实体类扩展
 * 类名：ShopOrderVo
 * 日期：2017/11/10  17:55
 * 作者：czl
 * 详细说明：额外添加 查询或返回结果所需参数,
 * 修改备注:
 */
@Data
@ToString
@Accessors(chain = true)
public class ShopOrderVo extends ShopOrder {
    /**
     * 订单商品
     */
    private List<ShopOrderGoods> shopOrderGoods;

    /**
     * 订单收货地址信息
     */
    private ShopOrderAddress address;

    /**
     * 订单操作日志
     */
    private List<ShopOrderLog> shopOrderLogs;

    /**
     * 多状态搜索
     */
    private int[] orderStates;
    /**
     * 改订单商品是否全部有货  0没有 1有
     */
    private Integer isStorage;
    /**
     * 改订单商品是否可以部分发货  0不可以 1可以
     */
    private Integer isPartial;

    /** 订单固定pv值 */
    private BigDecimal fixedPpv;
}
