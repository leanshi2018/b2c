package com.framework.loippi.dto;

import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.order.ShopOrderLogistics;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单商品分物流--返回后台数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingDto {

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
    public static class goodsInfo {

        /**
         * 商品图片
         */
        public String goodsImg;
        /**
         * 商品名称
         */
        public String goodsName;

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
        /**
         * 规格信息
         */
        private String specInfo;
        /**
         * 商品类型
         */
        public Integer goodsType;


    }

    public static List <ShippingDto> buildList(List<ShopOrderLogistics> shopOrderGoodslist, String shippingCode) {
        Map <String, List <ShopOrderLogistics>> groupOrderGoods = shopOrderGoodslist.stream().collect(Collectors.groupingBy(item -> item.getShippingCode()));
        List <ShippingDto> results = new ArrayList <>();
        Map<String,Integer> result3=shopOrderGoodslist.stream().collect(
                Collectors.groupingBy(ShopOrderLogistics::getShippingCode,Collectors.summingInt(ShopOrderLogistics::getGoodsNum))
        );

        for (List <ShopOrderLogistics> orderGoods : groupOrderGoods.values()) {

            ShippingDto shippingResult = new ShippingDto();
//            shippingResult.setItemList(OrderItemResult.buildList(orderGoods));
            String Code = orderGoods.get(0).getShippingCode();
            String expressCode = orderGoods.get(0).getShippingExpressCode();
            if (!"".equals(expressCode) && !"".equals(Code)){
                shippingResult.setShippingCode(Code);
                shippingResult.setExpressCode(expressCode);
                shippingResult.setGoodsInfoList(ShippingDto.buildList2(orderGoods));
                shippingResult.setTotalQuantity(result3.get(Code));
                if (Code.equals(shippingCode)){
                    results.add(0,shippingResult);
                }else{
                    results.add(shippingResult);
                }
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
            goodsInfo.setGoodsName(item.getGoodsName());
            goodsInfo.setSpecInfo(item.getSpecInfo());
            goodsInfo.setGoodsType(item.getGoodsType());
            results.add(goodsInfo);
        }

        return results;
    }
}
