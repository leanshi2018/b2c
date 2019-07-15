package com.framework.loippi.pojo.activity;

import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.entity.activity.ShopActivityPromotionRule;
import com.framework.loippi.enus.ActivityRuleTypeEnus;
import com.framework.loippi.pojo.cart.CartInfo;

/**
 * 促销-满xx元送优惠卷
 */
public class ManSongJuan implements Promotion {



    private Promotion promotion;

    public ManSongJuan(Promotion romotion) {
        this.promotion = promotion;
    }

    /**
     * 例如： 满xx元送优惠券元
     */
    @Override
    public CartInfo process() {
        CartInfo cartInfo = promotion.process();

        return cartInfo;
    }
}
