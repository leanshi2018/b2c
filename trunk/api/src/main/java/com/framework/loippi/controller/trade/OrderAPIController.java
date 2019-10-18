package com.framework.loippi.controller.trade;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.framework.loippi.entity.integration.RdMmIntegralRule;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderAddress;
import com.framework.loippi.entity.order.ShopOrderDiscountType;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.entity.product.ShopGoodsEvaluate;
import com.framework.loippi.entity.trade.ShopRefundReturn;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
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
import com.framework.loippi.result.order.RefundOrderResult;
import com.framework.loippi.service.KuaidiService;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.alipay.AlipayMobileService;
import com.framework.loippi.service.common.ShopCommonAreaService;
import com.framework.loippi.service.common.ShopCommonExpressService;
import com.framework.loippi.service.integration.RdMmIntegralRuleService;
import com.framework.loippi.service.order.ShopOrderAddressService;
import com.framework.loippi.service.order.ShopOrderDiscountTypeService;
import com.framework.loippi.service.order.ShopOrderGoodsService;
import com.framework.loippi.service.order.ShopOrderPayService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.product.ShopGoodsEvaluateService;
import com.framework.loippi.service.trade.ShopMemberPaymentTallyService;
import com.framework.loippi.service.trade.ShopRefundReturnService;
import com.framework.loippi.service.union.UnionpayService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmAddInfoService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.service.user.RdRanksService;
import com.framework.loippi.service.wechat.WechatMobileService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Digests;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
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
            shopMemberAddress = rdMmAddInfoService.find("aid", -1);
        }
        List LogisticsInformation = new ArrayList();
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
                    OrderDetailResult.build(results, shopMemberAddress).setLogisticsInformation(map));
            } else {
                return ApiUtils.success(
                    OrderDetailResult.build(results, shopMemberAddress).setLogisticsInformation(new HashMap<>()));
            }

        }
        return ApiUtils
            .success(OrderDetailResult.build(results, shopMemberAddress).setLogisticsInformation(new HashMap<>()));
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
        @RequestParam(defaultValue = "0") Integer integration,
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
            orderService.ProcessingIntegrals(paysn, integration, shopMember, pay, shoppingPointSr);
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
     * 去付款
     *
     * @param paysn 支付订单编码
     * @param paymentCode 支付代码名称: ZFB YL weiscan
     * @param paymentId 支付方式索引id
     * @param integration 积分
     * @param paypassword 支付密码
     * @param paymentType 1在线支付 2货到付款
     */
    @RequestMapping("/api/order/payCoupon")
    @ResponseBody
    public String payOrderCoupon(@RequestParam(value = "paysn") String paysn,
        @RequestParam(defaultValue = "pointsPaymentPlugin") String paymentCode,
        @RequestParam(defaultValue = "0") String paymentId,
        @RequestParam(defaultValue = "0") Integer integration,
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
}
