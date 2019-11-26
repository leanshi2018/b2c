package com.framework.loippi.service.impl.product;


import com.framework.loippi.consts.CartConstant;
import com.framework.loippi.consts.CouponConstant;
import com.framework.loippi.consts.GoodsState;
import com.framework.loippi.consts.ShopOrderDiscountTypeConsts;
import com.framework.loippi.controller.AppConstants;
import com.framework.loippi.controller.StateResult;
import com.framework.loippi.dao.cart.ShopCartDao;
import com.framework.loippi.dao.product.ShopGoodsDao;
import com.framework.loippi.dao.product.ShopGoodsSpecDao;
import com.framework.loippi.entity.activity.ShopActivityGoods;
import com.framework.loippi.entity.activity.ShopActivityGoodsSpec;
import com.framework.loippi.entity.activity.ShopActivityPromotionRule;
import com.framework.loippi.entity.cart.ShopCart;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponUser;
import com.framework.loippi.entity.order.ShopOrderDiscountType;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.entity.user.RdMmAddInfo;

import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.pojo.cart.CartInfo;
import com.framework.loippi.pojo.cart.CartVo;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.activity.*;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.service.coupon.CouponUserService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.impl.coupon.CouponServiceImpl;
import com.framework.loippi.service.product.*;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.GoodsUtils;
import com.framework.loippi.utils.NumberUtils;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.vo.cart.ShopCartVo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * SERVICE - ShopCart(购物车数据表)
 *
 * @author zijing
 * @version 2.0
 */
@Service
@Slf4j
public class ShopCartServiceImpl extends GenericServiceImpl<ShopCart, Long> implements ShopCartService {

    @Autowired
    private ShopCartDao cartDao;
    @Autowired
    private ShopGoodsSpecService goodsSpecService;
    @Autowired
    private ShopGoodsService goodsService;
    @Autowired
    private TwiterIdService twiterIdService;
    @Autowired
    private ShopActivityPromotionRuleService shopActivityPromotionRuleService;
    @Resource
    private ShopGoodsFreightService shopGoodsFreightService;
    @Resource
    private ShopGoodsFreightRuleService shopGoodsFreightRuleService;
    @Resource
    private PromotionService promotionService;
    @Autowired
    private ShopActivityGoodsSpecService shopActivityGoodsSpecService;
    @Autowired
    private ShopActivityGoodsService shopActivityGoodsService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponUserService couponUserService;

    @Autowired
    public void setGenericDao() {
        super.setGenericDao(cartDao);
    }

    /*********************
     * 业务方法
     *********************/
    @Override
    public Long saveCart(Long goodsId, String memberId, Integer rankId, Integer count, Long goodSpecId, Integer saveType,
                         Long activityId, Integer activitType, Long activityGoodsId, Long activityGoodsSpecId) {
        //商品信息
        ShopGoods goods = goodsService.find(goodsId);
        //商品规格信息
        ShopGoodsSpec goodsSpec = goodsSpecService.find(goodSpecId);
        if (goodsSpec.getSpecIsopen()!=1) {
            throw new RuntimeException("该商品目前不能购买");
        }
        //判断是否有购买权限
        if (!StringUtil.isEmpty(goods.getSalePopulationIds())){
            if (!goods.getSalePopulationIds().contains(","+rankId + ",")){
                throw new RuntimeException(goods.getGoodsName()+"目前您的会员等级不能购买该商品");
            }
        }
        // 能否修改购物车
        canSaveOrUpdateCart(goods, goodsSpec, count);
        //新建一个商品规格,通过GoodsUtils.getSepcMapAndColImgToGoodsSpec方法查询出规格的图片和值
        GoodsUtils.getSepcMapAndColImgToGoodsSpec(goods, goodsSpec);
        ShopCart cart = new ShopCart();
        cart.setId(twiterIdService.getTwiterId());
        cart.setMemberId(Long.parseLong(memberId));
        cart.setGoodsId(goodsSpec.getGoodsId());
        cart.setGoodsName(goods.getGoodsName());
        cart.setSpecId(goodsSpec.getId());
        cart.setGoodsType(goods.getGoodsType());
        //设置价格
        cart.setGoodsMemberPrice(goodsSpec.getSpecMemberPrice());
        cart.setGoodsBigPrice(goodsSpec.getSpecBigPrice());
        if(activityId!=null && activityGoodsSpecId!=null){
        List<ShopActivityGoodsSpec> shopActivityGoodsSpecList = shopActivityGoodsSpecService.findList(Paramap.create().put("activityId",activityId).put("specId",activityGoodsSpecId));
        if (!shopActivityGoodsSpecList.isEmpty()){
            cart.setGoodsRetailPrice(Optional.ofNullable(shopActivityGoodsSpecList.get(0).getActivityPrice()).orElse(goodsSpec.getSpecRetailPrice()));
        }
        }else{
            cart.setGoodsRetailPrice(goodsSpec.getSpecRetailPrice());
        }
        cart.setBigPpv(goodsSpec.getBigPpv());
        cart.setPpv(goodsSpec.getPpv());
        cart.setGoodsState(goods.getGoodsType());
        cart.setBrandId(goods.getBrandId());
        cart.setBrandName(goods.getBrandName());
        cart.setActivityId(activityId);
        cart.setActivityType(activitType);
        cart.setActivityGoodsId(activityGoodsId);
        cart.setActivitySpecId(activityGoodsSpecId);
        cart.setWeight(goodsSpec.getWeight());
        cart.setActivityId(activityId);
        cart.setActivityType(activitType);
        // 图片信息
        if (goods.getGoodsImage() != null) {
            //存储商品默认图片
            cart.setGoodsImages(goods.getGoodsImage().split(",")[0]);
        } else {
            //若商品没有默认图片存储空字段
            cart.setGoodsImages("");
        }
        // 规格信息--新建一个字段存储新的规格格式
        if (goods.getGoodsType()==3){
            cart.setSpecInfo(goodsSpec.getSpecGoodsSerial());
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
            cart.setSpecInfo(specInfo);
        }
        // 数量信息--判断是否存在相同购物车信息,包括商品id,用户id,和规格id
        Paramap paramap = Paramap.create()
                .put("goodsId", cart.getGoodsId()) // 商品id
                .put("memberId", cart.getMemberId()) // 用户id
                .put("specId", cart.getSpecId()); // 商品规格id
        List<ShopCart> cartList = cartDao.findByParams(paramap);
        //如果存在该商品购物车  就更新 否则就添加
        if (CollectionUtils.isNotEmpty(cartList)) {
            for (ShopCart carts : cartList) {
                Integer num = 0;
                // 判断是否为立即购买
                // 若为立即购买,直接将购物车数量更改为当前购买数量
                if (saveType == CartConstant.SAVE_TYPE_BUY_NOW) {
                    num = count;
                } else { //若为加入购物车,将原有商品数量加上新加入的商品数量
                    num = carts.getGoodsNum().intValue() + count;
                }
                limitBuyQuantity(num, carts.getActivityId(), carts.getActivitySpecId());
                cart.setGoodsNum(num);
                cart.setId(carts.getId());
                cartDao.update(cart);
            }
        } else {
            // 主播id
            cart.setGoodsNum(count);
            limitBuyQuantity(count, cart.getActivityId(), cart.getActivitySpecId());
            cartDao.insert(cart);
        }
        if (Optional.ofNullable(goodsSpec.getSpecIsopen()).orElse(0)==0){
            goodsSpec.setSpecGoodsStorage(0);
        }
        if (cart.getGoodsNum()> goodsSpec.getSpecGoodsStorage()) {
            throw new RuntimeException("购物车数量大于库存");
        }
        if (cart.getActivityId()!=null){
            List<ShopActivityGoodsSpec> shopActivityGoodsSpecList=shopActivityGoodsSpecService.findList(Paramap.create().put("activityId",cart.getActivityId()).put("specId",goodsSpec.getId()));
            if (shopActivityGoodsSpecList!=null && shopActivityGoodsSpecList.size()>0){
                ShopActivityGoodsSpec shopActivityGoodsSpec=shopActivityGoodsSpecList.get(0);
                if (cart.getGoodsNum()> shopActivityGoodsSpec.getActivityStock()) {
                    throw new RuntimeException("购物车数量大于库存");
                }
            }
        }
        return cart.getId();
    }

    @Override
    public void updateNum(long cartId, int count, long memberId) {
        ShopCart cart = find(cartId);
        if (cart == null) {
            throw new RuntimeException("不存在购物车");
        }

        if (!cart.getMemberId().equals(memberId)) {
            throw new RuntimeException("非法操作");
        }

        // 商品规格信息
        ShopGoodsSpec goodsSpec = goodsSpecService.find(cart.getSpecId());
        // 商品信息
        ShopGoods goods = goodsService.find(cart.getGoodsId());
        // 能否修改购物车
        canSaveOrUpdateCart(goods, goodsSpec, count);
        // 购买数量限制
        limitBuyQuantity(count, cart.getActivityId(), cart.getActivitySpecId());
        cart.setGoodsNum(count);

        //设置价格
        cart.setGoodsMemberPrice(goodsSpec.getSpecMemberPrice());
        cart.setGoodsBigPrice(goodsSpec.getSpecBigPrice());
        cart.setGoodsRetailPrice(goodsSpec.getSpecRetailPrice());
        cart.setBigPpv(goodsSpec.getBigPpv());
        cart.setPpv(goodsSpec.getPpv());

        cart.setId(cartId);
        cartDao.update(cart);
    }

    @Override
    public void updateNumBatch(Map<Long, Integer> cartIdNumMap, long memberId) {
        // 批量查询购物车
        List<ShopCartVo> carts = cartDao.listShopCartVoWithGoodsAndSpecByIds((Long[]) cartIdNumMap.keySet().toArray());
        if (carts.size() != cartIdNumMap.size()) {
            throw new RuntimeException("部分购物车不存在");
        }
        for (ShopCartVo cart : carts) {
            // 用户校验
            if (!cart.getMemberId().equals(memberId)) {
                throw new RuntimeException("非法操作");
            }
            // 商品是否存在、是否下架、规格是否变动、库存是否足够判断
            canSaveOrUpdateCart(cart.getGoods(), cart.getGoodsSpec(), cartIdNumMap.get(cart.getId()));
            // 限购数量校验
            limitBuyQuantity(cartIdNumMap.get(cart.getId()), cart.getActivityId(), cart.getActivitySpecId());
            // 更新数量
            ShopCart updateCart = new ShopCart();
            updateCart.setId(cart.getId());
            // 后台商品可能变更, 重新更新
            cart.setGoodsMemberPrice(cart.getGoodsSpec().getSpecMemberPrice());
            cart.setGoodsBigPrice(cart.getGoodsSpec().getSpecBigPrice());
            cart.setGoodsRetailPrice(cart.getGoodsSpec().getSpecRetailPrice());
            cart.setBigPpv(cart.getGoodsSpec().getBigPpv());
            cart.setPpv(cart.getGoodsSpec().getPpv());

            updateCart.setGoodsNum(cartIdNumMap.get(cart.getId()));
            cartDao.update(updateCart);
        }
    }

    @Override
    public Map<String, Object> queryTotalPrice(String cartIds, String memberId, String couponIds, Long groupBuyActivityId,
                                               ShopOrderDiscountType shopOrderDiscountType, RdMmAddInfo addr) {
        Map<String, Object> map = new HashMap<>(5);
        map.put("error", "false");
        //通过多个购物车id查询购物车数据
        List<ShopCart> cartList = Lists.newArrayList();
        if (StringUtils.isNotEmpty(cartIds) && !"null".equals(cartIds)) {
            String[] cartId = cartIds.split(",");
            if (cartId != null && cartId.length > 0) {
                cartList = cartDao.findByParams(Paramap.create().put("ids", cartId));
            }
        }
        List<Long> activityIds = new ArrayList<>();
        List<Long> goodsIds = Lists.newArrayList();
        List<Long> specIds = Lists.newArrayList();
        for (ShopCart shopCart : cartList) {
            activityIds.add(shopCart.getActivityId());
            goodsIds.add(shopCart.getGoodsId());
            specIds.add(shopCart.getSpecId());
        }
        // 待验证规格
        Map<Long, ShopGoods> goodsMap = goodsService.findGoodsMap(goodsIds);
        Map<Long, ShopGoodsSpec> specList = goodsSpecService.findMapSpec(specIds);
        for (ShopCart shopCart : cartList) {
            ShopGoods targetGoods = goodsMap.get(shopCart.getGoodsId());
            // 商品审核通过； 商品已上架; 商品未删除
            boolean isShow = targetGoods.getState() == GoodsState.GOODS_OPEN_STATE &&
                    targetGoods.getGoodsShow() != null &&
                    targetGoods.getGoodsShow() == GoodsState.GOODS_ON_SHOW &&
                    targetGoods.getIsDel() != null &&
                    targetGoods.getIsDel() == GoodsState.GOODS_NOT_DELETE;
            if (!isShow) {
                map.put("error", "true");
                map.put("message", targetGoods.getGoodsName() + "商品已下架");
                return map;
            }
            ShopGoodsSpec targetSpec = specList.get(shopCart.getSpecId());
            //确定购物车商品零售价
            if(shopCart.getActivityId()!=null && shopCart.getActivitySpecId()!=null){
                List<ShopActivityGoodsSpec> shopActivityGoodsSpecList = shopActivityGoodsSpecService.findList(Paramap.create().put("activityId",shopCart.getActivityId()).put("specId",shopCart.getActivitySpecId()));
                if (!shopActivityGoodsSpecList.isEmpty()){
                    shopCart.setGoodsRetailPrice(Optional.ofNullable(shopActivityGoodsSpecList.get(0).getActivityPrice()).orElse(targetSpec.getSpecRetailPrice()));
                }
            }else{
                shopCart.setGoodsRetailPrice(targetSpec.getSpecRetailPrice());
            }
            shopCart.setGoodsBigPrice(targetSpec.getSpecBigPrice());
            shopCart.setGoodsMemberPrice(targetSpec.getSpecMemberPrice());
            shopCart.setPpv(targetSpec.getPpv());
            shopCart.setBigPpv(targetSpec.getBigPpv());
            shopCart.setWeight(targetSpec.getWeight());
        }

        List<CartInfo> cartInfoList = getCartInfoList(cartList, shopOrderDiscountType, addr, memberId,activityIds);
        if (CollectionUtils.isEmpty(cartInfoList)) {
            throw new RuntimeException("购物车不存在");
        }

        // 新建一个商品总价
        double totalGoodsPrice = cartInfoList.stream().mapToDouble(item -> item.getGoodsTotalPrice().doubleValue())
                .sum();
        // 需要付金额
        double actotalGoodsPrice = cartInfoList.stream().mapToDouble(item -> item.getActualGoodsTotalPrice().doubleValue())
                .sum();
        // 需要运费
        double freightAmount = cartInfoList.stream().mapToDouble(item -> item.getFreightAmount().doubleValue())
                .sum();
        // 运费优惠
        double preferentialFreightAmount = cartInfoList.stream().mapToDouble(item -> item.getPreferentialFreightAmount().doubleValue())
                .sum();
        // 优惠金额
        double couponAmount = cartInfoList.stream().mapToDouble(item -> item.getCouponAmount().doubleValue())
                .sum();
        BigDecimal needPay = BigDecimal.valueOf(actotalGoodsPrice);

        // 商品总价
        map.put("totalGoodsPrice", BigDecimal.valueOf(totalGoodsPrice));
        /********************* 计算积分可抵扣金额 *********************/
//        calcPayOfRewardPoint(member, needPay, map);
        // 存储订单实际支付金额
        map.put("totalPrice", needPay);
        //存储订单实际运费
        map.put("freightAmount", BigDecimal.valueOf(freightAmount));
        //存储订单优惠运费
        map.put("preferentialFreightAmount", BigDecimal.valueOf(preferentialFreightAmount));
        //存储订单优惠金额
        map.put("couponAmount", BigDecimal.valueOf(couponAmount));
        return map;
    }

    @Override
    public Map<String, Object> queryTotalPrice1(String cartIds, String memberId, Long couponId, Long groupBuyActivityId, ShopOrderDiscountType shopOrderDiscountType, RdMmAddInfo addr) {
        Map<String, Object> map = new HashMap<>(5);
        map.put("error", "false");
        //通过多个购物车id查询购物车数据
        List<ShopCart> cartList = Lists.newArrayList();
        if (StringUtils.isNotEmpty(cartIds) && !"null".equals(cartIds)) {
            String[] cartId = cartIds.split(",");
            if (cartId != null && cartId.length > 0) {
                cartList = cartDao.findByParams(Paramap.create().put("ids", cartId));
            }
        }
        List<Long> activityIds = new ArrayList<>();
        List<Long> goodsIds = Lists.newArrayList();
        List<Long> specIds = Lists.newArrayList();
        for (ShopCart shopCart : cartList) {
            activityIds.add(shopCart.getActivityId());
            goodsIds.add(shopCart.getGoodsId());
            specIds.add(shopCart.getSpecId());
        }
        // 待验证规格
        Map<Long, ShopGoods> goodsMap = goodsService.findGoodsMap(goodsIds);
        Map<Long, ShopGoodsSpec> specList = goodsSpecService.findMapSpec(specIds);
        for (ShopCart shopCart : cartList) {
            ShopGoods targetGoods = goodsMap.get(shopCart.getGoodsId());
            // 商品审核通过； 商品已上架; 商品未删除
            boolean isShow = targetGoods.getState() == GoodsState.GOODS_OPEN_STATE &&
                    targetGoods.getGoodsShow() != null &&
                    targetGoods.getGoodsShow() == GoodsState.GOODS_ON_SHOW &&
                    targetGoods.getIsDel() != null &&
                    targetGoods.getIsDel() == GoodsState.GOODS_NOT_DELETE;
            if (!isShow) {
                map.put("error", "true");
                map.put("message", targetGoods.getGoodsName() + "商品已下架");
                return map;
            }
            ShopGoodsSpec targetSpec = specList.get(shopCart.getSpecId());
            //确定购物车商品零售价
            if(shopCart.getActivityId()!=null && shopCart.getActivitySpecId()!=null){
                List<ShopActivityGoodsSpec> shopActivityGoodsSpecList = shopActivityGoodsSpecService.findList(Paramap.create().put("activityId",shopCart.getActivityId()).put("specId",shopCart.getActivitySpecId()));
                if (!shopActivityGoodsSpecList.isEmpty()){
                    shopCart.setGoodsRetailPrice(Optional.ofNullable(shopActivityGoodsSpecList.get(0).getActivityPrice()).orElse(targetSpec.getSpecRetailPrice()));
                }
            }else{
                shopCart.setGoodsRetailPrice(targetSpec.getSpecRetailPrice());
            }
            shopCart.setGoodsBigPrice(targetSpec.getSpecBigPrice());
            shopCart.setGoodsMemberPrice(targetSpec.getSpecMemberPrice());
            shopCart.setPpv(targetSpec.getPpv());
            shopCart.setBigPpv(targetSpec.getBigPpv());
            shopCart.setWeight(targetSpec.getWeight());
        }

        List<CartInfo> cartInfoList = getCartInfoList1(cartList, shopOrderDiscountType, addr, memberId,activityIds,couponId);
        if (CollectionUtils.isEmpty(cartInfoList)) {
            throw new RuntimeException("购物车不存在");
        }

        // 新建一个商品总价
        double totalGoodsPrice = cartInfoList.stream().mapToDouble(item -> item.getGoodsTotalPrice().doubleValue())
                .sum();
        // 需要付金额
        double actotalGoodsPrice = cartInfoList.stream().mapToDouble(item -> item.getActualGoodsTotalPrice().doubleValue())
                .sum();
        // 需要运费
        double freightAmount = cartInfoList.stream().mapToDouble(item -> item.getFreightAmount().doubleValue())
                .sum();
        // 运费优惠
        double preferentialFreightAmount = cartInfoList.stream().mapToDouble(item -> item.getPreferentialFreightAmount().doubleValue())
                .sum();
        // 优惠金额
        double couponAmount = cartInfoList.stream().mapToDouble(item -> item.getCouponAmount().doubleValue())
                .sum();
        // 等级优惠金额
        double rankDiscount = cartInfoList.stream().mapToDouble(item -> item.getRankAmount().doubleValue())
                .sum();
        // 优惠券优惠金额
        double useCouponAmount = cartInfoList.stream().mapToDouble(item -> item.getUseCouponAmount().doubleValue())
                .sum();
        BigDecimal needPay = BigDecimal.valueOf(actotalGoodsPrice);

        // 商品总价
        map.put("totalGoodsPrice", BigDecimal.valueOf(totalGoodsPrice));
        /********************* 计算积分可抵扣金额 *********************/
//        calcPayOfRewardPoint(member, needPay, map);
        // 存储订单实际支付金额
        map.put("totalPrice", needPay);
        //存储订单实际运费
        map.put("freightAmount", BigDecimal.valueOf(freightAmount));
        //存储订单优惠运费
        map.put("preferentialFreightAmount", BigDecimal.valueOf(preferentialFreightAmount));
        //存储订单优惠金额
        map.put("couponAmount", BigDecimal.valueOf(couponAmount));
        //TODO add 2019-11-12
        //自营商店不进行分单
        CartInfo cartInfo = cartInfoList.get(0);
        //存储优惠券id
        map.put("couponId",Optional.ofNullable(cartInfo.getCouponId()).orElse(null) );
        //存储优惠券map
        map.put("couponList",Optional.ofNullable(cartInfo.getCouponList()).orElse(new ArrayList<Coupon>()));
        //存储不可用优惠券map
        map.put("noUseCouponList",Optional.ofNullable(cartInfo.getNoUseCouponList()).orElse(new ArrayList<Coupon>()));
        //存储等级优惠金额
        map.put("rankDiscount",BigDecimal.valueOf(rankDiscount));
        //存储优惠券优惠金额
        map.put("useCouponAmount",BigDecimal.valueOf(useCouponAmount));
        return map;
    }

    /**
     * 购买数量限制
     */
    private BigDecimal limitBuyQuantity(Integer buynum, Long activityId, Long activitySpecId) {
        //优惠购物车购买数量限制
        Integer minBuyNum = 1;
        BigDecimal price = null;
        // 商品限购数
        if (minBuyNum != null && buynum < minBuyNum) {
            throw new StateResult(AppConstants.LIMIT_BUY_QUANTITY, "购买数量最少" + minBuyNum);
        }
        //检查活动约束
        if (activityId != null && activityId!=-1 && activityId!=0) {
            ShopActivityGoodsSpec shopActivityGoodsSpec = null;
            List<ShopActivityGoodsSpec> shopActivityGoodsSpecList=shopActivityGoodsSpecService.findList(Paramap.create().put("activityId",activityId).put("specId",activitySpecId));
              if (shopActivityGoodsSpecList!=null && shopActivityGoodsSpecList.size()>0){
                  shopActivityGoodsSpec=shopActivityGoodsSpecList.get(0);
              }
            if (shopActivityGoodsSpec!=null){
                //活动限购数
                ShopActivityGoods nShopActivityGoods = shopActivityGoodsService.find(shopActivityGoodsSpec.getActivityGoodsId());
                if (nShopActivityGoods.getStockNumber() != null && nShopActivityGoods.getStockNumber() != 0 && nShopActivityGoods.getStockNumber() < buynum) {
                    throw new StateResult(AppConstants.LIMIT_BUY_QUANTITY, "活动单笔订单数量不能超过" + nShopActivityGoods.getStockNumber());
                }
                //检查活动库存
                if (shopActivityGoodsSpec != null) {
                    //返回商品活动价格
                    price = shopActivityGoodsSpec.getActivityPrice();
                    if (shopActivityGoodsSpec.getActivityStock() != null && shopActivityGoodsSpec.getActivityStock().intValue() != 0
                            && shopActivityGoodsSpec.getActivityStock().intValue() < buynum) {
                        throw new StateResult(AppConstants.LIMIT_BUY_QUANTITY, "活动库存仅剩" + shopActivityGoodsSpec.getActivityStock().intValue());
                    }
                }
            }

        }
        return price;
    }

    /**
     * 商品是否存在、是否下架、规格是否变动、库存是否足够判断
     */
    private void canSaveOrUpdateCart(ShopGoods goods, ShopGoodsSpec goodsSpec, int count) {
        if (goods == null) {
            throw new StateResult(AppConstants.GOODS_NOT_EXIST, "不存在商品");
        }

        if (goods.getState() != 1) {
            throw new StateResult(AppConstants.GOODS_IN_AUDIT, "商品审核未通过");
        }

        if (goods.getIsDel() == 1) {
            throw new StateResult(AppConstants.GOODS_IS_DEL, "商品不存在");
        }

        if (goods.getGoodsShow() != null && goods.getGoodsShow() == 2) {
            throw new StateResult(AppConstants.GOODS_NOT_SHOW, "商品已下架");
        }

        if (goodsSpec == null) {
            throw new StateResult(AppConstants.GOODS_SPEC_NOT_EXISTS, "商品规格变动, 请重新添加购物车");
        }

        if (goodsSpec.getSpecGoodsStorage() < count) {
            throw new StateResult(AppConstants.GOODS_NO_MORE, "库存不足");
        }
    }

    /**
     * 通过多个购物车id查询购物车, 更加商家分单
     *
     * @return 分单后的CartInfo集合, 一个CartInfo为一个订单
     */
    @Override
    public List<CartInfo> queryCartInfoList(String cartIds, ShopOrderDiscountType shopOrderDiscountType, RdMmAddInfo addr, String memberId) {
        //通过多个购物车id查询购物车数据
        List<ShopCart> cartList = Lists.newArrayList();
        if (StringUtils.isNotEmpty(cartIds) && !"null".equals(cartIds)) {
            String[] cartId = cartIds.split(",");
            if (cartId != null && cartId.length > 0) {
                cartList = cartDao.findByParams(Paramap.create().put("ids", cartId));
            }
        }
        List<Long> activityIds = new ArrayList<>();
        for (ShopCart shopCart : cartList) {
            activityIds.add(shopCart.getActivityId());//购物车集合中存在活动id的集合
        }
        if (cartList.isEmpty()){
            throw new StateResult(AppConstants.FAIL, "购物车不存在");
        }
        // 获取基本数据
        return getCartInfoList(cartList, shopOrderDiscountType, addr, memberId,activityIds);
    }

    @Override
    public List<CartInfo> queryCartInfoList1(String cartIds, ShopOrderDiscountType shopOrderDiscountType, RdMmAddInfo addr, String memberId, Long couponId) {
        //通过多个购物车id查询购物车数据
        List<ShopCart> cartList = Lists.newArrayList();
        if (StringUtils.isNotEmpty(cartIds) && !"null".equals(cartIds)) {
            String[] cartId = cartIds.split(",");
            if (cartId != null && cartId.length > 0) {
                cartList = cartDao.findByParams(Paramap.create().put("ids", cartId));
            }
        }
        List<Long> activityIds = new ArrayList<>();
        for (ShopCart shopCart : cartList) {
            activityIds.add(shopCart.getActivityId());//购物车集合中存在活动id的集合
        }
        if (cartList.isEmpty()){
            throw new StateResult(AppConstants.FAIL, "购物车不存在");
        }
        // 获取基本数据
        return getCartInfoList1(cartList, shopOrderDiscountType, addr, memberId,activityIds,couponId);
    }

    /**
     * 获取cartInfoList
     */
    public List<CartInfo> getCartInfoList(List<ShopCart> cartList, ShopOrderDiscountType shopOrderDiscountType, RdMmAddInfo addr, String memberId,List<Long> activityIds) {
        Integer type = shopOrderDiscountType.getPreferentialType();//获取订单优惠类型
        // 拆单,将购物车数据存入map,键为品牌id,值为店铺名称,得到所有品牌的信息(唯一,去重)
        //本项目不拆单了
//        Map<Long, String> brandMap = new HashMap<>();
//
//            brandMap.put(0L, "0");

//        // 去重后店铺id
//        Set<Long> brandIds = brandMap.keySet();
        // 一个店铺对应一个cartInfo 分单
        List<CartInfo> cartInfoList = Lists.newArrayList();
//        for ( CartInfo item : cartInfoList) {
            CartInfo cartInfo = new CartInfo();
//            cartInfo.setBrandId(item.getBrandId());
//            cartInfo.setBrandName(item.getBrandName());
            //创建一个新的Cart集合
            List<CartVo> newCartList = Lists.newArrayList();
            //新建一个字符串,用来存储多个id字段
            String ids = "";
            //商品种类数量
            int goodsNum = 0;
            //购物车商品总价
            double goodsTotalPrice = 0;
            //购物车实际支付商品总价
            double actualGoodsTotalPrice = 0;
            //商品种类数量
        BigDecimal pv = BigDecimal.ZERO;
            //订单商品总重量
            Double totalWeight = 0d;
            //遍历所有购物车数据
            for (ShopCart cart : cartList) {
                //如果购物车的品牌id与当前品牌id一致,将购物车信息存入购物车集合
//                if (cart.get().equals(brandId)) {
                    CartVo cartVo = new CartVo();
                    BeanUtils.copyProperties(cart, cartVo);
                cartInfo.setBrandId(cart.getBrandId());
                cartInfo.setBrandName(cart.getBrandName());
                    ids += cart.getId() + ",";
                    goodsNum += cart.getGoodsNum();
                    totalWeight += Optional.ofNullable(cart.getWeight()).orElse(0d) * cart.getGoodsNum();
                    goodsTotalPrice += (NumberUtils.getBigDecimal(String.valueOf(cart.getGoodsNum()))
                            .multiply(cart.getGoodsRetailPrice())).doubleValue();
                    cartVo.setActualPrice(cart.getGoodsRetailPrice());
                    //2.会员价 3.PV大单价
                    if (type == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_MEMBER) {
                        actualGoodsTotalPrice += (NumberUtils.getBigDecimal(String.valueOf(cart.getGoodsNum()))
                                .multiply(cart.getGoodsMemberPrice())).doubleValue();
                        cartVo.setActualPrice(cart.getGoodsMemberPrice());
                    }
                    if (type == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PREFERENTIAL) {
                        BigDecimal money = cart.getGoodsRetailPrice().subtract(shopOrderDiscountType.getPreferential());
                        if (money.compareTo(new BigDecimal("0")) == -1) {
                            money = new BigDecimal("0");
                        }
                        actualGoodsTotalPrice += (NumberUtils.getBigDecimal(String.valueOf(cart.getGoodsNum()))
                                .multiply(money)).doubleValue();
                        cartVo.setActualPrice(money);
                    }
                    if (type == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PPV) {
                        actualGoodsTotalPrice += (NumberUtils.getBigDecimal(String.valueOf(cart.getGoodsNum()))
                                .multiply(cart.getGoodsBigPrice())).doubleValue();
                        cartVo.setActualPrice(cart.getGoodsBigPrice());
                    }
                    if (type == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PPV) {
                        pv=pv.add(cart.getBigPpv().multiply(BigDecimal.valueOf(cart.getGoodsNum())));
//                        pv += cart.getBigPpv().multiply(BigDecimal.valueOf(cart.getGoodsNum())) ;
                    } else if (type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_RETAIL){
                        pv=pv.add(cart.getPpv().multiply(BigDecimal.valueOf(cart.getGoodsNum())));
//                        pv += Optional.ofNullable(cart.getPpv()).orElse(0) * cart.getGoodsNum();
                    }else {
                        pv = BigDecimal.ZERO;
                    }
//                }
                    cartVo.setItemVoucherPrice(BigDecimal.valueOf(actualGoodsTotalPrice == 0 ? goodsTotalPrice : actualGoodsTotalPrice));
                    cartVo.setItemTotalPrice(cartVo.getActualPrice().multiply(new BigDecimal(cartVo.getGoodsNum())));
                    newCartList.add(cartVo);
//                }
            }

            // 该商店的购物车
            cartInfo.setList(newCartList);
            // 该商店的商品数量
            cartInfo.setGoodsNum(goodsNum);
            // 该商店的商品pv值
            cartInfo.setPpvNum(pv);
            // 该商店商品总价格
            cartInfo.setGoodsTotalPrice(BigDecimal.valueOf(goodsTotalPrice));
            if (type == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_RETAIL) {
                cartInfo.setActualGoodsTotalPrice(BigDecimal.valueOf(goodsTotalPrice));
            } else {
                cartInfo.setActualGoodsTotalPrice(BigDecimal.valueOf(actualGoodsTotalPrice));
            }
            //优惠金额
            cartInfo.setCouponAmount(cartInfo.getGoodsTotalPrice().subtract(cartInfo.getActualGoodsTotalPrice()));
        //运费计算
        if (addr != null) {
            //运费
            BigDecimal freightAmount = shopGoodsFreightService.CalculateFreight(addr.getAddProvinceCode(), totalWeight);
            //运费优惠
            BigDecimal preferentialFreightAmount = shopGoodsFreightRuleService.CalculateFreightDiscount(memberId, cartInfo.getActualGoodsTotalPrice());
            if (freightAmount.compareTo(new BigDecimal("0")) == 0) {
                cartInfo.setFreightAmount(new BigDecimal("0"));
                cartInfo.setPreferentialFreightAmount(new BigDecimal("0"));
            }else{
                cartInfo.setFreightAmount(freightAmount);
                cartInfo.setPreferentialFreightAmount(preferentialFreightAmount);
            }
            //计算加上运费的订单总价
            cartInfo.setActualGoodsTotalPrice(cartInfo.getActualGoodsTotalPrice().add(cartInfo.getFreightAmount().subtract(cartInfo.getPreferentialFreightAmount())));
        } else {
            cartInfo.setFreightAmount(new BigDecimal("0"));
            cartInfo.setPreferentialFreightAmount(new BigDecimal("0"));
        }
            // 该商店购物车id集合
//            cartInfo.setCartIds(ids);
            cartInfoList.add(cartInfo);
//        }
        //活动满减,满折处理
        Map<Long, ShopActivityPromotionRule> shopActivityPromotionRuleMap = shopActivityPromotionRuleService.findByAtiIdsMap(activityIds);
        promotionService.promotion(cartInfoList, shopActivityPromotionRuleMap);
        return cartInfoList;
    }

    /**
     * 获取cartInfoList
     */
    public List<CartInfo> getCartInfoList1(List<ShopCart> cartList, ShopOrderDiscountType shopOrderDiscountType, RdMmAddInfo addr, String memberId,List<Long> activityIds,Long couponId) {
        Integer type = shopOrderDiscountType.getPreferentialType();//获取订单优惠类型
        // 拆单,将购物车数据存入map,键为品牌id,值为店铺名称,得到所有品牌的信息(唯一,去重)
        //本项目不拆单了
//        Map<Long, String> brandMap = new HashMap<>();
//
//            brandMap.put(0L, "0");

//        // 去重后店铺id
//        Set<Long> brandIds = brandMap.keySet();
        // 一个店铺对应一个cartInfo 分单
        List<CartInfo> cartInfoList = Lists.newArrayList();
//        for ( CartInfo item : cartInfoList) {
        CartInfo cartInfo = new CartInfo();
//            cartInfo.setBrandId(item.getBrandId());
//            cartInfo.setBrandName(item.getBrandName());
        //创建一个新的Cart集合
        List<CartVo> newCartList = Lists.newArrayList();
        //新建一个字符串,用来存储多个id字段
        String ids = "";
        //商品种类数量
        int goodsNum = 0;
        //购物车商品总价
        double goodsTotalPrice = 0;
        //购物车实际支付商品总价
        double actualGoodsTotalPrice = 0;
        //订单等级优惠金额
        BigDecimal rankDiscount=BigDecimal.ZERO;
        //商品种类数量
        BigDecimal pv = BigDecimal.ZERO;
        //订单商品总重量
        Double totalWeight = 0d;
        //遍历所有购物车数据
        for (ShopCart cart : cartList) {
            //如果购物车的品牌id与当前品牌id一致,将购物车信息存入购物车集合
//                if (cart.get().equals(brandId)) {
            CartVo cartVo = new CartVo();
            BeanUtils.copyProperties(cart, cartVo);
            cartInfo.setBrandId(cart.getBrandId());
            cartInfo.setBrandName(cart.getBrandName());
            ids += cart.getId() + ",";
            goodsNum += cart.getGoodsNum();
            totalWeight += Optional.ofNullable(cart.getWeight()).orElse(0d) * cart.getGoodsNum();
            goodsTotalPrice += (NumberUtils.getBigDecimal(String.valueOf(cart.getGoodsNum()))
                    .multiply(cart.getGoodsRetailPrice())).doubleValue();
            cartVo.setActualPrice(cart.getGoodsRetailPrice());
            //2.会员价 3.PV大单价
            if (type == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_MEMBER) {
                actualGoodsTotalPrice += (NumberUtils.getBigDecimal(String.valueOf(cart.getGoodsNum()))
                        .multiply(cart.getGoodsMemberPrice())).doubleValue();
                cartVo.setActualPrice(cart.getGoodsMemberPrice());
                rankDiscount=rankDiscount.add((cart.getGoodsRetailPrice().subtract(cart.getGoodsMemberPrice())).multiply(new BigDecimal(cart.getGoodsNum())));
            }
            if (type == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PREFERENTIAL) {
                BigDecimal money = cart.getGoodsRetailPrice().subtract(shopOrderDiscountType.getPreferential());
                if (money.compareTo(new BigDecimal("0")) == -1) {
                    money = new BigDecimal("0");
                }
                actualGoodsTotalPrice += (NumberUtils.getBigDecimal(String.valueOf(cart.getGoodsNum()))
                        .multiply(money)).doubleValue();
                cartVo.setActualPrice(money);
            }
            if (type == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PPV) {
                actualGoodsTotalPrice += (NumberUtils.getBigDecimal(String.valueOf(cart.getGoodsNum()))
                        .multiply(cart.getGoodsBigPrice())).doubleValue();
                cartVo.setActualPrice(cart.getGoodsBigPrice());
                rankDiscount=rankDiscount.add((cart.getGoodsRetailPrice().subtract(cart.getGoodsBigPrice())).multiply(new BigDecimal(cart.getGoodsNum())));
            }
            if (type == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PPV) {
                pv=pv.add(cart.getBigPpv().multiply(BigDecimal.valueOf(cart.getGoodsNum())));
//                        pv += cart.getBigPpv().multiply(BigDecimal.valueOf(cart.getGoodsNum())) ;
            } else if (type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_RETAIL){
                pv=pv.add(cart.getPpv().multiply(BigDecimal.valueOf(cart.getGoodsNum())));
//                        pv += Optional.ofNullable(cart.getPpv()).orElse(0) * cart.getGoodsNum();
            }else {
                pv = BigDecimal.ZERO;
            }
//                }
            cartVo.setItemVoucherPrice(BigDecimal.valueOf(actualGoodsTotalPrice == 0 ? goodsTotalPrice : actualGoodsTotalPrice));
            cartVo.setItemTotalPrice(cartVo.getActualPrice().multiply(new BigDecimal(cartVo.getGoodsNum())));
            newCartList.add(cartVo);
//                }
        }

        // 该商店的购物车
        cartInfo.setList(newCartList);
        // 该商店的商品数量
        cartInfo.setGoodsNum(goodsNum);
        // 该商店的商品pv值
        cartInfo.setPpvNum(pv);
        // 该商店商品总价格
        cartInfo.setGoodsTotalPrice(BigDecimal.valueOf(goodsTotalPrice));
        if (type == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_RETAIL) {
            cartInfo.setActualGoodsTotalPrice(BigDecimal.valueOf(goodsTotalPrice));
        } else {
            cartInfo.setActualGoodsTotalPrice(BigDecimal.valueOf(actualGoodsTotalPrice));
        }
        //会员等级优惠金额
        cartInfo.setRankAmount(rankDiscount);
        //优惠金额
        cartInfo.setCouponAmount(cartInfo.getGoodsTotalPrice().subtract(cartInfo.getActualGoodsTotalPrice()));
        //TODO create by zc 2019-11-08 计算优惠券折扣
        BigDecimal useCouponAmount=BigDecimal.ZERO;
        if (type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PPV) {
            if(couponId!=null){//由于第一次进入couponId为null，给前端展示的优惠券均可以使用，此处不再次验证
                Coupon coupon = couponService.find(couponId);
                if(coupon==null||coupon.getStatus()!=2){
                    throw new RuntimeException("当前选取优惠券异常");
                }
                Map<String, Object> map = getCouponList(memberId, cartList, cartInfo);
                ArrayList<Coupon> coupons = (ArrayList<Coupon>) map.get("coupons");
                if(coupons!=null&&coupons.size()>0){
                    cartInfo.setCouponList(coupons);
                }
                ArrayList<Coupon> noUseCoupons = (ArrayList<Coupon>) map.get("noUseCoupons");
                if(noUseCoupons!=null&&noUseCoupons.size()>0){
                    cartInfo.setNoUseCouponList(noUseCoupons);
                }
                BigDecimal couponMoney=BigDecimal.ZERO;
                if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_FULL_REDUCTION){//满减
                    couponMoney=coupon.getCouponValue();
                }
                if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_KNOCK){//立减
                    if(cartInfo.getActualGoodsTotalPrice().compareTo(coupon.getCouponValue())==-1){
                        couponMoney=cartInfo.getActualGoodsTotalPrice();
                    }else {
                        couponMoney=coupon.getCouponValue();
                    }
                }
                if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_FULL_RATE||coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_NOMONEY_RATE){//满金额折扣或者无金额折扣
                    BigDecimal subtract = new BigDecimal("1.00").subtract(coupon.getCouponValue());
                    couponMoney=cartInfo.getActualGoodsTotalPrice().multiply(subtract).setScale(2,BigDecimal.ROUND_HALF_UP);
                }
                cartInfo.setActualGoodsTotalPrice(cartInfo.getActualGoodsTotalPrice().subtract(couponMoney));
                cartInfo.setCouponAmount(cartInfo.getCouponAmount().add(couponMoney));
                cartInfo.setCouponPrice(couponMoney);
                cartInfo.setCouponId(couponId);
                useCouponAmount=couponMoney;
            }else {
                Map<String, Object> map = getCouponList(memberId, cartList, cartInfo);
                ArrayList<Coupon> coupons = (ArrayList<Coupon>) map.get("coupons");
                BigDecimal couponMoney = (BigDecimal) map.get("couponMoney");
                couponId = (Long) map.get("bestCouponId");
                if(coupons!=null&&coupons.size()>0){
                    cartInfo.setActualGoodsTotalPrice(cartInfo.getActualGoodsTotalPrice().subtract(couponMoney));
                    cartInfo.setCouponAmount(cartInfo.getCouponAmount().add(couponMoney));
                    cartInfo.setCouponPrice(couponMoney);
                    cartInfo.setCouponId(couponId);
                    cartInfo.setCouponList(coupons);
                    useCouponAmount=couponMoney;
                }
                ArrayList<Coupon> noUseCoupons = (ArrayList<Coupon>) map.get("noUseCoupons");
                if(noUseCoupons!=null&&noUseCoupons.size()>0){
                    cartInfo.setNoUseCouponList(noUseCoupons);
                }
            }
        }
        cartInfo.setUseCouponAmount(useCouponAmount);
        //运费计算
        if (addr != null) {
            //运费
            BigDecimal freightAmount = shopGoodsFreightService.CalculateFreight(addr.getAddProvinceCode(), totalWeight);
            //运费优惠
            BigDecimal preferentialFreightAmount = shopGoodsFreightRuleService.CalculateFreightDiscount(memberId, cartInfo.getActualGoodsTotalPrice());
            if (freightAmount.compareTo(new BigDecimal("0")) == 0) {
                cartInfo.setFreightAmount(new BigDecimal("0"));
                cartInfo.setPreferentialFreightAmount(new BigDecimal("0"));
            }else{
                cartInfo.setFreightAmount(freightAmount);
                cartInfo.setPreferentialFreightAmount(preferentialFreightAmount);
            }
            //计算加上运费的订单总价
            cartInfo.setActualGoodsTotalPrice(cartInfo.getActualGoodsTotalPrice().add(cartInfo.getFreightAmount().subtract(cartInfo.getPreferentialFreightAmount())));
        } else {
            cartInfo.setFreightAmount(new BigDecimal("0"));
            cartInfo.setPreferentialFreightAmount(new BigDecimal("0"));
        }
        // 该商店购物车id集合
//            cartInfo.setCartIds(ids);
        cartInfoList.add(cartInfo);
//        }
        //活动满减,满折处理
        Map<Long, ShopActivityPromotionRule> shopActivityPromotionRuleMap = shopActivityPromotionRuleService.findByAtiIdsMap(activityIds);
        promotionService.promotion(cartInfoList, shopActivityPromotionRuleMap);
        return cartInfoList;
    }

    /**
     * 查询cartList可用优惠券集合
     * @param memberId
     * @param cartList
     * @param cartInfo
     * @return
     */
    public Map<String,Object> getCouponList(String memberId,List<ShopCart> cartList,CartInfo cartInfo)  {
        HashMap<String, Object> map = new HashMap<>();
        List<CouponUser> list = couponUserService.findList("mCode",memberId);
        ArrayList<Coupon> noUseList=new ArrayList<Coupon>();
        if(list!=null&&list.size()>0){
            //设计一个变量统计优惠券使用优惠的最大额度
            BigDecimal best=BigDecimal.ZERO;
            Long couponId=0L;
            ArrayList<Coupon> coupons = new ArrayList<>();
            for (CouponUser couponUser : list) {
                if(couponUser.getOwnNum()<=0){
                    //如果当前优惠券拥有量为0或者当前优惠券已经使用张数大于可使用张数，从couponUser集合中删除
                    //list.remove(couponUser);
                    continue;
                }
                if(couponUser.getUseAbleNum()!=0&&couponUser.getUseAbleNum()<=couponUser.getUseNum()){
                    List<Coupon> couponList = couponService.findList(Paramap.create().put("id", couponUser.getCouponId()).put("status", 2));
                    if(couponList!=null&&couponList.size()>0){
                        Coupon coupon = couponList.get(0);
                        coupon.setNoUseFalg(7);
                        noUseList.add(coupon);//使用数量达到上限
                    }
                    continue;
                }
                List<Coupon> couponList = couponService.findList(Paramap.create().put("id", couponUser.getCouponId()).put("status", 2));
                if(couponList!=null&&couponList.size()>0){
                    coupons.add(couponList.get(0));
                }
            }
            ArrayList<Coupon> arrayList=new ArrayList<Coupon>();
            //获得了当前用户拥有的合法可以使用的优惠券种类的集合
            //遍历集合，判断合法优惠券集合中可以用于当前订单的部分
            if(coupons!=null&&coupons.size()>0){
                for (Coupon coupon : coupons) {
                    //解析优惠券的使用说明，判断优惠券是否适用于当前订单场景
                    //0.判断优惠券使用时间是否满足要求
                    Coupon coupon1=couponService.judgeNoStart(Paramap.create().put("id",coupon.getId()).put("searchUseTime",new Date()));
                    if(coupon1!=null){//如果优惠券开始时间大于查询时间，说明优惠券使用未开始
                        coupon.setNoUseFalg(0);
                        noUseList.add(coupon);
                        continue;
                    }
                    Coupon coupon2=couponService.judgeUseEnd(Paramap.create().put("id",coupon.getId()).put("searchUseTime",new Date()));
                    if(coupon2!=null){//如果优惠券结束时间小于查询时间，说明优惠券已经结束使用
                        coupon.setNoUseFalg(1);
                        noUseList.add(coupon);
                        continue;
                    }
                    //1.判断店家id TODO 当前自营商店不进行判断，默认优惠券匹配店家id
                    //2.判断品牌id TODO 当前自营商店不进行判断，默认优惠券匹配品牌id
                    //3.判断使用范围 useScope
                    //3.1如果优惠券没有使用范围限制
                    if(coupon.getUseScope()== CouponConstant.USE_SCOPE_NO_LIMIT){
                        //3.1.1判断是否有mi值要求
                        if((coupon.getMinMi()!=null&&coupon.getMinMi().compareTo(BigDecimal.ZERO)==1)
                                &&(coupon.getMinAmount()!=null&&coupon.getMinAmount().compareTo(BigDecimal.ZERO)==1)){//mi和金额限制都存在
                            if(cartInfo.getPpvNum().compareTo(BigDecimal.ZERO)==1){//有mi值，用mi值判断是否可以使用
                                if(cartInfo.getPpvNum().compareTo(coupon.getMinMi())!=-1){//可以使用
                                    arrayList.add(coupon);
                                    if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_FULL_REDUCTION){
                                        if(coupon.getCouponValue().compareTo(best)==1){
                                            best=coupon.getCouponValue();
                                            couponId=coupon.getId();
                                        }
                                    }
                                    if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_FULL_RATE){
                                        if(coupon.getCouponValue().compareTo(new BigDecimal("1.00"))==1){
                                            throw new RuntimeException("优惠券信息异常");
                                        }
                                        BigDecimal subtract = new BigDecimal("1.00").subtract(coupon.getCouponValue());
                                        if(cartInfo.getActualGoodsTotalPrice().multiply(subtract).compareTo(best)==1){
                                            best=cartInfo.getActualGoodsTotalPrice().multiply(subtract).setScale(2,BigDecimal.ROUND_HALF_UP);
                                            couponId=coupon.getId();
                                        }
                                    }
                                }else {//不可以使用
                                    coupon.setNoUseFalg(2);
                                    noUseList.add(coupon);
                                    continue;
                                }
                            }else {//没有mi值按金额计算
                                if(cartInfo.getActualGoodsTotalPrice().compareTo(coupon.getMinAmount())!=-1){//可以使用
                                    arrayList.add(coupon);
                                    if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_FULL_REDUCTION){
                                        if(coupon.getCouponValue().compareTo(best)==1){
                                            best=coupon.getCouponValue();
                                            couponId=coupon.getId();
                                        }
                                    }
                                    if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_FULL_RATE){
                                        if(coupon.getCouponValue().compareTo(new BigDecimal("1.00"))==1){
                                            throw new RuntimeException("优惠券信息异常");
                                        }
                                        BigDecimal subtract = new BigDecimal("1.00").subtract(coupon.getCouponValue());
                                        if(cartInfo.getActualGoodsTotalPrice().multiply(subtract).compareTo(best)==1){
                                            best=cartInfo.getActualGoodsTotalPrice().multiply(subtract).setScale(2,BigDecimal.ROUND_HALF_UP);
                                            couponId=coupon.getId();
                                        }
                                    }
                                }else {//不可使用
                                    coupon.setNoUseFalg(3);
                                    noUseList.add(coupon);
                                    continue;
                                }
                            }
                        }else if (coupon.getMinMi()!=null&&coupon.getMinMi().compareTo(BigDecimal.ZERO)==1){//mi限制存在
                            if(cartInfo.getPpvNum().compareTo(BigDecimal.ZERO)==1&&cartInfo.getPpvNum().compareTo(coupon.getMinMi())!=-1){//订单内有mi值且大于优惠券使用最小mi值限制
                                arrayList.add(coupon);
                                if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_FULL_REDUCTION){
                                    if(coupon.getCouponValue().compareTo(best)==1){
                                        best=coupon.getCouponValue();
                                        couponId=coupon.getId();
                                    }
                                }
                                if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_FULL_RATE){
                                    if(coupon.getCouponValue().compareTo(new BigDecimal("1.00"))==1){
                                        throw new RuntimeException("优惠券信息异常");
                                    }
                                    BigDecimal subtract = new BigDecimal("1.00").subtract(coupon.getCouponValue());
                                    if(cartInfo.getActualGoodsTotalPrice().multiply(subtract).compareTo(best)==1){
                                        best=cartInfo.getActualGoodsTotalPrice().multiply(subtract).setScale(2,BigDecimal.ROUND_HALF_UP);
                                        couponId=coupon.getId();
                                    }
                                }
                            }else {//不可以使用
                                coupon.setNoUseFalg(2);
                                noUseList.add(coupon);
                                continue;
                            }
                        }else if(coupon.getMinAmount()!=null&&coupon.getMinAmount().compareTo(BigDecimal.ZERO)==1){//金额限制存在
                            if(cartInfo.getActualGoodsTotalPrice().compareTo(coupon.getMinAmount())!=-1){//可以使用
                                arrayList.add(coupon);
                                if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_FULL_REDUCTION){
                                    if(coupon.getCouponValue().compareTo(best)==1){
                                        best=coupon.getCouponValue();
                                        couponId=coupon.getId();
                                    }
                                }
                                if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_FULL_RATE){
                                    if(coupon.getCouponValue().compareTo(new BigDecimal("1.00"))==1){
                                        throw new RuntimeException("优惠券信息异常");
                                    }
                                    BigDecimal subtract = new BigDecimal("1.00").subtract(coupon.getCouponValue());
                                    if(cartInfo.getActualGoodsTotalPrice().multiply(subtract).compareTo(best)==1){
                                        best=cartInfo.getActualGoodsTotalPrice().multiply(subtract).setScale(2,BigDecimal.ROUND_HALF_UP);
                                        couponId=coupon.getId();
                                    }
                                }
                            }else {
                                coupon.setNoUseFalg(3);
                                noUseList.add(coupon);
                                continue;
                            }
                        }else {//没有限制
                            arrayList.add(coupon);
                            if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_KNOCK){//立减
                                if(cartInfo.getActualGoodsTotalPrice().compareTo(coupon.getCouponValue())==-1){
                                    if(cartInfo.getActualGoodsTotalPrice().compareTo(best)==1){
                                        best=cartInfo.getActualGoodsTotalPrice();
                                        couponId=coupon.getId();
                                    }
                                }else {
                                    if(coupon.getCouponValue().compareTo(best)==1){
                                        best=coupon.getCouponValue();
                                        couponId=coupon.getId();
                                    }
                                }
                            }else if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_NOMONEY_RATE){//立折
                                BigDecimal subtract = new BigDecimal("1.00").subtract(coupon.getCouponValue());
                                if(cartInfo.getActualGoodsTotalPrice().multiply(subtract).compareTo(best)==1){
                                    best=cartInfo.getActualGoodsTotalPrice().multiply(subtract).setScale(2,BigDecimal.ROUND_HALF_UP);
                                    couponId=coupon.getId();
                                }
                            }
                        }
                    }
                    else if(coupon.getUseScope()== CouponConstant.USE_SCOPE_SINGLE_LIMIT){//适用于单品
                        Long goodsId = coupon.getGoodsId();
                        List<CartVo> cartVoList = cartInfo.getList();
                        BigDecimal totalMoney=BigDecimal.ZERO;
                        BigDecimal totalPv=BigDecimal.ZERO;
                        Boolean flag=false;
                        if(cartVoList!=null&&cartVoList.size()>0){
                            for (CartVo cartVo : cartVoList) {
                                if(cartVo.getGoodsId().equals(goodsId)){
                                    flag=true;
                                    totalMoney=totalMoney.add(cartVo.getItemTotalPrice());
                                    totalPv=totalPv.add(cartVo.getPpv().multiply(new BigDecimal(cartVo.getGoodsNum())));
                                }
                            }
                        }
                        if(flag){//订单中有指定单品存在，进一步判断优惠券是否可以使用
                            //判断是否符合优惠券使用条件，mi值金额等
                            if((coupon.getMinMi()!=null&&coupon.getMinMi().compareTo(BigDecimal.ZERO)==1)
                                    &&(coupon.getMinAmount()!=null&&coupon.getMinAmount().compareTo(BigDecimal.ZERO)==1)) {//mi和金额限制都存在
                                if(totalPv.compareTo(BigDecimal.ZERO)==1){
                                    if(totalPv.compareTo(coupon.getMinMi())!=-1){//可以使用优惠券
                                        arrayList.add(coupon);
                                        if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_FULL_REDUCTION){
                                            if(coupon.getCouponValue().compareTo(best)==1){
                                                best=coupon.getCouponValue();
                                                couponId=coupon.getId();
                                            }
                                        }
                                        if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_FULL_RATE){
                                            if(coupon.getCouponValue().compareTo(new BigDecimal("1.00"))==1){
                                                throw new RuntimeException("优惠券信息异常");
                                            }
                                            BigDecimal subtract = new BigDecimal("1.00").subtract(coupon.getCouponValue());
                                            if(totalMoney.multiply(subtract).compareTo(best)==1){
                                                best=totalMoney.multiply(subtract).setScale(2,BigDecimal.ROUND_HALF_UP);
                                                couponId=coupon.getId();
                                            }
                                        }
                                    }
                                    else {//不可以使用优惠券
                                        coupon.setNoUseFalg(2);
                                        noUseList.add(coupon);
                                        continue;
                                    }
                                }else {
                                    if(totalMoney.compareTo(coupon.getMinAmount())!=-1){
                                        arrayList.add(coupon);
                                        if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_FULL_REDUCTION){
                                            if(coupon.getCouponValue().compareTo(best)==1){
                                                best=coupon.getCouponValue();
                                                couponId=coupon.getId();
                                            }
                                        }
                                        if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_FULL_RATE){
                                            if(coupon.getCouponValue().compareTo(new BigDecimal("1.00"))==1){
                                                throw new RuntimeException("优惠券信息异常");
                                            }
                                            BigDecimal subtract = new BigDecimal("1.00").subtract(coupon.getCouponValue());
                                            if(totalMoney.multiply(subtract).compareTo(best)==1){
                                                best=totalMoney.multiply(subtract).setScale(2,BigDecimal.ROUND_HALF_UP);
                                                couponId=coupon.getId();
                                            }
                                        }
                                    }
                                    else {
                                        coupon.setNoUseFalg(3);
                                        noUseList.add(coupon);
                                        continue;
                                    }
                                }
                            }else if (coupon.getMinMi()!=null&&coupon.getMinMi().compareTo(BigDecimal.ZERO)==1) {//mi限制存在
                                if(totalPv.compareTo(BigDecimal.ZERO)==1&&totalPv.compareTo(coupon.getMinMi())!=-1){//有mi值且满足要求
                                    arrayList.add(coupon);
                                    if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_FULL_REDUCTION){
                                        if(coupon.getCouponValue().compareTo(best)==1){
                                            best=coupon.getCouponValue();
                                            couponId=coupon.getId();
                                        }
                                    }
                                    if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_FULL_RATE){
                                        if(coupon.getCouponValue().compareTo(new BigDecimal("1.00"))==1){
                                            throw new RuntimeException("优惠券信息异常");
                                        }
                                        BigDecimal subtract = new BigDecimal("1.00").subtract(coupon.getCouponValue());
                                        if(totalMoney.multiply(subtract).compareTo(best)==1){
                                            best=totalMoney.multiply(subtract).setScale(2,BigDecimal.ROUND_HALF_UP);
                                            couponId=coupon.getId();
                                        }
                                    }
                                }
                                else {
                                    coupon.setNoUseFalg(2);
                                    noUseList.add(coupon);
                                    continue;
                                }
                            }else if(coupon.getMinAmount()!=null&&coupon.getMinAmount().compareTo(BigDecimal.ZERO)==1){//金额限制
                                if(totalMoney.compareTo(BigDecimal.ZERO)==1&&totalMoney.compareTo(coupon.getMinAmount())!=-1){//有金额且满足要求
                                    arrayList.add(coupon);
                                    if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_FULL_REDUCTION){
                                        if(coupon.getCouponValue().compareTo(best)==1){
                                            best=coupon.getCouponValue();
                                            couponId=coupon.getId();
                                        }
                                    }
                                    if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_FULL_RATE){
                                        if(coupon.getCouponValue().compareTo(new BigDecimal("1.00"))==1){
                                            throw new RuntimeException("优惠券信息异常");
                                        }
                                        BigDecimal subtract = new BigDecimal("1.00").subtract(coupon.getCouponValue());
                                        if(totalMoney.multiply(subtract).compareTo(best)==1){
                                            best=totalMoney.multiply(subtract).setScale(2,BigDecimal.ROUND_HALF_UP);
                                            couponId=coupon.getId();
                                        }
                                    }
                                }
                                else {
                                    coupon.setNoUseFalg(3);
                                    noUseList.add(coupon);
                                    continue;
                                }
                            }else {//不限制
                                arrayList.add(coupon);
                                if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_KNOCK){//立减
                                    if(totalMoney.compareTo(coupon.getCouponValue())==-1){
                                        if(totalMoney.compareTo(best)==1){
                                            best=totalMoney;
                                            couponId=coupon.getId();
                                        }
                                    }else {
                                        if(coupon.getCouponValue().compareTo(best)==1){
                                            best=coupon.getCouponValue();
                                            couponId=coupon.getId();
                                        }
                                    }
                                }else if(coupon.getReduceType()==CouponConstant.DISCOUNT_TYPE_NOMONEY_RATE){//立折
                                    BigDecimal subtract = new BigDecimal("1.00").subtract(coupon.getCouponValue());
                                    if(totalMoney.multiply(subtract).compareTo(best)==1){
                                        best=totalMoney.multiply(subtract).setScale(2,BigDecimal.ROUND_HALF_UP);
                                        couponId=coupon.getId();
                                    }
                                }
                            }
                        }
                        else {
                            coupon.setNoUseFalg(8);
                            noUseList.add(coupon);
                            continue;
                        }
                    }
                }
            }
            map.put("noUseCoupons",noUseList);
            map.put("coupons",arrayList);
            map.put("couponMoney",best);
            map.put("bestCouponId",couponId);
            return map;
        }else {
            map.put("noUseCoupons",null);
            map.put("coupons",null);
            map.put("couponMoney",null);
            map.put("bestCouponId",null);
            return map;
        }
    }
    /*********************
     * 扩展查询
     *********************/

    @Override
    public List<ShopCartVo> listWithGoodsAndSpec(Pageable pageable) {
        return cartDao.listShopCartVoWithGoodsAndSpec(pageable.getParameter(), pageable.getPageBounds());
    }


    /*********************
     * 其他人添加
     *********************/
    @Override
    public List<Long> saveCartList(List<ShopCart> cartList, String memberId,RdRanks rdRanks) {
        List<Long> longList = new ArrayList<>();
        for (ShopCart item : cartList) {
            longList.add(saveCart(item.getGoodsId(), memberId, rdRanks.getRankId(), item.getGoodsNum(), item.getSpecId(), 1,
                    item.getActivityId(), item.getActivityType(), item.getActivityGoodsId(), item.getActivitySpecId()));
        }
        return longList;
    }
}

