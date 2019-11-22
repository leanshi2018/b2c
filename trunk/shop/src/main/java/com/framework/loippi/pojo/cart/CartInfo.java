package com.framework.loippi.pojo.cart;

import com.framework.loippi.consts.ActivityConsts;
import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.entity.activity.ShopActivityPromotionRule;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.enus.ActivityRuleTypeEnus;
import com.framework.loippi.enus.ActivityTypeEnus;
import com.framework.loippi.pojo.activity.Promotion;
import com.framework.loippi.service.activity.ShopActivityPromotionRuleService;
import com.framework.loippi.service.activity.ShopActivityService;
import com.framework.loippi.utils.NumberUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;


/**
 * 购物车超类
 * <p>
 * 项目名称：leimingtech-front
 * 类名称：Cart
 * 类描述：
 * 修改备注：
 */
@Data
@ToString
public class CartInfo implements Serializable {

    private ShopActivityService activityService;

    private ShopActivityPromotionRuleService ruleService;

    /**
     * 品牌id
     */
    private Long brandId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 订单留言
     */
    private String orderMessage;

    /**
     * 购物车集合
     */
    private List<CartVo> list = Lists.newArrayList();

    /**
     * 优惠 key优惠类型， value-map【key-活动id, value-活动】
     */
    private Map<Long, ShopActivity> activityMap = Maps.newHashMap();

    /**
     * key-活动id, value购物车
     */
    private Map<Long, List<CartVo>> actCartVoMap = Maps.newHashMap();
    /**
     * 可使用优惠券列表
     */
    private ArrayList<Coupon> couponList=Lists.newArrayList();

    /**
     * 商品总数量
     */
    private int goodsNum;

    /**
     * 商品总pv值
     */
    private BigDecimal ppvNum;

    /**
     * 商品总价格
     */
    private BigDecimal goodsTotalPrice;
    /**
     * 商品实际总价格
     */
    private BigDecimal actualGoodsTotalPrice;

    /**
     * 商品优惠价格
     */
    private BigDecimal couponAmount;
    /**
     * 优惠券优惠金额
     */
    public BigDecimal useCouponAmount;
    /**
     * 活动优惠金额
     */
    public BigDecimal activityAmount;
    /**
     * 会员等级优惠金额
     */
    public BigDecimal rankAmount;
    /**
     * 运费
     */
    public BigDecimal freightAmount;
    /**
     * 优惠运费
     */
    public BigDecimal preferentialFreightAmount;

    /**
     * 代金券优惠金额（优惠券分三种 自营平台有 通用券 专场券, 普通店铺 有代金券)
     */
    private BigDecimal voucherPrice = new BigDecimal(0);

    /**
     * 优惠券金额（自营商店的通用券）
     */
    private BigDecimal couponPrice = new BigDecimal(0);

    /**
     * 优惠券id
     */
    private Long couponId;

    /**
     * 代金券id
     */
    private Long voucherId;

}

