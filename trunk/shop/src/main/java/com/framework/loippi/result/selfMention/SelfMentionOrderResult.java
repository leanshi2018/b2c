package com.framework.loippi.result.selfMention;

import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderGoods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 自提订单信息实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelfMentionOrderResult {
    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 订单状态
     */
    private Integer orderState;

    /**
     * 订单类型 1 零售订单 2 会员订单 3 pv订单 4 优惠订单 5 换购订单 6换货订单 7新会员启动包订单
     */
    private Integer orderType;

    /**
     * 订单总价格
     */
    private BigDecimal orderTotalPrice;

    /**
     * 订单mi值
     */
    private BigDecimal ppv;

    /**
     * 订单支付时间
     */
    private Date paymentTime;

    /**
     * 提货人会员编号
     */
    private String buyerId;

    /**
     * 提货人会员名称
     */
    private String buyerName;

    /**
     * 提货人手机号
     */
    private String buyerPhone;

    /**
     * 订单总商品数
     */
    private Integer goodsNumTotal;

    /**
     * 订单总商品数
     */
    private List<ShopOrderGoods> orderGoods;

    public static List<SelfMentionOrderResult> buildList(List<ShopOrder> shopOrders, HashMap<Long,List<ShopOrderGoods>> map) {
        ArrayList<SelfMentionOrderResult> results = new ArrayList<>();
        if(shopOrders!=null&&shopOrders.size()>0){
            for (ShopOrder shopOrder : shopOrders) {
                SelfMentionOrderResult result = new SelfMentionOrderResult();
                result.setOrderId(shopOrder.getId());
                result.setOrderSn(shopOrder.getOrderSn());
                result.setOrderState(shopOrder.getOrderState());
                result.setOrderTotalPrice(shopOrder.getOrderTotalPrice());
                result.setPpv(shopOrder.getPpv());
                result.setPaymentTime(shopOrder.getPaymentTime());
                result.setBuyerId(Long.toString(shopOrder.getBuyerId()));
                result.setBuyerName(shopOrder.getBuyerName());
                result.setBuyerPhone(shopOrder.getBuyerPhone());
                result.setOrderType(shopOrder.getOrderType());
                List<ShopOrderGoods> shopOrderGoods = map.get(shopOrder.getId());
                Integer num=0;
                if(shopOrderGoods!=null&&shopOrderGoods.size()>0){
                    for (ShopOrderGoods shopOrderGood : shopOrderGoods) {
                        num=num+shopOrderGood.getGoodsNum();
                    }
                }
                result.setGoodsNumTotal(num);
                result.setOrderGoods(shopOrderGoods);
                results.add(result);
            }
        }
        return results;
    }
}
