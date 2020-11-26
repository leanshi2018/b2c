package com.framework.loippi.result.app.cart;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.formula.functions.T;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.result.app.order.ResultFunction;
import com.framework.loippi.utils.NumberUtils;
import com.google.common.collect.Lists;

/**
 * 功能： app页面通用商品数据
 * 类名：BaseGoodsResult
 * 日期：2017/11/22  11:23
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
@Data
@Accessors(chain = true)
public class BaseGoodsResult {

    /**
     * 商品id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long goodsId;

    /**
     * 商品描述
     */
    private String goodsName;

    /**
     * 商品价格
     */
    private BigDecimal goodsMarketPrice;

    /**
     * 默认图片
     */
    private String defaultImage;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 商品规格内容
     */
    private String specInfo;

    /**
     * 商品规格id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long specId;
    /**
     * 商品pv值
     */
    private BigDecimal ppv;
    /**
     * vip价格
     */
    private BigDecimal vipPrice;
    /**
     * 大单pv价格
     */
    private BigDecimal bigPpvPrice;
    /**
     * 商品类型 1-普通2-换购3-组合
     */
    private Integer goodsType;

//    //是否发货 0没发货 1已发货
//    private Integer isShipment;

    public T getInstance(T t) {
        return t;
    }

    public static <T extends BaseGoodsResult> List<T> buildList(List<ShopOrderGoods> shopOrderGoods, ResultFunction<T, ShopOrderGoods> fun)
            throws Exception {
        if (CollectionUtils.isEmpty(shopOrderGoods)) {
            return Lists.newArrayList();
        }

        List<T> results = Lists.newArrayList();
        for (ShopOrderGoods shopOrderGood : shopOrderGoods) {
            if(shopOrderGood.getIsPresentation()==null||shopOrderGood.getIsPresentation()==0){
                Optional<ShopOrderGoods> optOrderGoods = Optional.ofNullable(shopOrderGood);
                T baseGoodsResult = fun.callback(shopOrderGood);
                baseGoodsResult.setGoodsId(optOrderGoods.map(ShopOrderGoods::getGoodsId).orElse(-1L));
                baseGoodsResult.setGoodsName(optOrderGoods.map(ShopOrderGoods::getGoodsName).orElse(""));
//            baseGoodsResult.setGoodsStorePrice(NumberUtils.format(shopOrderGood.getGoodsPayPrice()));
                // todo 订单实际支付价格
                baseGoodsResult.setGoodsMarketPrice(NumberUtils.format(shopOrderGood.getGoodsPayPrice()));
                baseGoodsResult.setDefaultImage(optOrderGoods.map(ShopOrderGoods::getGoodsImage).orElse(""));
                baseGoodsResult.setQuantity(optOrderGoods.map(ShopOrderGoods::getGoodsNum).orElse(0));
                baseGoodsResult.setSpecInfo(optOrderGoods.map(ShopOrderGoods::getSpecInfo).orElse(""));
                baseGoodsResult.setSpecId(optOrderGoods.map(ShopOrderGoods::getSpecId).orElse(-1L));
                baseGoodsResult.setPpv(optOrderGoods.map(ShopOrderGoods::getPpv).orElse(BigDecimal.ZERO));
                baseGoodsResult.setVipPrice(optOrderGoods.map(ShopOrderGoods::getVipPrice).orElse(BigDecimal.ZERO));
                baseGoodsResult.setBigPpvPrice(optOrderGoods.map(ShopOrderGoods::getGoodsPrice).orElse(BigDecimal.ZERO));
                baseGoodsResult.setGoodsType(optOrderGoods.map(ShopOrderGoods::getGoodsType).orElse(1));
//            if (shopOrderGood.getShippingExpressId()!=null && shopOrderGood.getShippingCode()!=null && !"".equals(shopOrderGood.getShippingCode())){
//                baseGoodsResult.setIsShipment(1);
//            }else{
//                baseGoodsResult.setIsShipment(0);
//            }
                results.add(baseGoodsResult);
            }
/*            Optional<ShopOrderGoods> optOrderGoods = Optional.ofNullable(shopOrderGood);
            T baseGoodsResult = fun.callback(shopOrderGood);
            baseGoodsResult.setGoodsId(optOrderGoods.map(ShopOrderGoods::getGoodsId).orElse(-1L));
            baseGoodsResult.setGoodsName(optOrderGoods.map(ShopOrderGoods::getGoodsName).orElse(""));
//            baseGoodsResult.setGoodsStorePrice(NumberUtils.format(shopOrderGood.getGoodsPayPrice()));
            // todo 订单实际支付价格
            baseGoodsResult.setGoodsMarketPrice(NumberUtils.format(shopOrderGood.getGoodsPayPrice()));
            baseGoodsResult.setDefaultImage(optOrderGoods.map(ShopOrderGoods::getGoodsImage).orElse(""));
            baseGoodsResult.setQuantity(optOrderGoods.map(ShopOrderGoods::getGoodsNum).orElse(0));
            baseGoodsResult.setSpecInfo(optOrderGoods.map(ShopOrderGoods::getSpecInfo).orElse(""));
            baseGoodsResult.setSpecId(optOrderGoods.map(ShopOrderGoods::getSpecId).orElse(-1L));
            baseGoodsResult.setPpv(optOrderGoods.map(ShopOrderGoods::getPpv).orElse(BigDecimal.ZERO));
//            if (shopOrderGood.getShippingExpressId()!=null && shopOrderGood.getShippingCode()!=null && !"".equals(shopOrderGood.getShippingCode())){
//                baseGoodsResult.setIsShipment(1);
//            }else{
//                baseGoodsResult.setIsShipment(0);
//            }
            results.add(baseGoodsResult);*/
        }

        return results;
    }

    public static <T extends BaseGoodsResult> List<T> buildList1(List<ShopOrderGoods> shopOrderGoods,Integer orderType, ResultFunction<T, ShopOrderGoods> fun)
            throws Exception {
        if (CollectionUtils.isEmpty(shopOrderGoods)) {
            return Lists.newArrayList();
        }

        List<T> results = Lists.newArrayList();
        for (ShopOrderGoods shopOrderGood : shopOrderGoods) {
            if(shopOrderGood.getIsPresentation()==null||shopOrderGood.getIsPresentation()==0){
                Optional<ShopOrderGoods> optOrderGoods = Optional.ofNullable(shopOrderGood);
                T baseGoodsResult = fun.callback(shopOrderGood);
                baseGoodsResult.setGoodsId(optOrderGoods.map(ShopOrderGoods::getGoodsId).orElse(-1L));
                baseGoodsResult.setGoodsName(optOrderGoods.map(ShopOrderGoods::getGoodsName).orElse(""));
//            baseGoodsResult.setGoodsStorePrice(NumberUtils.format(shopOrderGood.getGoodsPayPrice()));
                // todo 订单实际支付价格
                baseGoodsResult.setGoodsMarketPrice(NumberUtils.format(shopOrderGood.getGoodsPayPrice()));
                baseGoodsResult.setDefaultImage(optOrderGoods.map(ShopOrderGoods::getGoodsImage).orElse(""));
                baseGoodsResult.setQuantity(optOrderGoods.map(ShopOrderGoods::getGoodsNum).orElse(0));
                baseGoodsResult.setSpecInfo(optOrderGoods.map(ShopOrderGoods::getSpecInfo).orElse(""));
                baseGoodsResult.setSpecId(optOrderGoods.map(ShopOrderGoods::getSpecId).orElse(-1L));
                if(orderType==3||orderType==8){
                    baseGoodsResult.setPpv(optOrderGoods.map(ShopOrderGoods::getBigPpv).orElse(BigDecimal.ZERO));
                }else {
                    baseGoodsResult.setPpv(optOrderGoods.map(ShopOrderGoods::getPpv).orElse(BigDecimal.ZERO));
                }
                baseGoodsResult.setVipPrice(optOrderGoods.map(ShopOrderGoods::getVipPrice).orElse(BigDecimal.ZERO));
                baseGoodsResult.setBigPpvPrice(optOrderGoods.map(ShopOrderGoods::getGoodsPrice).orElse(BigDecimal.ZERO));
//            if (shopOrderGood.getShippingExpressId()!=null && shopOrderGood.getShippingCode()!=null && !"".equals(shopOrderGood.getShippingCode())){
//                baseGoodsResult.setIsShipment(1);
//            }else{
//                baseGoodsResult.setIsShipment(0);
//            }
                results.add(baseGoodsResult);
            }
/*            Optional<ShopOrderGoods> optOrderGoods = Optional.ofNullable(shopOrderGood);
            T baseGoodsResult = fun.callback(shopOrderGood);
            baseGoodsResult.setGoodsId(optOrderGoods.map(ShopOrderGoods::getGoodsId).orElse(-1L));
            baseGoodsResult.setGoodsName(optOrderGoods.map(ShopOrderGoods::getGoodsName).orElse(""));
//            baseGoodsResult.setGoodsStorePrice(NumberUtils.format(shopOrderGood.getGoodsPayPrice()));
            // todo 订单实际支付价格
            baseGoodsResult.setGoodsMarketPrice(NumberUtils.format(shopOrderGood.getGoodsPayPrice()));
            baseGoodsResult.setDefaultImage(optOrderGoods.map(ShopOrderGoods::getGoodsImage).orElse(""));
            baseGoodsResult.setQuantity(optOrderGoods.map(ShopOrderGoods::getGoodsNum).orElse(0));
            baseGoodsResult.setSpecInfo(optOrderGoods.map(ShopOrderGoods::getSpecInfo).orElse(""));
            baseGoodsResult.setSpecId(optOrderGoods.map(ShopOrderGoods::getSpecId).orElse(-1L));
            baseGoodsResult.setPpv(optOrderGoods.map(ShopOrderGoods::getPpv).orElse(BigDecimal.ZERO));
//            if (shopOrderGood.getShippingExpressId()!=null && shopOrderGood.getShippingCode()!=null && !"".equals(shopOrderGood.getShippingCode())){
//                baseGoodsResult.setIsShipment(1);
//            }else{
//                baseGoodsResult.setIsShipment(0);
//            }
            results.add(baseGoodsResult);*/
        }

        return results;
    }
}
