package com.framework.loippi.result.evaluate;

import com.framework.loippi.entity.order.ShopOrderGoods;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.collections.CollectionUtils;

/**
 * 评价中心商品返回app结果
 */
@Data
@Accessors(chain = true)
public class EvaluationCenterGoodsResult {

    /**
     * 订单商品id
     */
    private Long orderGoodsId;
    /**
     * 评价id
     */
    private Long evalGoodsId;

    /**
     * 图片
     */
    private String showImage;

    /**
     * 标题
     */
    private String goodsName;

    /**
     * 已追评 0未评价 1已评价
     */
    private Integer evaluateState;

    public static List<EvaluationCenterGoodsResult> buildList(List<ShopOrderGoods> shopOrderGoods) {
        if (CollectionUtils.isEmpty(shopOrderGoods)) {
            return Lists.newArrayList();
        }

        List<EvaluationCenterGoodsResult> resultList = new ArrayList<>();
        for (ShopOrderGoods orderGoods : shopOrderGoods) {
            Optional<ShopOrderGoods> optOrderGoods = Optional.ofNullable(orderGoods);
            EvaluationCenterGoodsResult result = new EvaluationCenterGoodsResult()
                    .setOrderGoodsId(optOrderGoods.map(ShopOrderGoods::getId).orElse(-1L))
                    .setShowImage(optOrderGoods.map(ShopOrderGoods::getGoodsImage).orElse(""))
                    .setGoodsName(optOrderGoods.map(ShopOrderGoods::getGoodsName).orElse(""))
                    .setEvaluateState(optOrderGoods.map(ShopOrderGoods::getEvaluationStatus).orElse(0))
                    .setEvalGoodsId(optOrderGoods.map(ShopOrderGoods::getEvaluationId).orElse(-1L));
            resultList.add(result);
        }

        return resultList;
    }
}
