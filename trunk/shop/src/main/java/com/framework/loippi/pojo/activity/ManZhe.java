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
 * 促销-满xx元减xx元
 */
public class ManZhe implements Promotion {

    private Promotion promotion;
    public ManZhe(Promotion promotion) {
        this.promotion = promotion;
    }

    /**
     * 例如： 满xx元减少打折
     */
    @Override
    public CartInfo process() {
        CartInfo cartInfo = promotion.process();

        return cartInfo;
    }
}
