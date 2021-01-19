package com.framework.loippi.controller.trade;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.framework.loippi.consts.AllInPayBillCutConstant;
import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.NotifyConsts;
import com.framework.loippi.consts.OrderState;
import com.framework.loippi.consts.PaymentTallyState;
import com.framework.loippi.consts.ShopOrderDiscountTypeConsts;
import com.framework.loippi.controller.AppConstants;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.controller.StateResult;
import com.framework.loippi.dao.ShopCommonMessageDao;
import com.framework.loippi.dao.ShopMemberMessageDao;
import com.framework.loippi.entity.PayCommon;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.entity.ShopMemberMessage;
import com.framework.loippi.entity.cart.ShopCart;
import com.framework.loippi.entity.cart.ShopCartExchange;
import com.framework.loippi.entity.common.ShopCommonArea;
import com.framework.loippi.entity.common.ShopCommonExpress;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponDetail;
import com.framework.loippi.entity.integration.RdMmIntegralRule;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderAddress;
import com.framework.loippi.entity.order.ShopOrderDiscountType;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.entity.order.ShopOrderSplit;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsBrand;
import com.framework.loippi.entity.product.ShopGoodsEvaluate;
import com.framework.loippi.entity.product.ShopGoodsFreightRule;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.entity.trade.ShopRefundReturn;
import com.framework.loippi.entity.user.MemberPrivilege;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.entity.walet.RdBizPay;
import com.framework.loippi.enus.RefundReturnState;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.param.cart.CartAddParam;
import com.framework.loippi.param.order.OrderImmediatelySubmitParam;
import com.framework.loippi.param.order.OrderSubmitParam;
import com.framework.loippi.param.order.RefundReturnParam;
import com.framework.loippi.result.app.order.ApplyRefundReturnResult;
import com.framework.loippi.result.app.order.OrderDetailResult;
import com.framework.loippi.result.app.order.OrderResult;
import com.framework.loippi.result.app.order.OrderSubmitResult;
import com.framework.loippi.result.app.order.RedemptionOrderSubmitResult;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.result.evaluate.EvaluateOrderGoodsResult;
import com.framework.loippi.result.order.AppletsPayTLResult;
import com.framework.loippi.result.order.RefundOrderResult;
import com.framework.loippi.service.KuaidiService;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.alipay.AlipayMobileService;
import com.framework.loippi.service.common.ShopCommonAreaService;
import com.framework.loippi.service.common.ShopCommonExpressService;
import com.framework.loippi.service.coupon.CouponDetailService;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.service.integration.RdMmIntegralRuleService;
import com.framework.loippi.service.order.ShopOrderAddressService;
import com.framework.loippi.service.order.ShopOrderDiscountTypeService;
import com.framework.loippi.service.order.ShopOrderGoodsService;
import com.framework.loippi.service.order.ShopOrderPayService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.order.ShopOrderSplitService;
import com.framework.loippi.service.product.ShopCartService;
import com.framework.loippi.service.product.ShopGoodsBrandService;
import com.framework.loippi.service.product.ShopGoodsEvaluateService;
import com.framework.loippi.service.product.ShopGoodsFreightRuleService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.product.ShopGoodsSpecService;
import com.framework.loippi.service.trade.ShopMemberPaymentTallyService;
import com.framework.loippi.service.trade.ShopRefundReturnService;
import com.framework.loippi.service.union.UnionpayService;
import com.framework.loippi.service.user.MemberPrivilegeService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmAccountLogService;
import com.framework.loippi.service.user.RdMmAddInfoService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.service.user.RdRanksService;
import com.framework.loippi.service.user.RdSysPeriodService;
import com.framework.loippi.service.wallet.RdBizPayService;
import com.framework.loippi.service.wechat.WechatH5Service;
import com.framework.loippi.service.wechat.WechatMobileService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Digests;
import com.framework.loippi.utils.GoodsUtils;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.utils.TongLianUtils;
import com.framework.loippi.utils.Xerror;
import com.framework.loippi.vo.order.ShopOrderVo;
import com.framework.loippi.vo.refund.ReturnGoodsVo;
import com.google.common.collect.Lists;

/**
 * API - 订单模块接口
 *
 * @author czl
 * @version 1.0
 */
@Controller("apiOrderController")
@Slf4j
public class OrderAPIController extends BaseController {

    @Resource
    private ShopMemberMessageDao shopMemberMessageDao;
    @Resource
    private ShopCommonMessageDao shopCommonMessageDao;
    @Resource
    private ShopOrderService orderService;
    @Resource
    private ShopOrderGoodsService orderGoodsService;
    @Resource
    private ShopRefundReturnService refundReturnService;
    @Resource
    private ShopOrderPayService orderPayService;
    @Resource
    private ShopMemberPaymentTallyService paymentTallyService;
    @Resource
    private AlipayMobileService alipayMobileService;
    @Resource
    private UnionpayService unionpayService;
    @Resource
    private WechatMobileService wechatMobileService;
    @Resource
    private WechatH5Service wechatH5Service;
    @Resource
    private ShopOrderDiscountTypeService shopOrderDiscountTypeService;
    @Resource
    private ShopGoodsEvaluateService shopGoodsEvaluateService;
    @Autowired
    private ShopCommonExpressService commonExpressService;
    @Autowired
    private KuaidiService kuaidiService;
    @Resource
    private RdMmIntegralRuleService rdMmIntegralRuleService;
    @Resource
    private RdMmAccountInfoService rdMmAccountInfoService;
    @Resource
    private RdMmAddInfoService rdMmAddInfoService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private RdMmRelationService rdMmRelationService;
    @Resource
    private ShopCommonAreaService areaService;
    @Resource
    private ShopOrderAddressService shopOrderAddressService;
    @Resource
    private RdRanksService rdRanksService;
    @Resource
    private RedisService redisService;
    @Resource
    private ShopGoodsService shopGoodsService;
    @Resource
    private ShopGoodsSpecService shopGoodsSpecService;
    @Resource
    private CouponService couponService;
    @Resource
    private CouponDetailService couponDetailService;
    @Resource
    private RdBizPayService rdBizPayService;
    @Resource
    private RdSysPeriodService rdSysPeriodService;
    @Resource
    private RdMmAccountLogService rdMmAccountLogService;
    @Resource
    private MemberPrivilegeService memberPrivilegeService;
    @Resource
    private ShopGoodsBrandService shopGoodsBrandService;
    @Resource
    private ShopGoodsFreightRuleService shopGoodsFreightRuleService;
    @Resource
    private ShopCartService shopCartService;
    @Resource
    private ShopOrderSplitService shopOrderSplitService;
    /**
     * 提交订单
     */
    @RequestMapping(value = "/api/order/submit")
    @ResponseBody
    public String submitOrder(@Valid OrderSubmitParam param, BindingResult vResult, HttpServletRequest request) {
        if (vResult.hasErrors()) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        //***************************************************************************
        List<ShopCart> cartList = Lists.newArrayList();
        String cartIds = param.getCartIds();
        if (StringUtils.isNotEmpty(cartIds) && !"null".equals(cartIds)) {
            String[] cartId = cartIds.split(",");
            if (cartId != null && cartId.length > 0) {
                cartList =shopCartService.findList(Paramap.create().put("ids", cartId));
            }
        }
        for (ShopCart shopCart : cartList) {
            Long goodsId = shopCart.getGoodsId();
            ShopGoods goods = shopGoodsService.find(goodsId);
            if(goods==null||goods.getPlusVipType()==null){
                return ApiUtils.error("商品属性异常");
            }
            if(goods.getPlusVipType()==1){
                return ApiUtils.error("因版本太低，您购买的商品"+goods.getGoodsName()+"无法享受PLUS VIP折扣，请您升级最新版本获取优惠");
            }
        }
        //***************************************************************************
        // 订单留言
        Map<String, Object> orderMsgMap = new HashMap<>();
        if (StringUtils.isNotBlank(param.getOrderMessages())) {//验证是否有留言信息
            orderMsgMap.put("orderMessages", param.getOrderMessages());
        }
        if (param.getLogisticType() == 2) {//如果订单为自提 需要记录自提人姓名和电话
            orderMsgMap.put("userName", param.getUserName());
            orderMsgMap.put("userPhone", param.getUserPhone());
        }
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
        Integer type = 1; //默认显示零售价  判断商品是按零售价还是会员价出售
        if (rdRanks.getRankClass() > 0) {
            type = 2;
        }
        ShopOrderDiscountType shopOrderDiscountType = null;//订单优惠类型
        if (param.getShopOrderTypeId() != -1) {
            shopOrderDiscountType = shopOrderDiscountTypeService.find(param.getShopOrderTypeId());
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
        //提交订单,返回订单支付实体                                             
        ShopOrderPay orderPay = orderService.addOrderReturnPaySn(param.getCartIds(), member.getMmCode()
            , orderMsgMap, param.getAddressId()
            , null, param.getIsPp()
            , OrderState.PLATFORM_APP, param.getGroupBuyActivityId()
            , param.getGroupOrderId(), shopOrderDiscountType, param.getLogisticType(), param.getPaymentType());
        List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
            .findList(Paramap.create().put("order", "RID desc"));
        RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
        if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
            rdMmIntegralRule = rdMmIntegralRuleList.get(0);
        }
        return ApiUtils.success(OrderSubmitResult
            .build(rdMmIntegralRule, orderPay, rdMmAccountInfoService.find("mmCode", member.getMmCode())));
    }

    /**
     * 提交订单
     */
    @RequestMapping(value = "/api/order/submitNew")
    @ResponseBody
    public String submitOrderNew(@Valid OrderSubmitParam param, BindingResult vResult, HttpServletRequest request,@RequestParam(required = false,value = "giftId")Long giftId,Integer giftNum) {
        if (vResult.hasErrors()) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        //***************************************************************************
        List<ShopCart> cartList = Lists.newArrayList();
        String cartIds = param.getCartIds();
        if (StringUtils.isNotEmpty(cartIds) && !"null".equals(cartIds)) {
            String[] cartId = cartIds.split(",");
            if (cartId != null && cartId.length > 0) {
                cartList =shopCartService.findList(Paramap.create().put("ids", cartId));
            }
        }
        for (ShopCart shopCart : cartList) {
            Long goodsId = shopCart.getGoodsId();
            ShopGoods goods = shopGoodsService.find(goodsId);
            if(goods==null||goods.getPlusVipType()==null){
                return ApiUtils.error("商品属性异常");
            }
            if(goods.getPlusVipType()==1){
                return ApiUtils.error("因版本太低，您购买的商品"+goods.getGoodsName()+"无法享受PLUS VIP折扣，请您升级最新版本获取优惠");
            }
        }
        //***************************************************************************
        // 订单留言
        Map<String, Object> orderMsgMap = new HashMap<>();
        if (StringUtils.isNotBlank(param.getOrderMessages())) {//验证是否有留言信息
            orderMsgMap.put("orderMessages", param.getOrderMessages());
        }
        if (param.getLogisticType() == 2) {//如果订单为自提 需要记录自提人姓名和电话
            orderMsgMap.put("userName", param.getUserName());
            orderMsgMap.put("userPhone", param.getUserPhone());
        }
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
        Integer type = 1; //默认显示零售价  判断商品是按零售价还是会员价出售
        if (rdRanks.getRankClass() > 0) {
            type = 2;
        }
        ShopOrderDiscountType shopOrderDiscountType = null;//订单优惠类型
        if (param.getShopOrderTypeId() != -1) {
            shopOrderDiscountType = shopOrderDiscountTypeService.find(param.getShopOrderTypeId());
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
        if(giftId!=null){
            if(giftNum==null){
                return ApiUtils.error("赠送数量不可以为空");
            }
            ShopGoods shopGoods = shopGoodsService.find(giftId);
            if(shopGoods==null){
                return ApiUtils.error("所选赠品不存在");
            }
            ShopGoodsSpec goodsSpec = shopGoodsSpecService.find("goodsId", giftId);
            if(goodsSpec==null){
                return ApiUtils.error("所选赠品不存在");
            }
            if(goodsSpec.getSpecGoodsStorage()<giftNum){
                return ApiUtils.error("所选赠品已赠完，请选择其他类型赠品");
            }
        }
        //提交订单,返回订单支付实体
        ShopOrderPay orderPay = orderService.addOrderReturnPaySnNew(param.getCartIds(), member.getMmCode()
                , orderMsgMap, param.getAddressId()
                , null, param.getIsPp()
                , OrderState.PLATFORM_APP, param.getGroupBuyActivityId()
                , param.getGroupOrderId(), shopOrderDiscountType, param.getLogisticType(), param.getPaymentType(),giftId,giftNum);
        List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
                .findList(Paramap.create().put("order", "RID desc"));
        RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
        if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
            rdMmIntegralRule = rdMmIntegralRuleList.get(0);
        }
        return ApiUtils.success(OrderSubmitResult
                .build(rdMmIntegralRule, orderPay, rdMmAccountInfoService.find("mmCode", member.getMmCode())));
    }

    /**
     * 提交订单（添加优惠券相关逻辑）TODO
     */
    @RequestMapping(value = "/api/order/submitNew1")
    @ResponseBody
    public String submitOrderNew1(@Valid OrderSubmitParam param, BindingResult vResult, HttpServletRequest request,
                                  @RequestParam(required = false,value = "giftId")Long giftId,Integer giftNum,Long couponId,@RequestParam(required = false,value = "platform")String platform) throws Exception {
        if (vResult.hasErrors()) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        //***************************************************************************
        List<ShopCart> cartList = Lists.newArrayList();
        String cartIds = param.getCartIds();
        if (StringUtils.isNotEmpty(cartIds) && !"null".equals(cartIds)) {
            String[] cartId = cartIds.split(",");
            if (cartId != null && cartId.length > 0) {
                cartList =shopCartService.findList(Paramap.create().put("ids", cartId));
            }
        }
        for (ShopCart shopCart : cartList) {
            Long goodsId = shopCart.getGoodsId();
            ShopGoods goods = shopGoodsService.find(goodsId);
            if(goods==null||goods.getPlusVipType()==null){
                return ApiUtils.error("商品属性异常");
            }
            if(goods.getPlusVipType()==1){
                return ApiUtils.error("因版本太低，您购买的商品"+goods.getGoodsName()+"无法享受PLUS VIP折扣，请您升级最新版本获取优惠");
            }
        }
        //***************************************************************************
        // 订单留言
        Map<String, Object> orderMsgMap = new HashMap<>();
        if (StringUtils.isNotBlank(param.getOrderMessages())) {//验证是否有留言信息
            orderMsgMap.put("orderMessages", param.getOrderMessages());
        }
        if (param.getLogisticType() == 2) {//如果订单为自提 需要记录自提人姓名和电话
            orderMsgMap.put("userName", param.getUserName());
            orderMsgMap.put("userPhone", param.getUserPhone());
        }
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
        Integer type = 1; //默认显示零售价  判断商品是按零售价还是会员价出售
        if (rdRanks.getRankClass() > 0) {
            type = 2;
        }
        ShopOrderDiscountType shopOrderDiscountType = null;//订单优惠类型
        if (param.getShopOrderTypeId() != -1) {
            shopOrderDiscountType = shopOrderDiscountTypeService.find(param.getShopOrderTypeId());
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
        if(giftId!=null){
            if(giftNum==null){
                return ApiUtils.error("赠送数量不可以为空");
            }
            ShopGoods shopGoods = shopGoodsService.find(giftId);
            if(shopGoods==null){
                return ApiUtils.error("所选赠品不存在");
            }
            ShopGoodsSpec goodsSpec = shopGoodsSpecService.find("goodsId", giftId);
            if(goodsSpec==null){
                return ApiUtils.error("所选赠品不存在");
            }
            if(goodsSpec.getSpecGoodsStorage()<giftNum){
                return ApiUtils.error("所选赠品已赠完，请选择其他类型赠品");
            }
        }
        //提交订单,返回订单支付实体
        ShopOrderPay orderPay = new ShopOrderPay();
        if(platform!=null&&platform.equals("weixinAppletsPaymentPlugin")){
            orderPay = orderService.addOrderReturnPaySnNew1(param.getCartIds(), member.getMmCode()
                    , orderMsgMap, param.getAddressId()
                    , couponId, param.getIsPp()
                    , OrderState.PLATFORM_WECHAT, param.getGroupBuyActivityId()
                    , param.getGroupOrderId(), shopOrderDiscountType, param.getLogisticType(), param.getPaymentType(),giftId,giftNum);
        }else{
            orderPay= orderService.addOrderReturnPaySnNew1(param.getCartIds(), member.getMmCode()
                    , orderMsgMap, param.getAddressId()
                    , couponId, param.getIsPp()
                    , OrderState.PLATFORM_APP, param.getGroupBuyActivityId()
                    , param.getGroupOrderId(), shopOrderDiscountType, param.getLogisticType(), param.getPaymentType(),giftId,giftNum);
        }
        List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
                .findList(Paramap.create().put("order", "RID desc"));
        RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
        if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
            rdMmIntegralRule = rdMmIntegralRuleList.get(0);
        }
        return ApiUtils.success(OrderSubmitResult
                .build(rdMmIntegralRule, orderPay, rdMmAccountInfoService.find("mmCode", member.getMmCode())));
    }

    /**
     * 提交订单（添加plus订单类型）TODO
     */
    @RequestMapping(value = "/api/order/submitNew2")
    @ResponseBody
    public String submitOrderNew2(@Valid OrderSubmitParam param, BindingResult vResult, HttpServletRequest request,
                                  @RequestParam(required = false,value = "giftId")Long giftId,Integer giftNum,Long couponId,@RequestParam(required = false,value = "platform")String platform,
                                    Integer plusOrderFlag) throws Exception {
        if (vResult.hasErrors()) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        // 订单留言
        Map<String, Object> orderMsgMap = new HashMap<>();
        if (StringUtils.isNotBlank(param.getOrderMessages())) {//验证是否有留言信息
            orderMsgMap.put("orderMessages", param.getOrderMessages());
        }
        if (param.getLogisticType() == 2) {//如果订单为自提 需要记录自提人姓名和电话
            orderMsgMap.put("userName", param.getUserName());
            orderMsgMap.put("userPhone", param.getUserPhone());
        }
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
        Integer type = 1; //默认显示零售价  判断商品是按零售价还是会员价出售
        if (rdRanks.getRankClass() > 0) {
            type = 2;
        }
        /*ShopOrderDiscountType shopOrderDiscountType = null;//订单优惠类型
        if (param.getShopOrderTypeId() != -1) {
            shopOrderDiscountType = shopOrderDiscountTypeService.find(param.getShopOrderTypeId());
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
        }*/
        ShopOrderDiscountType shopOrderDiscountType=new ShopOrderDiscountType();
        shopOrderDiscountType.setId(-1L);
        Long shopOrderTypeId = param.getShopOrderTypeId();
        if(shopOrderTypeId==-1L){
            shopOrderDiscountType.setPreferentialType(type);
        }
        if(shopOrderTypeId==1L&&rdRanks.getRankClass() > 0){
            shopOrderDiscountType.setPreferentialType(2);
        }
        if(shopOrderTypeId==1L&&rdRanks.getRankClass() == 0){
            shopOrderDiscountType.setPreferentialType(1);
        }
        if(shopOrderTypeId==2L&&rdRanks.getRankClass() == 0){
            shopOrderDiscountType.setPreferentialType(1);
        }
        if(shopOrderTypeId==2L&&rdRanks.getRankClass() > 0){
            shopOrderDiscountType.setPreferentialType(2);
        }
        if(shopOrderTypeId==3L&&rdRanks.getRankClass() > 0){
            shopOrderDiscountType.setId(17L);
            shopOrderDiscountType.setPreferentialType(3);
        }
        if(plusOrderFlag!=null&&plusOrderFlag==1){
            shopOrderDiscountType.setId(-1L);
            shopOrderDiscountType.setPreferentialType(ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PLUS);
            /*return ApiUtils.error("请升级最新版本购买plus商品");//TODO*/
        }
        if(giftId!=null){
            if(giftNum==null){
                return ApiUtils.error("赠送数量不可以为空");
            }
            ShopGoods shopGoods = shopGoodsService.find(giftId);
            if(shopGoods==null){
                return ApiUtils.error("所选赠品不存在");
            }
            ShopGoodsSpec goodsSpec = shopGoodsSpecService.find("goodsId", giftId);
            if(goodsSpec==null){
                return ApiUtils.error("所选赠品不存在");
            }
            if(goodsSpec.getSpecGoodsStorage()<giftNum){
                return ApiUtils.error("所选赠品已赠完，请选择其他类型赠品");
            }
        }
        //提交订单,返回订单支付实体
        ShopOrderPay orderPay = new ShopOrderPay();
        if(platform!=null&&platform.equals("weixinAppletsPaymentPlugin")){
            orderPay = orderService.addOrderReturnPaySnNew1(param.getCartIds(), member.getMmCode()
                    , orderMsgMap, param.getAddressId()
                    , couponId, param.getIsPp()
                    , OrderState.PLATFORM_WECHAT, param.getGroupBuyActivityId()
                    , param.getGroupOrderId(), shopOrderDiscountType, param.getLogisticType(), param.getPaymentType(),giftId,giftNum);
        }else{
            orderPay= orderService.addOrderReturnPaySnNew1(param.getCartIds(), member.getMmCode()
                    , orderMsgMap, param.getAddressId()
                    , couponId, param.getIsPp()
                    , OrderState.PLATFORM_APP, param.getGroupBuyActivityId()
                    , param.getGroupOrderId(), shopOrderDiscountType, param.getLogisticType(), param.getPaymentType(),giftId,giftNum);
        }
        List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
                .findList(Paramap.create().put("order", "RID desc"));
        RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
        if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
            rdMmIntegralRule = rdMmIntegralRuleList.get(0);
        }
        return ApiUtils.success(OrderSubmitResult
                .build(rdMmIntegralRule, orderPay, rdMmAccountInfoService.find("mmCode", member.getMmCode())));
    }
    /**
     * 提交换购商品订单
     * @param param
     * @param vResult
     * @param request
     * @param platform
     * @return
     */
    @RequestMapping(value = "/api/redemption/order/submit")
    @ResponseBody
    public String submitRedemptionOrder(@Valid OrderSubmitParam param, BindingResult vResult, HttpServletRequest request,
                                  @RequestParam(required = false,value = "platform")String platform) {
        if (vResult.hasErrors()) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        // 订单留言
        Map<String, Object> orderMsgMap = new HashMap<>();
        if (StringUtils.isNotBlank(param.getOrderMessages())) {//验证是否有留言信息
            orderMsgMap.put("orderMessages", param.getOrderMessages());
        }
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        //提交订单,返回订单支付实体
        ShopOrderPay orderPay = new ShopOrderPay();
        Integer platformCode=null;
        if(platform!=null&&platform.equals("weixinAppletsPaymentPlugin")){
            platformCode=OrderState.PLATFORM_WECHAT;
        }else {
            platformCode=OrderState.PLATFORM_APP;
        }
        orderPay=orderService.addOrderReturnPaySnRedemption(param.getCartIds(),member.getMmCode(),
                orderMsgMap,param.getAddressId(),platformCode,param.getLogisticType(), param.getPaymentType());
        List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
                .findList(Paramap.create().put("order", "RID desc"));
        RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
        if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
            rdMmIntegralRule = rdMmIntegralRuleList.get(0);
        }
        return ApiUtils.success(RedemptionOrderSubmitResult
                .build(rdMmIntegralRule, orderPay, rdMmAccountInfoService.find("mmCode", member.getMmCode())));
    }

    /**
     * 订单列表
     */
    @RequestMapping(value = "/api/order/list", method = RequestMethod.POST)
    @ResponseBody
    public String listOrder(HttpServletRequest request, Integer orderStatus, Pageable pager) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Paramap paramap = Paramap.create();
        paramap.put("buyerId", member.getMmCode());
        paramap.put("isDel", 0);
        // -1 查询所有订单
        if (orderStatus != null && orderStatus != -1 && orderStatus != 80) {//0:已取消;5待审核;10:待付款;20:待发货;30:待收货;40:交易完成;50:已提交;60:已确认
            // 0:已取消;10:待付款;20:待发货;30:待收货;40:交易完成;50:已提交;60:已确认;
            paramap.put("orderState", orderStatus);
            paramap.put("lockState", 0);
        }
        pager.setOrderDirection(Order.Direction.DESC);
        pager.setOrderProperty("create_time");
        if (orderStatus != null && orderStatus == 40) {
            //已评价状态
            paramap.put("evaluationStatus", "0");
            pager.setOrderDirection(Order.Direction.DESC);
            pager.setOrderProperty("finnshed_time");
        }
        pager.setParameter(paramap);
        Page<ShopOrderVo> orderPage = orderService.listWithGoods(pager);
        return ApiUtils.success(OrderResult.buildList(orderPage.getContent()));


    }

    /**
     * 订单详情
     */
    @RequestMapping(value = "/api/order/detail", method = RequestMethod.POST)
    @ResponseBody
    public String detailOrder(@RequestParam long orderId, HttpServletRequest request) throws Exception {
        ShopOrderVo results = orderService.findWithAddrAndGoods(orderId);
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if (!member.getMmCode().equals(results.getBuyerId() + "")) {
            return ApiUtils.error(Xerror.SYSTEM_ILLEGALITY);
        }
        RdMmAddInfo shopMemberAddress = null;
        //如果取货地址等于-1L 即为自提
        if (results.getLogisticType() == 2) {
            //表示为自提
            shopMemberAddress = rdMmAddInfoService.find(results.getAddress().getMentionId());
        }
        List LogisticsInformation = new ArrayList();
        OrderDetailResult orderDetailResult=new OrderDetailResult();
        if(results.getSplitFlag()!=null&&results.getSplitFlag()==1){
            List<ShopOrderSplit> orderSplits = shopOrderSplitService.findList(Paramap.create().put("orderId",results.getId()).put("buyFlag",2));
            ArrayList<String> strings = new ArrayList<>();
            if(orderSplits!=null&&orderSplits.size()>0){
                for (ShopOrderSplit orderSplit : orderSplits) {
                    strings.add(orderSplit.getMmCode());
                }
            }
            orderDetailResult.setSplitFlag(1);
            orderDetailResult.setSplitMmCodes(strings);
        }else {
            orderDetailResult.setSplitFlag(0);
        }
        //查询当前订单是否使用了优惠券
        List<CouponDetail> couponDetails = couponDetailService.findList(Paramap.create().put("useOrderId",results.getId()).put("holdId",results.getBuyerId()));
        if(couponDetails!=null&&couponDetails.size()>0){
            ArrayList<Coupon> coupons = new ArrayList<>();
            for (CouponDetail couponDetail : couponDetails) {
                Coupon coupon = couponService.find(couponDetail.getCouponId());
                coupon.setCustomerUseNum(1);//现阶段优惠券只能使用一张
                coupons.add(coupon);
            }
            orderDetailResult = OrderDetailResult.build2(coupons,orderDetailResult);
        }
        if (results.getLogisticType()!=2&&(results.getOrderState() == 30 || results.getOrderState() == 40 || (results.getShippingExpressCode() != null
            && !"".equals(results.getShippingExpressCode())))) {
            //ShopCommonExpress eCode = commonExpressService.find("eCode", results.getShippingExpressCode());
            ShopCommonExpress eCode = commonExpressService.find(results.getShippingExpressId());
            String kuaiInfo = kuaidiService.query(eCode.getEAliCode(), results.getShippingCode());
            Map mapType = JacksonUtil.convertMap(kuaiInfo);
            if (StringUtils.isBlank(kuaiInfo)) {
                return ApiUtils.error();
            }
            //List<Map<String, String>> datainfo = (List) mapType.get("data");//快递100的返回格式
            Map<String, List<Map<String,String>>> result = (Map<String, List<Map<String,String>>>) mapType.get("result");
            List<Map<String, String>> datainfo = result.get("list");
            //是否存在物流信息
            if (datainfo != null) {
                Map<String, String> map=new HashMap<>();
                //如果存在物流信息 则显示有信息的那一条
                Integer num = 1;
                for (Map<String, String> item :datainfo) {
                    if (item!=null){
                        if (num==1){
                            map=item;
                            num++;
                        }
                    }
                }
                return ApiUtils.success(
                    OrderDetailResult.build(results, shopMemberAddress,orderDetailResult).setLogisticsInformation(map));
            } else {
                return ApiUtils.success(
                    OrderDetailResult.build(results, shopMemberAddress,orderDetailResult).setLogisticsInformation(new HashMap<>()));
            }

        }
        return ApiUtils
            .success(OrderDetailResult.build(results, shopMemberAddress,orderDetailResult).setLogisticsInformation(new HashMap<>()));
    }

    /**
     * 去付款
     */
    @RequestMapping(value = "/api/order/pay/forward", method = RequestMethod.POST)
    @ResponseBody
    public String payOrderForward(String paySn, HttpServletRequest request) {
        if (StringUtil.isEmpty(paySn)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        ShopOrderPay bySn = orderPayService.findBySn(paySn);
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
            .findList(Paramap.create().put("order", "RID desc"));
        RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
        if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
            rdMmIntegralRule = rdMmIntegralRuleList.get(0);
        }
        return ApiUtils.success(
            OrderSubmitResult.build(rdMmIntegralRule, bySn, rdMmAccountInfoService.find("mmCode", member.getMmCode())));
    }

    /**
     * 取消订单
     */
    @RequestMapping(value = "/api/order/cancel", method = RequestMethod.POST)
    @ResponseBody
    public String cancelOrder(@RequestParam long orderId, HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        // todo 秒杀订单库存
        // 更新订单状态与库存
        orderService.updateCancelOrder(orderId, Constants.OPERATOR_MEMBER, Long.parseLong(member.getMmCode()),
            PaymentTallyState.PAYMENTTALLY_TREM_MB, "用户自己取消订单","");
        return ApiUtils.success();
    }

    /**
     * 删除订单
     */
    @RequestMapping(value = "/api/order/delete", method = RequestMethod.POST)
    @ResponseBody
    public String deleteOrder(@RequestParam Long orderId, HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        orderService.updateToDelState(orderId, member.getMmCode());
        return ApiUtils.success();
    }

    /**
     * 订单完成--收货确认
     */
    @RequestMapping("/api/order/finish")
    @ResponseBody
    public String finishOrder(@RequestParam(value = "orderId") long orderId, HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        orderService.updateFinishOrder(orderId, Long.parseLong(member.getMmCode()), Constants.OPERATOR_MEMBER);
        if (member.getLookVip()==0){
            RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
            RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
            if (rdRanks != null) {
                if (Optional.ofNullable(rdRanks.getRankClass()).orElse(0) > 0) {
                    member.setLookPpv(1);
                    member.setLookVip(1);
                    member.setRankId(rdRanks.getRankId());
                    redisService.save(member.getSessionid(),member );
                }
            }

        }
        return ApiUtils.success();
    }

    /**
     * 发货提醒
     */
    @RequestMapping(value = "/api/order/delivery/remind", method = RequestMethod.POST)
    @ResponseBody
    public String orderDeliveryRemind(@RequestParam long orderId, HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        orderService.updateRemindDelivery(orderId, Long.parseLong(member.getMmCode()));
        return ApiUtils.success();
    }


    /**
     * 去付款
     *
     * @param paysn 支付订单编码
     * @param paymentCode 支付代码名称: ZFB YL weiscan
     * @param paymentId 支付方式索引id
     * @param integration 积分
     * @param paypassword 支付密码
     * @param paymentType 1在线支付 2货到付款
     */
    @RequestMapping("/api/order/pay")
    @ResponseBody
    public String payOrder(@RequestParam(value = "paysn") String paysn,
        @RequestParam(defaultValue = "pointsPaymentPlugin") String paymentCode,
        @RequestParam(defaultValue = "0") String paymentId,
        @RequestParam(defaultValue = "0") String integration,
        //@RequestParam(defaultValue = "0") Integer integration,
        @RequestParam(defaultValue = "0") String paypassword,
        @RequestParam(defaultValue = "1") Integer paymentType,
        HttpServletRequest request) {

        /*if (paymentCode.equals("alipayMobilePaymentPlugin")){
            return ApiUtils.error("支付宝功能升级中，请先选用其他支付方式");
        }*/

        if(integration==null&&"".equals(integration)){
            return ApiUtils.error("请输入支付的积分金额");
        }
        BigDecimal i = new BigDecimal("0.00");
        if (integration==null||"".equals(integration)){
            i = new BigDecimal("0.00");
        }else {
            i = new BigDecimal(integration).setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        /*try {
            String[] strings = integration.split("\\.");
            String string = strings[0];
            i = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ApiUtils.error("输入积分数额有误");
        }*/
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo shopMember = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        //处理购物积分
        //获取购物积分购物比例
        List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
            .findList(Paramap.create().put("order", "RID desc"));
        RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
        if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
            rdMmIntegralRule = rdMmIntegralRuleList.get(0);
        }
        int shoppingPointSr = Optional.ofNullable(rdMmIntegralRule.getShoppingPointSr()).orElse(0);
        //if (integration != 0) {
        if (i.compareTo(new BigDecimal("0.00")) != 0) {
            if (rdMmAccountInfo.getPaymentPwd() == null) {
                return ApiUtils.error(80002,"你还未设置支付密码");
            }
            if (!Digests.validatePassword(paypassword, rdMmAccountInfo.getPaymentPwd())) {
                return ApiUtils.error(80001,"支付密码错误");
            }
            if (rdMmAccountInfo.getWalletStatus() != 0) {
                return ApiUtils.error("购物积分账户状态未激活或者已被冻结");
            }
            ShopOrderPay pay = orderPayService.findBySn(paysn);
            //处理积分支付
            orderService.ProcessingIntegrals(paysn, i, shopMember, pay, shoppingPointSr);
            //orderService.ProcessingIntegrals(paysn, integration, shopMember, pay, shoppingPointSr);
        }
        List<ShopOrder> orderList = orderService.findList("paySn", paysn);
        if (CollectionUtils.isEmpty(orderList)) {
            return ApiUtils.error("订单不存在");
        }
        ShopOrder shopOrder = orderList.get(0);
        if(shopOrder.getOrderState()==0){//避免系统定时任务取消订单 订单可以继续支付
            return ApiUtils.error("订单已超时");
        }
        System.out.println("##########################################");
        System.out
            .println("###  订单支付编号：" + paysn + "  |  支付方式名称：" + paymentCode + " |  支付方式索引id：" + paymentId + "#########");
        System.out.println("##########################################");
        ShopOrderPay pay = orderPayService.findBySn(paysn);

        //货到付款判断
        if (paymentType == 2) {
            if (pay.getPaymentType() != 2) {
                return ApiUtils.error("该订单不是货到付款,请选择支付方式");
            }
        }
        PayCommon payCommon = new PayCommon();
        payCommon.setOutTradeNo(pay.getPaySn());
        if ("balancePaymentPlugin".equals(paymentCode)) {
            payCommon.setPayAmount(pay.getPayAmount());
        } else {
            //payCommon.setPayAmount(new BigDecimal(0.01));
            payCommon.setPayAmount(pay.getPayAmount());
        }
        payCommon.setTitle("订单支付");
        payCommon.setBody(pay.getPaySn() + "订单支付");
        if (paymentCode.equals("alipayMobilePaymentPlugin")){
            payCommon.setNotifyUrl(NotifyConsts.ADMIN_NOTIFY_FILE+ "/admin/paynotify/alipayNotify/"+paysn+".jhtml");
        }else {
            payCommon.setNotifyUrl(server + "/api/paynotify/notifyMobile/" + paymentCode + "/" + paysn + ".json");
        }
        payCommon.setReturnUrl(server + "/payment/payfront");
        String sHtmlText = "";
        Map<String, Object> model = new HashMap<String, Object>();
        if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("alipayMobilePaymentPlugin")) {
            orderService.updateByPaySn(paysn, Long.valueOf(paymentId));
            //保存支付流水记录
            System.out.println("dd:" + PaymentTallyState.PAYMENTTALLY_TREM_PC);
            paymentTallyService.savePaymentTally(paymentCode, "支付宝", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 1);
            //修改订单付款信息
            sHtmlText = alipayMobileService.toPay(payCommon);//TODO
            model.put("tocodeurl", sHtmlText);
            model.put("orderSn", pay.getOrderSn());
        } else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("YL")) {
            //修改订单付款信息
            orderService.updateByPaySn(paysn, Long.valueOf(paymentId));
            //保存支付流水记录
            paymentTallyService.savePaymentTally(paymentCode, "银联", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 1);
            sHtmlText = unionpayService.prePay(payCommon, request);//构造提交银联的表单
            model.put("tocodeurl", sHtmlText);
            model.put("orderSn", pay.getOrderSn());
        } else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("weixinMobilePaymentPlugin")) {
            //修改订单付款信息
            orderService.updateByPaySn(paysn, Long.valueOf(paymentId));
            //保存支付流水记录
            paymentTallyService.savePaymentTally(paymentCode, "微信支付", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 1);
            String tocodeurl = wechatMobileService.toPay(payCommon);//微信扫码url
            model.put("tocodeurl", tocodeurl);
            model.put("orderSn", pay.getOrderSn());
        } else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("weixinAppletsPaymentPlugin")) {
            //修改订单付款信息
            orderService.updateByPaySn(paysn, Long.valueOf(paymentId));
            //保存支付流水记录
            paymentTallyService.savePaymentTally(paymentCode, "微信小程序支付", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 1);
            //String tocodeurl = wechatMobileService.toPay(payCommon);//微信扫码url
            model.put("tocodeurl", "");
            model.put("orderSn", pay.getOrderSn());
        } else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("balancePaymentPlugin")) {//余额支付
//            Map<String, Object> data = orderService.payWallet(payCommon, member.getMmCode());
//            model.putAll(data);
        } else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("pointsPaymentPlugin")) {//积分全额支付
            // TODO: 2018/12/18
            //积分全额支付判断
            if (paymentCode.equals("pointsPaymentPlugin")) {
                if (pay.getPayAmount().compareTo(new BigDecimal(0)) != 0) {
                    return ApiUtils.error("该订单不符合购物积分全抵现,请选择支付方式");
                }
            }
            paymentTallyService.savePaymentTally(paymentCode, "积分全抵扣", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 2);
            Map<String, Object> data = orderService
                .updateOrderpay(payCommon, member.getMmCode(), "在线支付-购物积分", paymentCode, paymentId);
            model.putAll(data);
        } else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("cashOnDeliveryPlugin")) {//货到付款
            // TODO: 2018/12/18
            paymentTallyService.savePaymentTally(paymentCode, "货到付款", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 2);
            Map<String, Object> data = orderService
                .updateOrderpay(payCommon, member.getMmCode(), "货到付款", paymentCode, paymentId);
            model.putAll(data);
        }

        return ApiUtils.success(model);
    }


    /**
     * 去付款
     *
     * @param paysn 支付订单编码
     * @param paymentCode 支付代码名称: ZFB YL weiscan
     * @param paymentId 支付方式索引id
     * @param integration 积分
     * @param paypassword 支付密码
     * @param paymentType 1在线支付 2货到付款
     */
    @RequestMapping("/api/order/payNew")
    @ResponseBody
    public String payOrderNew(@RequestParam(value = "paysn") String paysn,
                              @RequestParam(defaultValue = "pointsPaymentPlugin") String paymentCode,
                              @RequestParam(defaultValue = "0") String paymentId,
                              @RequestParam(defaultValue = "0") String integration,
                              //@RequestParam(defaultValue = "0") Integer integration,
                              @RequestParam(defaultValue = "0") String paypassword,
                              @RequestParam(defaultValue = "1") Integer paymentType,
                              @RequestParam(defaultValue = "0") String openId,
                              @RequestParam(defaultValue = "0") Integer type,
                           HttpServletRequest request) {
        if(integration==null&&"".equals(integration)){
            return ApiUtils.error("请输入支付的积分金额");
        }
        BigDecimal i = new BigDecimal("0.00");
        if (integration==null||"".equals(integration)){
            i = new BigDecimal("0.00");
        }else {
            i = new BigDecimal(integration).setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo shopMember = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        //处理购物积分
        //获取购物积分购物比例
        List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
                .findList(Paramap.create().put("order", "RID desc"));
        RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
        if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
            rdMmIntegralRule = rdMmIntegralRuleList.get(0);
        }
        int shoppingPointSr = Optional.ofNullable(rdMmIntegralRule.getShoppingPointSr()).orElse(0);
        //if (integration != 0) {
        if (i.compareTo(new BigDecimal("0.00")) != 0) {
            if (rdMmAccountInfo.getPaymentPwd() == null) {
                return ApiUtils.error(80002,"你还未设置支付密码");
            }
            if (!Digests.validatePassword(paypassword, rdMmAccountInfo.getPaymentPwd())) {
                return ApiUtils.error(80001,"支付密码错误");
            }
            if (rdMmAccountInfo.getWalletStatus() != 0) {
                return ApiUtils.error("购物积分账户状态未激活或者已被冻结");
            }
            ShopOrderPay pay = orderPayService.findBySn(paysn);
            //处理积分支付
            orderService.ProcessingIntegrals(paysn, i, shopMember, pay, shoppingPointSr);
            //orderService.ProcessingIntegrals(paysn, integration, shopMember, pay, shoppingPointSr);
        }
        List<ShopOrder> orderList = orderService.findList("paySn", paysn);
        if (CollectionUtils.isEmpty(orderList)) {
            return ApiUtils.error("订单不存在");
        }
        ShopOrder shopOrder = orderList.get(0);
        if(shopOrder.getOrderState()==0){//避免系统定时任务取消订单 订单可以继续支付
            return ApiUtils.error("订单已超时");
        }
        System.out.println("##########################################");
        System.out
                .println("###  订单支付编号：" + paysn + "  |  支付方式名称：" + paymentCode + " |  支付方式索引id：" + paymentId + "#########");
        System.out.println("##########################################");
        ShopOrderPay pay = orderPayService.findBySn(paysn);

        //货到付款判断
        if (paymentType == 2) {
            if (pay.getPaymentType() != 2) {
                return ApiUtils.error("该订单不是货到付款,请选择支付方式");
            }
        }
        PayCommon payCommon = new PayCommon();
        payCommon.setOutTradeNo(pay.getPaySn());
        if ("balancePaymentPlugin".equals(paymentCode)) {
            payCommon.setPayAmount(pay.getPayAmount());
        } else {
            //payCommon.setPayAmount(new BigDecimal(0.01));
            payCommon.setPayAmount(pay.getPayAmount());
        }
        payCommon.setTitle("订单支付");
        payCommon.setBody(pay.getPaySn() + "订单支付");
        if (paymentCode.equals("alipayMobilePaymentPlugin")){
            payCommon.setNotifyUrl(NotifyConsts.ADMIN_NOTIFY_FILE+ "/admin/paynotify/alipayNotify/"+paysn+".jhtml");
        }else {
            payCommon.setNotifyUrl(server + "/api/paynotify/notifyMobile/" + paymentCode + "/" + paysn + ".json");
        }
        payCommon.setReturnUrl(server + "/payment/payfront");
        payCommon.setType(type);
        String sHtmlText = "";
        Map<String, Object> model = new HashMap<String, Object>();
        if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("alipayMobilePaymentPlugin")) {
            orderService.updateByPaySn(paysn, Long.valueOf(paymentId));
            //保存支付流水记录
            System.out.println("dd:" + PaymentTallyState.PAYMENTTALLY_TREM_PC);
            paymentTallyService.savePaymentTally(paymentCode, "支付宝", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 1);
            //修改订单付款信息
            sHtmlText = alipayMobileService.toPay(payCommon);//TODO
            model.put("tocodeurl", sHtmlText);
            model.put("orderSn", pay.getOrderSn());
        } else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("YL")) {
            //修改订单付款信息
            orderService.updateByPaySn(paysn, Long.valueOf(paymentId));
            //保存支付流水记录
            paymentTallyService.savePaymentTally(paymentCode, "银联", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 1);
            sHtmlText = unionpayService.prePay(payCommon, request);//构造提交银联的表单
            model.put("tocodeurl", sHtmlText);
            model.put("orderSn", pay.getOrderSn());
        } else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("weixinMobilePaymentPlugin")) {
            //修改订单付款信息
            orderService.updateByPaySn(paysn, Long.valueOf(paymentId));
            //保存支付流水记录
            paymentTallyService.savePaymentTally(paymentCode, "微信支付", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 1);
            if (type==1){
                payCommon.setOpenId(openId);
            }
            String tocodeurl = wechatMobileService.toPay(payCommon);//微信扫码url
            model.put("tocodeurl", tocodeurl);
            model.put("orderSn", pay.getOrderSn());
        } else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("weixinH5PaymentPlugin")) {
            //修改订单付款信息
            orderService.updateByPaySn(paysn, Long.valueOf(paymentId));
            //保存支付流水记录
            paymentTallyService.savePaymentTally(paymentCode, "微信公众号", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 1);
            if (type==1){
                payCommon.setOpenId(openId);
            }
            String tocodeurl = wechatH5Service.toPay(payCommon);// todo 小程序H5
            model.put("tocodeurl", tocodeurl);
            model.put("orderSn", pay.getOrderSn());
        } else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("weixinAppletsPaymentPlugin")) {
            //修改订单付款信息
            orderService.updateByPaySn(paysn, Long.valueOf(paymentId));
            //保存支付流水记录
            paymentTallyService.savePaymentTally(paymentCode, "微信小程序支付", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 1);
            //String tocodeurl = wechatMobileService.toPay(payCommon);//微信扫码url
            model.put("tocodeurl", "");
            model.put("orderSn", pay.getOrderSn());
        } else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("balancePaymentPlugin")) {//余额支付
//            Map<String, Object> data = orderService.payWallet(payCommon, member.getMmCode());
//            model.putAll(data);
        } else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("pointsPaymentPlugin")) {//积分全额支付
            // TODO: 2018/12/18
            //积分全额支付判断
            if (paymentCode.equals("pointsPaymentPlugin")) {
                if (pay.getPayAmount().compareTo(new BigDecimal(0)) != 0) {
                    return ApiUtils.error("该订单不符合购物积分全抵现,请选择支付方式");
                }
            }
            paymentTallyService.savePaymentTally(paymentCode, "积分全抵扣", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 2);
            Map<String, Object> data = orderService
                    .updateOrderpay(payCommon, member.getMmCode(), "在线支付-购物积分", paymentCode, paymentId);
            model.putAll(data);
        } else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("cashOnDeliveryPlugin")) {//货到付款
            // TODO: 2018/12/18
            paymentTallyService.savePaymentTally(paymentCode, "货到付款", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 2);
            Map<String, Object> data = orderService
                    .updateOrderpay(payCommon, member.getMmCode(), "货到付款", paymentCode, paymentId);
            model.putAll(data);
        }

        return ApiUtils.success(model);
    }

    /**
     * TODO 要更新 跳转到申请售后
     */
    @RequestMapping("/api/order/refundReturn/forward")
    @ResponseBody
    public String toApplyRefundReturn(Pageable pager, HttpServletRequest request, Long orderId) throws Exception {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if (orderId == null) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        ShopOrder shopOrder = orderService.find(orderId);
        if (shopOrder.getOrderType() == 5) {
            return ApiUtils.error("换购订单不支持售后");
        }
        if (shopOrder.getOrderType() == 6) {
            return ApiUtils.error("售后订单不支持售后");
        }

        Paramap paramap = Paramap.create()
            .put("buyerId", member.getMmCode())
            .put("orderState", OrderState.ORDER_STATE_FINISH)
            .put("id", orderId);
        pager.setParameter(paramap);
        Page<ShopOrderVo> orderPage = orderService.listWithGoods(pager);
//        List<ShopReturnOrderGoods> shopReturnOrderGoods=shopReturnOrderGoodsService.find("orderId",orderId)
        List<ShopRefundReturn> shopRefundReturnList = refundReturnService
            .findList(Paramap.create().put("buyerId", member.getMmCode()).put("orderId", orderId));
        return ApiUtils
            .success(ApplyRefundReturnResult.buildList(orderPage.getContent(), orderId, shopRefundReturnList));
    }

    /**
     * 申请售后
     */
    @RequestMapping("/api/order/refundReturn")
    @ResponseBody
    public String applyRefundReturn(@Valid RefundReturnParam param, String goodsSpecInfo, BindingResult vResult,
        HttpServletRequest request) {
        if (vResult.hasErrors()) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        if (StringUtils.isBlank(goodsSpecInfo)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        List<ShopOrderGoods> shopOrderGoodsList = com.alibaba.fastjson.JSONArray
            .parseArray(goodsSpecInfo, ShopOrderGoods.class);

        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Paramap paramap = Paramap.create();
        paramap.put("buyerId", member.getMmCode());
        paramap.put("orderId", param.getOrderId());
        //当审核为不同意时可重复申请
        paramap.put("sellerState", RefundReturnState.SELLER_STATE_CONFIRM_AUDIT);
        long refundReturns = refundReturnService.findList(paramap).size();
        if (refundReturns > 0) {
            return ApiUtils.error("有商品还在审核,请勿重复提交");
        }
        Long refundReturnId = orderService.addOrderRefundReturn(Long.parseLong(member.getMmCode()),
            param.getBuyerMessage(), param.getGoodsImageMore(), param.getType(), shopOrderGoodsList,
            param.getBrandName());
        return ApiUtils.success(Paramap.create().put("refundReturnId", refundReturnId));
    }

    /**
     * 售后列表
     */
    @RequestMapping("/api/order/refundReturn/list")
    @ResponseBody
    public String refundReturnList(HttpServletRequest request, Pageable pager) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        pager.setParameter(Paramap.create().put("buyerId", member.getMmCode()));
        pager.setOrderProperty("create_time");
        pager.setOrderDirection(Order.Direction.DESC);
        Page<ReturnGoodsVo> refundReturnPage = refundReturnService.listWithGoods(pager);
        ////后期业务需求项目不同可删除该处
        //List<ReturnGoodsVo> returnGoodsVoList=refundReturnPage.getContent();
        //List<Long> orderIds
        // if (returnGoodsVoList!=null && returnGoodsVoList.size()>0){
        //
        // }
        return ApiUtils.success(OrderResult.buildList2(refundReturnPage.getContent()));
    }

    /**
     * 售后订单详情
     */
    @RequestMapping(value = "/api/order/refundReturn/detail", method = RequestMethod.POST)
    @ResponseBody
    public String detailRefundReturn(@RequestParam Long orderId, HttpServletRequest request) throws Exception {
        if (orderId == null) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        ShopRefundReturn results = refundReturnService.find(orderId);
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if (!member.getMmCode().equals(results.getBuyerId() + "")) {
            return ApiUtils.error(Xerror.SYSTEM_ILLEGALITY);
        }
        Pageable pager = new Pageable();
        pager.setParameter(Paramap.create().put("id", orderId));
        Page<ReturnGoodsVo> refundReturnPage = refundReturnService.listWithGoods1(pager);
        System.out.println(refundReturnPage.getContent());
        //Page<ReturnGoodsVo> refundReturnPage = refundReturnService.listWithGoods(pager);
        RdMmAddInfo shopMemberAddress = rdMmAddInfoService.find("aid", 0);
        return ApiUtils.success(RefundOrderResult.buildList(refundReturnPage.getContent(), shopMemberAddress));
    }

    /**
     * 确认售后信息
     *
     * @param expressName 物流公司名称
     * @param invoiceNo 物流单号
     */
    @RequestMapping(value = "/api/order/refundReturn/finish", method = RequestMethod.POST)
    @ResponseBody
    public String finishRefundReturn(@RequestParam Long orderId, @RequestParam String expressName,
        @RequestParam String invoiceNo, HttpServletRequest request) throws Exception {
        if (orderId == null || StringUtil.isEmpty(expressName) || StringUtil.isEmpty(invoiceNo)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        ShopRefundReturn results = refundReturnService.find(orderId);
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if (!member.getMmCode().equals(results.getBuyerId()+"")) {
            return ApiUtils.error(Xerror.SYSTEM_ILLEGALITY);
        }
//        ShopCommonExpress eCode = commonExpressService.find("eName", expressName);
//        if (eCode==null){
//            return ApiUtils.error("没有该物流公司信息,请输入正确物流信息");
//        }
//        String kuaiInfo = kuaidiService.query(eCode.getECode(), invoiceNo);
//        Map mapType = JacksonUtil.convertMap(kuaiInfo);
//        if (StringUtils.isBlank(kuaiInfo) || "false".equals(mapType.get(""))) {
//            return ApiUtils.error("没有该物流信息,请输入正确物流信息");
//        }
        results.setExpressName(expressName);
        results.setInvoiceNo(invoiceNo);
//        results.setExpressCode(eCode.getECode());
        refundReturnService.update(results);
        return ApiUtils.success();
    }

    /**
     * 批量评价商品
     */
    @RequestMapping("/api/order/evaluate/saveBatchEvaluate.json")
    @ResponseBody
    public String saveBatchEvaluate(String evaluateJson, HttpServletRequest request) {
        if (StringUtil.isEmpty(evaluateJson)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo shopMember = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        String successTipWolds = shopGoodsEvaluateService.saveBatchEvaluate(evaluateJson, shopMember);
        return ApiUtils.success();
    }

    /**
     * TODO 要更新 跳转到评价
     */
    @RequestMapping("/api/order/evaluate/forward")
    @ResponseBody
    public String toApplyEvaluate(Pageable pager, HttpServletRequest request, Long orderId) throws Exception {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if (orderId == null) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        Paramap paramap = Paramap.create()
            .put("buyerId", member.getMmCode())
            .put("orderId", orderId);
        pager.setParameter(paramap);
        ShopOrder shopOrder = orderService.find(orderId);
        Page<ShopOrderGoods> shopOrderGoodsPage = orderGoodsService.findByPage(pager);
        return ApiUtils.success(EvaluateOrderGoodsResult.build(shopOrderGoodsPage.getContent(), shopOrder));
    }


    @RequestMapping("/api/order/evaluate/forwardTwo")
    @ResponseBody
    public String toApplyEvaluate2(Pageable pager, HttpServletRequest request, Long orderId) throws Exception {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if (orderId == null) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        Paramap paramap = Paramap.create()
            .put("buyerId", member.getMmCode())
            .put("orderId", orderId);
        pager.setParameter(paramap);
        ShopOrder shopOrder = orderService.find(orderId);
        Page<ShopOrderGoods> shopOrderGoodsPage = orderGoodsService.findByPage(pager);
        List<ShopGoodsEvaluate> evaluateList = shopGoodsEvaluateService.findByOrderId(orderId);
        return ApiUtils.success(EvaluateOrderGoodsResult.build1(shopOrderGoodsPage.getContent(), shopOrder, evaluateList));
    }

//
//    /**
//     * 评价
//     */
//    @RequestMapping("/api/order/evaluation")
//    @ResponseBody
//    public String showRating(HttpServletRequest request, @RequestParam("orderId") long orderId) {
//        Paramap paramap = Paramap.create();
//        paramap.put("orderId", orderId);
//        paramap.put("orderState", OrderState.ORDER_STATE_FINISH);
//        Long count = orderService.count(paramap);
//        if (count == null || count.intValue() <= 0) {
//            return ApiUtils.error(Xerror.PARAM_INVALID);
//        }
//        return ApiUtils.success(EvaluationCenterGoodsResult.buildList(orderGoodsService.findList("orderId", orderId)));
//    }

    /**
     * 提交积分订单
     */
    @RequestMapping(value = "/api/replacementOrder/submit")
    @ResponseBody
    public String submitReplacementOrder(@Valid CartAddParam param, BindingResult vResult, HttpServletRequest request) {
        if (vResult.hasErrors()) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        //  自提地址 自提地址 id为-1 表示平台地址
        RdMmAddInfo shopMemberAddress = rdMmAddInfoService.find("aid", -1);
        String contactName = "";
        String contactPhone = "";
        String contactAddrInfo = "";
        String contactAddrProvice = "";
        String contactAddrDetail = "";
        if (shopMemberAddress != null) {
            contactName = (Optional.ofNullable(shopMemberAddress.getConsigneeName()).orElse("后台还未设置"));
            contactPhone = (Optional.ofNullable(shopMemberAddress.getMobile()).orElse("后台还未设置"));
            contactAddrInfo = (Optional.ofNullable(
                    shopMemberAddress.getAddProvinceCode() + shopMemberAddress.getAddCityCode() + shopMemberAddress
                            .getAddCountryCode()).orElse("后台还未设置") + Optional.ofNullable(shopMemberAddress.getAddDetial())
                    .orElse(""));
            contactAddrProvice = (Optional.ofNullable(
                    shopMemberAddress.getAddProvinceCode() + shopMemberAddress.getAddCityCode() + shopMemberAddress
                            .getAddCountryCode()).orElse("后台还未设置"));
            contactAddrDetail=Optional.ofNullable(shopMemberAddress.getAddDetial()).orElse("");
        } else {
            contactName = ("后台还未设置");
            contactPhone = ("后台还未设置");
            contactAddrInfo = ("后台还未设置");
            contactAddrProvice = ("后台还未设置");
            contactAddrDetail = ("");
        }
        List<RdMmAddInfo> addrList = rdMmAddInfoService.findList("mmCode", member.getMmCode());
        RdMmAddInfo addr = new RdMmAddInfo();
        Integer hadReceiveAddr=null;
        if (CollectionUtils.isNotEmpty(addrList)) {
            addr = addrList.stream()
                    .filter(item -> item.getDefaultadd() != null && item.getDefaultadd() == 1)
                    .findFirst()
                    .orElse(addrList.get(0));
            hadReceiveAddr=1;
        } else {
            addr.setAid(-1);
            hadReceiveAddr=2;
        }
        ShopOrderPay orderPay = orderService
            .addReplacementOrder(param.getGoodsId(), param.getCount(), param.getSpecId(),
                Long.parseLong(member.getMmCode()),addr);
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        ShopGoods goods = shopGoodsService.find(param.getGoodsId());
        ShopGoodsSpec goodsSpec = shopGoodsSpecService.find(param.getSpecId());
        GoodsUtils.getSepcMapAndColImgToGoodsSpec(goods, goodsSpec);
        ShopCartExchange cart = new ShopCartExchange();
        cart.setId(twiterIdService.getTwiterId());
        cart.setMemberId(Long.parseLong(member.getMmCode()));
        cart.setGoodsId(goodsSpec.getGoodsId());
        cart.setGoodsName(goods.getGoodsName());
        cart.setSpecId(goodsSpec.getId());
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
        cart.setGoodsType(goods.getGoodsType());
        //设置价格
        cart.setGoodsMemberPrice(goodsSpec.getSpecMemberPrice());
        cart.setGoodsBigPrice(goodsSpec.getSpecBigPrice());
        cart.setGoodsRetailPrice(goodsSpec.getSpecRetailPrice());
        cart.setBigPpv(goodsSpec.getBigPpv());
        cart.setPpv(goodsSpec.getPpv());
        cart.setGoodsState(goods.getGoodsType());
        cart.setBrandId(goods.getBrandId());
        ShopGoodsBrand shopGoodsBrand = shopGoodsBrandService.find(goods.getBrandId());
        cart.setBrandIcon(shopGoodsBrand.getBrandIcon());
        cart.setBrandName(goods.getBrandName());
        cart.setWeight(goodsSpec.getWeight());
        // 图片信息
        if (goods.getGoodsImage() != null) {
            //存储商品默认图片
            cart.setGoodsImages(goods.getGoodsImage().split(",")[0]);
        } else {
            //若商品没有默认图片存储空字段
            cart.setGoodsImages("");
        }
        cart.setGoodsNum(param.getCount());
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        ShopGoodsFreightRule shopGoodsFreightRule = shopGoodsFreightRuleService.find("memberGradeId",rdMmRelation.getRank());
        return ApiUtils.success(Paramap.create().put("goodsintegration", orderPay.getPayAmount())
            .put("redemptionBlance",
                Optional.ofNullable(rdMmAccountInfo.getRedemptionBlance()).orElse(BigDecimal.valueOf(0)))
            .put("contactName", contactName).put("contactPhone", contactPhone).put("contactAddrInfo", contactAddrInfo)
            .put("paySn", orderPay.getPaySn()).put("ismodify", 1)
            .put("orderId", orderPay.getOrderId()).put("addr", addr).put("hadReceiveAddr",hadReceiveAddr)
        .put("shippingFee",orderPay.getShippingFee().subtract(orderPay.getShippingPreferentialFee())).put("goodsNum",param.getCount())
        .put("goodsTotal",orderPay.getPayAmount().subtract(orderPay.getShippingFee()).add(orderPay.getShippingPreferentialFee()))
        .put("goodsInfo",cart).put("packageAmount",shopGoodsFreightRule.getMinimumOrderAmount()).put("specInfo",goodsSpec)
        .put("contactAddrProvice",contactAddrProvice).put("contactAddrDetail",contactAddrDetail));
    }

    /**
     * 支付积分订单
     *
     * @param paySn 支付编号
     * @param integration 积分
     * @param paypassword 支付密码
     * @param logisticType 1快递 2自提
     * @param addressId 收货地址id
     */
    @RequestMapping(value = "/api/replacementOrder/pay")
    @ResponseBody
    public String payReplacementOrder(HttpServletRequest request,
        @RequestParam(defaultValue = "0") Double integration,
        @RequestParam(defaultValue = "0") String paySn,
        @RequestParam(defaultValue = "0") Long orderId,
        @RequestParam(defaultValue = "0") String paypassword,
        @RequestParam(defaultValue = "2") Integer logisticType,
        @RequestParam(defaultValue = "0") Long addressId,
        @RequestParam(defaultValue = "0") Integer ismodify,
        @RequestParam(defaultValue = "0") String userName,
        @RequestParam(defaultValue = "0") String userPhone
    ) {
        String paymentCode = "replacementPaymentPlugin";
        String paymentId = "10";
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo shopMember = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        if (rdMmAccountInfo.getPaymentPwd() == null) {
            return ApiUtils.error("你还未设置支付密码");
        }
        if (!Digests.validatePassword(paypassword, rdMmAccountInfo.getPaymentPwd())) {
            return ApiUtils.error("支付密码错误");
        }

        if (rdMmAccountInfo.getRedemptionBlance().compareTo(BigDecimal.valueOf(integration)) == -1) {
            return ApiUtils.error("可用积分不够");
        }
        ShopOrderPay pay = orderPayService.findBySn(paySn);
        PayCommon payCommon = new PayCommon();
        payCommon.setOutTradeNo(pay.getPaySn());
        ShopOrder order = orderService.find(orderId);
        if (order == null) {
            return ApiUtils.error("订单不存在");
        }
        if (order.getOrderState() != 10) {
            return ApiUtils.error("订单已支付");
        }
        BigDecimal price = order.getOrderAmount();

        if (price.compareTo(BigDecimal.valueOf(integration)) != 0) {
            return ApiUtils.error("积分数量不正确");
        }
        ShopOrderAddress orderAddress = new ShopOrderAddress();
        //可用修改 并且 选择了快提 默认是自提无需修改
        Long orderAddressId = twiterIdService.getTwiterId();
        order.setAddressId(orderAddressId);
        if (ismodify == 1 && logisticType == 1) {

            order.setLogisticType(1);

        //    RdMmAddInfo address = null;
        //    /*********************保存发货地址*********************/
        //    address = rdMmAddInfoService.find("aid", addressId);
        //    if (address == null) {
        //        throw new StateResult(AppConstants.RECEIVED_ADDRESS_NOT_EXIT, "收货地址不能为空");
        //    }
        //    BeanUtils.copyProperties(address, orderAddress);
        //    orderAddress.setIsDefault(Optional.ofNullable(address.getDefaultadd()).orElse(0).toString());
        //    orderAddress.setId(orderAddressId);
        //
        //    orderService.update(order);
        //} else {
        //    orderAddress.setMemberId(Long.parseLong(member.getMmCode()));
        //    orderAddress.setIsDefault("0");
        //    orderAddress.setTrueName(userName);
        //    orderAddress.setMobPhone(userPhone);
        //    orderAddress.setAreaInfo("自提没有保存收货地址");
        //    orderAddress.setId(twiterIdService.getTwiterId());
        //    orderAddress.setAreaId(-1L);
        //    orderAddress.setCityId(-1L);
        //    orderAddress.setProvinceId(-1L);
        //    orderAddress.setAddress("");
        //}
            //1快递 2自提
            RdMmAddInfo address = null;
                /*********************保存发货地址*********************/
                address = rdMmAddInfoService.find("aid", addressId);
                if (address == null) {
                    throw new StateResult(AppConstants.RECEIVED_ADDRESS_NOT_EXIT, "收货地址不能为空");
                }
                orderAddress.setIsDefault(Optional.ofNullable(address.getDefaultadd()).orElse(0).toString());
                orderAddress.setId(orderAddressId);
                orderAddress.setMemberId(Long.parseLong(address.getMmCode()));
                orderAddress.setTrueName(address.getConsigneeName());
                orderAddress.setAddress(address.getAddDetial());
                orderAddress.setMobPhone(address.getMobile());
                orderAddress
                    .setAreaInfo(address.getAddProvinceCode() + address.getAddCityCode() + address.getAddCountryCode());

                /*ShopCommonArea shopCommonArea = areaService.find("areaName", address.getAddCountryCode());
                orderAddress.setAreaId(shopCommonArea.getId());
                orderAddress.setCityId(shopCommonArea.getAreaParentId());
                ShopCommonArea shopCommonArea2 = areaService.find(shopCommonArea.getAreaParentId());
                orderAddress.setProvinceId(shopCommonArea2.getAreaParentId());*/

            if ("".equals(address.getAddCountryCode())){
                ShopCommonArea shopCommonArea = areaService.find("areaName", address.getAddCityCode());
                if (shopCommonArea==null) {
                    throw new RuntimeException("请检查APP是否最新版本，并重新添加地址");
                }
                if (shopCommonArea.getExpressState()==1){//不配送
                    throw new StateResult(AppConstants.RECEIVED_ADDRESS_NOT_EXPRESS, "该收货地址暂不配送");
                }
                orderAddress.setAreaId(shopCommonArea.getId());
                orderAddress.setCityId(shopCommonArea.getId());
                orderAddress.setProvinceId(shopCommonArea.getAreaParentId());
            }else{
                List<ShopCommonArea> shopCommonAreas = areaService.findByAreaName(address.getAddCountryCode());//区
                if (CollectionUtils.isEmpty(shopCommonAreas)) {
                    throw new RuntimeException("请检查APP是否最新版本，并重新添加地址");
                }
                if (shopCommonAreas.size()>1){
                    List<ShopCommonArea> shopCommonCitys = areaService.findByAreaName(address.getAddCityCode());//市
                    if (shopCommonCitys==null) {
                        throw new RuntimeException("请检查APP是否最新版本，并重新添加地址");
                    }
                    if (shopCommonCitys.size()==1){
                        ShopCommonArea shopCommonCity = shopCommonCitys.get(0);
                        orderAddress.setCityId(shopCommonCity.getId());
                        orderAddress.setProvinceId(shopCommonCity.getAreaParentId());
                        for (ShopCommonArea shopCommonArea : shopCommonAreas) {
                            if (shopCommonArea.getAreaParentId().longValue()==shopCommonCity.getId().longValue()){
                                orderAddress.setAreaId(shopCommonArea.getId());
                                if (shopCommonArea.getExpressState()==1){//不配送
                                    throw new StateResult(AppConstants.RECEIVED_ADDRESS_NOT_EXPRESS, "该收货地址不配送");
                                }
                            }
                        }
                    }else {
                        ShopCommonArea shopCommonProvice = areaService.find("areaName", address.getAddProvinceCode());//省
                        for (ShopCommonArea shopCommonCity : shopCommonCitys) {
                            if (shopCommonCity.getAreaParentId().longValue()==shopCommonProvice.getId().longValue()){
                                orderAddress.setCityId(shopCommonCity.getId());
                                orderAddress.setProvinceId(shopCommonCity.getAreaParentId());
                                for (ShopCommonArea shopCommonArea : shopCommonAreas) {
                                    if (shopCommonArea.getAreaParentId().longValue()==shopCommonCity.getId().longValue()){
                                        orderAddress.setAreaId(shopCommonArea.getId());
                                        if (shopCommonArea.getExpressState()==1){//不配送
                                            throw new StateResult(AppConstants.RECEIVED_ADDRESS_NOT_EXPRESS, "该收货地址不配送");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }else{
                    ShopCommonArea shopCommonArea = shopCommonAreas.get(0);
                    if (shopCommonArea.getExpressState()==1){//不配送
                        throw new StateResult(AppConstants.RECEIVED_ADDRESS_NOT_EXPRESS, "该收货地址不配送");
                    }
                    orderAddress.setAreaId(shopCommonArea.getId());
                    //if ()
                    orderAddress.setCityId(shopCommonArea.getAreaParentId());
                    ShopCommonArea shopCommonArea2 = areaService.find(shopCommonArea.getAreaParentId());
                    if (shopCommonArea2==null) {
                        throw new RuntimeException("请检查APP是否最新版本，并重新添加地址");
                    }
                    orderAddress.setProvinceId(shopCommonArea2.getAreaParentId());
                }
            }
        }
            if (logisticType == 2) {
                /*********************保存发货地址*********************/
                orderAddress.setMemberId(Long.parseLong(member.getMmCode()));
                orderAddress.setIsDefault("0");
                orderAddress.setTrueName(userName);
                orderAddress.setMobPhone(userPhone);
                orderAddress.setAreaInfo("自提没有保存收货地址");
                orderAddress.setId(orderAddressId);
                orderAddress.setAreaId(-1L);
                orderAddress.setCityId(-1L);
                orderAddress.setProvinceId(-1L);
                orderAddress.setAddress("");
            }
        orderService.update(order);
        shopOrderAddressService.save(orderAddress);
        Map<String, Object> data = orderService
            .updateOrderpay(payCommon, member.getMmCode(), "在线支付-换购积分", paymentCode, paymentId);
        return ApiUtils.success(Paramap.create().put("orderSn", order.getOrderSn())
            .put("redemptionBlance", rdMmAccountInfo.getRedemptionBlance().subtract(BigDecimal.valueOf(integration))));
    }

    /**
     * 去付款
     */
    @RequestMapping(value = "/api/replacementOrder/pay/forward", method = RequestMethod.POST)
    @ResponseBody
    public String payreplacementOrderForward(Long orderId, HttpServletRequest request) {
        if (orderId == null) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        ShopOrder shopOrder = orderService.find(orderId);
        if (shopOrder == null) {
            return ApiUtils.error("订单不存在");
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);

        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        //  自提地址 自提地址 id为-1 表示平台地址
        RdMmAddInfo shopMemberAddress = rdMmAddInfoService.find("aid", -1);
        String contactName = "";
        String contactPhone = "";
        String contactAddrInfo = "";
        if (shopMemberAddress != null) {
            contactName = (Optional.ofNullable(shopMemberAddress.getConsigneeName()).orElse("后台还未设置"));
            contactPhone = (Optional.ofNullable(shopMemberAddress.getMobile()).orElse("后台还未设置"));
            contactAddrInfo = (Optional.ofNullable(
                shopMemberAddress.getAddProvinceCode() + shopMemberAddress.getAddCityCode() + shopMemberAddress
                    .getAddCountryCode()).orElse("后台还未设置") + Optional.ofNullable(shopMemberAddress.getAddDetial())
                .orElse(""));
        } else {
            contactName = ("后台还未设置");
            contactPhone = ("后台还未设置");
            contactAddrInfo = ("后台还未设置");
        }
        List<RdMmAddInfo> addrList = rdMmAddInfoService.findList("mmCode", member.getMmCode());
        RdMmAddInfo addr = new RdMmAddInfo();
        if (CollectionUtils.isNotEmpty(addrList)) {
            addr = addrList.stream()
                .filter(item -> item.getDefaultadd() != null && item.getDefaultadd() == 1)
                .findFirst()
                .orElse(addrList.get(0));
        } else {
            addr.setAid(-1);
        }
        return ApiUtils.success(Paramap.create().put("goodsintegration", shopOrder.getOrderAmount())
            .put("redemptionBlance",
                Optional.ofNullable(rdMmAccountInfo.getRedemptionBlance()).orElse(BigDecimal.valueOf(0)))
            .put("contactName", contactName).put("contactPhone", contactPhone).put("contactAddrInfo", contactAddrInfo)
            .put("paySn", shopOrder.getPaySn()).put("ismodify", 1)
            .put("orderId", orderId).put("addr", addr));
    }

    @RequestMapping("/api/order/checkSuccessOrNo")
    @ResponseBody
    public String checkSuccessOrNo( HttpServletRequest request, String orderSn) throws Exception {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if (orderSn == null) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        Map<String,Object> map=orderService.checkSuccessOrNo(member.getMmCode(),orderSn);
        return ApiUtils.success(map);
    }

    /***************************通联接口***************************/

    /**
     * 通联
     * 去付款
     *
     * @param paysn 支付订单编码
     * @param paymentCode 支付代码名称: ZFB YL weiscan
     * @param paymentId 支付方式索引id
     * @param integration 积分
     * @param paypassword 支付密码
     * @param paymentType 1在线支付 2货到付款
     */
    @RequestMapping("/api/order/tl/pay")
    @ResponseBody
    public String payTlOrder(@RequestParam(value = "paysn") String paysn,
                           @RequestParam(defaultValue = "pointsPaymentPlugin") String paymentCode,
                           @RequestParam(defaultValue = "0") String paymentId,
                           @RequestParam(defaultValue = "0") String integration,
                           //@RequestParam(defaultValue = "0") Integer integration,
                           @RequestParam(defaultValue = "0") String paypassword,
                           @RequestParam(defaultValue = "1") Integer paymentType,
                           HttpServletRequest request) {
        if(integration==null&&"".equals(integration)){
            return ApiUtils.error("请输入支付的积分金额");
        }
        BigDecimal i = new BigDecimal("0.00");
        if (integration==null||"".equals(integration)){
            i = new BigDecimal("0.00");
        }else {
            i = new BigDecimal(integration).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        /*try {
            String[] strings = integration.split("\\.");
            String string = strings[0];
            i = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ApiUtils.error("输入积分数额有误");
        }*/
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo shopMember = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        //处理购物积分
        //获取购物积分购物比例
        List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
                .findList(Paramap.create().put("order", "RID desc"));
        RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
        if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
            rdMmIntegralRule = rdMmIntegralRuleList.get(0);
        }
        int shoppingPointSr = Optional.ofNullable(rdMmIntegralRule.getShoppingPointSr()).orElse(0);
        //if (integration != 0) {
        if (i.compareTo(new BigDecimal("0.00")) != 0) {
            if (rdMmAccountInfo.getPaymentPwd() == null) {
                return ApiUtils.error("你还未设置支付密码");
            }
            if (!Digests.validatePassword(paypassword, rdMmAccountInfo.getPaymentPwd())) {
                return ApiUtils.error("支付密码错误");
            }
            if (rdMmAccountInfo.getWalletStatus() != 0) {
                return ApiUtils.error("购物积分账户状态未激活或者已被冻结");
            }
            ShopOrderPay pay = orderPayService.findBySn(paysn);
            //处理积分支付
            orderService.ProcessingIntegrals(paysn, i, shopMember, pay, shoppingPointSr);
            //orderService.ProcessingIntegrals(paysn, integration, shopMember, pay, shoppingPointSr);
        }
        List<ShopOrder> orderList = orderService.findList("paySn", paysn);
        if (CollectionUtils.isEmpty(orderList)) {
            return ApiUtils.error("订单不存在");
        }

        System.out.println("##########################################");
        System.out
                .println("###  订单支付编号：" + paysn + "  |  支付方式名称：" + paymentCode + " |  支付方式索引id：" + paymentId + "#########");
        System.out.println("##########################################");
        ShopOrderPay pay = orderPayService.findBySn(paysn);

        //货到付款判断
        if (paymentType == 2) {
            if (pay.getPaymentType() != 2) {
                return ApiUtils.error("该订单不是货到付款,请选择支付方式");
            }
        }
        PayCommon payCommon = new PayCommon();
        payCommon.setOutTradeNo(pay.getPaySn());
        if ("balancePaymentPlugin".equals(paymentCode)) {
            payCommon.setPayAmount(pay.getPayAmount());
        } else {
            //payCommon.setPayAmount(new BigDecimal(0.01));
            payCommon.setPayAmount(pay.getPayAmount());
        }
        payCommon.setTitle("订单支付");
        payCommon.setBody(pay.getPaySn() + "订单支付");
        payCommon.setNotifyUrl(server + "/api/paynotify/notifyMobile/" + paymentCode + "/" + paysn + ".json");
        payCommon.setReturnUrl(server + "/payment/payfront");
        String sHtmlText = "";
        Map<String, Object> model = new HashMap<String, Object>();
        if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("alipayMobilePaymentPlugin")) {
            orderService.updateByPaySn(paysn, Long.valueOf(paymentId));
            //保存支付流水记录
            System.out.println("dd:" + PaymentTallyState.PAYMENTTALLY_TREM_PC);
            paymentTallyService.savePaymentTally(paymentCode, "支付宝", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 1);
            //修改订单付款信息
            sHtmlText = alipayMobileService.toPay(payCommon);//TODO
            model.put("tocodeurl", sHtmlText);
            model.put("orderSn", pay.getOrderSn());
        } else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("YL")) {
            //修改订单付款信息
            orderService.updateByPaySn(paysn, Long.valueOf(paymentId));
            //保存支付流水记录
            paymentTallyService.savePaymentTally(paymentCode, "银联", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 1);
            sHtmlText = unionpayService.prePay(payCommon, request);//构造提交银联的表单
            model.put("tocodeurl", sHtmlText);
            model.put("orderSn", pay.getOrderSn());
        } else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("weixinMobilePaymentPlugin")) {
            //修改订单付款信息
            orderService.updateByPaySn(paysn, Long.valueOf(paymentId));
            //保存支付流水记录
            paymentTallyService.savePaymentTally(paymentCode, "微信支付", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 1);
            String tocodeurl = wechatMobileService.toPay(payCommon);//微信扫码url
            model.put("tocodeurl", tocodeurl);
            model.put("orderSn", pay.getOrderSn());
        } else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("balancePaymentPlugin")) {//余额支付
//            Map<String, Object> data = orderService.payWallet(payCommon, member.getMmCode());
//            model.putAll(data);
        } else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("pointsPaymentPlugin")) {//积分全额支付
            // TODO: 2018/12/18
            //积分全额支付判断
            if (paymentCode.equals("pointsPaymentPlugin")) {
                if (pay.getPayAmount().compareTo(new BigDecimal(0)) != 0) {
                    return ApiUtils.error("该订单不符合购物积分全抵现,请选择支付方式");
                }
            }
            paymentTallyService.savePaymentTally(paymentCode, "积分全抵扣", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 2);
            Map<String, Object> data = orderService
                    .updateOrderpay(payCommon, member.getMmCode(), "在线支付-购物积分", paymentCode, paymentId);
            model.putAll(data);
        } else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("cashOnDeliveryPlugin")) {//货到付款
            // TODO: 2018/12/18
            paymentTallyService.savePaymentTally(paymentCode, "货到付款", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 2);
            Map<String, Object> data = orderService
                    .updateOrderpay(payCommon, member.getMmCode(), "货到付款", paymentCode, paymentId);
            model.putAll(data);
        }

        return ApiUtils.success(model);
    }



    /**
     * 通联接口 托管代收（支付）
     * 小程序付款
     *
     * @param paysn 支付订单编码
     * @param openId 微信返回的openId
     */
    @RequestMapping("/api/index/order/applets/pay")
    @ResponseBody
    public String appletsPayTL(@RequestParam(value = "paysn") String paysn,@RequestParam(value = "openId") String openId,HttpServletRequest request) {


        if (paysn==null || "".equals(paysn)){
            return ApiUtils.error("订单号不存在");
        }

        String[] split = paysn.split("WOMI");
        String mmPaySn = split[0];//这个才是我们的pansn

        System.out.println("*****************");
        System.out.println("paysn="+paysn);
        System.out.println("mmPaySn="+mmPaySn);
        System.out.println("*****************");

        //TODO  微信 通联
        List<ShopOrder> orderList = orderService.findList("paySn", mmPaySn);
        if (CollectionUtils.isEmpty(orderList)) {
            return ApiUtils.error("订单不存在");
        }else {
            ShopOrder shopOrder = orderList.get(0);
            Integer orderState = shopOrder.getOrderState();
            if (orderState!=10){
                return ApiUtils.error("订单已支付");
            }
        }

        //过去的作废
        List<RdBizPay> rdBizPayList = rdBizPayService.findByPaysn(mmPaySn);
        if(rdBizPayList.size()>0){
            rdBizPayService.updateStatus(mmPaySn);
        }

        RdBizPay rdBizPay = new RdBizPay();
        rdBizPay.setPaySn(mmPaySn);
        rdBizPay.setBizPaySn(paysn);
        rdBizPay.setInvalidStatus(1);
        rdBizPay.setCutPaySn("C"+mmPaySn);
        rdBizPayService.save(rdBizPay);

        ShopOrder shopOrder = orderList.get(0);
        BigDecimal orderAmount = shopOrder.getOrderAmount();
        //double b = orderAmount.doubleValue()*100;
        //Long oAmount = new Double(b).longValue();
        BigDecimal b= orderAmount.multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP);
        Long oAmount = b.longValue();
        //收款列表
        Map<String, Object> reciever = new LinkedHashMap<>();
        //查询一个满足条件的收款人，扣除积分，返回用户编号以及分账金额
        if(shopOrder.getCutStatus()!=null&&shopOrder.getCutStatus()==5){
            //如果该订单以及扣除过推荐人积分预提现，先返回推荐人积分
            refundSpoPoint(shopOrder);
        }
        HashMap<String,Object> map=getCutNumber(shopOrder);
        RdMmAccountInfo accountInfo = (RdMmAccountInfo) map.get("accountInfo");
        BigDecimal acc = (BigDecimal) map.get("acc");
        BigDecimal ac= acc.multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP);
        //reciever.put("bizUserId", TongLianUtils.BIZ_USER_ID);//TODO
        reciever.put("bizUserId", accountInfo.getMmCode());//TODO
        //reciever.put("amount",shopOrder.getOrderAmount().longValue()*100); //TODO 正式
        reciever.put("amount",ac.longValue());
        List<Map<String, Object>> recieverList = new ArrayList<Map<String, Object>>();
        /*HashMap<String,Object> map=getCutNumber(shopOrder);
        RdMmAccountInfo accountInfo = (RdMmAccountInfo) map.get("accountInfo");
        BigDecimal acc = (BigDecimal) map.get("acc");
        double accdouble = acc.doubleValue() * 100;
        //reciever.put("bizUserId", TongLianUtils.BIZ_USER_ID);//TODO
        Long accL = new Double(accdouble).longValue();
        reciever.put("bizUserId", accountInfo.getMmCode());//TODO
        //reciever.put("amount",shopOrder.getOrderAmount().longValue()*100); //TODO 正式
        /*JSONObject reciever = new JSONObject();
        reciever.accumulate("bizUserId", TongLianUtils.BIZ_USER_ID);
        reciever.accumulate("amount",shopOrder.getOrderAmount().longValue()*100);*/
        //JSONArray recieverList = new JSONArray();
        recieverList.add(reciever);
        //支付方式
        Map<String, Object> object1 = new LinkedHashMap<>();
        //object1.put("limitPay","no_credit");//String 非贷记卡：no_credit 借、贷记卡：””需要传空字符串，不能不传
        object1.put("amount",oAmount);//Long支付金额，单位：分 TODO 正式
        //object1.put("amount",1l);//Long支付金额，单位：分
        object1.put("acct",openId);//String  微信 JS 支付 openid——微信分配
        object1.put("vspCusid","56029005999Z8RA");
        object1.put("subAppid","wx6e94bb18bedf3c4c");
        object1.put("limitPay","");
        Map<String, Object> payMethods = new LinkedHashMap<>();
        //payMethods.put("WECHATPAY_MINIPROGRAM",object1);
        payMethods.put("WECHATPAY_MINIPROGRAM_ORG",object1);
        /*JSONObject object1 = new JSONObject();
        object1.accumulate("limitPay","no_credit");//String 非贷记卡：no_credit 借、贷记卡：””需要传空字符串，不能不传
        object1.accumulate("amount",shopOrder.getOrderAmount().longValue()*100);//Long支付金额，单位：分
        object1.accumulate("acct",openId);//String  微信 JS 支付 openid——微信分配
        JSONObject payMethods = new JSONObject();
        payMethods.accumulate("WECHATPAY_MINIPROGRAM",object1);*/

        //String notifyUrl = server + "/api/paynotify/notifyMobile/" + "weixinAppletsPaymentPlugin" + "/" + mmPaySn + ".json";
        //String notifyUrl = NotifyConsts.APP_NOTIFY_FILE+ "/api/paynotify/notifyMobile/" + "weixinAppletsPaymentPlugin" + "/" + mmPaySn + ".json";
        String notifyUrl = NotifyConsts.ADMIN_NOTIFY_FILE+ "/admin/paynotify/notifyMobile.jhtml";
        //TODO 正式
        String s = TongLianUtils.agentCollectApply(paysn, shopOrder.getBuyerId().toString(), recieverList, 3l, "", "3001",
                oAmount, oAmount-(Long)reciever.get("amount"), 0l, "", notifyUrl, "",
                payMethods, "", "", "1910", "其他", 1l, "", "");

        /*String s = TongLianUtils.agentCollectApply(paysn, shopOrder.getBuyerId().toString(), recieverList, 3l, "", "3001",
                1l, 0l, 0l, "", notifyUrl, "",
                payMethods, "", "", "1910", "其他", 1l, "", "");*/

        /*
        response:{"sysid":"1908201117222883218",
                   "sign":"tgoTflSieEo5WPKacv0V3+jQHDtVtzub8H92D+5MK2MKNrZZxy5NikM5UqamA7Ic8/q5AQL/poyZSlMCQEY1VoJxrxyo3bn8KjqcfZtgoIVurl+uQs8xJYuRuZQd0HPDFpcuFFhVqJ/8Nww0lpfu2njK8K5c+zpwW8ZEI8JhLnc=",
                   "signedValue":"{\"orderNo\":\"1249642168751001600\",
                                   \"payInfo\":\"{\\\"appId\\\":\\\"wx6e94bb18bedf3c4c\\\",
                                                   \\\"timeStamp\\\":\\\"1586772871\\\",
                                                   \\\"nonceStr\\\":\\\"52a1b1f1d5054597ad358b843fcee1bb\\\",
                                                   \\\"package\\\":\\\"prepay_id=wx131814316474011d690423be1628019200\\\",
                                                   \\\"signType\\\":\\\"RSA\\\",
                                                   \\\"paySign\\\":\\\"dgi9IHp1kF5AUl4NEiIwZdjzyXxuWfFeehz7hQETKcfjBGv3FjJERs7277GF1qolaU1TxlHsDadcl2AtwqU72xui47gbfjUfPm6uORalOpsetkHRpN6Z6FKlqx492na2HkXicl6s6BPWYf9uJ/xC3/tXKiDepGoUspOha0ViMG8NuSXVfskgWvtak/x48R8SAvqXtvPa49Dn+rj79DuxJ30gum2qsom3p7jxkC2iC9FR4V9euOQwndQFY2OB9cvzAn5cGzVsvEsPnrOG1qIxfme6gLf8a9ze4iUowKRyMDlKTujJirCy/lvbamF6RxbiuOxSd5O5ZuAWvhiJ9S+lBw==\\\"}\",
                                   \"bizUserId\":\"900013811\",
                                   \"bizOrderNo\":\"P20200413181414156WOMI28814\"}",
                   "status":"OK"}
        * */
        if(!"".equals(s)){
            Map maps = (Map) JSON.parse(s);
            String status = maps.get("status").toString();
            if (status.equals("OK")){
                String signedValue = maps.get("signedValue").toString();
                Map okMap = (Map) JSON.parse(signedValue);
                /*if (okMap.get("payStatus").toString()==null){

                }else {
                    String payStatus = Optional.ofNullable(okMap.get("payStatus").toString()).orElse("");//仅交易验证方式为“0”时返回成功：success 进行中：pending 失败：fail 订单成功时会发订单结果通知商户。
                    if (!"".equals(payStatus) && payStatus.equals("fail")){
                        String payFailMessage = okMap.get("payFailMessage").toString();//仅交易验证方式为“0”时返回 只有 payStatus 为 fail 时有效
                        return ApiUtils.error("支付失败"+","+payFailMessage);
                    }
                }*/
                String orderNo = Optional.ofNullable(okMap.get("orderNo").toString()).orElse("");//通商云订单号
                String bizUserId = Optional.ofNullable(okMap.get("bizUserId").toString()).orElse("");//商户系统用户标识，商户 系统中唯一编号。
                String bizOrderNo = Optional.ofNullable(okMap.get("bizOrderNo").toString()).orElse("");//商户订单号（支付订单）
                //String tradeNo = Optional.ofNullable(okMap.get("tradeNo").toString()).orElse("");//交易编号

                //Map<String, Object> weChatAPPInfo = (Map<String, Object>)okMap.get("weChatAPPInfo");//微信 APP 支付 返回信息
                //Map<String, Object> weiXinStr = (Map<String, Object>)weChatAPPInfo.get("weixinstr");//微信 APP 支付 返回信息

                String payInfo = okMap.get("payInfo").toString();//扫码支付信息/ JS 支付串信息（微信、支付宝、QQ 钱包）/微信小程序/微信原生 H5 支付串信息/支付宝原生 APP 支付串信息
                Map payMap = (Map) JSON.parse(payInfo);
                String appId = Optional.ofNullable(payMap.get("appId").toString()).orElse("");
                String timeStamp = Optional.ofNullable(payMap.get("timeStamp").toString()).orElse("");
                String nonceStr = Optional.ofNullable(payMap.get("nonceStr").toString()).orElse("");
                String packageS = Optional.ofNullable(payMap.get("package").toString()).orElse("");
                String signType = Optional.ofNullable(payMap.get("signType").toString()).orElse("");
                String paySign = Optional.ofNullable(payMap.get("paySign").toString()).orElse("");

                //Long validateType = (Long)okMap.get("validateType");//交易验证方式  当支付方式为收银宝快捷且 需验证短信验证码时才返回，返回值为“1”表示需继续调用 【确认支付（后台+短信验证码确认）】

                AppletsPayTLResult result = new AppletsPayTLResult();
                result.setPayStatus(status);
                result.setBizOrderNo(bizOrderNo);
                result.setPaySign(paySign);
                result.setSignType(signType);
                result.setNonceStr(nonceStr);
                result.setAppId(appId);
                result.setTimeStamp(timeStamp);
                result.setPackageS(packageS);
                return ApiUtils.success(result);

            }else {
                String message = Optional.ofNullable(maps.get("message").toString()).orElse("");
                return ApiUtils.error("支付失败"+","+message);
            }
        }else {
            return ApiUtils.error("支付失败");
        }

    }

    private void refundSpoPoint(ShopOrder order) {
        RdMmAccountInfo accountInfo = rdMmAccountInfoService.find("mmCode", order.getCutGetId());
        HashMap<String, Object> map = new HashMap<>();
        map.put("transTypeCode","AWD");
        map.put("accType","SBB");
        map.put("trSourceType","BNK");
        map.put("trOrderOid",order.getId());
        map.put("accStatus",0);
        RdMmAccountLog rdMmAccountLog1 =rdMmAccountLogService.findCutByOrderId(map);
        if(rdMmAccountLog1!=null){
            rdMmAccountLog1.setAccStatus(1);
            rdMmAccountLogService.updateByCutOrderId(map);
        }
        RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
        rdMmAccountLog.setMmCode(accountInfo.getMmCode());
        RdMmBasicInfo basicInfo = rdMmBasicInfoService.find("mmCode", accountInfo.getMmCode());
        rdMmAccountLog.setMmNickName(basicInfo.getMmNickName());
        rdMmAccountLog.setTransTypeCode("CF");
        rdMmAccountLog.setAccType("SBB");
        rdMmAccountLog.setTrSourceType("BNK");
        rdMmAccountLog.setTrOrderOid(order.getId());
        rdMmAccountLog.setBlanceBefore(accountInfo.getBonusBlance());
        rdMmAccountLog.setAmount(order.getCutAcc());
        rdMmAccountLog.setBlanceAfter(accountInfo.getBonusBlance().add(order.getCutAcc()));
        rdMmAccountLog.setTransDate(new Date());
        String period = rdSysPeriodService.getSysPeriodService(new Date());
        if(period!=null){
            rdMmAccountLog.setTransPeriod(period);
        }
        rdMmAccountLog.setTransDesc("多次跳转微信小程序退还用户奖励积分");
        rdMmAccountLog.setAutohrizeDesc("多次跳转微信小程序退还用户奖励积分");
        rdMmAccountLog.setStatus(3);
        rdMmAccountLogService.save(rdMmAccountLog);
        accountInfo.setBonusBlance(accountInfo.getBonusBlance().add(order.getCutAcc()));
        rdMmAccountInfoService.update(accountInfo);
        //4.生成通知消息
        ShopCommonMessage shopCommonMessage=new ShopCommonMessage();
        shopCommonMessage.setSendUid(accountInfo.getMmCode());
        shopCommonMessage.setType(1);
        shopCommonMessage.setOnLine(1);
        shopCommonMessage.setCreateTime(new Date());
        shopCommonMessage.setBizType(2);
        shopCommonMessage.setIsTop(1);
        shopCommonMessage.setCreateTime(new Date());
        shopCommonMessage.setTitle("自动提现失败积分退还通知");
        shopCommonMessage.setContent("提现订单创建失败，退还"+order.getCutAcc()+"奖励积分到积分账户");
        Long msgId = twiterIdService.getTwiterId();
        shopCommonMessage.setId(msgId);
        shopCommonMessageDao.insert(shopCommonMessage);
        ShopMemberMessage shopMemberMessage=new ShopMemberMessage();
        shopMemberMessage.setBizType(2);
        shopMemberMessage.setCreateTime(new Date());
        shopMemberMessage.setId(twiterIdService.getTwiterId());
        shopMemberMessage.setIsRead(0);
        shopMemberMessage.setMsgId(msgId);
        shopMemberMessage.setUid(Long.parseLong(accountInfo.getMmCode()));
        shopMemberMessageDao.insert(shopMemberMessage);
    }

    /**
     * 查找合适的分账收款人 并扣减积分
     * @param shopOrder
     * @return
     */
    private HashMap<String, Object> getCutNumber(ShopOrder shopOrder) {
        HashMap<String, Object> map = new HashMap<>();
        //1.判断支付金额是否满足
        BigDecimal orderAmount = shopOrder.getOrderAmount();
        if(orderAmount.compareTo(new BigDecimal(Integer.toString(AllInPayBillCutConstant.CUT_MINIMUM)))==-1){
            //如果订单支付金额不满足分账条件，由于需要从中间账户提款，设置一个虚拟公司账户，分账
            RdMmAccountInfo accountInfo = rdMmAccountInfoService.find("mmCode",AllInPayBillCutConstant.COMPANY_CUT_B);
            map.put("accountInfo",accountInfo);
            map.put("acc",new BigDecimal("0.01"));//TODO
            rdMmAccountInfoService.reduceAcc(shopOrder,accountInfo,BigDecimal.ZERO);
            return map;
        }
        BigDecimal amount = orderAmount.multiply(new BigDecimal(Integer.toString(AllInPayBillCutConstant.PERCENTAGE))).multiply(new BigDecimal("0.01")).setScale(0,BigDecimal.ROUND_UP);//当前订单需要分出去多少钱，单位为圆
        BigDecimal acc = amount;//奖励积分需要的积分数量 积分取整
        //******************************特权会员提现*****************************************
        List<MemberPrivilege> list=memberPrivilegeService.findAscTime();
        if(list!=null&&list.size()>0){
            for (MemberPrivilege memberPrivilege : list) {
                RdMmAccountInfo accountInfo = rdMmAccountInfoService.find("mmCode",memberPrivilege.getMmCode());
                if(accountInfo!=null&&accountInfo.getBonusStatus()!=null&&accountInfo.getBonusStatus()==0&&accountInfo.getAutomaticWithdrawal()!=null&&accountInfo.getAutomaticWithdrawal()==1&&
                        accountInfo.getWithdrawalLine()!=null&&(accountInfo.getBonusBlance().subtract(accountInfo.getWithdrawalLine())).compareTo(acc)!=-1){
                    map.put("accountInfo",accountInfo);
                    map.put("acc",acc);//TODO
                    rdMmAccountInfoService.reduceAcc(shopOrder,accountInfo,acc);
                    return map;
                }
            }
        }
        //**********************************************************************************
        RdMmAccountInfo rdMmAccountInfo = cutGetPeople(shopOrder, acc);
        if(rdMmAccountInfo!=null&&rdMmAccountInfo.getMmCode()!=null){
            map.put("accountInfo",rdMmAccountInfo);
            map.put("acc",acc);
            rdMmAccountInfoService.reduceAcc(shopOrder,rdMmAccountInfo,acc);
            return map;
        }else {
            List<RdMmAccountInfo> accountInfos=rdMmAccountInfoService.findLastWithdrawalOneHundred(acc);
            if(accountInfos!=null&&accountInfos.size()>0){
                for (RdMmAccountInfo accountInfo : accountInfos) {
                    if(accountInfo.getBonusBlance().subtract(accountInfo.getWithdrawalLine()).compareTo(acc)!=-1){
                        map.put("accountInfo",accountInfo);
                        map.put("acc",acc);
                        rdMmAccountInfoService.reduceAcc(shopOrder,accountInfo,acc);
                        return map;
                    }
                }
            }
        }
        //都不满足，走公司小B分账
        RdMmAccountInfo accountInfo = rdMmAccountInfoService.find("mmCode",AllInPayBillCutConstant.COMPANY_CUT_B);
        map.put("accountInfo",accountInfo);
        map.put("acc",new BigDecimal("0.01"));
        rdMmAccountInfoService.reduceAcc(shopOrder,accountInfo,BigDecimal.ZERO);
        return map;
    }

    /**
     * 根据扣减积分金额，找到一个合适的获取分账信息的会员
     * @param shopOrder
     * @param acc
     */
    public RdMmAccountInfo cutGetPeople(ShopOrder shopOrder,BigDecimal acc){
        Boolean flag=true;
        String code=Long.toString(shopOrder.getBuyerId());
        RdMmAccountInfo info = new RdMmAccountInfo();
        while (flag){//根据订单购买人查询其推荐人信息，看推荐人是否满足分账条件，如果不满足继续往上查找，直至找到或者查询到公司节点为止 找到返回该会员积分账户信息 如果未找到，返回null
            System.out.println(code);
            RdMmRelation rdMmRelation=rdMmRelationService.find("mmCode",code);
            if(rdMmRelation==null||rdMmRelation.getSponsorCode()==null){//如果关系表为null或者该会员推荐人信息异常 结束该方法
                flag=false;
            }else if(rdMmRelation.getSponsorCode().equals("101000158")||(rdMmRelation.getSponsorCode().equals("900000000"))){//如果推荐人为公司节点
                flag=false;
            } else {
                //获取推荐人的积分账户信息记录
                RdMmAccountInfo rdMmAccountInfo=rdMmAccountInfoService.find("mmCode",rdMmRelation.getSponsorCode());
                if(rdMmAccountInfo==null||rdMmAccountInfo.getBonusStatus()==null||rdMmAccountInfo.getBonusStatus()!=0||
                        (rdMmAccountInfo.getBonusBlance().subtract(rdMmAccountInfo.getWithdrawalLine())).compareTo(acc)==-1||rdMmAccountInfo.getAutomaticWithdrawal()==null||rdMmAccountInfo.getAutomaticWithdrawal()==0){
                    code=rdMmRelation.getSponsorCode();
                }else {
                    info=rdMmAccountInfo;
                    flag=false;
                }
            }
        }
        return info;//返回后判断info中是否存在会员编号即可判断是否通过推荐人方式查询找到分账对象
    }

    /**
     * 通联接口 确认支付（后台+短信验证码确认）
     * 小程序付款
     *
     * @param paySn 订单申请的商户订单号 （支付订单）
     * @param tradeNo  交易编号
     * @param verificationCode  短信验证码
     * @param consumerIp  ip 地址  用户公网 IP 用于分控校验 注：不能使用“127.0.0.1” “localhost”
     */
    /*@RequestMapping("/api/order/applets/confirm/pay")
    @ResponseBody
    public String confirmPay(@RequestParam(value = "paySn") String paySn,@RequestParam(value = "tradeNo") String tradeNo,
                               @RequestParam(value = "verificationCode") String verificationCode,@RequestParam(value = "consumerIp") String consumerIp,
                             HttpServletRequest request) {

        String s = TongLianUtils.confirmPay(TongLianUtils.BIZ_USER_ID, paySn, tradeNo, verificationCode, consumerIp);
        if(!"".equals(s)) {
            Map maps = (Map) JSON.parse(s);
            String status = maps.get("status").toString();
            if (status.equals("OK")) {
                String signedValue = maps.get("signedValue").toString();
                Map okMap = (Map) JSON.parse(signedValue);
                String payStatus = okMap.get("payStatus").toString();//成功：success 进行中：pending 失败：fail 支付状态发生变化时还将发送异步通知：提现在成功和失败都会通知商户；其他订单只在成功时通知商户。
                if (payStatus.equals("fail")){
                    String payFailMessage = okMap.get("payFailMessage").toString();//仅交易验证方式为“0”时返回 只有 payStatus 为 fail 时有效
                    return ApiUtils.error("支付失败"+","+payFailMessage);
                }
                String bizUserId = okMap.get("bizUserId").toString();//商户系统用户标识，商户 系统中唯一编号。
                String bizOrderNo = okMap.get("bizOrderNo").toString();//商户订单号（支付订单）
                return ApiUtils.success();
            }else {
                String message = maps.get("message").toString();
                return ApiUtils.error("支付失败");
            }
        }else {
            return ApiUtils.error("支付失败");
        }
    }*/

    /**
     * 通联接口 发送短信验证码
     *
     * @param phone  手机号码
     * @param verificationCodeType  验证码类型 9-绑定手机  6-解绑手机
     */
    @RequestMapping("/api/order/applets/sendVerificationCode")
    @ResponseBody
    public String sendVerificationCode(@RequestParam(value = "phone") String phone,@RequestParam(value = "verificationCodeType") Long verificationCodeType,
                                       HttpServletRequest request) {

        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(com.framework.loippi.utils.Constants.CURRENT_USER);
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", session.getMmCode());

        if (phone==null||phone.trim().equals("")){
            return ApiUtils.error("请输入手机号码");
        }
        String s = TongLianUtils.sendVerificationCode(member.getMmCode(), phone, verificationCodeType);
        if (!"".equals(s)){
            Map maps = (Map) JSON.parse(s);
            String status = maps.get("status").toString();
            if (status.equals("OK")) {
                //String signedValue = maps.get("signedValue").toString();
                //Map okMap = (Map) JSON.parse(signedValue);
                //String bizUserId = okMap.get("bizUserId").toString();
                //String phoneBack = okMap.get("phone").toString();

                return ApiUtils.success("发送成功");
            }else {
                String message = Optional.ofNullable(maps.get("message").toString()).orElse("");
                return ApiUtils.error("发送失败："+message);
            }

        }else {
            return ApiUtils.error("发送失败");
        }
    }


    /**
     * 通联接口 解绑手机
     *
     * @param phone  手机号码
     * @param verificationCode  验证码
     */
    @RequestMapping("/api/order/applets/unbindPhone")
    @ResponseBody
    public String unbindPhone(@RequestParam(value = "phone") String phone,
                             @RequestParam(value = "verificationCode") String verificationCode,HttpServletRequest request) {

        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(com.framework.loippi.utils.Constants.CURRENT_USER);
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", session.getMmCode());

        if (phone==null||phone.trim().equals("")){
            return ApiUtils.error("请输入手机号码");
        }
        if (verificationCode==null||verificationCode.trim().equals("")){
            return ApiUtils.error("请输入验证码");
        }

        if(member.getAllInPayPhone()==null || "".equals(member.getAllInPayPhone())){
            return ApiUtils.error("该用户通联账户未绑定手机号码");
        }

        if(phone.equals(member.getAllInPayPhone()) && member.getAllInPayPhoneStatus()==2){
            return ApiUtils.error("该"+phone+"号码已为绑定状态");
        }

        if(member.getAllInPayPhone()!=null && !phone.equals(member.getAllInPayPhone())){
            return ApiUtils.error("该"+phone+"号码不是该用户绑定的手机号码");
        }

        String s = TongLianUtils.unbindPhone(member.getMmCode(), phone, verificationCode);
        if (!"".equals(s)){
            Map maps = (Map) JSON.parse(s);
            String status = maps.get("status").toString();
            if (status.equals("OK")) {
                String signedValue = maps.get("signedValue").toString();
                Map okMap = (Map) JSON.parse(signedValue);
                //String bizUserId = Optional.ofNullable(okMap.get("bizUserId").toString()).orElse("");
                //String phoneBack = Optional.ofNullable(okMap.get("phone").toString()).orElse("");
                String result = Optional.ofNullable(okMap.get("result").toString()).orElse("");
                if(result.equals("OK")){
                    rdMmBasicInfoService.updatePhoneStatusByMCode(2,member.getMmCode());
                    return ApiUtils.success("解绑成功");
                }else {
                    return ApiUtils.error("解绑失败");
                }
            }else {
                String message = Optional.ofNullable(maps.get("message").toString()).orElse("");
                return ApiUtils.error("解绑失败："+message);
            }

        }else {
            return ApiUtils.error("解绑失败");
        }
    }

    /**
     * 通联接口 绑定手机
     *
     * @param phone  手机号码
     * @param verificationCode  验证码
     */
    @RequestMapping("/api/order/applets/bindPhone")
    @ResponseBody
    public String bindPhone(@RequestParam(value = "phone") String phone,
                             @RequestParam(value = "verificationCode") String verificationCode,HttpServletRequest request) {

        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(com.framework.loippi.utils.Constants.CURRENT_USER);
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", session.getMmCode());

        if (phone==null||phone.trim().equals("")){
            return ApiUtils.error("请输入手机号码");
        }
        if (verificationCode==null||verificationCode.trim().equals("")){
            return ApiUtils.error("请输入验证码");
        }
        String s = TongLianUtils.bindPhone(member.getMmCode(), phone, verificationCode);
        if (!"".equals(s)){
            Map maps = (Map) JSON.parse(s);
            String status = maps.get("status").toString();
            if (status.equals("OK")) {
                String signedValue = maps.get("signedValue").toString();
                Map okMap = (Map) JSON.parse(signedValue);
                //String bizUserId = Optional.ofNullable(okMap.get("bizUserId").toString()).orElse("");
                String phoneBack = Optional.ofNullable(okMap.get("phone").toString()).orElse("");
                rdMmBasicInfoService.updatePhoneStatusAndPhoneByMCode(phoneBack,1,member.getMmCode());
                return ApiUtils.success("绑定成功");

            }else {
                String message = Optional.ofNullable(maps.get("message").toString()).orElse("");
                return ApiUtils.error("绑定失败："+message);
            }

        }else {
            return ApiUtils.error("绑定失败");
        }
    }

    /**
     * 通联接口 发送短信验证码（无需验证码）
     *
     * @param verificationCodeType  验证码类型 9-绑定手机  6-解绑手机
     */
    @RequestMapping("/api/order/applets/sendVerificationCodeNew")
    @ResponseBody
    public String sendVerificationCodeNew(@RequestParam(value = "verificationCodeType") Long verificationCodeType,
                                       HttpServletRequest request) {

        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(com.framework.loippi.utils.Constants.CURRENT_USER);
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", session.getMmCode());

        if (member==null){
            return ApiUtils.error("该用户还未登陆");
        }

        if (member.getMobile()==null||member.getMobile().trim().equals("")){
            return ApiUtils.error("该用户未绑定手机号");
        }
        String s = TongLianUtils.sendVerificationCode(member.getMmCode(), member.getMobile(), verificationCodeType);
        if (!"".equals(s)){
            Map maps = (Map) JSON.parse(s);
            String status = maps.get("status").toString();
            if (status.equals("OK")) {
                //String signedValue = maps.get("signedValue").toString();
                //Map okMap = (Map) JSON.parse(signedValue);
                //String bizUserId = okMap.get("bizUserId").toString();
                //String phoneBack = okMap.get("phone").toString();

                return ApiUtils.success(member.getMobile());
            }else {
                if(Optional.ofNullable(maps.get("errorCode").toString()).orElse("").equals("30024")){//手机已绑定对应的错误代码
                    //判断会员基础信息表中的手机绑定状态是否为已经绑定，如果未绑定，则修改绑定状态
                    if (member.getAllInPayPhoneStatus()==0){
                        member.setAllInPayPhoneStatus(1);
                        member.setAllInPayPhone(member.getMobile());
                        rdMmBasicInfoService.update(member);
                    }
                }
                String message = Optional.ofNullable(maps.get("message").toString()).orElse("");
                return ApiUtils.error("发送失败："+message);
            }

        }else {
            return ApiUtils.error("发送失败");
        }
    }


    /**
     * 通联接口 绑定手机（无需）
     *
     * @param verificationCode  验证码
     */
    @RequestMapping("/api/order/applets/bindPhoneNew")
    @ResponseBody
    public String bindPhoneNew(@RequestParam(value = "verificationCode") String verificationCode,HttpServletRequest request) {

        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(com.framework.loippi.utils.Constants.CURRENT_USER);
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", session.getMmCode());

        if (member==null){
            return ApiUtils.error("该用户还未登陆");
        }

        if (member.getMobile()==null||member.getMobile().trim().equals("")){
            return ApiUtils.error("该用户未绑定手机号");
        }
        if (verificationCode==null||verificationCode.trim().equals("")){
            return ApiUtils.error("请输入验证码");
        }
        String s = TongLianUtils.bindPhone(member.getMmCode(), member.getMobile(), verificationCode);
        if (!"".equals(s)){
            Map maps = (Map) JSON.parse(s);
            String status = maps.get("status").toString();
            if (status.equals("OK")) {
                String signedValue = maps.get("signedValue").toString();
                Map okMap = (Map) JSON.parse(signedValue);
                //String bizUserId = Optional.ofNullable(okMap.get("bizUserId").toString()).orElse("");
                String phoneBack = Optional.ofNullable(okMap.get("phone").toString()).orElse("");
                rdMmBasicInfoService.updatePhoneStatusAndPhoneByMCode(phoneBack,1,member.getMmCode());
                return ApiUtils.success("绑定成功");

            }else {
                String message = Optional.ofNullable(maps.get("message").toString()).orElse("");
                return ApiUtils.error("绑定失败："+message);
            }

        }else {
            return ApiUtils.error("绑定失败");
        }
    }

    /**
     * 立即购买提交订单返回订单支付实体类
     * @param param
     * @param vResult
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/order/immediately/submit")
    @ResponseBody
    public String immediatelyOrderSubmit(@Valid OrderImmediatelySubmitParam param, BindingResult vResult, HttpServletRequest request) {
        if (vResult.hasErrors()) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        // 订单留言
        Map<String, Object> orderMsgMap = new HashMap<>();
        if (StringUtils.isNotBlank(param.getOrderMessages())) {//验证是否有留言信息
            orderMsgMap.put("orderMessages", param.getOrderMessages());
        }
        if (param.getLogisticType() == 2) {//如果订单为自提 需要记录自提人姓名和电话
            orderMsgMap.put("userName", param.getUserName());
            orderMsgMap.put("userPhone", param.getUserPhone());
        }
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
        Integer type = 1; //默认显示零售价  判断商品是按零售价还是会员价出售
        if (rdRanks.getRankClass() > 0) {
            type = 2;
        }
        ShopOrderDiscountType shopOrderDiscountType=new ShopOrderDiscountType();
        shopOrderDiscountType.setId(-1L);
        Long shopOrderTypeId = param.getShopOrderTypeId();
        if(shopOrderTypeId==-1L){
            shopOrderDiscountType.setPreferentialType(type);
        }
        if(shopOrderTypeId==1L&&rdRanks.getRankClass() > 0){
            shopOrderDiscountType.setPreferentialType(2);
        }
        if(shopOrderTypeId==1L&&rdRanks.getRankClass() == 0){
            shopOrderDiscountType.setPreferentialType(1);
        }
        if(shopOrderTypeId==2L&&rdRanks.getRankClass() == 0){
            shopOrderDiscountType.setPreferentialType(1);
        }
        if(shopOrderTypeId==2L&&rdRanks.getRankClass() > 0){
            shopOrderDiscountType.setPreferentialType(2);
        }
        if(shopOrderTypeId==3L&&rdRanks.getRankClass() > 0){
            shopOrderDiscountType.setId(17L);
            shopOrderDiscountType.setPreferentialType(3);
        }
        if(param.getPlusOrderFlag()!=null&&param.getPlusOrderFlag()==1){
            shopOrderDiscountType.setId(-1L);
            shopOrderDiscountType.setPreferentialType(ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PLUS);
        }
        if(param.getGiftId()!=null){
            if(param.getGiftNum()==null){
                return ApiUtils.error("赠送数量不可以为空");
            }
            ShopGoods shopGoods = shopGoodsService.find(param.getGiftId());
            if(shopGoods==null){
                return ApiUtils.error("所选赠品不存在");
            }
            ShopGoodsSpec goodsSpec = shopGoodsSpecService.find("goodsId", param.getGiftId());//TODO 暂针对单规格商品
            if(goodsSpec==null){
                return ApiUtils.error("所选赠品不存在");
            }
            if(goodsSpec.getSpecGoodsStorage()<param.getGiftNum()){
                return ApiUtils.error("所选赠品已赠完，请选择其他类型赠品");
            }
        }
        //************************兼容ios错误***************************
        Integer splitOrderFlag=param.getSplitOrderFlag();
        if(param.getSplitCodes()==null||param.getSplitCodes().equals("")){
            splitOrderFlag=0;
        }
        //************************兼容ios错误***************************
        //提交订单,返回订单支付实体
        ShopOrderPay orderPay = new ShopOrderPay();
        if(param.getPlatform()!=null&&param.getPlatform().equals("weixinAppletsPaymentPlugin")){
            orderPay = orderService.addImmediatelyOrderReturnPaySn(param.getGoodsId(),param.getCount(),param.getSpecId(),param.getActivityId(),
                    param.getActivityType(), param.getActivityGoodsId(), param.getActivitySkuId(),member.getMmCode()
                    , orderMsgMap, param.getAddressId()
                    , param.getCouponId(),OrderState.PLATFORM_WECHAT,shopOrderDiscountType, param.getLogisticType(), param.getPaymentType(),param.getGiftId(),param.getGiftNum(),
                    splitOrderFlag,param.getSplitCodes());
        }else{
            orderPay = orderService.addImmediatelyOrderReturnPaySn(param.getGoodsId(),param.getCount(),param.getSpecId(),param.getActivityId(),
                    param.getActivityType(), param.getActivityGoodsId(), param.getActivitySkuId(),member.getMmCode()
                    , orderMsgMap, param.getAddressId()
                    , param.getCouponId(),OrderState.PLATFORM_APP,shopOrderDiscountType, param.getLogisticType(), param.getPaymentType(),param.getGiftId(),param.getGiftNum(),
                    splitOrderFlag,param.getSplitCodes());
        }
        List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
                .findList(Paramap.create().put("order", "RID desc"));
        RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
        if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
            rdMmIntegralRule = rdMmIntegralRuleList.get(0);
        }
        return ApiUtils.success(OrderSubmitResult
                .build(rdMmIntegralRule, orderPay, rdMmAccountInfoService.find("mmCode", member.getMmCode())));
    }


    /**
     * 订单搜索(根据商品名称搜)
     */
    @RequestMapping(value = "/api/order/selectOrder", method = RequestMethod.POST)
    @ResponseBody
    public String selectOrder(@RequestParam(required = false,value = "goodsName",defaultValue = "") String goodsName, HttpServletRequest request) throws Exception {

        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if (member==null) {
            return ApiUtils.error(Xerror.SYSTEM_ILLEGALITY);
        }

        List<ShopOrderGoods> goodsList = orderGoodsService.selectGoodsName(member.getMmCode(),goodsName);
        return ApiUtils.success(goodsList);
    }


    /**
     * 订单删除
     */
    @RequestMapping(value = "/api/order/delOrder", method = RequestMethod.POST)
    @ResponseBody
    public String delOrder(@RequestParam(required = false,value = "id") Long id, HttpServletRequest request) throws Exception {

        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if (member==null) {
            return ApiUtils.error(Xerror.SYSTEM_ILLEGALITY);
        }

        if (id==null){
            return ApiUtils.error("删除订单发生错误,id未存在");
        }
        ShopOrder order = orderService.find(id);
        if (order==null){
            return ApiUtils.error("删除订单发生错误,该订单不存在");
        }

        //删除
        order.setIsDel(1);
        orderService.update(order);

        return ApiUtils.success("删除成功");
    }

    /**
     * 订单搜索列表
     */
    @RequestMapping(value = "/api/order/selectList", method = RequestMethod.POST)
    @ResponseBody
    public String selectList(HttpServletRequest request, Long specId, Pageable pager) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);

        if (specId==null){
            return ApiUtils.error("规格ID不存在");
        }
        Paramap paramap = Paramap.create();
        paramap.put("buyerId", member.getMmCode());
        paramap.put("isDel", 0);
        paramap.put("specId", specId);
        // -1 查询所有订单
        //Integer orderStatus = -1;
        /*if (orderStatus != null && orderStatus != -1 && orderStatus != 80) {//0:已取消;5待审核;10:待付款;20:待发货;30:待收货;40:交易完成;50:已提交;60:已确认
            // 0:已取消;10:待付款;20:待发货;30:待收货;40:交易完成;50:已提交;60:已确认;
            paramap.put("orderState", orderStatus);
            paramap.put("lockState", 0);
        }*/
        pager.setOrderDirection(Order.Direction.DESC);
        pager.setOrderProperty("create_time");
        /*if (orderStatus != null && orderStatus == 40) {
            //已评价状态
            paramap.put("evaluationStatus", "0");
            pager.setOrderDirection(Order.Direction.DESC);
            pager.setOrderProperty("finnshed_time");
        }*/
        pager.setParameter(paramap);
        Page<ShopOrderVo> orderPage = orderService.selectListWithGoods(pager);
        return ApiUtils.success(OrderResult.buildList(orderPage.getContent()));


    }

}
