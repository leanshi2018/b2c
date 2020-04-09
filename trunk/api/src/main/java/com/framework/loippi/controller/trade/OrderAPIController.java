package com.framework.loippi.controller.trade;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.OrderState;
import com.framework.loippi.consts.PaymentTallyState;
import com.framework.loippi.consts.ShopOrderDiscountTypeConsts;
import com.framework.loippi.controller.AppConstants;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.controller.StateResult;
import com.framework.loippi.entity.PayCommon;
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
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsEvaluate;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.entity.trade.ShopRefundReturn;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.entity.walet.RdBizPay;
import com.framework.loippi.enus.RefundReturnState;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.param.cart.CartAddParam;
import com.framework.loippi.param.order.OrderSubmitParam;
import com.framework.loippi.param.order.RefundReturnParam;
import com.framework.loippi.result.app.order.ApplyRefundReturnResult;
import com.framework.loippi.result.app.order.OrderDetailResult;
import com.framework.loippi.result.app.order.OrderResult;
import com.framework.loippi.result.app.order.OrderSubmitResult;
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
import com.framework.loippi.service.product.ShopGoodsEvaluateService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.product.ShopGoodsSpecService;
import com.framework.loippi.service.trade.ShopMemberPaymentTallyService;
import com.framework.loippi.service.trade.ShopRefundReturnService;
import com.framework.loippi.service.union.UnionpayService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmAddInfoService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.service.user.RdRanksService;
import com.framework.loippi.service.wallet.RdBizPayService;
import com.framework.loippi.service.wechat.WechatMobileService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Digests;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.utils.TongLianUtils;
import com.framework.loippi.utils.Xerror;
import com.framework.loippi.vo.order.ShopOrderVo;
import com.framework.loippi.vo.refund.ReturnGoodsVo;

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
                                  @RequestParam(required = false,value = "giftId")Long giftId,Integer giftNum,Long couponId) {
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
        ShopOrderPay orderPay = orderService.addOrderReturnPaySnNew1(param.getCartIds(), member.getMmCode()
                , orderMsgMap, param.getAddressId()
                , couponId, param.getIsPp()
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
        if (results.getOrderState() == 30 || results.getOrderState() == 40 || (results.getShippingExpressCode() != null
            && !"".equals(results.getShippingExpressCode()))) {
            ShopCommonExpress eCode = commonExpressService.find("eCode", results.getShippingExpressCode());
            String kuaiInfo = kuaidiService.query(results.getShippingExpressCode(), results.getShippingCode());
            Map mapType = JacksonUtil.convertMap(kuaiInfo);
            if (StringUtils.isBlank(kuaiInfo)) {
                return ApiUtils.error();
            }
            List<Map<String, String>> datainfo = (List) mapType.get("data");
            //是否存在物流信息
            if (datainfo != null) {
                Map<String, String> map=new HashMap<>();
                //如果存在物流信息 则显示有信息的那一条
                for (Map<String, String> item :datainfo) {
                    if (item!=null){
                        map=item;
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
        if(integration==null&&"".equals(integration)){
            return ApiUtils.error("请输入支付的积分金额");
        }
        int i = 0;
        try {
            String[] strings = integration.split("\\.");
            String string = strings[0];
            i = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ApiUtils.error("输入积分数额有误");
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
        if (i != 0) {
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
     * 优惠券付款
     *
     * @param paysn 支付订单编码
     * @param paymentCode 支付代码名称: ZFB YL weiscan
     * @param paymentId 支付方式索引id
     * @param integration 积分
     * @param paypassword 支付密码
     */
    /*@RequestMapping("/api/order/payCoupon")
    @ResponseBody
    public String payOrderCoupon(@RequestParam(value = "paysn") String paysn,
        @RequestParam(defaultValue = "pointsPaymentPlugin") String paymentCode,
        @RequestParam(defaultValue = "0") String paymentId,
        @RequestParam(defaultValue = "0") Integer integration,
        @RequestParam(defaultValue = "0") String paypassword,
        HttpServletRequest request) {

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
        if (integration != 0) {
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
            orderService.ProcessingIntegralsCoupon(paysn, integration, shopMember, pay, shoppingPointSr);
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
    }*/

/*    *//**
     * 去付款
     *
     * @param paysn 支付订单编码
     * @param paymentCode 支付代码名称: ZFB YL weiscan
     * @param paymentId 支付方式索引id
     * @param integration 积分
     * @param paypassword 支付密码
     * @param paymentType 1在线支付 2货到付款
     *//*
    @RequestMapping("/api/order/payNew")
    @ResponseBody
    public String payOrderNew(@RequestParam(value = "paysn") String paysn,
                           @RequestParam(defaultValue = "pointsPaymentPlugin") String paymentCode,
                           @RequestParam(defaultValue = "0") String paymentId,
                           @RequestParam(defaultValue = "0") Double integration,
                           @RequestParam(defaultValue = "0") String paypassword,
                           @RequestParam(defaultValue = "1") Integer paymentType,
                           HttpServletRequest request) {
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
        if (integration != 0) {
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
            orderService.ProcessingIntegralsNew(paysn, integration, shopMember, pay, shoppingPointSr);
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
    }*/

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
        Page<ReturnGoodsVo> refundReturnPage = refundReturnService.listWithGoods(pager);
        RdMmAddInfo shopMemberAddress = rdMmAddInfoService.find("aid", -1);
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
        ShopOrderPay orderPay = orderService
            .addReplacementOrder(param.getGoodsId(), param.getCount(), param.getSpecId(),
                Long.parseLong(member.getMmCode()));
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
        return ApiUtils.success(Paramap.create().put("goodsintegration", orderPay.getPayAmount())
            .put("redemptionBlance",
                Optional.ofNullable(rdMmAccountInfo.getRedemptionBlance()).orElse(BigDecimal.valueOf(0)))
            .put("contactName", contactName).put("contactPhone", contactPhone).put("contactAddrInfo", contactAddrInfo)
            .put("paySn", orderPay.getPaySn()).put("ismodify", 1)
            .put("orderId", orderPay.getOrderId()).put("addr", addr));
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
                ShopCommonArea shopCommonArea = areaService.find("areaName", address.getAddCountryCode());
                orderAddress.setAreaId(shopCommonArea.getId());
                orderAddress.setCityId(shopCommonArea.getAreaParentId());
                ShopCommonArea shopCommonArea2 = areaService.find(shopCommonArea.getAreaParentId());
                orderAddress.setProvinceId(shopCommonArea2.getAreaParentId());
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
            .updateOrderpay(payCommon, member.getMmCode(), "在线支付", paymentCode, paymentId);
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
            .put("paySn", shopOrder.getPaySn()).put("ismodify", 0)
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
        int i = 0;
        try {
            String[] strings = integration.split("\\.");
            String string = strings[0];
            i = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ApiUtils.error("输入积分数额有误");
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
        if (i != 0) {
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

        String[] split = paysn.split("###");
        String mmPaySn = split[0];//这个才是我们的pansn

        //TODO  微信 通联
        List<ShopOrder> orderList = orderService.findList("paySn", mmPaySn);
        if (CollectionUtils.isEmpty(orderList)) {
            return ApiUtils.error("订单不存在");
        }
        RdBizPay rdBizPay = new RdBizPay();
        rdBizPay.setPaySn(mmPaySn);
        rdBizPay.setBizPaySn(paysn);
        rdBizPayService.save(rdBizPay);

        ShopOrder shopOrder = orderList.get(0);
        //收款列表
        Map<String, Object> reciever = new LinkedHashMap<>();
        reciever.put("bizUserId", TongLianUtils.BIZ_USER_ID);
        //reciever.put("amount",shopOrder.getOrderAmount().longValue()*100); //TODO 正式
        reciever.put("amount",1l);
        List<Map<String, Object>> recieverList = new ArrayList<Map<String, Object>>();
        /*JSONObject reciever = new JSONObject();
        reciever.accumulate("bizUserId", TongLianUtils.BIZ_USER_ID);
        reciever.accumulate("amount",shopOrder.getOrderAmount().longValue()*100);*/
        //JSONArray recieverList = new JSONArray();
        recieverList.add(reciever);
        //支付方式
        Map<String, Object> object1 = new LinkedHashMap<>();
        object1.put("limitPay","no_credit");//String 非贷记卡：no_credit 借、贷记卡：””需要传空字符串，不能不传
        //object1.put("amount",shopOrder.getOrderAmount().longValue()*100);//Long支付金额，单位：分 TODO 正式
        object1.put("amount",1l);//Long支付金额，单位：分
        object1.put("acct",openId);//String  微信 JS 支付 openid——微信分配
        Map<String, Object> payMethods = new LinkedHashMap<>();
        payMethods.put("WECHATPAY_MINIPROGRAM",object1);
        /*JSONObject object1 = new JSONObject();
        object1.accumulate("limitPay","no_credit");//String 非贷记卡：no_credit 借、贷记卡：””需要传空字符串，不能不传
        object1.accumulate("amount",shopOrder.getOrderAmount().longValue()*100);//Long支付金额，单位：分
        object1.accumulate("acct",openId);//String  微信 JS 支付 openid——微信分配
        JSONObject payMethods = new JSONObject();
        payMethods.accumulate("WECHATPAY_MINIPROGRAM",object1);*/

        String notifyUrl = server + "/api/paynotify/notifyMobile/" + "weixinAppletsPaymentPlugin" + "/" + mmPaySn + ".json";
        //TODO 正式
        /*String s = TongLianUtils.agentCollectApply(paysn, shopOrder.getBuyerId().toString(), recieverList, 3l, "", "3001",
                shopOrder.getOrderAmount().longValue() * 100, 0l, 0l, "", notifyUrl, "",
                payMethods, "", "", "1910", "其他", 1l, "", "");*/

        String s = TongLianUtils.agentCollectApply(paysn, shopOrder.getBuyerId().toString(), recieverList, 3l, "", "3001",
                1l, 0l, 0l, "", notifyUrl, "",
                payMethods, "", "", "1910", "其他", 1l, "", "");

        /*
        response:{"sysid":"1902271423530473681","sign":"WWAff/cGqCfJUmUO/raqavyS+b3LsltQcxatJEDyLi8BG9JJvPxrNZ7IQ+sOJVK1k9nRfcm1XPMIK7nnyC/
        5h8dywkhx1yMNTf89y7M/rmO61Y7OKbEUMZe+mJ13QDWSPjwhC/IDQ/URfFQy+kksjEvdKjm+cG4h1B6oH7hDTgc=",
        "signedValue":"{\"orderNo\":\"1247788418784595968\",\"payInfo\":\"{\\\"appId\\\":\\\"wxd9b2267890ad0c2c\\\",\\\"timeStamp\\\":\\\"1586330905\\\",
        \\\"nonceStr\\\":\\\"78a2fe1092ea42919fd084d2219a40e1\\\",\\\"package\\\":\\\"prepay_id=wx081528252809422e50b5bf871236939600\\\",\\\"signType\\\":\\\"RSA\\\",
        \\\"paySign\\\":\\\"VLbYq97wdvhTYW9oTzoVOWBpK0jGALl+EPfUvTtq3vun25qvdc1IBLVeMWc1Ib89uCd5skIMGcitdIjUM/3h7KjuuFRpgJVpJb/FPMmuwe7Ij4zj8ocpj7U7qy5IKQFaxAHt
        OAcRAu0phTRQXrS9cIzEIyjcCgydCuY9fYDN+C95rQ+1QemchAotp4GUs08+dXcTFm+gSdb5zySHtq+Qd8sMnTghUjmBDmvpJltfhgV/6yg2yAwXbfOgmnaM/FvKr63nEwmOssZvdi4kIxfUNL1cJOtBdMwoPF
        WoIwDeHNxPZ1Db3v9np96ljc9q3haELrosH8U6r+OivtZuRGAJYw==\\\"}\",\"bizUserId\":\"900013801\",\"bizOrderNo\":\"P20200408151314390\"}","status":"OK"}


        {\"orderNo\":\"1247788418784595968\",
        \"payInfo\":{\\\"appId\\\":\\\"wxd9b2267890ad0c2c\\\",
                    \\\"timeStamp\\\":\\\"1586330905\\\",
                    \\\"nonceStr\\\":\\\"78a2fe1092ea42919fd084d2219a40e1\\\",
                    \\\"package\\\":\\\"prepay_id=wx081528252809422e50b5bf871236939600\\\",
                    \\\"signType\\\":\\\"RSA\\\",
                    \\\"paySign\\\":\\\"VLbYq97wdvhTYW9oTzoVOWBpK0jGALl+EPfUvTtq3vun25qvdc1IBLVeMWc1Ib89uCd5skIMGcitdIjUM/3h7KjuuFRpgJVpJb/FPMmuwe7Ij4zj8ocpj7U7qy5IKQFaxAHtOAcRAu0phTRQXrS9cIzEIyjcCgydCuY9fYDN+C95rQ+1QemchAotp4GUs08+dXcTFm+gSdb5zySHtq+Qd8sMnTghUjmBDmvpJltfhgV/6yg2yAwXbfOgmnaM/FvKr63nEwmOssZvdi4kIxfUNL1cJOtBdMwoPFWoIwDeHNxPZ1Db3v9np96ljc9q3haELrosH8U6r+OivtZuRGAJYw==\\\"}\",
        \"bizUserId\":\"900013801\",
        \"bizOrderNo\":\"P20200408151314390\"}


        response:{"sysid":"1902271423530473681",
        "sign":"g4s/E0Bo/wbE15LRyZYDfR3yb3gWamNCY3DX8GFNQOfzx1RieFPj2Ca7Pt8mV4rs775a6HMHq4As7lVlLrF8E67axMaClW3NgxumKIxtLlU+3X8uj0+kHXek67QZou5Od16ZHi0FERRZe33kVgQzUyeQ4HGW1Hbnzy9uC8nn8Vs=",
        "signedValue":"{\"orderNo\":\"1248079192394862592\",
                        \"payInfo\":\"{\\\"appId\\\":\\\"wxd9b2267890ad0c2c\\\",
                                        \\\"timeStamp\\\":\\\"1586400230\\\",
                                        \\\"nonceStr\\\":\\\"812c69734d564129bf198916261753c7\\\",
                                        \\\"package\\\":\\\"prepay_id=wx09104350750980868426ce9b1724485100\\\",
                                        \\\"signType\\\":\\\"RSA\\\",
                                        \\\"paySign\\\":\\\"rMxpYxjDyiujorLUiaqK+0BWJJxkZTKGBEW1Cfet1eAy3gQ4Icgrsyy+A2amJP+B5dI8NZ75I/V1eHx9LEF9NHiBodaGAPnlpUhO0KinXXsW3IoNtkJBh+z39cVFjjPJlLtNYpOkorzYX4tv8tLtEEVnAaHHVixJNVeAwsW/FtmWo5EnNC8iQKzcPoIIL143UnZ9+aFcx77i7EyvB1KOYw/P3gofe4feqNb9tMNVHCuBjpdmD3FwV8K4ur/LPlXcb9JP33DkoA012w+vHEEWcjXxoisEoEDuYuEcH+yTFsa71s0CCl5JUronUb4ghpzIoYZYCVObz0sGyAD73xb/6A==\\\"}\",
                        \"bizUserId\":\"900013801\",
                        \"bizOrderNo\":\"P20200409104339751\"}",
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



}
