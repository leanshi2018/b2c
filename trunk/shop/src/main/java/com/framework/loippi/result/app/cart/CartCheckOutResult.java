package com.framework.loippi.result.app.cart;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.consts.ShopOrderDiscountTypeConsts;
import com.framework.loippi.entity.cart.ShopCart;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.order.ShopOrderDiscountType;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.enus.ActivityTypeEnus;
import com.framework.loippi.utils.GoodsUtils;
import com.framework.loippi.vo.gifts.Gifts;
import com.framework.loippi.vo.user.UserInfoVo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 购物车结算
 */
@Data
@Accessors(chain = true)
public class CartCheckOutResult {
    /*************************************2019双11用********************************************/
    /**
     * 是否显示赠品 0：不显示 1：显示
     */
    private Integer showFlag;

    /**
     * 是否显示赠品 0：不显示 1：显示
     */
    private Integer bShowFlag;

    /**
     * 赠品集合
     */
    public List<Gifts> gifts;

    /**
     * 赠品集合(小程序)
     */
    @JsonSerialize(using = ToStringSerializer.class)
    public List<Gifts> giftsApplet;

    /**
     * 附赠商品集合
     */
    public List<Gifts> bundledGoods;

    /**
     * 附赠商品集合(小程序)
     */
    @JsonSerialize(using = ToStringSerializer.class)
    public List<Gifts> bundledGoodsApplet;
    /*************************************2019双11用********************************************/
    /**
     * 有没有收货地址
     */
    private Integer hadReceiveAddr;

    /**
     * 订单优惠类型id
     */
    private Long shopOrderTypeId;

    /**
     * 收货名
     */
    private String receiveName;

    /**
     * 收货手机
     */
    private String receivePhone;

    /**
     * 收货地址【广东省广州市天河区棠安路188号乐天大厦8楼808-812】
     */
    private String receiveAddrInfo;

    /**
     * 收货地址省市区
     */
    private String receiveProvice;
    /**
     * 收货地址详情
     */
    private String receiveDetail;
    /**
     * 收货地址id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer addressId;

    /**
     * 商品件数
     */
    private Integer totalQuantity;

    /**
     * 商品图片列表, 逗号隔开
     */
//    private String goodsImgs;

    /**
     * 可用积分+可抵扣【可用3000打赏积分抵扣30元】
     */
//    private Integer rewardPoint;

//    /**
//     * 积分抵扣金额
//     */
//    private BigDecimal rewardPointAmount;

    /**
     * 商品金额
     */
    private BigDecimal goodsTotalAmount;

    /**
     * 优惠总额
     */
    public BigDecimal couponAmount;
    /**
     * 优惠券优惠金额
     */
    public BigDecimal useCouponAmount;
    /**
     * plus会员优惠金额
     */
    public BigDecimal plusVipAmount;
    /**
     * 活动优惠金额
     */
    public BigDecimal activityAmount;
    /**
     * 会员等级优惠金额
     */
    public BigDecimal rankAmount;

    /**
     * 实付金额
     */
    public BigDecimal needToPay;
    /**
     * 订单PV总值
     */
    public BigDecimal totalPpv;
    /**
     * 实际订单PV总值
     */
    public BigDecimal actualTotalPpv;
    /**
     * 运费
     */
    public BigDecimal freightAmount;
    /**
     * 优惠运费
     */
    public BigDecimal preferentialFreightAmount;


    /**
     * 购物车id
     */
    public List<Long> cartIds;

    /**
     * 购物车id json字符串
     */
    @JsonSerialize(using = ToStringSerializer.class)
    public List<Long> cartIdsStr;

    /**
     * 自提用户信息
     */
    public userInfo userInfo;

    /**
     * 商品根据品牌分组
     */
    public List<StoreGoodsContainer> storeGoodsContainers;
    /**
     * 订单可选择类型的集合
     */
    public List<selectShopOrderType> selectShopOrderTypeList;
    //**************************************************************************
    /**
     * 可使用优惠券列表
     */
    private ArrayList<Coupon> couponList=Lists.newArrayList();
    /**
     * 不可使用优惠券列表
     */
    private ArrayList<Coupon> noUseCouponList=Lists.newArrayList();
    /**
     * 优惠券id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long couponId;
    /**
     * 优惠券使用说明
     */
    private String couponScopeRemark;

    /**
     * 是否存在plus会员商品 1：不存在  2：存在
     */
    private Integer svipFlag;
    /**
     * 可分单会员列表
     */
    private ArrayList<UserInfoVo> cutUserInfoList=Lists.newArrayList();

    /**
     * 0:购物车结算页  1：立即购买结算页
     */
    private Integer immediatelyFlag;
    /**
     * 活动id
     */
    private Long activityId;

    //活动规格id
    private Long activitySkuId;

    //活动商品id
    private Long activityGoodsId;

    /**
     * 活动id String
     */
    private String activityIdStr;

    //活动规格id String
    private String activitySkuIdStr;

    //活动商品id String
    private String activityGoodsIdStr;

    /**
     * 活动类型
     *
     * @see ActivityTypeEnus
     */
    private Integer activityType;
    @Data
    public static class selectShopOrderType {
        Long shopOrderTypeId;
        String shopOrderTypeName;
        Integer isSelect;
    }

    @Data
    public static class userInfo {
        String userName;
        String userPhone;
        String contactName;
        String contactPhone;
        String contactAddrInfo;
    }

    @Data
    @Accessors(chain = true)
    public static class StoreGoodsContainer {

        private Long brandId;

        private String brandName;

        private String brandIcon;

        private List<BaseGoodsResult> goodsList;

        public static List<StoreGoodsContainer> buildList(List<ShopCart> cartList,ShopOrderDiscountType shopOrderDiscountType) {
            // 拆单,将购物车数据存入map,键为品牌id,值为品牌名称,得到所有品牌的信息(唯一,去重)
            Map<Long, String> storeMap = Maps.newHashMap();
            Map<Long, String> storeMap2 = Maps.newHashMap();
            for (ShopCart cart : cartList) {
                storeMap.put(cart.getBrandId(), cart.getBrandName());
                storeMap2.put(cart.getBrandId(), cart.getBrandIcon());
            }

            // 去重后店铺id
            Set<Long> brandIds = storeMap.keySet();
            // 一个品牌对应一个cartInfo 分单
            List<StoreGoodsContainer> goodsContiners = Lists.newArrayList();
            for (Long brandId : brandIds) {
                StoreGoodsContainer container = new StoreGoodsContainer()
                        .setBrandId(brandId)
                        .setBrandIcon(Optional.ofNullable(storeMap2.get(brandId)).orElse(""))
                        .setBrandName(Optional.ofNullable(storeMap.get(brandId)).orElse("no name"));
                container.setGoodsList(Lists.newArrayList());
                for (ShopCart cart : cartList) {
                    if (cart.getBrandId().equals(brandId)) {
                        Optional<ShopCart> optCart = Optional.ofNullable(cart);
                        BaseGoodsResult goodsResult = new BaseGoodsResult()
                                .setGoodsId(optCart.map(ShopCart::getGoodsId).orElse(0L))
                                .setGoodsName(optCart.map(ShopCart::getGoodsName).orElse(""))
                                //TODO 设置价格
//                                .setGoodsMarketPrice(optCart.map(ShopCart::getGoodsRetailPrice).orElse(0))
                                //.setGoodsStorePrice(NumberUtils.format(cart.getGoodsStorePrice()))
                                .setDefaultImage(optCart.map(ShopCart::getGoodsImages).orElse(""))
                                .setQuantity(optCart.map(ShopCart::getGoodsNum).orElse(0))
                                .setSpecId(optCart.map(ShopCart::getSpecId).orElse(0L))
                                .setSpecInfo(optCart.map(ShopCart::getSpecInfo).orElse(""))
                                .setPpv(optCart.map(ShopCart::getPpv).orElse(BigDecimal.ZERO))
                                .setGoodsType(optCart.map(ShopCart::getGoodsType).orElse(1));
                        Integer type=shopOrderDiscountType.getPreferentialType();
                        goodsResult.setGoodsMarketPrice(cart.getGoodsRetailPrice());
                        goodsResult.setVipPrice(cart.getGoodsMemberPrice());
                        goodsResult.setBigPpvPrice(cart.getGoodsBigPrice());
//                        if (type == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_MEMBER) {
//                            goodsResult.setGoodsMarketPrice(cart.getGoodsMemberPrice());
//                        }
//                        if (type == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PREFERENTIAL) {
//                            BigDecimal money = cart.getGoodsRetailPrice().subtract(shopOrderDiscountType.getPreferential());
//                            if (money.compareTo(new BigDecimal("0")) == -1) {
//                                money = new BigDecimal("0");
//                            }
//                            goodsResult.setGoodsMarketPrice(money);
//                        }
                        if (type == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PPV||type == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PLUS) {
//                            goodsResult.setGoodsMarketPrice(cart.getGoodsBigPrice());
                            goodsResult.setPpv(cart.getBigPpv());
                        }
                        container.getGoodsList().add(goodsResult);
                    }
                }
                goodsContiners.add(container);
            }
            return goodsContiners;
        }
    }

    /**
     * @param address
     * @param moneyMap
     * @return
     */
    public static CartCheckOutResult build(Map<String, Object> moneyMap,
                                           List<ShopCart> cartList, RdMmAddInfo address, Long shopOrderTypeId, ShopOrderDiscountType shopOrderDiscountType) {
        CartCheckOutResult cartCheckOutResult = new CartCheckOutResult().setHadReceiveAddr(address == null ? 0 : 1);
        Optional<RdMmAddInfo> optAddress = Optional.ofNullable(address);
        // 个人收货信息信息
        cartCheckOutResult
                .setAddressId(optAddress.map(RdMmAddInfo::getAid).orElse(-1))
                .setReceiveName(optAddress.map(RdMmAddInfo::getConsigneeName).orElse(""))
                .setReceivePhone(optAddress.map(RdMmAddInfo::getMobile).orElse(""))
                .setReceiveAddrInfo(optAddress.map(RdMmAddInfo::getAddProvinceCode).orElse("") +
                        optAddress.map(RdMmAddInfo::getAddCityCode).orElse("")+
                        optAddress.map(RdMmAddInfo::getAddCountryCode).orElse("")+
                        optAddress.map(RdMmAddInfo::getAddDetial).orElse(""))
                .setReceiveProvice(optAddress.map(RdMmAddInfo::getAddProvinceCode).orElse("") +
                        optAddress.map(RdMmAddInfo::getAddCityCode).orElse("")+
                        optAddress.map(RdMmAddInfo::getAddCountryCode).orElse(""))
                .setReceiveDetail(optAddress.map(RdMmAddInfo::getAddDetial).orElse(""))
                .setShopOrderTypeId(Optional.ofNullable(shopOrderTypeId).orElse(-1L));

        // 购物车总数量
        int totalNum = 0;
        BigDecimal totalPpv = BigDecimal.ZERO;
        BigDecimal actualTotalPpv = BigDecimal.ZERO;
        Double totalWeight = 0d;
        // 商品图片
//        StringBuilder imgs = new StringBuilder();
        for (ShopCart cart : cartList) {
            totalNum += cart.getGoodsNum();
//            imgs.append(cart.getGoodsImages()).append(",");
            if (cartCheckOutResult.getCartIds() == null) {
                cartCheckOutResult.setCartIds(new ArrayList<Long>());
                cartCheckOutResult.setCartIdsStr(new ArrayList<Long>());
            }
            totalPpv=totalPpv.add(Optional.ofNullable(cart.getPpv()).orElse(BigDecimal.ZERO).multiply(BigDecimal.valueOf(cart.getGoodsNum())));
//            totalPpv += Optional.ofNullable(cart.getPpv()).orElse(0) * cart.getGoodsNum();
            totalWeight += Optional.ofNullable(cart.getWeight()).orElse(0d) * cart.getGoodsNum();
            if (shopOrderDiscountType.getPreferentialType() == 3||shopOrderDiscountType.getPreferentialType()==8) {
                actualTotalPpv=actualTotalPpv.add(Optional.ofNullable(cart.getBigPpv()).orElse(BigDecimal.ZERO).multiply(BigDecimal.valueOf(cart.getGoodsNum())));
//                actualTotalPpv += Optional.ofNullable(cart.getBigPpv()).orElse(0) * cart.getGoodsNum();
            } else {
                actualTotalPpv=actualTotalPpv.add(Optional.ofNullable(cart.getPpv()).orElse(BigDecimal.ZERO).multiply(BigDecimal.valueOf(cart.getGoodsNum())));
//                actualTotalPpv += Optional.ofNullable(cart.getPpv()).orElse(0) * cart.getGoodsNum();
            }
            cartCheckOutResult.getCartIds().add(cart.getId());
            cartCheckOutResult.getCartIdsStr().add(cart.getId());
        }

        cartCheckOutResult
                .setTotalQuantity(totalNum)
                .setTotalPpv(totalPpv)
                .setActualTotalPpv(actualTotalPpv)
//            .setGoodsImgs(imgs.toString())
                // 可用积分
//            .setRewardPoint(Optional.ofNullable((int) moneyMap.get("rewardPoint")).orElse(0))
                // 可用积分抵扣金额
//            .setRewardPointAmount(Optional.ofNullable((BigDecimal) moneyMap.get("rewardPointPrice")).orElse(new BigDecimal("0")))
                // 商品金额
                .setGoodsTotalAmount(Optional.ofNullable((BigDecimal) moneyMap.get("totalGoodsPrice")).orElse(new BigDecimal("0")))
                // 优惠券金额
                .setCouponAmount(Optional.ofNullable((BigDecimal) moneyMap.get("couponPrice")).orElse(new BigDecimal("0")))
                // 实付款
                .setNeedToPay(Optional.ofNullable((BigDecimal) moneyMap.get("totalPrice")).orElse(new BigDecimal("0")))
                //运费
                .setFreightAmount(Optional.ofNullable((BigDecimal) moneyMap.get("freightAmount")).orElse(new BigDecimal("0")))
                //运费优惠
                .setPreferentialFreightAmount(Optional.ofNullable((BigDecimal) moneyMap.get("preferentialFreightAmount")).orElse(new BigDecimal("0")))
                //优惠金额
                .setCouponAmount(Optional.ofNullable((BigDecimal) moneyMap.get("couponAmount")).orElse(new BigDecimal("0")))
                // 商品数据
                .setStoreGoodsContainers(StoreGoodsContainer.buildList(cartList, shopOrderDiscountType));
        return cartCheckOutResult;
    }

    public static CartCheckOutResult buildNew(Map<String, Object> moneyMap,
                                              List<ShopCart> cartList, RdMmAddInfo address, Long shopOrderTypeId, ShopOrderDiscountType shopOrderDiscountType) {
        CartCheckOutResult cartCheckOutResult = new CartCheckOutResult().setHadReceiveAddr(address == null ? 0 : 1);
        Integer type = Optional.ofNullable((Integer) moneyMap.get("discountType")).orElse(1);
        shopOrderDiscountType.setPreferentialType(type);
        Optional<RdMmAddInfo> optAddress = Optional.ofNullable(address);
        // 个人收货信息信息
        cartCheckOutResult
                .setAddressId(optAddress.map(RdMmAddInfo::getAid).orElse(-1))
                .setReceiveName(optAddress.map(RdMmAddInfo::getConsigneeName).orElse(""))
                .setReceivePhone(optAddress.map(RdMmAddInfo::getMobile).orElse(""))
                .setReceiveAddrInfo(optAddress.map(RdMmAddInfo::getAddProvinceCode).orElse("") +
                        optAddress.map(RdMmAddInfo::getAddCityCode).orElse("")+
                        optAddress.map(RdMmAddInfo::getAddCountryCode).orElse("")+
                        optAddress.map(RdMmAddInfo::getAddDetial).orElse(""))
                .setReceiveProvice(optAddress.map(RdMmAddInfo::getAddProvinceCode).orElse("") +
                        optAddress.map(RdMmAddInfo::getAddCityCode).orElse("")+
                        optAddress.map(RdMmAddInfo::getAddCountryCode).orElse(""))
                .setReceiveDetail(optAddress.map(RdMmAddInfo::getAddDetial).orElse(""))
                .setShopOrderTypeId(Optional.ofNullable(shopOrderTypeId).orElse(-1L));

        // 购物车总数量
        int totalNum = 0;
        BigDecimal totalPpv = BigDecimal.ZERO;
        BigDecimal actualTotalPpv = BigDecimal.ZERO;
        Double totalWeight = 0d;
        // 商品图片
//        StringBuilder imgs = new StringBuilder();
        for (ShopCart cart : cartList) {
            totalNum += cart.getGoodsNum();
//            imgs.append(cart.getGoodsImages()).append(",");
            if (cartCheckOutResult.getCartIds() == null) {
                cartCheckOutResult.setCartIds(new ArrayList<Long>());
                cartCheckOutResult.setCartIdsStr(new ArrayList<Long>());
            }
            totalPpv=totalPpv.add(Optional.ofNullable(cart.getPpv()).orElse(BigDecimal.ZERO).multiply(BigDecimal.valueOf(cart.getGoodsNum())));
//            totalPpv += Optional.ofNullable(cart.getPpv()).orElse(0) * cart.getGoodsNum();
            totalWeight += Optional.ofNullable(cart.getWeight()).orElse(0d) * cart.getGoodsNum();
            if (shopOrderDiscountType.getPreferentialType() == 3||shopOrderDiscountType.getPreferentialType() == 8) {
                actualTotalPpv=actualTotalPpv.add(Optional.ofNullable(cart.getBigPpv()).orElse(BigDecimal.ZERO).multiply(BigDecimal.valueOf(cart.getGoodsNum())));
//                actualTotalPpv += Optional.ofNullable(cart.getBigPpv()).orElse(0) * cart.getGoodsNum();
            } else {
                actualTotalPpv=actualTotalPpv.add(Optional.ofNullable(cart.getPpv()).orElse(BigDecimal.ZERO).multiply(BigDecimal.valueOf(cart.getGoodsNum())));
//                actualTotalPpv += Optional.ofNullable(cart.getPpv()).orElse(0) * cart.getGoodsNum();
            }
            cartCheckOutResult.getCartIds().add(cart.getId());
            cartCheckOutResult.getCartIdsStr().add(cart.getId());
        }

        cartCheckOutResult
                .setTotalQuantity(totalNum)
                .setTotalPpv(totalPpv)
                .setActualTotalPpv(actualTotalPpv)
//            .setGoodsImgs(imgs.toString())
                // 可用积分
//            .setRewardPoint(Optional.ofNullable((int) moneyMap.get("rewardPoint")).orElse(0))
                // 可用积分抵扣金额
//            .setRewardPointAmount(Optional.ofNullable((BigDecimal) moneyMap.get("rewardPointPrice")).orElse(new BigDecimal("0")))
                // 商品金额
                .setGoodsTotalAmount(Optional.ofNullable((BigDecimal) moneyMap.get("totalGoodsPrice")).orElse(new BigDecimal("0")).subtract(Optional.ofNullable((BigDecimal) moneyMap.get("rankDiscount")).orElse(new BigDecimal("0"))))
                // 优惠总金额
/*                .setCouponAmount(Optional.ofNullable((BigDecimal) moneyMap.get("couponPrice")).orElse(new BigDecimal("0")).subtract(Optional.ofNullable((BigDecimal) moneyMap.get("rankDiscount")).orElse(new BigDecimal("0"))))*/
                // 实付款
                .setNeedToPay(Optional.ofNullable((BigDecimal) moneyMap.get("totalPrice")).orElse(new BigDecimal("0")))
                //运费
                .setFreightAmount(Optional.ofNullable((BigDecimal) moneyMap.get("freightAmount")).orElse(new BigDecimal("0")))
                //运费优惠
                .setPreferentialFreightAmount(Optional.ofNullable((BigDecimal) moneyMap.get("preferentialFreightAmount")).orElse(new BigDecimal("0")))
                //优惠金额
                .setCouponAmount(Optional.ofNullable((BigDecimal) moneyMap.get("couponAmount")).orElse(new BigDecimal("0")).subtract(Optional.ofNullable((BigDecimal) moneyMap.get("rankDiscount")).orElse(new BigDecimal("0"))))
                //等级优惠
                //.setRankAmount(Optional.ofNullable((BigDecimal) moneyMap.get("rankDiscount")).orElse(new BigDecimal("0")))
                .setRankAmount(BigDecimal.ZERO)
                //优惠券优惠
                .setUseCouponAmount(Optional.ofNullable((BigDecimal) moneyMap.get("useCouponAmount")).orElse(new BigDecimal("0")))
                //plus会员优惠
                .setPlusVipAmount(Optional.ofNullable((BigDecimal) moneyMap.get("plusVipAmount")).orElse(new BigDecimal("0")))
                // 商品数据
                .setStoreGoodsContainers(StoreGoodsContainer.buildList(cartList, shopOrderDiscountType))
        .setSvipFlag(Optional.ofNullable((Integer) moneyMap.get("svipGoods")).orElse(1));
        cartCheckOutResult.setCouponId(Optional.ofNullable((Long) moneyMap.get("couponId")).orElse(null));
        ArrayList<Coupon> couponList = (ArrayList<Coupon>) moneyMap.get("couponList");
        if(couponList!=null&&couponList.size()>0){
            Boolean flag=false;
            for (Coupon coupon : couponList) {
                if(coupon.getId().equals(cartCheckOutResult.getCouponId())){
                    cartCheckOutResult.setCouponScopeRemark(coupon.getScopeRemark());
                    flag=true;
                    break;
                }
            }
            if(!flag){
                cartCheckOutResult.setCouponScopeRemark("");
            }
        }else {
            cartCheckOutResult.setCouponScopeRemark("");
        }
        cartCheckOutResult.setCouponList(couponList);
        ArrayList<Coupon> noUseCouponList = (ArrayList<Coupon>) moneyMap.get("noUseCouponList");
        cartCheckOutResult.setNoUseCouponList(noUseCouponList);
        cartCheckOutResult.setShopOrderTypeId(type.longValue());
        return cartCheckOutResult;
    }

    public static CartCheckOutResult build2(CartCheckOutResult result, List<ShopOrderDiscountType> shopOrderDiscountTypeList, RdRanks shopMemberGrade,
                                            RdMmBasicInfo shopMember, RdMmAddInfo shopMemberAddress, List<ShopOrderDiscountType> orderDiscountTypeList) {
        List<selectShopOrderType> selectShopOrderTypeList = new ArrayList<>();
        com.framework.loippi.result.app.cart.CartCheckOutResult.userInfo userInfo = new userInfo();
        Map<String, ShopOrderDiscountType> orderMap = new HashMap<>();
        if (shopOrderDiscountTypeList != null && shopOrderDiscountTypeList.size() > 0 && shopMemberGrade != null) {
            for (ShopOrderDiscountType item : shopOrderDiscountTypeList) {
                orderMap.put(item.getId() + "", item);
                if (orderDiscountTypeList == null || orderDiscountTypeList.size() <= 0) {
                    selectShopOrderType selectShopOrderType = new selectShopOrderType();
                    selectShopOrderType.setShopOrderTypeId(item.getId());
                    selectShopOrderType.setShopOrderTypeName(item.getOrderName());
                    selectShopOrderType.setIsSelect(0);
                    selectShopOrderTypeList.add(selectShopOrderType);
                }
            }
        }
        if (orderDiscountTypeList != null && orderDiscountTypeList.size() > 0 && shopMemberGrade != null) {
            for (ShopOrderDiscountType item : orderDiscountTypeList) {
                if (orderMap.get(item.getId() + "") != null) {
                    selectShopOrderType selectShopOrderType = new selectShopOrderType();
                    selectShopOrderType.setShopOrderTypeId(item.getId());
                    selectShopOrderType.setShopOrderTypeName(item.getOrderName());
                    selectShopOrderType.setIsSelect(1);
                    selectShopOrderTypeList.add(0, selectShopOrderType);
                } else {
                    selectShopOrderType selectShopOrderType = new selectShopOrderType();
                    selectShopOrderType.setShopOrderTypeId(item.getId());
                    selectShopOrderType.setShopOrderTypeName(item.getOrderName()+"(还差"+(item.getPpv().subtract(result.getTotalPpv()))+"MI可享受该优惠)");
                    selectShopOrderType.setIsSelect(0);
                    selectShopOrderTypeList.add(selectShopOrderType);
                }

            }
        }

        selectShopOrderType selectShopOrderType = new selectShopOrderType();
        selectShopOrderType.setShopOrderTypeId(-1L);
        if (shopMemberGrade.getRankClass()>0){
            selectShopOrderType.setShopOrderTypeName("会员订单");
        }else{
            selectShopOrderType.setShopOrderTypeName("普通订单");
        }
        selectShopOrderType.setIsSelect(1);
        selectShopOrderTypeList.add(0, selectShopOrderType);

        userInfo.setUserName(shopMember.getMmNickName());
        userInfo.setUserPhone(shopMember.getMobile());
        // TODO: 2018/12/14 自提待后台补齐
        if (shopMemberAddress != null) {
            userInfo.setContactName(Optional.ofNullable(shopMemberAddress.getConsigneeName()).orElse("后台还未设置"));
            userInfo.setContactPhone(Optional.ofNullable(shopMemberAddress.getMobile()).orElse("后台还未设置"));
            userInfo.setContactAddrInfo(Optional.ofNullable(
                    shopMemberAddress.getAddProvinceCode()+shopMemberAddress.getAddCityCode()+shopMemberAddress.getAddCountryCode()
            ).orElse("后台还未设置") + Optional.ofNullable(shopMemberAddress.getAddDetial()).orElse(""));
        } else {
            userInfo.setContactName("后台还未设置");
            userInfo.setContactPhone("后台还未设置");
            userInfo.setContactAddrInfo("后台还未设置");
        }
        result.setUserInfo(userInfo);
        result.setSelectShopOrderTypeList(selectShopOrderTypeList);
        return result;
    }

    public CartCheckOutResult build2New(CartCheckOutResult result, List<ShopOrderDiscountType> shopOrderDiscountTypeList, RdRanks shopMemberGrade, RdMmBasicInfo shopMember, RdMmAddInfo shopMemberAddress) {
        List<selectShopOrderType> selectShopOrderTypeList = new ArrayList<>();
        com.framework.loippi.result.app.cart.CartCheckOutResult.userInfo userInfo = new userInfo();
        Long shopOrderTypeId = result.getShopOrderTypeId();
        if(shopOrderTypeId.equals(8L)){
            selectShopOrderType selectShopOrderType = new selectShopOrderType();
            selectShopOrderType.setShopOrderTypeId(8L);
            selectShopOrderType.setShopOrderTypeName("PLUS VIP订单");
            selectShopOrderType.setIsSelect(1);
            selectShopOrderTypeList.add(selectShopOrderType);
        }else if (shopOrderTypeId.equals(3L)){
            selectShopOrderType selectShopOrderType = new selectShopOrderType();
            selectShopOrderType.setShopOrderTypeId(3L);
            selectShopOrderType.setShopOrderTypeName("大单价");
            selectShopOrderType.setIsSelect(1);
            selectShopOrderTypeList.add(selectShopOrderType);
            selectShopOrderType selectShopOrderType1 = new selectShopOrderType();
            selectShopOrderType1.setShopOrderTypeId(2L);
            selectShopOrderType1.setShopOrderTypeName("会员订单");
            selectShopOrderType1.setIsSelect(1);
            selectShopOrderTypeList.add(selectShopOrderType1);
        } else if (shopOrderTypeId.equals(2L)){
            selectShopOrderType selectShopOrderType = new selectShopOrderType();
            selectShopOrderType.setShopOrderTypeId(3L);
            if((shopOrderDiscountTypeList.get(0).getPpv().subtract(result.getTotalPpv())).compareTo(BigDecimal.ZERO)!=1){
                selectShopOrderType.setShopOrderTypeName("大单价");
                selectShopOrderType.setIsSelect(1);
            }else {
                selectShopOrderType.setShopOrderTypeName("大单价(还差"+(shopOrderDiscountTypeList.get(0).getPpv().subtract(result.getTotalPpv()))+"MI可享受该优惠)");
                selectShopOrderType.setIsSelect(0);
            }
            selectShopOrderTypeList.add(selectShopOrderType);
            selectShopOrderType selectShopOrderType1 = new selectShopOrderType();
            selectShopOrderType1.setShopOrderTypeId(2L);
            selectShopOrderType1.setShopOrderTypeName("会员订单");
            selectShopOrderType1.setIsSelect(1);
            selectShopOrderTypeList.add(selectShopOrderType1);
        }else {
            selectShopOrderType selectShopOrderType = new selectShopOrderType();
            selectShopOrderType.setShopOrderTypeId(1L);
            selectShopOrderType.setShopOrderTypeName("普通订单");
            selectShopOrderType.setIsSelect(1);
            selectShopOrderTypeList.add(selectShopOrderType);
        }
        userInfo.setUserName(shopMember.getMmNickName());
        userInfo.setUserPhone(shopMember.getMobile());
        // TODO: 2018/12/14 自提待后台补齐
        if (shopMemberAddress != null) {
            userInfo.setContactName(Optional.ofNullable(shopMemberAddress.getConsigneeName()).orElse("后台还未设置"));
            userInfo.setContactPhone(Optional.ofNullable(shopMemberAddress.getMobile()).orElse("后台还未设置"));
            userInfo.setContactAddrInfo(Optional.ofNullable(
                    shopMemberAddress.getAddProvinceCode()+shopMemberAddress.getAddCityCode()+shopMemberAddress.getAddCountryCode()
            ).orElse("后台还未设置") + Optional.ofNullable(shopMemberAddress.getAddDetial()).orElse(""));
        } else {
            userInfo.setContactName("后台还未设置");
            userInfo.setContactPhone("后台还未设置");
            userInfo.setContactAddrInfo("后台还未设置");
        }
        result.setUserInfo(userInfo);
        result.setSelectShopOrderTypeList(selectShopOrderTypeList);
        return result;
    }

    /*********************************************************************************************************************************************************/
    public static CartCheckOutResult build3(CartCheckOutResult result, List<ShopGoods> shopGoods, Integer flag,Integer giftsNum) {
        result.setShowFlag(flag);
        ArrayList<Gifts> gifts = new ArrayList<>();
        ArrayList<Gifts> giftsApplet = new ArrayList<>();
        for (ShopGoods goods : shopGoods) {
            Gifts gift = new Gifts();
            gift.setGoodsId(goods.getId());
            gift.setGoodsName(goods.getGoodsName());
            gift.setGoodsImage(goods.getGoodsImage());
            gift.setStock(goods.getStock());
            gift.setGiftsNum(giftsNum);
            ShopGoodsSpec goodsSpec = goods.getShopGoodsSpec();
            GoodsUtils.getSepcMapAndColImgToGoodsSpec(goods, goodsSpec);
            if (goods.getGoodsType()==3){
                goodsSpec.setSpecInfo(goodsSpec.getSpecGoodsSerial());
            }else{
                String specInfo = "";
                Map<String, String> map = goodsSpec.getSepcMap();
                //遍历规格map,取出键值对,拼接specInfo
                if (map != null) {
                    Set<String> set = map.keySet();
                    for (String str : set) {
                        specInfo += str + ":" + map.get(str) + "、";
                    }
                    specInfo = specInfo.substring(0, specInfo.length() - 1);
                }
                goodsSpec.setSpecInfo(specInfo);
            }
            gift.setGoodsSpec(goodsSpec);
            gifts.add(gift);
            giftsApplet.add(gift);
        }
        result.setGifts(gifts);
        result.setGiftsApplet(giftsApplet);
        return result;
    }

    public static CartCheckOutResult build4(CartCheckOutResult result, Map<Long,ShopGoods> bGoodsMap) {
        if (bGoodsMap.size()>0){
            result.setBShowFlag(1);
        }
        ArrayList<Gifts> bundledGoods = new ArrayList<>();
        ArrayList<Gifts> bundledGoodsApplet = new ArrayList<>();
        for (Map.Entry<Long,ShopGoods> entry : bGoodsMap.entrySet()) {
            //System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
            ShopGoods goods = entry.getValue();
            Gifts gift = new Gifts();
            gift.setGoodsId(goods.getId());
            gift.setGoodsName(goods.getGoodsName());
            gift.setGoodsImage(goods.getGoodsImage());
            gift.setStock(goods.getStock());
            gift.setGiftsNum(goods.getBNum());
            ShopGoodsSpec goodsSpec = goods.getShopGoodsSpec();
            GoodsUtils.getSepcMapAndColImgToGoodsSpec(goods, goodsSpec);
            if (goods.getGoodsType()==3){
                goodsSpec.setSpecInfo(goodsSpec.getSpecGoodsSerial());
            }else{
                String specInfo = "";
                Map<String, String> map = goodsSpec.getSepcMap();
                //遍历规格map,取出键值对,拼接specInfo
                if (map != null) {
                    Set<String> set = map.keySet();
                    for (String str : set) {
                        specInfo += str + ":" + map.get(str) + "、";
                    }
                    specInfo = specInfo.substring(0, specInfo.length() - 1);
                }
                goodsSpec.setSpecInfo(specInfo);
            }
            gift.setGoodsSpec(goodsSpec);
            bundledGoods.add(gift);
            bundledGoodsApplet.add(gift);
        }
        result.setBundledGoods(bundledGoods);
        result.setBundledGoodsApplet(bundledGoodsApplet);
        return result;
    }
}
