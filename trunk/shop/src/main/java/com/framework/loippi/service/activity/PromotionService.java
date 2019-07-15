package com.framework.loippi.service.activity;

import com.framework.loippi.entity.activity.ShopActivityGoodsSpec;
import com.framework.loippi.entity.activity.ShopActivityPromotionRule;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.pojo.cart.CartInfo;
import com.framework.loippi.pojo.cart.CartVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 营销活动问题处理
 */
public interface PromotionService {

    BigDecimal promotion(List<CartInfo> cartInfoList, Map<Long, ShopActivityPromotionRule> ruleMap);

    void manjian(ShopActivityPromotionRule shopActivityPromotionRule, CartInfo cartInfo);

    void manmianyou(ShopActivityPromotionRule shopActivityPromotionRule, CartInfo cartInfo);

    void manze(ShopActivityPromotionRule shopActivityPromotionRule, CartInfo cartInfo);

    void yikoujia(ShopActivityPromotionRule shopActivityPromotionRule, CartInfo cartInfo);

    void zekou(ShopActivityPromotionRule shopActivityPromotionRule, CartInfo cartInfo);

    void manzen(ShopActivityPromotionRule shopActivityPromotionRule, CartInfo cartInfo);

    void mansong(ShopActivityPromotionRule shopActivityPromotionRule, CartInfo cartInfo);

}
