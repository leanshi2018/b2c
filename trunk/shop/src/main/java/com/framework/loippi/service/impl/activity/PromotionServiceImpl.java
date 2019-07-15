package com.framework.loippi.service.impl.activity;

import com.framework.loippi.entity.activity.ShopActivityGoodsSpec;
import com.framework.loippi.entity.activity.ShopActivityPromotionRule;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.enus.ActivityRuleTypeEnus;
import com.framework.loippi.pojo.cart.CartInfo;
import com.framework.loippi.pojo.cart.CartVo;
import com.framework.loippi.service.activity.PromotionService;
import com.framework.loippi.utils.NumberUtils;
import com.framework.loippi.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 计算活动的优惠情况
 */
@Service
@Slf4j
public class PromotionServiceImpl implements PromotionService {

    /**
     * @param cartInfoList
     * @param ruleMap
     * @return
     */
    @Override
    public BigDecimal promotion(List<CartInfo> cartInfoList, Map<Long, ShopActivityPromotionRule> ruleMap) {
        BigDecimal promotion = new BigDecimal(0);
        if (ruleMap.size() == 0) {
            return promotion;
        }
        //循环计算单品是否满足活动
        for (CartInfo cartInfo : cartInfoList) {
            for (CartVo cartVo : cartInfo.getList()) {
                if (cartVo.getActivityType() == null) {
                    continue;
                }
                if (cartVo.getActivityType() == 1 || cartVo.getActivityType() == 120) {
                    //满减活动
                    ShopActivityPromotionRule shopActivityPromotionRule = ruleMap.get(cartVo.getActivityId());
                    if (shopActivityPromotionRule != null) {
                        if (shopActivityPromotionRule.getRuleType() == ActivityRuleTypeEnus.MAN_JIAN) {//满减
                            manjian(shopActivityPromotionRule, cartInfo);
                        } else if (shopActivityPromotionRule.getRuleType() == ActivityRuleTypeEnus.MAN_MIANYOU) {//满免邮
                            manmianyou(shopActivityPromotionRule, cartInfo);
                        } else if (shopActivityPromotionRule.getRuleType() == ActivityRuleTypeEnus.MAN_SONG) {//满送
                            mansong(shopActivityPromotionRule, cartInfo);
                        } else if (shopActivityPromotionRule.getRuleType() == ActivityRuleTypeEnus.MAN_ZHE) {//满则
                            manzen(shopActivityPromotionRule, cartInfo);
                        } else if (shopActivityPromotionRule.getRuleType() == 50) {//团购
                            manjian(shopActivityPromotionRule, cartInfo);
                        } else if (shopActivityPromotionRule.getRuleType() == ActivityRuleTypeEnus.YI_KOU_JIA) {//一口价
                            yikoujia(shopActivityPromotionRule, cartInfo);
                        } else if (shopActivityPromotionRule.getRuleType() == ActivityRuleTypeEnus.ZHE_KOU) {//折扣
                            zekou(shopActivityPromotionRule, cartInfo);
                        }
                        promotion = promotion.add(cartVo.getItemPromotionPrice());
                    }
                }
            }
        }
        return promotion;
    }

    @Override
    public void manjian(ShopActivityPromotionRule shopActivityPromotionRule, CartInfo cartInfo) {
        if (shopActivityPromotionRule.getLimitType() == 1) {
            //购买金额类型
            BigDecimal limitAmount = NumberUtils.getBigDecimal(shopActivityPromotionRule.getLimitWhere());
            BigDecimal discountAmount = NumberUtils.getBigDecimal(shopActivityPromotionRule.getCouponSource());
            BigDecimal money=cartInfo.getActualGoodsTotalPrice().add(cartInfo.getPreferentialFreightAmount()).subtract(cartInfo.getFreightAmount());
            if (money.compareTo(limitAmount) >= 0) {
                //满足满减条件
                cartInfo.setCouponAmount(cartInfo.getCouponAmount().add(discountAmount));
                cartInfo.setActualGoodsTotalPrice(cartInfo.getActualGoodsTotalPrice().subtract(discountAmount));
//                cartVo.setItemTotalPrice(cartVo.getItemTotalPrice().subtract(discountAmount));
//                cartVo.setItemPromotionPrice(discountAmount);
            }
        } else if (shopActivityPromotionRule.getLimitType() == 2) {
            //购买数量
            Integer countNum = StringUtil.toInt(shopActivityPromotionRule.getLimitWhere());
            BigDecimal discountAmount = NumberUtils.getBigDecimal(shopActivityPromotionRule.getCouponSource());
            if (cartInfo.getGoodsNum() >= countNum) {
                //满足满减条件
//                cartVo.setItemPromotionPrice(discountAmount);
//                cartVo.setItemTotalPrice(cartVo.getItemTotalPrice().subtract(discountAmount));

            }
        }
    }

    @Override
    public void manmianyou(ShopActivityPromotionRule shopActivityPromotionRule,CartInfo cartInfo) {

    }

    @Override
    public void manze(ShopActivityPromotionRule shopActivityPromotionRule, CartInfo cartInfo) {
        // 满减xx元打xx折
        BigDecimal limitAmount = NumberUtils.getBigDecimal(shopActivityPromotionRule.getLimitWhere());
        BigDecimal discount = NumberUtils.getBigDecimal(shopActivityPromotionRule.getCouponSource());
        BigDecimal money=cartInfo.getActualGoodsTotalPrice().add(cartInfo.getPreferentialFreightAmount()).subtract(cartInfo.getFreightAmount());
        BigDecimal itemTotalPrice = BigDecimal.ZERO;
        if (money.compareTo(limitAmount) >= 0) {
            //满足满减条件
            itemTotalPrice=cartInfo.getActualGoodsTotalPrice()
                    .multiply(discount)
                    .divide(new BigDecimal(100))
                    .setScale(2, BigDecimal.ROUND_HALF_EVEN);
            cartInfo.setActualGoodsTotalPrice(cartInfo.getActualGoodsTotalPrice().subtract(itemTotalPrice));
            cartInfo.setCouponAmount(cartInfo.getCouponAmount().add(itemTotalPrice));
//            cartVo.setGoodsRetailPrice(cartVo.getGoodsRetailPrice()
//                    .multiply(discount)
//                    .divide(new BigDecimal(100))
//                    .setScale(2, BigDecimal.ROUND_HALF_EVEN));
//            cartVo.setGoodsBigPrice(cartVo.getGoodsBigPrice()
//                    .multiply(discount)
//                    .divide(new BigDecimal(100))
//                    .setScale(2, BigDecimal.ROUND_HALF_EVEN));
//            cartVo.setGoodsMemberPrice(cartVo.getGoodsMemberPrice()
//                    .multiply(discount)
//                    .divide(new BigDecimal(100))
//                    .setScale(2, BigDecimal.ROUND_HALF_EVEN));
        }
    }

    @Override
    public void yikoujia(ShopActivityPromotionRule shopActivityPromotionRule, CartInfo cartInfo) {
//        BigDecimal itemTotalPrice = cartVo.getItemTotalPrice();
//        BigDecimal discount = NumberUtils.getBigDecimal(shopActivityPromotionRule.getCouponSource());
//        cartVo.setItemTotalPrice(discount.multiply(new BigDecimal(cartVo.getGoodsNum())));
//        cartVo.setItemPromotionPrice(itemTotalPrice.subtract(cartVo.getItemTotalPrice()));
    }

    @Override
    public void zekou(ShopActivityPromotionRule shopActivityPromotionRule, CartInfo cartInfo) {
        //购买即有折扣
        BigDecimal discount = NumberUtils.getBigDecimal(shopActivityPromotionRule.getCouponSource());
        BigDecimal itemTotalPrice=(cartInfo.getActualGoodsTotalPrice().add(cartInfo.getPreferentialFreightAmount()).subtract(cartInfo.getFreightAmount()))
                .multiply(discount)
                .divide(new BigDecimal(100))
                .setScale(2, BigDecimal.ROUND_HALF_EVEN);
        cartInfo.setActualGoodsTotalPrice(cartInfo.getActualGoodsTotalPrice().subtract(itemTotalPrice));
        cartInfo.setCouponAmount(cartInfo.getCouponAmount().add(itemTotalPrice));
//        BigDecimal itemTotalPrice = cartVo.getItemTotalPrice();
//        cartVo.setItemTotalPrice(cartVo.getItemTotalPrice()
//                .multiply(discount)
//                .divide(new BigDecimal(100))
//                .setScale(2, BigDecimal.ROUND_HALF_EVEN));
//        cartVo.setGoodsRetailPrice(cartVo.getGoodsRetailPrice()
//                .multiply(discount)
//                .divide(new BigDecimal(100))
//                .setScale(2, BigDecimal.ROUND_HALF_EVEN));
//        cartVo.setGoodsBigPrice(cartVo.getGoodsBigPrice()
//                .multiply(discount)
//                .divide(new BigDecimal(100))
//                .setScale(2, BigDecimal.ROUND_HALF_EVEN));
//        cartVo.setGoodsMemberPrice(cartVo.getGoodsMemberPrice()
//                .multiply(discount)
//                .divide(new BigDecimal(100))
//                .setScale(2, BigDecimal.ROUND_HALF_EVEN));
//        cartVo.setItemPromotionPrice(itemTotalPrice.subtract(cartVo.getItemTotalPrice()));
    }

    @Override
    public void manzen(ShopActivityPromotionRule shopActivityPromotionRule, CartInfo cartInfo) {

    }

    @Override
    public void mansong(ShopActivityPromotionRule shopActivityPromotionRule, CartInfo cartInfo) {

    }
}
