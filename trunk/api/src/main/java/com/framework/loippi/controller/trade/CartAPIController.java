package com.framework.loippi.controller.trade;

import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.service.product.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.framework.loippi.consts.CartConstant;
import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.GatherAreaConstant;
import com.framework.loippi.consts.ShopOrderDiscountTypeConsts;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.cart.ShopCart;
import com.framework.loippi.entity.gift.ShopGiftActivity;
import com.framework.loippi.entity.gift.ShopGiftGoods;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderDiscountType;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsFreightRule;
import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.mybatis.paginator.domain.Order.Direction;
import com.framework.loippi.param.cart.CartAddParam;
import com.framework.loippi.result.app.cart.CartCheckOutResult;
import com.framework.loippi.result.app.cart.CartResult;
import com.framework.loippi.result.app.cart.GiftResult;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.result.common.goods.GoodsListResult;
import com.framework.loippi.service.activity.ShopActivityGoodsService;
import com.framework.loippi.service.gift.ShopGiftActivityService;
import com.framework.loippi.service.gift.ShopGiftGoodsService;
import com.framework.loippi.service.order.ShopOrderDiscountTypeService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmAddInfoService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.service.user.RdRanksService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.Xerror;
import com.framework.loippi.vo.cart.ShopCartVo;
import com.framework.loippi.vo.goods.GoodsStatisticsVo;
import com.framework.loippi.vo.user.UserInfoVo;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * 功能： api购物车模块接口 类名：CartAPIController 日期：2017/11/20  10:40 作者：czl 详细说明： 修改备注:
 */
@Controller("apiCartController")
@Slf4j
public class CartAPIController extends BaseController {

    @Resource
    private ShopCartService cartService;
    @Resource
    private ShopOrderDiscountTypeService shopOrderDiscountTypeService;
    @Resource
    private ShopOrderService orderService;
    @Resource
    private RdMmAddInfoService rdMmAddInfoService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private RdMmRelationService rdMmRelationService;
    @Resource
    private RdRanksService rdRanksService;
    @Resource
    private RdMmAccountInfoService rdMmAccountInfoService;
    @Resource
    private ShopGoodsService goodsService;
    @Resource
    private ShopGoodsFreightService shopGoodsFreightService;
    @Resource
    private ShopGoodsFreightRuleService shopGoodsFreightRuleService;
    @Resource
    private ShopActivityGoodsService shopActivityGoodsService;
    @Resource
    private ShopGiftActivityService shopGiftActivityService;
    @Resource
    private ShopGiftGoodsService shopGiftGoodsService;
    @Resource
    private ShopGoodsSpecService shopGoodsSpecService;
    /**
     * 购物车列表
     */
    @RequestMapping(value = "/api/cart/list", method = RequestMethod.POST)
    @ResponseBody
    public String list(Pageable pageable, HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        pageable.setParameter(Paramap.create().put("memberId", member.getMmCode()));
        pageable.setOrderDirection(Direction.DESC);
        pageable.setOrderProperty("id");
        List<ShopCartVo> shopCartVos = cartService.listWithGoodsAndSpec(pageable);
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode",member.getMmCode());
        ShopGoodsFreightRule shopGoodsFreightRule = shopGoodsFreightRuleService.find("memberGradeId",rdMmRelation.getRank());
        List<CartResult> cartResults = CartResult.buildList(shopCartVos,shopGoodsFreightRule.getMinimumOrderAmount());
        return ApiUtils.success(cartResults);
    }

    /**
     * 移出购物车
     */
    @RequestMapping(value = "/api/cart/remove", method = RequestMethod.POST)
    @ResponseBody
    public String remove(String cartIds, HttpServletRequest request) {
        if (StringUtils.isEmpty(cartIds)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }

        // cartIds 购物车id字符串, 用逗号隔开
        String[] idsStr = cartIds.split(",");
        Long[] ids = new Long[idsStr.length];
        for (int i = 0; i < idsStr.length; i++) {
            ids[i] = Long.valueOf(idsStr[i]);
        }
        // 删除条数
        cartService.deleteAll(ids);
        return ApiUtils.success();
    }

    /**
     * 加入购物车
     */
    @RequestMapping(value = "/api/cart/add", method = RequestMethod.POST)
    @ResponseBody
    public String add(@Valid CartAddParam param, BindingResult vResult, HttpServletRequest request) {
        if (vResult.hasErrors()) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
        cartService.saveCart(param.getGoodsId(), member.getMmCode(), rdRanks.getRankId(),
            param.getCount(), param.getSpecId(),
            CartConstant.SAVE_TYPE_ADD_TO_CART,
            param.getActivityId(), param.getActivityType(), param.getActivityGoodsId(), param.getActivitySkuId());
        return ApiUtils.success();
    }

    /**
     * 更新购物车
     */
    @RequestMapping(value = "/api/cart/update", method = RequestMethod.POST)
    @ResponseBody
    public String update(@RequestParam long cartId, @RequestParam int num, HttpServletRequest request) {
        if (num < 1) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        //根据商品规格id查询商品规格
        cartService.updateNum(cartId, num, Long.parseLong(member.getMmCode()));
        return ApiUtils.success();
    }

    /**
     * 批量更新
     */
    @RequestMapping(value = "/api/cart/updateBatch", method = RequestMethod.POST)
    @ResponseBody
    public String updateBatch(@RequestParam Map<Long, Integer> cartIdNumMap, HttpServletRequest request) {
        // 检查不为空
        for (Entry<Long, Integer> entry : cartIdNumMap.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                return ApiUtils.error(Xerror.PARAM_INVALID);
            }
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        cartService.updateNumBatch(cartIdNumMap, Long.parseLong(member.getMmCode()));
        return ApiUtils.success();
    }


    /**
     * 购物车结算
     */
    @RequestMapping(value = "/api/cart/checkout", method = RequestMethod.POST)
    @ResponseBody
    public String checkout(@RequestParam String cartIds, Long groupBuyActivityId, Long shopOrderTypeId,
        @RequestParam(defaultValue = "1") Integer logisticType,
        HttpServletRequest request, Long addressId) {
        if (StringUtils.isBlank(cartIds)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);

        //订单类型相关
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
        //查看该会员类型下 所有可选择的订单类型
        Integer type = 1; //默认显示零售价
        List<ShopOrderDiscountType> orderDiscountTypeList = new ArrayList<>();
        if (rdRanks != null && rdRanks.getRankClass() != null && rdRanks.getRankClass() > 0) {
            type = 2;
            orderDiscountTypeList = shopOrderDiscountTypeService.findAll();
        }

        // 获取收货地址
        RdMmAddInfo addr = null;
        if (addressId != null) {
            addr = rdMmAddInfoService.find("aid", addressId);
        } else {
            List<RdMmAddInfo> addrList = rdMmAddInfoService.findList("mmCode", member.getMmCode());
            if (CollectionUtils.isNotEmpty(addrList)) {
                addr = addrList.stream()
                    .filter(item -> item.getDefaultadd() != null && item.getDefaultadd() == 1)
                    .findFirst()
                    .orElse(addrList.get(0));
            }
        }
        if (logisticType == 2) {
            addr = null;
        }
        ShopOrderDiscountType shopOrderDiscountType = null;
        if (shopOrderTypeId != null) {
            shopOrderDiscountType = shopOrderDiscountTypeService.find(shopOrderTypeId);
            if (shopOrderDiscountType != null) {
                type = shopOrderDiscountType.getPreferentialType();
                if (type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_MEMBER
                    && type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PPV
                    && type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PREFERENTIAL
                    && type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_RETAIL) {
                    type = ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_RETAIL;
                    shopOrderDiscountType.setPreferentialType(type);
                }
            }
        }
        if (shopOrderDiscountType == null) {
            shopOrderDiscountType = new ShopOrderDiscountType();
            shopOrderDiscountType.setId(-1L);
            shopOrderDiscountType.setPreferentialType(type);
        }
        Map<String, Object> map = cartService
            .queryTotalPrice(cartIds, member.getMmCode(), null, groupBuyActivityId, shopOrderDiscountType, addr);
        // 购物车数据
        if (map.get("error").equals("true")) {
            return ApiUtils.error("商品属性发生改变,请重新结算");
        }
        List<ShopCart> cartList = Lists.newArrayList();
        if (StringUtils.isNotEmpty(cartIds) && !"null".equals(cartIds)) {
            String[] cartId = cartIds.split(",");
            if (cartId != null && cartId.length > 0) {
                cartList = cartService.findList("ids", cartId);
            }
        }
        if (CollectionUtils.isEmpty(cartList)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        CartCheckOutResult result = CartCheckOutResult
            .build(map, cartList, addr, shopOrderTypeId, shopOrderDiscountType);
        if (log.isDebugEnabled()) {
            log.debug(JacksonUtil.toJson(result));
        }

        // TODO: 2018/12/14 自提地址 自提地址 id为-1 表示平台地址
        RdMmAddInfo shopMemberAddress = rdMmAddInfoService.find("aid", -1);
        List<ShopOrderDiscountType> shopOrderDiscountTypeList = new ArrayList<>();
        if (rdRanks != null && rdRanks.getRankClass() != null && rdRanks.getRankClass() > 0) {
            shopOrderDiscountTypeList = shopOrderDiscountTypeService.findList("totalPpv", result.getTotalPpv());
        }
        result = result.build2(result, shopOrderDiscountTypeList, rdRanks, rdMmBasicInfo, shopMemberAddress,
            orderDiscountTypeList);
        result.setImmediatelyFlag(0);
        return ApiUtils.success(result);
    }

    /**
     * 购物车结算新 TODO
     */
    @RequestMapping(value = "/api/cart/checkoutNew", method = RequestMethod.POST)
    @ResponseBody
    public String checkoutNew(@RequestParam String cartIds, Long groupBuyActivityId, Long shopOrderTypeId,
                           @RequestParam(defaultValue = "1") Integer logisticType,
                           HttpServletRequest request, Long addressId) {
        if (StringUtils.isBlank(cartIds)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);

        //订单类型相关
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
        //查看该会员类型下 所有可选择的订单类型
        Integer type = 1; //默认显示零售价
        List<ShopOrderDiscountType> orderDiscountTypeList = new ArrayList<>();
        if (rdRanks != null && rdRanks.getRankClass() != null && rdRanks.getRankClass() > 0) {
            type = 2;
            orderDiscountTypeList = shopOrderDiscountTypeService.findAll();
        }

        // 获取收货地址
        RdMmAddInfo addr = null;
        if (addressId != null) {
            addr = rdMmAddInfoService.find("aid", addressId);
        } else {
            List<RdMmAddInfo> addrList = rdMmAddInfoService.findList("mmCode", member.getMmCode());
            if (CollectionUtils.isNotEmpty(addrList)) {
                addr = addrList.stream()
                        .filter(item -> item.getDefaultadd() != null && item.getDefaultadd() == 1)
                        .findFirst()
                        .orElse(addrList.get(0));
            }
        }
        if (logisticType == 2) {
            addr = null;
        }
        ShopOrderDiscountType shopOrderDiscountType = null;
        if (shopOrderTypeId != null) {
            shopOrderDiscountType = shopOrderDiscountTypeService.find(shopOrderTypeId);
            if (shopOrderDiscountType != null) {
                type = shopOrderDiscountType.getPreferentialType();
                if (type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_MEMBER
                        && type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PPV
                        && type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PREFERENTIAL
                        && type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_RETAIL) {
                    type = ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_RETAIL;
                    shopOrderDiscountType.setPreferentialType(type);
                }
            }
        }
        if (shopOrderDiscountType == null) {
            shopOrderDiscountType = new ShopOrderDiscountType();
            shopOrderDiscountType.setId(-1L);
            shopOrderDiscountType.setPreferentialType(type);
        }
        Map<String, Object> map = cartService
                .queryTotalPrice(cartIds, member.getMmCode(), null, groupBuyActivityId, shopOrderDiscountType, addr);
        // 购物车数据
        if (map.get("error").equals("true")) {
            return ApiUtils.error("商品属性发生改变,请重新结算");
        }
        List<ShopCart> cartList = Lists.newArrayList();
        if (StringUtils.isNotEmpty(cartIds) && !"null".equals(cartIds)) {
            String[] cartId = cartIds.split(",");
            if (cartId != null && cartId.length > 0) {
                cartList = cartService.findList("ids", cartId);
            }
        }
        if (CollectionUtils.isEmpty(cartList)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        CartCheckOutResult result = CartCheckOutResult
                .build(map, cartList, addr, shopOrderTypeId, shopOrderDiscountType);
        if (log.isDebugEnabled()) {
            log.debug(JacksonUtil.toJson(result));
        }

        // TODO: 2018/12/14 自提地址 自提地址 id为-1 表示平台地址
        RdMmAddInfo shopMemberAddress = rdMmAddInfoService.find("aid", -1);
        List<ShopOrderDiscountType> shopOrderDiscountTypeList = new ArrayList<>();
        if (rdRanks != null && rdRanks.getRankClass() != null && rdRanks.getRankClass() > 0) {
            shopOrderDiscountTypeList = shopOrderDiscountTypeService.findList("totalPpv", result.getTotalPpv());
        }
        result = result.build2(result, shopOrderDiscountTypeList, rdRanks, rdMmBasicInfo, shopMemberAddress,
                orderDiscountTypeList);
        /*********************************************************************************************************************/
        Integer flag=0;
        Integer giftsNum=0;
        ArrayList<ShopGoods> shopGoods = new ArrayList<>();
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            /*Date startTime = format.parse("2019-11-01 00:00:00");
            Date endTime = format.parse("2019-11-12 23:59:59");
            Date nowTime = new Date();
            boolean b = belongCalendar(nowTime, startTime, endTime);
            if(b){
                if(rdMmRelation.getRank()==0&&result.getNeedToPay().compareTo(new BigDecimal("360"))!=-1){
                    flag=1;
                    giftsNum=result.getNeedToPay().divideToIntegralValue(new BigDecimal("360")).intValue();
                    ShopGoods goods = goodsService.find(6587959889232924672L);//老棉农棉柔巾20*20cm*100抽*3包/提
                    ShopGoods goods1 = goodsService.find(6587960502192705536L);//老棉农棉柔巾（加厚款）20*20cm*60抽*3包/提
                    ShopGoods goods2 = goodsService.find(6587961856550244352L);//老棉农婴儿棉柔巾 10*20cm*100抽*6包/提
                    ShopGoods goods3 = goodsService.find(6573038322627645440L);//OLOMI母婴抑菌倍护洗衣凝珠
                    ShopGoods goods4 = goodsService.find(6552743534695288832L);//OLOMI植萃精华保湿面膜
                    ShopGoods goods5 = goodsService.find(6552746788883795968L);//OLOMI植萃精华修护面膜
                    if (goods!=null){
                        shopGoods.add(goods);
                    }
                    if (goods1!=null){
                        shopGoods.add(goods1);
                    }
                    if (goods2!=null){
                        shopGoods.add(goods2);
                    }
                    if (goods3!=null){
                        shopGoods.add(goods3);
                    }
                    if (goods4!=null){
                        shopGoods.add(goods4);
                    }
                    if (goods5!=null){
                        shopGoods.add(goods5);
                    }
                }else if(rdMmRelation.getRank()>0&&result.getActualTotalPpv().compareTo(new BigDecimal("25"))!=-1&&result.getActualTotalPpv().compareTo(new BigDecimal("50"))==-1){
                    flag=1;
                    giftsNum=result.getActualTotalPpv().divideToIntegralValue(new BigDecimal("25")).intValue();
                    ShopGoods goods = goodsService.find(6587959889232924672L);//老棉农棉柔巾20*20cm*100抽*3包/提
                    ShopGoods goods1 = goodsService.find(6587960502192705536L);//老棉农棉柔巾（加厚款）20*20cm*60抽*3包/提
                    ShopGoods goods2 = goodsService.find(6587961856550244352L);//老棉农婴儿棉柔巾 10*20cm*100抽*6包/提
                    ShopGoods goods3 = goodsService.find(6573038322627645440L);//OLOMI母婴抑菌倍护洗衣凝珠
                    ShopGoods goods4 = goodsService.find(6552743534695288832L);//OLOMI植萃精华保湿面膜
                    ShopGoods goods5 = goodsService.find(6552746788883795968L);//OLOMI植萃精华修护面膜
                    if (goods!=null){
                        shopGoods.add(goods);
                    }
                    if (goods1!=null){
                        shopGoods.add(goods1);
                    }
                    if (goods2!=null){
                        shopGoods.add(goods2);
                    }
                    if (goods3!=null){
                        shopGoods.add(goods3);
                    }
                    if (goods4!=null){
                        shopGoods.add(goods4);
                    }
                    if (goods5!=null){
                        shopGoods.add(goods5);
                    }
                }else if(rdMmRelation.getRank()>0&&result.getActualTotalPpv().compareTo(new BigDecimal("50"))!=-1){
                    flag=1;
                    giftsNum=result.getActualTotalPpv().divideToIntegralValue(new BigDecimal("50")).intValue();
                    ShopGoods goods = goodsService.find(6552806260624855040L);//OLOMI氨基酸温和修护洁面乳
                    ShopGoods goods1 = goodsService.find(6587956972564254720L);//OLOMI橙花精油温和卸妆水
                    ShopGoods goods2 = goodsService.find(6592325180272414720L);//【组合套装】家具清洁套装
                    ShopGoods goods3 = goodsService.find(6573037643838263296L);//OLOMI酵素多效洗衣凝珠
                    if (goods!=null){
                        shopGoods.add(goods);
                    }
                    if (goods1!=null){
                        shopGoods.add(goods1);
                    }
                    if (goods2!=null){
                        shopGoods.add(goods2);
                    }
                    if (goods3!=null){
                        shopGoods.add(goods3);
                    }
                }
            }*/
            GiftResult gift = getGift(result.getActualTotalPpv(), shopGoods, flag, giftsNum);
            result=result.build3(result,gift.getShopGoods(),gift.getFlag(),gift.getGiftsNum());
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.setImmediatelyFlag(0);
        return ApiUtils.success(result);
    }

    /**
     * 购物车结算新(优惠券使用) TODO
     */
    @RequestMapping(value = "/api/cart/checkoutNew1", method = RequestMethod.POST)
    @ResponseBody
    public String checkoutNew1(@RequestParam String cartIds, Long groupBuyActivityId, Long shopOrderTypeId,
                              @RequestParam(defaultValue = "1") Integer logisticType,
                              @RequestParam(required = false,value = "couponId") Long couponId,
                              HttpServletRequest request, Long addressId) {
        if (StringUtils.isBlank(cartIds)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);

        //订单类型相关
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
        //查看该会员类型下 所有可选择的订单类型
        Integer type = 1; //默认显示零售价
        List<ShopOrderDiscountType> orderDiscountTypeList = new ArrayList<>();
        if (rdRanks != null && rdRanks.getRankClass() != null && rdRanks.getRankClass() > 0) {
            type = 2;
            orderDiscountTypeList = shopOrderDiscountTypeService.findAll();
        }
        //***************************************************************************
        List<ShopCart> cartList = Lists.newArrayList();
        if (StringUtils.isNotEmpty(cartIds) && !"null".equals(cartIds)) {
            String[] cartId = cartIds.split(",");
            if (cartId != null && cartId.length > 0) {
                cartList = cartService.findList("ids", cartId);
            }
        }
        if (CollectionUtils.isEmpty(cartList)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        for (ShopCart shopCart : cartList) {
            Long goodsId = shopCart.getGoodsId();
            ShopGoods goods = goodsService.find(goodsId);
            if(goods==null||goods.getPlusVipType()==null){
                return ApiUtils.error("商品属性异常");
            }
            if(goods.getPlusVipType()==1){
                type = 8;
                break;
            }
        }
        //***************************************************************************
        // 获取收货地址
        RdMmAddInfo addr = null;
        if (addressId != null) {
            addr = rdMmAddInfoService.find("aid", addressId);
        } else {
            List<RdMmAddInfo> addrList = rdMmAddInfoService.findList("mmCode", member.getMmCode());
            if (CollectionUtils.isNotEmpty(addrList)) {
                addr = addrList.stream()
                        .filter(item -> item.getDefaultadd() != null && item.getDefaultadd() == 1)
                        .findFirst()
                        .orElse(addrList.get(0));
            }
        }
        if (logisticType == 2) {
            addr = null;
        }
        ShopOrderDiscountType shopOrderDiscountType = null;
        if (shopOrderTypeId != null&&type!=8) {
            shopOrderDiscountType = shopOrderDiscountTypeService.find(shopOrderTypeId);
            if (shopOrderDiscountType != null) {
                type = shopOrderDiscountType.getPreferentialType();
                if (type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_MEMBER
                        && type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PPV
                        && type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PREFERENTIAL
                        && type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_RETAIL
                        && type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PLUS) {
                    type = ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_RETAIL;
                    shopOrderDiscountType.setPreferentialType(type);
                }
            }
        }
        if (shopOrderDiscountType == null) {
            shopOrderDiscountType = new ShopOrderDiscountType();
            shopOrderDiscountType.setId(-1L);
            shopOrderDiscountType.setPreferentialType(type);
        }
        Map<String, Object> map = cartService
                .queryTotalPrice1(cartIds, member.getMmCode(), couponId, groupBuyActivityId, shopOrderDiscountType, addr);
        // 购物车数据
        if (map.get("error").equals("true")) {
            if (map.get("code").equals("10002")){
                return ApiUtils.error(map.get("message").toString());
            }
            return ApiUtils.error("商品属性发生改变,请重新结算");
        }
        CartCheckOutResult result = CartCheckOutResult
                .buildNew(map, cartList, addr, shopOrderTypeId, shopOrderDiscountType);
        if (log.isDebugEnabled()) {
            log.debug(JacksonUtil.toJson(result));
        }

        // TODO: 2018/12/14 自提地址 自提地址 id为-1 表示平台地址
        RdMmAddInfo shopMemberAddress = rdMmAddInfoService.find("aid", -1);
        List<ShopOrderDiscountType> shopOrderDiscountTypeList = new ArrayList<>();
        if (rdRanks != null && rdRanks.getRankClass() != null && rdRanks.getRankClass() > 0) {
            shopOrderDiscountTypeList = shopOrderDiscountTypeService.findList("totalPpv", result.getTotalPpv());
        }
        result = result.build2(result, shopOrderDiscountTypeList, rdRanks, rdMmBasicInfo, shopMemberAddress,
                orderDiscountTypeList);
        //***************************************************************************************************************************
        Integer flag=0;
        Integer giftsNum=0;
        ArrayList<ShopGoods> shopGoods = new ArrayList<>();
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            /*Date startTime = format.parse("2020-05-01 00:00:00");
            Date endTime = format.parse("2020-06-10 23:59:59");
            Date nowTime = new Date();
            boolean b = belongCalendar(nowTime, startTime, endTime);
            if(b){
                if((rdMmRelation.getRank()==0&&(result.getNeedToPay().add(result.getUseCouponAmount())).compareTo(new BigDecimal("360"))!=-1)||
                        (rdMmRelation.getRank()>0&&result.getActualTotalPpv().compareTo(new BigDecimal("25"))!=-1&&result.getActualTotalPpv().compareTo(new BigDecimal("50"))==-1)){
                    //单笔订单满360或25mi，赠送护手霜一支
                    ShopGoods goods1 = goodsService.find(6638361764952018944L);//护手霜
                    //ShopGoods goods1 = goodsService.find(6659359562891530240L);//护手霜 formal
                    if (goods1!=null){
                        shopGoods.add(goods1);
                    }
                    ShopGoods goods2 = goodsService.find(6552746788883795968L);//护手霜
                    if (goods2!=null){
                        shopGoods.add(goods2);
                    }
                    flag=1;
                }
                if(rdMmRelation.getRank()>0&&(result.getActualTotalPpv().compareTo(new BigDecimal("50"))!=-1)){
                    ShopGoods goods1 = goodsService.find(6661516062787375104L);//护手霜+护手霜组合套装
                    //ShopGoods goods1 = goodsService.find(6661525543797657600L);//护手霜+护手霜组合套装 formal
                    if (goods1!=null){
                        shopGoods.add(goods1);
                    }
                    flag=1;
                }
                giftsNum=1;
            }*/
            GiftResult gift = getGift(result.getActualTotalPpv(), shopGoods, flag, giftsNum);
            result=result.build3(result,gift.getShopGoods(),gift.getFlag(),gift.getGiftsNum());
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.setImmediatelyFlag(0);
        return ApiUtils.success(result);
    }

    /**
     * 购物车结算(修改shopOrderTypeId)
     * shopOrderTypeId：-1默认进入自行判断 1零售订单 2vip订单 3pv大单 8plusvip订单
     */
    @RequestMapping(value = "/api/cart/checkoutNew2", method = RequestMethod.POST)
    @ResponseBody
    public String checkoutNew2(@RequestParam String cartIds, Long groupBuyActivityId, Long shopOrderTypeId,
                               @RequestParam(defaultValue = "1") Integer logisticType,
                               @RequestParam(required = false,value = "couponId") Long couponId,
                               HttpServletRequest request, Long addressId) {
        if (StringUtils.isBlank(cartIds)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        if(shopOrderTypeId==null){
            return ApiUtils.error("请选择需要支付的订单类型");
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        //***************************************************************************
        Boolean plusFlag=false;
        List<ShopCart> cartList = Lists.newArrayList();
        if (StringUtils.isNotEmpty(cartIds) && !"null".equals(cartIds)) {
            String[] cartId = cartIds.split(",");
            if (cartId != null && cartId.length > 0) {
                cartList = cartService.findList("ids", cartId);
            }
        }
        if (CollectionUtils.isEmpty(cartList)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        for (ShopCart shopCart : cartList) {
            Long goodsId = shopCart.getGoodsId();
            ShopGoods goods = goodsService.find(goodsId);
            if(goods==null||goods.getPlusVipType()==null){
                return ApiUtils.error("商品属性异常");
            }
            if(goods.getPlusVipType()==1){
                plusFlag=true;
                break;
            }
        }
        //***************************************************************************
        //订单类型相关
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
        //查看该会员类型下 所有可选择的订单类型
        ShopOrderDiscountType shopOrderDiscountType = new ShopOrderDiscountType();
        if(shopOrderTypeId.equals(-1L)){
            shopOrderDiscountType.setId(-1L);
            Integer type = 1; //默认显示零售价
            if (rdRanks != null && rdRanks.getRankClass() != null && rdRanks.getRankClass() > 0) {
                type = 2;
            }
            shopOrderDiscountType.setPreferentialType(type);
        }else {
            shopOrderDiscountType.setId(shopOrderTypeId);
            shopOrderDiscountType.setPreferentialType(shopOrderTypeId.intValue());
        }
        if(plusFlag){
            shopOrderDiscountType.setPreferentialType(8);
        }
        // 获取收货地址
        RdMmAddInfo addr = null;
        if (addressId != null) {
            addr = rdMmAddInfoService.find("aid", addressId);
        } else {
            List<RdMmAddInfo> addrList = rdMmAddInfoService.findList("mmCode", member.getMmCode());
            if (CollectionUtils.isNotEmpty(addrList)) {
                addr = addrList.stream()
                        .filter(item -> item.getDefaultadd() != null && item.getDefaultadd() == 1)
                        .findFirst()
                        .orElse(addrList.get(0));
            }
        }
        if (logisticType == 2) {
            addr = null;
        }
        Map<String, Object> map = cartService
                .queryTotalPrice2(cartIds, member.getMmCode(), couponId, groupBuyActivityId, shopOrderDiscountType, addr);
        // 购物车数据
        if (map.get("error").equals("true")) {
            //if (map.get("code").equals("10002")){
                return ApiUtils.error(map.get("message").toString());
            //}
            //return ApiUtils.error("商品属性发生改变,请重新结算");
        }
        CartCheckOutResult result = CartCheckOutResult
                .buildNew(map, cartList, addr, shopOrderTypeId, shopOrderDiscountType);
        if (log.isDebugEnabled()) {
            log.debug(JacksonUtil.toJson(result));
        }

        // TODO: 2018/12/14 自提地址 自提地址 id为-1 表示平台地址
        RdMmAddInfo shopMemberAddress = rdMmAddInfoService.find("aid", -1);
        /*List<ShopOrderDiscountType> shopOrderDiscountTypeList = new ArrayList<>();
        if (rdRanks != null && rdRanks.getRankClass() != null && rdRanks.getRankClass() > 0) {
            shopOrderDiscountTypeList = shopOrderDiscountTypeService.findAll();
        }*/
        List<ShopOrderDiscountType> shopOrderDiscountTypeList =  shopOrderDiscountTypeService.findAll();
        result = result.build2New(result, shopOrderDiscountTypeList, rdRanks, rdMmBasicInfo, shopMemberAddress);
        //***************************************************************************************************************************
        Integer flag=0;
        Integer giftsNum=0;
        ArrayList<ShopGoods> shopGoods = new ArrayList<>();
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            /*Date startTime = format.parse("2020-05-01 00:00:00");
            Date endTime = format.parse("2020-06-10 23:59:59");
            Date nowTime = new Date();
            boolean b = belongCalendar(nowTime, startTime, endTime);
            if(b){
                if((rdMmRelation.getRank()==0&&(result.getNeedToPay().add(result.getUseCouponAmount())).compareTo(new BigDecimal("360"))!=-1)||
                        (rdMmRelation.getRank()>0&&result.getActualTotalPpv().compareTo(new BigDecimal("25"))!=-1&&result.getActualTotalPpv().compareTo(new BigDecimal("50"))==-1)){
                    //单笔订单满360或25mi，赠送护手霜一支
                    ShopGoods goods1 = goodsService.find(6638361764952018944L);//护手霜
                    //ShopGoods goods1 = goodsService.find(6659359562891530240L);//护手霜 formal
                    if (goods1!=null){
                        shopGoods.add(goods1);
                    }
                    ShopGoods goods2 = goodsService.find(6552746788883795968L);//护手霜
                    if (goods2!=null){
                        shopGoods.add(goods2);
                    }
                    flag=1;
                }
                if(rdMmRelation.getRank()>0&&(result.getActualTotalPpv().compareTo(new BigDecimal("50"))!=-1)){
                    ShopGoods goods1 = goodsService.find(6661516062787375104L);//护手霜+护手霜组合套装
                    //ShopGoods goods1 = goodsService.find(6661525543797657600L);//护手霜+护手霜组合套装 formal
                    if (goods1!=null){
                        shopGoods.add(goods1);
                    }
                    flag=1;
                }
                giftsNum=1;
            }*/
            //赠品
            GiftResult gift = getGift(result.getActualTotalPpv(), shopGoods, flag, giftsNum);
            result=result.build3(result,gift.getShopGoods(),gift.getFlag(),gift.getGiftsNum());
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.setImmediatelyFlag(0);
        return ApiUtils.success(result);
    }

    public GiftResult getGift(BigDecimal totalPpv, ArrayList<ShopGoods> shopGoods, Integer flag, Integer giftsNum) {
        GiftResult result = new GiftResult();
        List<ShopGiftActivity> giftActivityList = shopGiftActivityService.findByState(0);
        if (giftActivityList.size()==1){
            ShopGiftActivity giftActivity = giftActivityList.get(0);
            Date startTime = giftActivity.getStartTime();
            Date endTime = giftActivity.getEndTime();
            Date nowTime = new Date();
            boolean b = belongCalendar(nowTime, startTime, endTime);
            if(b){
                if (giftActivity.getPpv1()==null){
                    flag=0;
                    giftsNum = 0;
                }else {
                    BigDecimal ppv1 = giftActivity.getPpv1();
                    List<ShopGiftGoods> giftGoodsList1 = shopGiftGoodsService.findByGiftIdAndWRule(giftActivity.getId(),1);
                    if (giftGoodsList1.size()==0){
                        flag=0;
                        giftsNum = 0;
                    }else {
                        if (giftActivity.getPpv2()==null||giftActivity.getPpv2().compareTo(new BigDecimal("0.00"))==0){
                            shopGoods = getGoods1(totalPpv, shopGoods, ppv1, giftGoodsList1);
                        }else {
                            BigDecimal ppv2 = giftActivity.getPpv2();
                            shopGoods = getGoods2(totalPpv, shopGoods, ppv1,ppv2,giftGoodsList1);
                            List<ShopGiftGoods> giftGoodsList2 = shopGiftGoodsService.findByGiftIdAndWRule(giftActivity.getId(),2);
                            if (giftGoodsList2.size()==0){
                                shopGoods = getGoods1(totalPpv, shopGoods, ppv1,giftGoodsList1);
                            }else {
                                if (giftActivity.getPpv3()==null||giftActivity.getPpv3().compareTo(new BigDecimal("0.00"))==0){
                                    shopGoods = getGoods1(totalPpv, shopGoods, ppv2,giftGoodsList2);
                                }else {
                                    BigDecimal ppv3 = giftActivity.getPpv3();
                                    shopGoods = getGoods2(totalPpv, shopGoods, ppv2,ppv3,giftGoodsList2);
                                    List<ShopGiftGoods> giftGoodsList3 = shopGiftGoodsService.findByGiftIdAndWRule(giftActivity.getId(),3);
                                    if (giftGoodsList3.size()==0){
                                        shopGoods = getGoods1(totalPpv, shopGoods, ppv2,giftGoodsList2);
                                    }else {
                                        shopGoods = getGoods1(totalPpv, shopGoods, ppv3,giftGoodsList3);
                                    }
                                }
                            }
                        }
                        flag=1;
                        if (giftActivity.getGiftNum()==null||giftActivity.getGiftNum()==0){
                            giftsNum=1;
                        }else {
                            giftsNum=giftActivity.getGiftNum();
                        }
                    }
                }

            }
        }
        result.setFlag(flag);
        result.setGiftsNum(giftsNum);
        result.setShopGoods(shopGoods);
        return result;
    }

    public ArrayList<ShopGoods> getGoods1(BigDecimal totalPpv,ArrayList<ShopGoods> shopGoods,BigDecimal ppv,List<ShopGiftGoods> giftGoodsList) {
                if(totalPpv.compareTo(ppv)!=-1){//大于等于ppv1
                    for (ShopGiftGoods giftGoods : giftGoodsList) {
                        ShopGoods goods = goodsService.find(giftGoods.getGoodsId());
                        if (goods!=null){
                            goods.setGiftSpecId(giftGoods.getSpecId());
                            ShopGoodsSpec goodsSpec = shopGoodsSpecService.find(giftGoods.getSpecId());
                            goods.setShopGoodsSpec(goodsSpec);
                            shopGoods.add(goods);
                        }
                    }
                }
        return shopGoods;
    }
    public ArrayList<ShopGoods> getGoods2(BigDecimal totalPpv,ArrayList<ShopGoods> shopGoods,BigDecimal ppv1,BigDecimal ppv2,List<ShopGiftGoods> giftGoodsList) {

                if(totalPpv.compareTo(ppv1)!=-1&&totalPpv.compareTo(ppv2)==-1){//大于等于ppv1 小于ppv2
                    for (ShopGiftGoods giftGoods : giftGoodsList) {
                        ShopGoods goods = goodsService.find(giftGoods.getGoodsId());
                        if (goods!=null){
                            goods.setGiftSpecId(giftGoods.getSpecId());
                            ShopGoodsSpec goodsSpec = shopGoodsSpecService.find(giftGoods.getSpecId());
                            goods.setShopGoodsSpec(goodsSpec);
                            shopGoods.add(goods);
                        }
                    }
                }
        return shopGoods;
    }


    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        //设置当前时间
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        //设置开始时间
        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);
        //设置结束时间
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        //处于开始时间之后，和结束时间之前的判断
        if (date.after(begin) && date.before(end)) {
         return true;
        } else {
         return false;
        }
    }

    /**
     * 立即购买
     */
    @RequestMapping(value = "/api/cart/buyNow", method = RequestMethod.POST)
    @ResponseBody
    public String buyNow(@Valid CartAddParam param, BindingResult vResult, HttpServletRequest request) {
        if (vResult.hasErrors()) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Long groupBuyActivityId = param.getActivityId();
        Long cartId = cartService.saveCart(param.getGoodsId(), member.getMmCode(),
            null, param.getCount(),
            param.getSpecId(), 1, param.getActivityId(),
            param.getActivityType(), param.getActivityGoodsId(), param.getActivitySkuId());
        return checkout(cartId.toString(), null, groupBuyActivityId, 1, request, null);
    }

    /**
     * 购物车数量
     */
    @RequestMapping(value = "/api/cart/count", method = RequestMethod.POST)
    @ResponseBody
    public String countOfMemberCart(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Long count = cartService.count(Paramap.create().put("memberId", member.getMmCode()));
        return ApiUtils.success(count);
    }

    /**
     * 再次购买
     */

    @RequestMapping(value = "/api/cart/buyAgain", method = RequestMethod.POST)
    @ResponseBody
    public String buyAgain(Long orderId, HttpServletRequest request) {
        try {
            if (orderId == null) {
                return ApiUtils.error(Xerror.PARAM_INVALID);
            }
            AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
            ShopOrder order = orderService.findWithOrderGoodsById(orderId);
            if (order == null || order.getShopOrderGoodses() == null || order.getShopOrderGoodses().size() < 1) {
                return ApiUtils.error("没有购买记录");
            }
            if(order.getOrderType()!=null&&order.getOrderType()==5){//换购订单不可以调用正常订单的购买
                return ApiUtils.error("订单类型错误");
            }
            List<ShopCart> cartList = new ArrayList<>();
            for (ShopOrderGoods item : order.getShopOrderGoodses()) {
                ShopCart cart = new ShopCart();
                cart.setGoodsId(item.getGoodsId());
                cart.setMemberId(Long.parseLong(item.getBuyerId()));
                cart.setGoodsNum(item.getGoodsNum());
                cart.setSpecId(item.getSpecId());
                cart.setActivityId(item.getActivityId());
                cart.setActivityType(item.getActivityType());
                cartList.add(cart);
            }
            RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
            RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
            List<Long> list = cartService.saveCartList(cartList, member.getMmCode(),rdRanks);
            return checkout(Joiner.on(",").join(list), null, -1L, 1, request, null);
        } catch (Exception e) {
            log.error("再次购买错误", e);
            return ApiUtils.error(e.getMessage());
        }
    }

    /**
     * 再次购买
     */

    @RequestMapping(value = "/api/cart/buyAgainNew", method = RequestMethod.POST)
    @ResponseBody
    public String buyAgainNew(Long orderId, HttpServletRequest request) {
        try {
            if (orderId == null) {
                return ApiUtils.error(Xerror.PARAM_INVALID);
            }
            AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
            ShopOrder order = orderService.findWithOrderGoodsById(orderId);
            if (order == null || order.getShopOrderGoodses() == null || order.getShopOrderGoodses().size() < 1) {
                return ApiUtils.error("没有购买记录");
            }
            if(order.getOrderType()!=null&&order.getOrderType()==5){//换购订单不可以调用正常订单的购买
                return ApiUtils.error("订单类型错误");
            }
            List<ShopCart> cartList = new ArrayList<>();
            for (ShopOrderGoods item : order.getShopOrderGoodses()) {
                ShopCart cart = new ShopCart();
                cart.setGoodsId(item.getGoodsId());
                cart.setMemberId(Long.parseLong(item.getBuyerId()));
                cart.setGoodsNum(item.getGoodsNum());
                cart.setSpecId(item.getSpecId());
                cart.setActivityId(item.getActivityId());
                cart.setActivityType(item.getActivityType());
                if(item.getIsPresentation()==null||item.getIsPresentation()!=1){
                    cartList.add(cart);
                }
            }
            RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
            RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
            List<Long> list = cartService.saveCartList(cartList, member.getMmCode(),rdRanks);
            return checkoutNew1(Joiner.on(",").join(list), null, -1L, 1,null,request, null);
        } catch (Exception e) {
            log.error("再次购买错误", e);
            return ApiUtils.error(e.getMessage());
        }
    }
    /**
     * 再次购买
     */

    @RequestMapping(value = "/api/cart/buyAgainNew1", method = RequestMethod.POST)
    @ResponseBody
    public String buyAgainNew1(Long orderId, HttpServletRequest request) {
        try {
            if (orderId == null) {
                return ApiUtils.error(Xerror.PARAM_INVALID);
            }
            AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
            ShopOrder order = orderService.findWithOrderGoodsById(orderId);
            if (order == null || order.getShopOrderGoodses() == null || order.getShopOrderGoodses().size() < 1) {
                return ApiUtils.error("没有购买记录");
            }
            if(order.getOrderType()!=null&&order.getOrderType()==5){//换购订单不可以调用正常订单的购买
                return ApiUtils.error("订单类型错误");
            }
            if(order.getImmediatelyFlag()!=null&&order.getImmediatelyFlag()==1){
                for (ShopOrderGoods item : order.getShopOrderGoodses()) {
                    if(item.getIsPresentation()==null||item.getIsPresentation()!=1){
                        CartAddParam param = new CartAddParam();
                        param.setGoodsId(item.getGoodsId());
                        param.setCount(item.getGoodsNum());
                        param.setSpecId(item.getSpecId());
                        param.setActivityId(item.getActivityId());
                        param.setActivityType(item.getActivityType());
                        return immediatelyCheckout(param,null,-1L,1,null,request,null);
                    }
                }
            }
            List<ShopCart> cartList = new ArrayList<>();
            for (ShopOrderGoods item : order.getShopOrderGoodses()) {
                ShopCart cart = new ShopCart();
                cart.setGoodsId(item.getGoodsId());
                cart.setMemberId(Long.parseLong(item.getBuyerId()));
                cart.setGoodsNum(item.getGoodsNum());
                cart.setSpecId(item.getSpecId());
                cart.setActivityId(item.getActivityId());
                cart.setActivityType(item.getActivityType());
                if(item.getIsPresentation()==null||item.getIsPresentation()!=1){
                    cartList.add(cart);
                }
            }
            RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
            RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
            List<Long> list = cartService.saveCartList(cartList, member.getMmCode(),rdRanks);
            return checkoutNew2(Joiner.on(",").join(list), null, -1L, 1,null,request, null);
        } catch (Exception e) {
            log.error("再次购买错误", e);
            return ApiUtils.error(e.getMessage());
        }
    }

    /**
     * 凑单区搜索
     * @param request
     * @param pageSize 当前页大小 默认为3
     * @param pageNum 页码
     * @param searchType 查询类别 白菜价精选：1     百元好物精选：2     包邮转区：3      升级复消区：4
     * @return
     */
    @RequestMapping(value = "/api/cart/gatherArea", method = RequestMethod.POST)
    @ResponseBody
    public String gatherArea(HttpServletRequest request,@RequestParam(required = false,value = "pageSize",defaultValue = "3")Integer pageSize,
                             @RequestParam(required = false,value = "pageNum",defaultValue = "1")Integer pageNum,Integer searchType) {
        if (searchType==null){
            return ApiUtils.error("请传入对应查询类型");
        }
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNum);
        pager.setPageSize(pageSize);
        Paramap paramap = Paramap.create();
        if(searchType==1){
            paramap.put("highPrice", GatherAreaConstant.CABBAGE_PRICE);
        }
        if(searchType==2){
            paramap.put("highPrice", GatherAreaConstant.HANDPICK_CLOSE_HUNDRED_HIGH);
            paramap.put("lowPrice", GatherAreaConstant.HANDPICK_CLOSE_HUNDRED_LOW);
        }
        if(searchType==3){
            paramap.put("highPrice", GatherAreaConstant.PACKAGE_MAIL_HIGH);
            paramap.put("lowPrice", GatherAreaConstant.PACKAGE_MAIL_LOW);
        }
        if(searchType==4){
            paramap.put("lowMi", GatherAreaConstant.AFTER_ELIMINATION_MI);
        }
        paramap.put("goodsSalenum","yes");
        paramap.put("noExchange","yes");
        paramap.put("goodsShow",1);
        pager.setParameter(paramap);
        Page<ShopGoods> page = goodsService.findByPage(pager);
        List<ShopGoods> goods = page.getContent();
        List<GoodsListResult> build = shopActivityGoodsService.findAndAddAtiInfo(goods, prefix);
        return ApiUtils.success(build);
    }

    /**
     * 热卖商品top10
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/cart/hotSaleGoods", method = RequestMethod.GET)
    @ResponseBody
    public String hotSaleGoods(HttpServletRequest request) {
        Pageable pager = new Pageable();
        pager.setPageSize(10);
        pager.setPageNumber(1);
        GoodsStatisticsVo goodsStatisticsVo = new GoodsStatisticsVo();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-1);
        Date time = calendar.getTime();
        //Date time = new Date();
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
        String str = format.format(time);
        goodsStatisticsVo.setStartTime(str+" 00:00:00");
        goodsStatisticsVo.setEndTime(str+" 23:59:59");
        goodsStatisticsVo.setNoExchange("yes");
        goodsStatisticsVo.setGoodsShow(1);
        pager.setParameter(goodsStatisticsVo);
        Page<GoodsStatisticsVo> page = orderService.listBestSellGoods(pager);
        List<GoodsStatisticsVo> list = page.getContent();
        Integer size=0;
        ArrayList<ShopGoods> shopGoods = new ArrayList<>();
        List<GoodsListResult> build = new ArrayList<>();
        if(list!=null&&list.size()>=10){
            int i=0;
            for (GoodsStatisticsVo statisticsVo : list) {
                if(i>9){
                    break;
                }
                ShopGoods goods = goodsService.find(statisticsVo.getGoodsId());
                shopGoods.add(goods);
                i++;
            }
            build = shopActivityGoodsService.findAndAddAtiInfo(shopGoods, prefix);
        }else {
            HashMap<Long, ShopGoods> goodsMap = new HashMap<>();
            if(list==null){
                size=10;
            }else {
                size=10-list.size();
                for (GoodsStatisticsVo statisticsVo : list) {
                    ShopGoods goods = goodsService.find(statisticsVo.getGoodsId());
                    shopGoods.add(goods);
                    goodsMap.put(goods.getId(),goods);
                }
            }
            Pageable pageable = new Pageable();
            pageable.setPageSize(20);
            pageable.setPageNumber(1);
            Paramap paramap = Paramap.create();
            paramap.put("goodsSalenum","yes");
            paramap.put("noExchange","yes");
            paramap.put("goodsShow",1);
            pageable.setParameter(paramap);
            Page<ShopGoods> page1 = goodsService.findByPage(pageable);
            List<ShopGoods> goods = page1.getContent();
            for (ShopGoods good : goods) {
                if(size<=0){
                    break;
                }
                if(goodsMap.get(good.getId())==null){
                    shopGoods.add(good);
                    size--;
                }
            }
            //填充活动信息
            build = shopActivityGoodsService.findAndAddAtiInfo(shopGoods, prefix);
        }
        return ApiUtils.success(build);
    }

    /**
     * 立即购买确认订单页
     */
    @RequestMapping(value = "/api/cart/immediately/checkout", method = RequestMethod.POST)
    @ResponseBody
    public String immediatelyCheckout(@Valid CartAddParam param, Long groupBuyActivityId, Long shopOrderTypeId,
                               @RequestParam(defaultValue = "1") Integer logisticType,
                               @RequestParam(required = false,value = "couponId") Long couponId,
                               HttpServletRequest request, Long addressId) {
        if(shopOrderTypeId==null){
            return ApiUtils.error("订单类型错误");
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Boolean plusFlag=false;
        Long goodsId = param.getGoodsId();
        ShopGoods goods = goodsService.find(goodsId);
        if(goods==null||goods.getPlusVipType()==null){
            return ApiUtils.error("商品属性异常");
        }
        if(goods.getPlusVipType()==1){
            plusFlag=true;
        }
        //订单类型相关
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
        //查看该会员类型下 所有可选择的订单类型
        ShopOrderDiscountType shopOrderDiscountType = new ShopOrderDiscountType();
        if(shopOrderTypeId.equals(-1L)){
            shopOrderDiscountType.setId(-1L);
            Integer type = 1; //默认显示零售价
            if (rdRanks != null && rdRanks.getRankClass() != null && rdRanks.getRankClass() > 0) {
                type = 2;
            }
            shopOrderDiscountType.setPreferentialType(type);
        }else {
            shopOrderDiscountType.setId(shopOrderTypeId);
            shopOrderDiscountType.setPreferentialType(shopOrderTypeId.intValue());
        }
        if(plusFlag){
            shopOrderDiscountType.setPreferentialType(8);
        }
        // 获取收货地址
        RdMmAddInfo addr = null;
        if (addressId != null) {
            addr = rdMmAddInfoService.find("aid", addressId);
        } else {
            List<RdMmAddInfo> addrList = rdMmAddInfoService.findList("mmCode", member.getMmCode());
            if (CollectionUtils.isNotEmpty(addrList)) {
                addr = addrList.stream()
                        .filter(item -> item.getDefaultadd() != null && item.getDefaultadd() == 1)
                        .findFirst()
                        .orElse(addrList.get(0));
            }
        }
        if (logisticType == 2) {
            addr = null;
        }
        Map<String, Object> map = cartService
                .queryTotalPriceImmediately(goods,param.getSpecId(),param.getCount(), member.getMmCode(),
                        couponId, groupBuyActivityId, shopOrderDiscountType, addr,rdRanks.getRankId(),param.getActivityId(),
                        param.getActivityType(), param.getActivityGoodsId(), param.getActivitySkuId());
        // 购物车数据
        if (map.get("error").equals("true")) {
            return ApiUtils.error(map.get("message").toString());
        }
        CartCheckOutResult result = CartCheckOutResult
                .buildNew(map, (List<ShopCart>) map.get("cartList"), addr, shopOrderTypeId, shopOrderDiscountType);
        if (log.isDebugEnabled()) {
            log.debug(JacksonUtil.toJson(result));
        }
        // TODO: 2018/12/14 自提地址 自提地址 id为-1 表示平台地址
        RdMmAddInfo shopMemberAddress = rdMmAddInfoService.find("aid", -1);
        List<ShopOrderDiscountType> shopOrderDiscountTypeList =  shopOrderDiscountTypeService.findAll();
        result = result.build2New(result, shopOrderDiscountTypeList, rdRanks, rdMmBasicInfo, shopMemberAddress);
        //***************************************************************************************************************************
        Integer flag=0;
        Integer giftsNum=0;
        ArrayList<ShopGoods> shopGoods = new ArrayList<>();
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            /*Date startTime = format.parse("2020-05-01 00:00:00");
            Date endTime = format.parse("2020-06-10 23:59:59");
            Date nowTime = new Date();
            boolean b = belongCalendar(nowTime, startTime, endTime);
            if(b){
                if((rdMmRelation.getRank()==0&&(result.getNeedToPay().add(result.getUseCouponAmount())).compareTo(new BigDecimal("360"))!=-1)||
                        (rdMmRelation.getRank()>0&&result.getActualTotalPpv().compareTo(new BigDecimal("25"))!=-1&&result.getActualTotalPpv().compareTo(new BigDecimal("50"))==-1)){
                    //单笔订单满360或25mi，赠送护手霜一支
                    ShopGoods goods1 = goodsService.find(6638361764952018944L);//护手霜
                    //ShopGoods goods1 = goodsService.find(6659359562891530240L);//护手霜 formal
                    if (goods1!=null){
                        shopGoods.add(goods1);
                    }
                    ShopGoods goods2 = goodsService.find(6552746788883795968L);//护手霜
                    if (goods2!=null){
                        shopGoods.add(goods2);
                    }
                    flag=1;
                }
                if(rdMmRelation.getRank()>0&&(result.getActualTotalPpv().compareTo(new BigDecimal("50"))!=-1)){
                    ShopGoods goods1 = goodsService.find(6661516062787375104L);//护手霜+护手霜组合套装
                    //ShopGoods goods1 = goodsService.find(6661525543797657600L);//护手霜+护手霜组合套装 formal
                    if (goods1!=null){
                        shopGoods.add(goods1);
                    }
                    flag=1;
                }
                giftsNum=1;
            }*/
            GiftResult gift = getGift(result.getActualTotalPpv(), shopGoods, flag, giftsNum);
            result=result.build3(result,gift.getShopGoods(),gift.getFlag(),gift.getGiftsNum());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(result.getShopOrderTypeId()==8){
            ArrayList<UserInfoVo> members=rdMmBasicInfoService.findMemberOneMobile(Paramap.create().put("mmCode",rdMmBasicInfo.getMmCode()).
                    put("mobile",rdMmBasicInfo.getMobile()));
            List<RdRanks> ranks = rdRanksService.findAll();
            RdRanks rdRankVip = rdRanksService.find("rankId",1L);
            HashMap<Integer, RdRanks> rankMap = new HashMap<>();
            for (RdRanks rank : ranks) {
                rankMap.put(rank.getRankId(),rank);
            }
            for (UserInfoVo userInfoVo : members) {
                RdRanks rankResult = rankMap.get(userInfoVo.getRank());
                if(userInfoVo.getRank()==2){
                    userInfoVo.setRankName(rdRankVip.getRankName());
                }else {
                    userInfoVo.setRankName(rankResult.getRankName());
                }
            }
            result.setCutUserInfoList(members);
        }
        if(param.getActivityId()!=null){
            result.setActivityId(param.getActivityId());
            result.setActivityIdStr(param.getActivityId().toString());
        }
        if(param.getActivityGoodsId()!=null){
            result.setActivityGoodsId(param.getActivityGoodsId());
            result.setActivityGoodsIdStr(param.getActivityGoodsId().toString());
        }
        if(param.getActivitySkuId()!=null){
            result.setActivitySkuId(param.getActivitySkuId());
            result.setActivitySkuIdStr(param.getActivitySkuId().toString());
        }
        if(param.getActivityType()!=null){
            result.setActivityType(param.getActivityType());
        }
        result.setImmediatelyFlag(1);
        return ApiUtils.success(result);
    }
}
