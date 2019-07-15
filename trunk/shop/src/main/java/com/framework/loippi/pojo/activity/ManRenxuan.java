package com.framework.loippi.pojo.activity;

import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.entity.activity.ShopActivityPromotionRule;
import com.framework.loippi.entity.cart.ShopCart;
import com.framework.loippi.enus.ActivityRuleTypeEnus;
import com.framework.loippi.pojo.cart.CartInfo;
import com.framework.loippi.pojo.cart.CartVo;
import com.framework.loippi.utils.CalcUtil;
import com.framework.loippi.utils.NumberUtils;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 促销-xx元任选xx件（如100块可选3件商品)
 */
public class ManRenxuan implements Promotion {

    private Promotion promotion;

    public ManRenxuan(Promotion promotion) {
        this.promotion = promotion;
    }

    @Override
    public CartInfo process() {
        CartInfo cartInfo = promotion.process();

        return cartInfo;
    }
}
