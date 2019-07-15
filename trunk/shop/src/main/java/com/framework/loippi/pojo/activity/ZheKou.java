package com.framework.loippi.pojo.activity;

import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.entity.activity.ShopActivityPromotionRule;
import com.framework.loippi.enus.ActivityRuleTypeEnus;
import com.framework.loippi.pojo.cart.CartInfo;
import com.framework.loippi.pojo.cart.CartVo;
import com.framework.loippi.utils.NumberUtils;

import java.math.BigDecimal;
import java.util.List;


/**
 * 促销-折扣
 */
public class ZheKou implements Promotion {

    private Promotion promotion;

    public ZheKou(Promotion promotion) {
        this.promotion = promotion;
    }

    /**
     * 例如： 打9折
     */
    @Override
    public CartInfo process() {
        CartInfo cartInfo = promotion.process();

        return cartInfo;
    }
}
