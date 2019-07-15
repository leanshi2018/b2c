package com.framework.loippi.vo;

import com.google.common.collect.Lists;
import com.framework.loippi.entity.order.ShopOrderGoods;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;


/**
 * Created by Administrator on 2017/9/4.
 */
@Data
@ToString
public class GeneralOrderGoods {
    /**
     * 订单项ID  订单商品表索引id
     */
    private Long OrderItemId;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 商品描述，冗余字段
     */
    private String title;

    /**
     * 价格
     */
    private BigDecimal price;

    /**  */
    private String defaultImage;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 商品规格id
     */
    private Long specId;

    /**
     * 商品规格内容
     */
    private String specInfo;


    public static List <GeneralOrderGoods> buildList(List <ShopOrderGoods> orderGoodsList) {
        if (CollectionUtils.isEmpty(orderGoodsList)) {
            return Lists.newArrayList();
        }

        List <GeneralOrderGoods> resultList = Lists.newArrayList();
        for (ShopOrderGoods item : orderGoodsList) {
            GeneralOrderGoods gOrderGoods = new GeneralOrderGoods();
            gOrderGoods.setDefaultImage(item.getGoodsImage());
            gOrderGoods.setOrderItemId(item.getId());
            gOrderGoods.setGoodsId(item.getGoodsId());
            gOrderGoods.setTitle(item.getGoodsName());
            gOrderGoods.setPrice(item.getGoodsPayPrice());
            gOrderGoods.setQuantity(item.getGoodsNum());
            gOrderGoods.setSpecId(item.getSpecId());
            gOrderGoods.setSpecInfo(item.getSpecInfo());
            resultList.add(gOrderGoods);
        }

        return resultList;
    }

}
