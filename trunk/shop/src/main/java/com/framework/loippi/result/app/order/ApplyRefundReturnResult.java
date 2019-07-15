package com.framework.loippi.result.app.order;

import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.trade.ShopRefundReturn;
import com.framework.loippi.result.app.cart.BaseGoodsResult;
import com.framework.loippi.vo.order.ShopOrderVo;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.*;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections.CollectionUtils;

/**
 * 功能： 售后 类名：RefundReturnResult 日期：2017/12/12  11:22 作者：czl 详细说明： 修改备注:
 */
@Data
@Accessors(chain = true)
public class ApplyRefundReturnResult {

    private String orderSn;
    private Long orderId;
    /**
     * pv值
     */
    private BigDecimal ppv;

    /**
     * 品牌名称
     */
    private String brandName;

    private List<ApplyItem> itemList;

    @Data
    @Accessors(chain = true)
    public static class ApplyItem extends BaseGoodsResult {

        private Long orderGoodsId;
        //该商品是否可以进行售后 1可以 0不可以
        private int isRefundReturn;

        public static List<ApplyItem> buildList(ShopOrderVo orderVo, Map<String, ShopRefundReturn> map)
            throws Exception {
            List<ShopOrderGoods> orderGoodsList = orderVo.getShopOrderGoods();
            return BaseGoodsResult.buildList(orderGoodsList,
                result -> new ApplyItem().setOrderGoodsId(result.getId())
                    .setIsRefundReturn(
                        map.get(result.getOrderId() + "") != null && result.getGoodsNum() != (result.getGoodsReturnnum()
                            + result.getGoodsBarternum()) ? 1 : 0)

            );
        }
    }


    public static ApplyRefundReturnResult buildList(List<ShopOrderVo> orderVoList, Long orderId,
        List<ShopRefundReturn> shopRefundReturnList) throws Exception {
        ApplyRefundReturnResult returnResults = new ApplyRefundReturnResult();
        if (CollectionUtils.isEmpty(orderVoList)) {
            return returnResults;
        }
        Map<String, ShopRefundReturn> map = new HashMap<>();
        if (shopRefundReturnList != null && shopRefundReturnList.size() > 0) {
            for (ShopRefundReturn item : shopRefundReturnList) {
                if (item.getSellerState() == 2 || item.getSellerState() == 3) {
                    map.put(item.getOrderId() + "", item);
                }

            }
        }

        for (ShopOrderVo orderVo : orderVoList) {
            Optional<ShopOrderVo> optOrder = Optional.ofNullable(orderVo);
            returnResults.setOrderSn(optOrder.map(ShopOrderVo::getOrderSn).orElse(""));
            returnResults.setBrandName(optOrder.map(ShopOrderVo::getBrandName).orElse(""));
            returnResults.setOrderId(orderId);

            returnResults.setItemList(ApplyItem.buildList(orderVo, map));
        }

        return returnResults;
    }

}
