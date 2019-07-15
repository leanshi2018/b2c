package com.framework.loippi.result.order;

import com.framework.loippi.consts.OrderState;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.order.ShopOrderLogistics;
import com.framework.loippi.result.app.order.OrderResult;
import com.framework.loippi.utils.validator.DateUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 订单商品分物流--返回app数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingResult {

//    /**
//     * 配送商品
//     */
//    private List <OrderItemResult> itemList;

    /**
     * 配送公司
     */
    private String expressCode;

    /**
     * 物流单号
     */
    private String shippingCode;

    /**
     * 该包裹的商品总数量
     */
    public Integer totalQuantity;


    /**
     * 商品信息列表
     */
    private List<goodsInfo> goodsInfoList;
    @Data
    static  class goodsInfo {

        /**
         * 商品图片
         */
        public String goodsImg;

        /**
         * 商品数量
         */
        public Integer quantity;

        /**
         * 商品id
         */
        private Long goodsId;
        /**
         * 规格id
         */
        private Long specId;

    }

    public static List <ShippingResult> buildList(List<ShopOrderLogistics> shopOrderGoodslist, String shippingCode) {
        Map <String, List <ShopOrderLogistics>> groupOrderGoods = shopOrderGoodslist.stream().collect(Collectors.groupingBy(item -> Optional.ofNullable(item.getShippingCode()).orElse("1")));

        List <ShippingResult> results = new ArrayList <>();
        Map<String,Integer> result3=shopOrderGoodslist.stream().collect(
                Collectors.groupingBy(item -> Optional.ofNullable(item.getShippingCode()).orElse("1"),Collectors.summingInt(ShopOrderLogistics::getGoodsNum))
        );

        for (List <ShopOrderLogistics> orderGoods : groupOrderGoods.values()) {

            ShippingResult shippingResult = new ShippingResult();
//            shippingResult.setItemList(OrderItemResult.buildList(orderGoods));
            String Code = Optional.ofNullable(orderGoods.get(0).getShippingCode()).orElse("");
            String expressCode = orderGoods.get(0).getShippingExpressCode();
            if (!"".equals(expressCode) && !"".equals(Code)){
                shippingResult.setShippingCode(Code);
                shippingResult.setExpressCode(expressCode);
                shippingResult.setGoodsInfoList(ShippingResult.buildList2(orderGoods));
                shippingResult.setTotalQuantity(result3.get(Code));
                results.add(shippingResult);
            }

        }

        return results;
    }
    public static List <goodsInfo> buildList2(List<ShopOrderLogistics> shopOrderGoodslist) {
        List <goodsInfo> results = new ArrayList <>();
        for (ShopOrderLogistics item:shopOrderGoodslist) {
            goodsInfo goodsInfo=new goodsInfo();
            goodsInfo.setGoodsId(item.getGoodsId());
            goodsInfo.setGoodsImg(item.getGoodsImage());
            goodsInfo.setQuantity(item.getGoodsNum());
            goodsInfo.setSpecId(item.getSpecId());
            results.add(goodsInfo);
        }

        return results;
    }
}
