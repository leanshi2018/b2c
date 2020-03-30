package com.framework.loippi.controller.trade;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContext;

import com.alibaba.fastjson.JSON;
import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.RefundReturnState;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.AliPayRefund;
import com.framework.loippi.entity.Principal;
import com.framework.loippi.entity.TSystemPluginConfig;
import com.framework.loippi.entity.User;
import com.framework.loippi.entity.WeiRefund;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponDetail;
import com.framework.loippi.entity.coupon.CouponPayDetail;
import com.framework.loippi.entity.coupon.CouponRefund;
import com.framework.loippi.entity.coupon.CouponUser;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.trade.ShopRefundReturn;
import com.framework.loippi.entity.trade.ShopReturnOrderGoods;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.TSystemPluginConfigService;
import com.framework.loippi.service.UserService;
import com.framework.loippi.service.alipay.AlipayRefundService;
import com.framework.loippi.service.coupon.CouponDetailService;
import com.framework.loippi.service.coupon.CouponPayDetailService;
import com.framework.loippi.service.coupon.CouponRefundService;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.service.coupon.CouponUserService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.trade.ShopRefundReturnService;
import com.framework.loippi.service.trade.ShopReturnOrderGoodsService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmAccountLogService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.service.user.RdRanksService;
import com.framework.loippi.service.user.RdSysPeriodService;
import com.framework.loippi.service.wechat.WechatMobileRefundService;
import com.framework.loippi.service.wechat.WechatRefundService;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Dateutil;
import com.framework.loippi.utils.NumberUtils;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.utils.TongLianUtils;
import com.framework.loippi.utils.validator.DateUtils;
import com.framework.loippi.vo.refund.ShopRefundReturnVo;

/**
 * 功能： 售后管理
 * 类名：RefundReturnSysController
 * 日期：2017/11/16  16:38
 * 作者：czl
 * 详细说明：
 */
@Controller("adminShopRefundReturnController")
@Slf4j
public class RefundReturnSysController extends GenericController {
    @Resource
    private UserService userService;
    @Resource
    private ShopRefundReturnService refundReturnService;
    @Resource
    private ShopReturnOrderGoodsService shopReturnOrderGoodsService;
    @Resource
    private RdMmRelationService rdMmRelationService;
    @Resource
    private ShopOrderService orderService;
    @Resource
    private RdRanksService rdRanksService;
    @Resource
    private WechatMobileRefundService wechatMobileRefundService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private AlipayRefundService alipayRefundService;

    @Resource
    private WechatRefundService wechatRefundService;
    @Resource
    private CouponDetailService couponDetailService;
    @Resource
    private CouponPayDetailService couponPayDetailService;
    @Resource
    private CouponService couponService;
    @Resource
    private CouponUserService couponUserService;
    @Autowired
    private TSystemPluginConfigService tSystemPluginConfigService;
    @Autowired
    private RdMmAccountLogService rdMmAccountLogService;
    @Autowired
    private RdMmAccountInfoService rdMmAccountInfoService;
    @Autowired
    private RdSysPeriodService rdSysPeriodService;
    @Autowired
    private CouponRefundService couponRefundService;



    /**
     * 列表查询
     * refundType【退货/退款】
     * sellerState【0为待审核,1审核确认,2为同意,3为不同意, 4为退款完成 默认为01】
     * refundSnKeyWord【服务单号搜索快捷键】
     *
     * @param refundSnKeyWord refundSnKeyWord服务单号关键字
     */
    @RequiresPermissions("admin:refundreturn:main")
    @RequestMapping(value = {"/admin/refundreturn/list"}, method = {RequestMethod.GET})
    public String list(Pageable pageable,@RequestParam(defaultValue = "1") Integer pageNo,Integer refundType, Integer sellerState, String refundSnKeyWord,
                       ModelMap model,ShopRefundReturn refundReturn) {
//        Paramap paramap = Paramap.create().put("refundType", refundType)
//                .put("sellerState", sellerState)
//                .put("refundSnKeyWord", refundSnKeyWord)
//                .put("storeId", 0L);
        pageable.setPageNumber(pageNo);
        refundReturn.setStoreId(0L);
        refundReturn.setSellerState(sellerState);
        refundReturn.setRefundSnKeyWord(refundSnKeyWord);
        pageable.setParameter(refundReturn);
        pageable.setOrderProperty("create_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        model.addAttribute("page", refundReturnService.findByPage(pageable));
        model.addAttribute("refundType", refundType);
        model.addAttribute("sellerState", sellerState);
        model.addAttribute("refundSnKeyWord", refundSnKeyWord);
        model.addAttribute("refundReturn", refundReturn);
        return "/trade/shop_refund_return/list";
    }

    /**
     * 详情
     */
    @RequiresPermissions("admin:refundreturn:main")
    @RequestMapping(value = "/admin/refundreturn/view", method = RequestMethod.GET)
    public String view(@RequestParam long id, ModelMap model) {
        // todo 退款日志没有 退货日志有
        ShopRefundReturnVo shopRefundReturn = refundReturnService.findWithRefundReturnLog(id, true);
//        ShopRefundReturnVo shopRefundReturn = refundReturnService.findWithRefundReturnLog(id, true);
        if (shopRefundReturn.getStoreId() != 0L) {
            addMessage(model, "不允许查看其他商家订单");
            return Constants.MSG_URL;
        }
        shopRefundReturn.setId(id);
        RdMmRelation rdMmRelation=rdMmRelationService.find("mmCode",shopRefundReturn.getBuyerId());
        RdMmBasicInfo rdMmBasicInfo=rdMmBasicInfoService.find("mmCode",shopRefundReturn.getBuyerId());
        if (rdMmRelation.getRank()!=null){
            RdRanks shopMemberGrade=rdRanksService.find("rankId",rdMmRelation.getRank());
            model.addAttribute("shopMemberGrade", shopMemberGrade);
        }
        List<ShopReturnOrderGoods> shopReturnOrderGoodsList=shopReturnOrderGoodsService.findList("returnOrderId",id);
        model.addAttribute("refundReturn", shopRefundReturn);
        model.addAttribute("shopMember", rdMmBasicInfo);
        model.addAttribute("shopReturnOrderGoodsList", shopReturnOrderGoodsList);
        return "/trade/shop_refund_return/view";
    }


    /**
     * 进入【确认审核/审核同意/审核不同意】页面
     *
     */
    @RequestMapping(value = "/admin/refundreturn/audit/forward", method = RequestMethod.GET)
    @RequiresPermissions("admin:refundreturn:audit")
    public String auditIndex(@RequestParam long refundId, @RequestParam int sellerState, ModelMap model) {
        if (sellerState != RefundReturnState.SELLER_STATE_CONFIRM_AUDIT
                && sellerState != RefundReturnState.SELLER_STATE_AGREE
                && sellerState != RefundReturnState.SELLER_STATE_DISAGREE) {
            addMessage(model, "状态错误");
            return Constants.MSG_URL;
        }
        ShopRefundReturn shopRefundReturn = refundReturnService.find(refundId);
        if (shopRefundReturn.getStoreId() != 0L) {
            addMessage(model, "不允许查看其他商家订单");
            return Constants.MSG_URL;
        }
        List<ShopReturnOrderGoods> shopReturnOrderGoodsList=shopReturnOrderGoodsService.findList("returnOrderId",refundId);
        model.addAttribute("shopReturnOrderGoodsList", shopReturnOrderGoodsList);
        model.addAttribute("refundReturn", shopRefundReturn);
        model.addAttribute("sellerState", sellerState);
        return "/trade/shop_refund_return/option_audit_view";
    }

    /**
     * 订单退款--审核确认
     *
     * @param sellerMessage 审核留言
     * @param processInfo   处理进度
     */
    @RequestMapping(value = "/admin/refundreturn/confirmAudit", method = RequestMethod.POST)
    @ResponseBody
    @RequiresPermissions("admin:refundreturn:audit")
    public String confirmAudit(@RequestParam long refundId, String sellerMessage, String processInfo) {
        if (StringUtil.isEmpty(processInfo)) {
            showErrorJson("处理进度不能为空");
            return json;
        }
        if (StringUtil.isEmpty(sellerMessage)) {
            showErrorJson("审核留言不能为空");
            return json;
        }
        refundReturnService.updateAuditConfirm(refundId, 0L, sellerMessage, processInfo);
        showSuccessJson("确认审核成功");
        return json;
    }

    /**
     * 订单退款--审核通过/审核不通过
     *
     * @param sellerMessage 审核留言
     * @param processInfo   处理进度
     */
    @RequiresPermissions("admin:refundreturn:audit")
    @RequestMapping(value = "/admin/refundreturn/passAudit")
    @ResponseBody
    public String passAudit(@RequestParam long refundId, @RequestParam int sellerState, String processInfo,
                            String sellerMessage) {
        if (StringUtil.isEmpty(processInfo)) {
            showErrorJson("处理进度不能为空");
            return json;
        }

        if (StringUtil.isEmpty(sellerMessage)) {
            showErrorJson("审核留言不能为空");
            return json;
        }
        // 状态必须为 同意/不同意
        if (sellerState != RefundReturnState.SELLER_STATE_AGREE
                && RefundReturnState.SELLER_STATE_DISAGREE != sellerState) {
            showErrorJson("审核状态错误");
            return json;
        }
        refundReturnService.updateAuditPass(refundId, 0L, sellerState, "平台自营", processInfo, sellerMessage);
        showSuccessJson("审核成功");
        return json;
    }

    /**
     * 进入退款页面
     */
    @RequestMapping(value = "/admin/refundreturn/refund/forward", method = RequestMethod.GET)
    @RequiresPermissions("admin:refundreturn:refund")
    public String refundIndex(@RequestParam long refundId, ModelMap model) {
        ShopRefundReturnVo returnDetailVo = refundReturnService.findWithRefundReturnLog(refundId, true);
        if (returnDetailVo.getStoreId() != 0L) {
            addMessage(model, "不允许查看其他商家订单");
            return Constants.MSG_URL;
        }
        model.addAttribute("refundReturn", returnDetailVo);
        if (returnDetailVo != null && returnDetailVo.getOrderId() != null) {
            //根据退款id查询出订单的支付类型
            ShopOrder order = orderService.find(returnDetailVo.getOrderId());
            String orderpaytype = order.getPaymentCode();
            //判断是否全部为货到付款
            if ("cashOnDeliveryPlugin".equals(order.getPaymentCode())) {
                orderpaytype = "2";
            }
            //查询改订单一共售后多少次
            List<ShopRefundReturn> shopRefundReturnList=refundReturnService.findList("orderId",returnDetailVo.getOrderId());
            double money=Optional.ofNullable(order.getRefundAmount()).orElse(BigDecimal.valueOf(0)).doubleValue();
            BigDecimal point=Optional.ofNullable(order.getRefundPoint()).orElse(BigDecimal.valueOf(0));
            BigDecimal totalPpv=BigDecimal.ZERO;
            RdMmRelation shopMember=rdMmRelationService.find("mmCode",returnDetailVo.getBuyerId());
            RdMmBasicInfo rdMmBasicInfo=rdMmBasicInfoService.find("mmCode",returnDetailVo.getBuyerId());
            if (shopMember.getRank()!=null){
                RdRanks shopMemberGrade=rdRanksService.find("rankId",shopMember.getRank());
                model.addAttribute("shopMemberGrade", shopMemberGrade);
            }
            List<ShopReturnOrderGoods> shopReturnOrderGoodsList=shopReturnOrderGoodsService.findList("returnOrderId",refundId);
            for (ShopReturnOrderGoods item:shopReturnOrderGoodsList) {
                BigDecimal num = new BigDecimal(Integer.toString(item.getGoodsNum()));
                totalPpv=totalPpv.add(Optional.ofNullable(item.getPpv()).orElse(BigDecimal.ZERO).multiply(num));//2019/10/25修改by zc
//                totalPpv+=Optional.ofNullable(item.getPpv()).orElse(BigDecimal.ZERO);
            }
            model.addAttribute("shopReturnOrderGoodsList", shopReturnOrderGoodsList);
            for (ShopReturnOrderGoods orderGoods : shopReturnOrderGoodsList) {
                System.out.println(orderGoods.getSpecId()+"============================================"+orderGoods.getSpecInfo());
            }
            model.addAttribute("orderpaytype", orderpaytype);
            model.addAttribute("shopMember", rdMmBasicInfo);
            model.addAttribute("paymentBranch", order.getPaymentBranch());
            model.addAttribute("paymentCode", order.getPaymentCode());
            model.addAttribute("refundreturnId", refundId);
            model.addAttribute("number", shopRefundReturnList.size());
            model.addAttribute("totalMoney", order.getOrderAmount());
            model.addAttribute("remainingMoney", order.getOrderAmount().doubleValue()-money);
            model.addAttribute("totalPoint", order.getUsePointNum());
            model.addAttribute("totalPpv", totalPpv);
            model.addAttribute("remainingPoint", BigDecimal.valueOf(Optional.ofNullable(order.getUsePointNum()).orElse(0)).subtract(point));
            return "/trade/shop_refund_return/refund_view";
        }
        return ERROR_VIEW;
    }

    /**
     * 查询待审核确认数量
     */
    @RequiresPermissions("admin:refundreturn:main")
    @RequestMapping(value = "/admin/refundreturn/countPendingAudit", method = RequestMethod.GET)
    @ResponseBody
    public String countPendingAudit() {
        Long count = refundReturnService.count(
                Paramap.create().put("sellerState", RefundReturnState.SELLER_STATE_PENDING_AUDIT).put("storeId", 0L));
        showSuccessJson(count.toString());
        return json;
    }

    /**
     * 退款
     */
    @RequiresPermissions("admin:refundreturn:main")
    @RequestMapping("/admin/refundreturn/refund")
    public String refund(Model model, @RequestParam Long id,
                         @RequestParam(required = false, value = "adminMessage", defaultValue = "") String adminMessage,
                         @RequestParam(required = false, value = "returntype", defaultValue = "") String returntype,//returntype 值为0时表示人工确认后打钱给用户  1表示自动返款给用户
                         @RequestParam(required = false, value = "orderpaytype", defaultValue = "") String orderpaytype,//订单支付类型
                         @RequestParam(required = false, value = "refundAmount", defaultValue = "0") String refundAmount,//退款金额
                         @RequestParam(required = false, value = "refundPpv", defaultValue = "0") String refundPpv,//退款pv
                         @RequestParam(required = false, value = "refundPoint", defaultValue = "0") Integer refundPoint,//退款积分
                         HttpServletRequest request, HttpServletResponse response) {
        Principal principal = userService.getPrincipal();
        User user = userService.find(principal.getId());
        ShopRefundReturn refundReturn = refundReturnService.find(id);
        //**************************update by zc 2019-10-30**********************************************
        Long orderId = refundReturn.getOrderId();
        ShopOrder shopOrder = orderService.find(orderId);
        BigDecimal totalSumMoney = shopOrder.getOrderAmount().subtract(Optional.ofNullable(shopOrder.getRefundAmount()).orElse(BigDecimal.ZERO));
        BigDecimal totalSumPoint = shopOrder.getPointRmbNum().subtract(Optional.ofNullable(shopOrder.getRefundPoint()).orElse(BigDecimal.ZERO));
        BigDecimal decimalMoney = new BigDecimal(refundAmount);
        BigDecimal decimalPoint = new BigDecimal(refundPoint);
        if(decimalMoney.compareTo(totalSumMoney)==1){
            model.addAttribute("msg", "退款金额大于可退款金额");
            return Constants.MSG_URL;
        }
        if(decimalPoint.compareTo(totalSumPoint)==1){
            model.addAttribute("msg", "退款积分大于可退款积分");
            return Constants.MSG_URL;
        }
        //************************************************************************
        if (refundReturn.getRefundType()==3 && "2".equals(returntype)){
            // TODO: 2019/1/5 换单
            List<ShopReturnOrderGoods> shopReturnOrderGoodsList=shopReturnOrderGoodsService.findList("returnOrderId",id);
             orderService.addExchangeOrderReturnOrderId(shopReturnOrderGoodsList,refundReturn.getOrderId());
            refundReturnService.updateRefundReturnAudiReturn(id, adminMessage,"1", user.getUsername());
            model.addAttribute("msg", "换货成功");
            model.addAttribute("referer", "list.jhtml");
            return Constants.MSG_URL;
        }
        else{
            String backurl = Constants.MSG_URL;
            BigDecimal money=new BigDecimal(0);
            try {
                money=new BigDecimal(refundAmount);
            }catch (Exception e){
                e.printStackTrace();
                model.addAttribute("msg", "退款金额应输入数字");
                return backurl;
            }
            refundReturn.setRefundAmount(money);
            //refundReturn.setPpv(BigDecimal.valueOf(refundPpv));
            refundReturn.setPpv(new BigDecimal(refundPpv));
            refundReturn.setRewardPointAmount(BigDecimal.valueOf(refundPoint));
            //处理积分,pv值
            if (refundReturn.getBatchNo()==null){
                orderService.addRefundPoint(refundReturn);
            }
            //returntype 值为0时表示人工确认后打钱给用户  1表示自动返款给用户
            //orderpaytype 等于2时为货到付款将执行人工退款
            if (returntype.equals("1") && !"2".equals(orderpaytype)) {
                //判断退款单的退款金额是否大于余额支付金额
                ShopOrder order = orderService.find(refundReturn.getOrderId());
                if (order != null && order.getStoreId().equals(0L)) {
                    if (order.getPaymentCode().equals("alipayMobilePaymentPlugin")) {//支付宝退款
                        String bathno =
                                DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();
                        ShopRefundReturn updateReturn = new ShopRefundReturn();
                        updateReturn.setId(id); //记录ID
                        updateReturn.setBatchNo(bathno); //退款批次号
                        updateReturn.setRefundAmount(money);
                        updateReturn.setRewardPointAmount(BigDecimal.valueOf(refundPoint));
                        refundReturnService.update(updateReturn);//将批次号存入退款表
                        AliPayRefund aliPayRefund = new AliPayRefund();
                        //支付宝交易号 ，退款金额，退款理由
                        aliPayRefund.setRefundAmountNum(1);//退款数量，目前是单笔退款
                        aliPayRefund.setBatchNo(bathno);
                        aliPayRefund.setTradeNo(order.getPaySn());
                         aliPayRefund.setRefundAmount(refundReturn.getRefundAmount());
                        //aliPayRefund.setRefundAmount(new BigDecimal(0.01));
                        aliPayRefund.setRRefundReason(refundReturn.getReasonInfo());
                        aliPayRefund.setDetaildata(order.getTradeSn(),
                                refundReturn.getRefundAmount(),
                                refundReturn.getReasonInfo());
                        backurl = toalirefund(aliPayRefund, model, id, adminMessage);
                    } else if (order.getPaymentCode().equals("weixinMobilePaymentPlugin")) {//微信开放平台支付
                        WeiRefund weiRefund = new WeiRefund();
                        String bathno =
                                DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();
                        ShopRefundReturn updateReturn = new ShopRefundReturn();
                        updateReturn.setId(id); //记录ID
                        updateReturn.setBatchNo(bathno); //退款批次号
                        updateReturn.setRefundAmount(money);
                        updateReturn.setRewardPointAmount(BigDecimal.valueOf(refundPoint));
                        refundReturnService.update(updateReturn);//将批次号存入退款表
                        weiRefund.setOutrefundno(bathno);//微信交易号
                        weiRefund.setOuttradeno(order.getPaySn());//订单号
                         weiRefund.setTotalfee((int) ((order.getOrderAmount().doubleValue()) * 100));//单位，整数微信里以分为单位
                         weiRefund.setRefundfee((int) ((refundReturn.getRefundAmount().doubleValue()) * 100));
                        //weiRefund.setRefundfee(1);
                        //weiRefund.setTotalfee(1);
                        backurl = toweichatrefund(weiRefund, id, adminMessage, "open_weichatpay", model, request);
                        //toweichatrefund();
                    } else if (order.getPaymentCode().equals("weixinAppletsPaymentPlugin")) {//微信小程序支付  通联退款 退款申请 TODO ldq.2020.3.17
                        WeiRefund weiRefund = new WeiRefund();
                        String bathno =
                                DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();
                        ShopRefundReturn updateReturn = new ShopRefundReturn();
                        updateReturn.setId(id); //记录ID
                        updateReturn.setBatchNo(bathno); //退款批次号
                        updateReturn.setRefundAmount(money);
                        updateReturn.setRewardPointAmount(BigDecimal.valueOf(refundPoint));
                        refundReturnService.update(updateReturn);//将批次号存入退款表
                        weiRefund.setOutrefundno(bathno);//微信交易号
                        weiRefund.setOuttradeno(order.getPaySn());//订单号
                         weiRefund.setTotalfee((int) ((order.getOrderAmount().doubleValue()) * 100));//单位，整数微信里以分为单位
                         weiRefund.setRefundfee((int) ((refundReturn.getRefundAmount().doubleValue()) * 100));
                        //weiRefund.setRefundfee(1);
                        //weiRefund.setTotalfee(1);
                        backurl = toweichatrefundTL(weiRefund, id, adminMessage, "open_weichatpay", model, request);
                        //toweichatrefund();
                    } else if (order.getPaymentCode().equals("weixinH5PaymentPlugin")) {//微信公共平台支付
                        WeiRefund weiRefund = new WeiRefund();
                        String bathno =
                                DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();
                        ShopRefundReturn updateReturn = new ShopRefundReturn();
                        updateReturn.setId(id); //记录ID
                        updateReturn.setBatchNo(bathno); //退款批次号
                        updateReturn.setRefundAmount(money);
                        updateReturn.setRewardPointAmount(BigDecimal.valueOf(refundPoint));
                        refundReturnService.update(updateReturn);//将批次号存入退款表
                        weiRefund.setOutrefundno(bathno);//微信交易号
                        weiRefund.setOuttradeno(order.getPaySn());//订单号
                        weiRefund.setTotalfee((int) ((order.getOrderAmount().doubleValue()) * 100));//单位，整数微信里以分为单位
                    weiRefund.setRefundfee((int) ((refundReturn.getRefundAmount().doubleValue()) * 100));
//                        weiRefund.setRefundfee((int) (0.01 * 100));
                        backurl = toweichatrefund(weiRefund, id, adminMessage, "mp_weichatpay", model, request);
                    } else if (order.getPaymentCode().equals("balancePaymentPlugin")) {
                        refundReturnService.updateRefundReturnAudiReturn(id, adminMessage,orderpaytype,user.getUsername());
                        model.addAttribute("msg", "退款成功");
                    }else if (order.getPaymentCode().equals("pointsPaymentPlugin")) {
                        if (refundReturn.getBatchNo()==null){
                            String bathno =
                                    DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();
                            ShopRefundReturn updateReturn = new ShopRefundReturn();
                            updateReturn.setId(id); //记录ID
                            updateReturn.setBatchNo(bathno); //退款批次号
                            updateReturn.setRefundAmount(money);
                            updateReturn.setRewardPointAmount(BigDecimal.valueOf(refundPoint));
                            refundReturnService.update(updateReturn);//将批次号存入退款表
                        }
                        refundReturnService.updateRefundReturnAudiReturn(id, adminMessage,orderpaytype, user.getUsername());
                        model.addAttribute("msg", "退款成功");
                        backurl = Constants.MSG_URL;
                    }
                }
            } else {
                String bathno =
                        DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();
                ShopRefundReturn updateReturn = new ShopRefundReturn();
                updateReturn.setId(id); //记录ID
                updateReturn.setBatchNo(bathno); //退款批次号
                updateReturn.setRefundAmount(money);
                updateReturn.setRewardPointAmount(BigDecimal.valueOf(refundPoint));
                refundReturnService.update(updateReturn);//将批次号存入退款表
                refundReturnService.updateRefundReturnAudiReturn(id, adminMessage,orderpaytype, user.getUsername());
                model.addAttribute("msg", "退款成功");
                backurl = Constants.MSG_URL;
            }
            model.addAttribute("referer", "list.jhtml");
            return backurl;///common/common/show_msg
        }


    }



    /**
     * 单张优惠券退款
     * @param model
     * @param couponDetailId 优惠券详情id
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("admin:refundreturn:main")
    @RequestMapping("/admin/coupon/refundreturn/refund")
    public String couponRefund(Model model, @RequestParam Long couponDetailId,
                         HttpServletRequest request, HttpServletResponse response) {
        //后台管理员
        String adminName="";
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            Principal principal = (Principal) subject.getPrincipal();
            if (principal != null && principal.getId() != null) {
                adminName=principal.getUsername();
            }
        }

        CouponDetail couponDetail = couponDetailService.find(couponDetailId);

        String backurl = Constants.MSG_URL;
        if (couponDetail!=null){

            //是否已使用
            if (couponDetail.getUseState()==1 || couponDetail.getUseState()==4){
                model.addAttribute("msg", "该优惠券已使用或已禁用");
                model.addAttribute("referer", request.getHeader("Referer"));
                return backurl;
                //return "redirect:admin/plarformShopCoupon/Coupon/findCouponUserLogList.jhtml";
            }

            //是否已退款
            if (couponDetail.getRefundState()!=1){
                model.addAttribute("msg", "该优惠券无需退款或已退款");
                model.addAttribute("referer", request.getHeader("Referer"));
                return backurl;
                //return "redirect:admin/plarformShopCoupon/Coupon/findCouponUserLogList.jhtml";
            }

            //优惠券
            Coupon coupon = couponService.find(couponDetail.getCouponId());
            //购买该优惠券订单详情
            CouponPayDetail couponPayDetail = couponPayDetailService.find(couponDetail.getBuyOrderId());

            //判断是否积分支付
            String paymentCode = "";
            if (couponPayDetail.getPaymentId()==6){
                paymentCode = "pointsPaymentPlugin";
            }else{
                //不是积分支付
                TSystemPluginConfig pluginConfig = tSystemPluginConfigService.find(couponPayDetail.getPaymentId());
                if (pluginConfig!=null){
                    paymentCode = pluginConfig.getPluginId();
                }
            }
            if ("".equals(paymentCode)){
                model.addAttribute("referer", request.getHeader("Referer"));
                model.addAttribute("msg", "该订单支付方式不存在");
                return backurl;
                //return "redirect:admin/plarformShopCoupon/Coupon/findCouponUserLogList.jhtml";
            }

            if (paymentCode.equals("alipayMobilePaymentPlugin")) {//支付宝退款
                String bathno =DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();

                AliPayRefund aliPayRefund = new AliPayRefund();
                //支付宝交易号 ，退款金额，退款理由
                aliPayRefund.setRefundAmountNum(1);//退款数量，目前是单笔退款
                aliPayRefund.setBatchNo(bathno);
                aliPayRefund.setTradeNo(couponPayDetail.getPaySn());
                aliPayRefund.setRefundAmount(coupon.getCouponPrice());
                //aliPayRefund.setRefundAmount(new BigDecimal(0.01));
                aliPayRefund.setRRefundReason("单张优惠券退款");
                aliPayRefund.setDetaildata(couponPayDetail.getTradeSn(),coupon.getCouponPrice(),"单张优惠券退款");

                //跳到支付宝退款接口
                String sHtmlText = alipayRefundService.toRefund(aliPayRefund);//构造提交支付宝的表单
                if ("true".equals(sHtmlText)) {
                    //保存批次号和修改订单数据
                    updateCoupon(couponDetail,bathno,coupon,couponPayDetail);
                    model.addAttribute("msg", "退款成功");
                } else {
                    model.addAttribute("msg", "退款失败:" + sHtmlText);
                }
                model.addAttribute("referer_url", request.getHeader("Referer"));
                return Constants.MSG_URL;

            } else if (paymentCode.equals("weixinMobilePaymentPlugin")) {//微信开放平台支付

                String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();

                WeiRefund weiRefund = new WeiRefund();
                weiRefund.setOutrefundno(bathno);//微信交易号
                weiRefund.setOuttradeno(couponPayDetail.getPaySn());//订单号
                weiRefund.setTotalfee((int) ((couponPayDetail.getOrderAmount().doubleValue()) * 100));//单位，整数微信里以分为单位
                weiRefund.setRefundfee((int) ((coupon.getCouponPrice().doubleValue()) * 100));
                //weiRefund.setRefundfee(1);
                //weiRefund.setTotalfee(1);
                //跳到微信退款接口
                //toweichatrefund();

                RequestContext requestContext = new RequestContext(request);
                Map<String, Object> map = wechatMobileRefundService.toRefund(weiRefund);
                String msg = "";
                if (map.size() != 0 && map.get("result_code").equals("SUCCESS")) {
                    //保存批次号和修改订单数据
                    updateCoupon(couponDetail,bathno,coupon,couponPayDetail);
                    msg = requestContext.getMessage("checksuccess");
                    model.addAttribute("msg", msg);
                } else if (map.size() != 0 && map.get("result_code").equals("FAIL")) {
                    model.addAttribute("msg",
                            requestContext.getMessage("tsn.order_no") + "：" + weiRefund.getOuttradeno() + "<br/>" + requestContext
                                    .getMessage("Micro-channel_number") + "：" + weiRefund.getOutrefundno()
                                    + "<br/><span style='color:red;'>" + requestContext.getMessage("Micro-channel_error") + "：" + map
                                    .get("err_code_des") + "</span>");
                    model.addAttribute("noAuto", true);
                }
                model.addAttribute("referer_url", request.getHeader("Referer"));
                return Constants.MSG_URL;


            } else if (paymentCode.equals("weixinH5PaymentPlugin")) {//微信公共平台支付
                String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();

                WeiRefund weiRefund = new WeiRefund();
                weiRefund.setOutrefundno(bathno);//微信交易号
                weiRefund.setOuttradeno(couponPayDetail.getPaySn());//订单号
                weiRefund.setTotalfee((int) ((couponPayDetail.getOrderAmount().doubleValue()) * 100));//单位，整数微信里以分为单位
                weiRefund.setRefundfee((int) ((coupon.getCouponPrice().doubleValue()) * 100));
//              weiRefund.setRefundfee((int) (0.01 * 100));
                //跳到微信退款接口
                //backurl = toweichatrefund(weiRefund, id, adminMessage, "mp_weichatpay", model, request);

                RequestContext requestContext = new RequestContext(request);
                Map<String, Object> map = wechatRefundService.toRefund(weiRefund);
                String msg = "";
                if (map.size() != 0 && map.get("result_code").equals("SUCCESS")) {
                    //保存批次号和修改订单数据
                    updateCoupon(couponDetail,bathno,coupon,couponPayDetail);
                    msg = requestContext.getMessage("checksuccess");
                    model.addAttribute("msg", msg);
                } else if (map.size() != 0 && map.get("result_code").equals("FAIL")) {
                    model.addAttribute("msg",
                            requestContext.getMessage("tsn.order_no") + "：" + weiRefund.getOuttradeno() + "<br/>" + requestContext
                                    .getMessage("Micro-channel_number") + "：" + weiRefund.getOutrefundno()
                                    + "<br/><span style='color:red;'>" + requestContext.getMessage("Micro-channel_error") + "：" + map
                                    .get("err_code_des") + "</span>");
                    model.addAttribute("noAuto", true);
                }
                model.addAttribute("referer_url", request.getHeader("Referer"));
                return Constants.MSG_URL;

            }else if (paymentCode.equals("pointsPaymentPlugin")) {

                String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();
                //把积分退还给用户
                String mCode = couponDetail.getReceiveId();
                RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", couponDetail.getReceiveId());
                if (rdMmAccountInfo!=null){
                    Date date = new Date();

                    //更新用户购物积分
                    RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
                    rdMmAccountLog.setTransTypeCode("OT");
                    rdMmAccountLog.setAccType("SWB");
                    rdMmAccountLog.setTrSourceType("SWB");
                    rdMmAccountLog.setMmCode(couponDetail.getReceiveId());
                    rdMmAccountLog.setMmNickName(couponDetail.getReceiveNickName());
                    rdMmAccountLog.setTrMmCode(couponDetail.getReceiveId());
                    rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getWalletBlance());
                    rdMmAccountLog.setTransDesc("优惠券退款");
                    //单张所需积分
                    BigDecimal pricePoint = couponPayDetail.getUsePointNum().divide(new BigDecimal(couponPayDetail.getCouponNumber()),0,BigDecimal.ROUND_HALF_UP);
                    rdMmAccountLog.setAmount(pricePoint);
                    rdMmAccountLog.setTransDate(date);
                    String period = rdSysPeriodService.getSysPeriodService(date);
                    rdMmAccountLog.setTransPeriod(period);
                    rdMmAccountLog.setTrOrderOid(couponDetail.getBuyOrderId());
                    //无需审核直接成功
                    rdMmAccountLog.setStatus(3);
                    rdMmAccountLog.setCreationBy(adminName);
                    rdMmAccountLog.setCreationTime(date);
                    rdMmAccountLog.setAutohrizeBy(adminName);
                    rdMmAccountLog.setAutohrizeTime(date);
                    rdMmAccountInfo.setWalletBlance(rdMmAccountInfo.getWalletBlance().add(pricePoint));
                    rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getWalletBlance());
                    rdMmAccountInfoService.update(rdMmAccountInfo);
                    rdMmAccountLogService.save(rdMmAccountLog);

                    //保存批次号和修改订单数据
                    //updateCoupon(couponDetailId,bathno,coupon,couponPayDetail);
                    CouponDetail couponDetail1 = new CouponDetail();
                    couponDetail1.setId(couponDetailId); //记录ID
                    couponDetail1.setUseState(3);
                    couponDetail1.setRefundState(2);//0：无需退款（非交易性优惠券）1：未退款 2：已退款
                    couponDetail1.setRefundSum(pricePoint);
                    couponDetail1.setBatchNo(bathno); //退款批次号
                    couponDetail1.setRefundTime(date);
                    couponDetailService.update(couponDetail1);//将批次号存入优惠券表

                    if (couponPayDetail.getRefundCouponNum()+1==couponPayDetail.getCouponNumber()){
                        couponPayDetail.setRefundState(2);
                    }else{
                        couponPayDetail.setRefundState(1);
                    }
                    couponPayDetail.setRefundCouponNum(couponPayDetail.getRefundCouponNum()+1);
                    couponPayDetail.setBatchNo(bathno);
                    couponPayDetail.setRefundTime(date);
                    couponPayDetail.setRefundAmount(couponPayDetail.getRefundAmount().add(pricePoint));
                    couponPayDetailService.update(couponPayDetail);

                    //改rd_coupon_user
                    List<CouponUser> couponUsers = couponUserService.findByMMCodeAndCouponId(couponDetail.getHoldId(), couponDetail.getCouponId());
                    CouponUser couponUser = couponUsers.get(0);
                    couponUser.setOwnNum(couponUser.getOwnNum()-1);
                    couponUserService.update(couponUser);
                    List<CouponUser> couponUserss = couponUserService.findByMMCodeAndCouponId(couponDetail.getReceiveId(), couponDetail.getCouponId());
                    CouponUser couponUser1 = couponUserss.get(0);
                    couponUser1.setHaveCouponNum(couponUser1.getHaveCouponNum()-1);
                    couponUserService.update(couponUser1);

                    //记录退款数
                    if (coupon.getRefundNum()==null){
                        coupon.setRefundNum(1);
                    }else {
                        coupon.setRefundNum(coupon.getRefundNum()+1);
                    }
                    couponService.update(coupon);

                    //添加退款记录表
                    CouponRefund couponRefund = new CouponRefund();
                    couponRefund.setId(twiterIdService.getTwiterId());
                    couponRefund.setRefundSn("9" + Dateutil.getDateString());
                    couponRefund.setPayDetailId(couponPayDetail.getId());
                    couponRefund.setRefundNum(1);
                    couponRefund.setCouponId(coupon.getId());
                    couponRefund.setRefundAmount(new BigDecimal("0.00"));
                    couponRefund.setRefundPoint(pricePoint);
                    couponRefund.setRefundTime(date);
                    couponRefund.setBuyerId(couponPayDetail.getReceiveId());
                    couponRefund.setBuyerName(couponPayDetail.getReceiveNickName());
                    couponRefund.setOrderPaySn(couponPayDetail.getPaySn());
                    couponRefund.setOrderSn(couponPayDetail.getCouponOrderSn());
                    couponRefundService.save(couponRefund);

                    model.addAttribute("msg", "退款成功");
                    backurl = Constants.MSG_URL;
                }else{
                    model.addAttribute("msg", "退款失败");
                    backurl = Constants.MSG_URL;
                }

            }
        }

        model.addAttribute("referer", request.getHeader("Referer"));
        return backurl;///common/common/show_msg
    }

    public void updateCoupon(CouponDetail couponDetail, String bathno, Coupon coupon, CouponPayDetail couponPayDetail) {
        Date date = new Date();

        couponDetail.setUseState(3);
        couponDetail.setRefundState(2);//0：无需退款（非交易性优惠券）1：未退款 2：已退款
        couponDetail.setRefundSum(coupon.getCouponPrice());
        couponDetail.setBatchNo(bathno); //退款批次号
        couponDetail.setRefundTime(date);
        couponDetailService.update(couponDetail);//将批次号存入优惠券表

        if (couponPayDetail.getRefundCouponNum()+1==couponPayDetail.getCouponNumber()){
            couponPayDetail.setRefundState(2);
        }else{
            couponPayDetail.setRefundState(1);
        }
        couponPayDetail.setRefundCouponNum(couponPayDetail.getRefundCouponNum()+1);
        couponPayDetail.setBatchNo(bathno);
        couponPayDetail.setRefundTime(date);
        couponPayDetail.setRefundAmount(couponPayDetail.getRefundAmount().add(coupon.getCouponPrice()));
        couponPayDetailService.update(couponPayDetail);

        //改rd_coupon_user
        List<CouponUser> couponUsers = couponUserService.findByMMCodeAndCouponId(couponDetail.getHoldId(), couponDetail.getCouponId());
        CouponUser couponUser = couponUsers.get(0);
        couponUser.setOwnNum(couponUser.getOwnNum()-1);
        couponUserService.update(couponUser);
        List<CouponUser> couponUserss = couponUserService.findByMMCodeAndCouponId(couponDetail.getReceiveId(), couponDetail.getCouponId());
        CouponUser couponUser1 = couponUserss.get(0);
        couponUser1.setHaveCouponNum(couponUser1.getHaveCouponNum()-1);
        couponUserService.update(couponUser1);

        //记录退款数
        if (coupon.getRefundNum()==null){
            coupon.setRefundNum(1);
        }else {
            coupon.setRefundNum(coupon.getRefundNum()+1);
        }
        couponService.update(coupon);

        //添加退款记录表
        CouponRefund couponRefund = new CouponRefund();
        couponRefund.setId(twiterIdService.getTwiterId());
        couponRefund.setRefundSn("9" + Dateutil.getDateString());
        couponRefund.setPayDetailId(couponPayDetail.getId());
        couponRefund.setRefundNum(1);
        couponRefund.setCouponId(coupon.getId());
        couponRefund.setRefundAmount(coupon.getCouponPrice());
        couponRefund.setRefundPoint(new BigDecimal("0.00"));
        couponRefund.setRefundTime(date);
        couponRefund.setBuyerId(couponPayDetail.getReceiveId());
        couponRefund.setBuyerName(couponPayDetail.getReceiveNickName());
        couponRefund.setOrderPaySn(couponPayDetail.getPaySn());
        couponRefund.setOrderSn(couponPayDetail.getCouponOrderSn());
        couponRefundService.save(couponRefund);
    }


    /**
     * 优惠券订单退款
     * @param model
     * @param couponPayDetailId 优惠券订单id
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("admin:refundreturn:main")
    @RequestMapping("/admin/couponPayDetail/refundreturn/refund")
    public String couponPayDetailRefund(Model model, @RequestParam Long couponPayDetailId,
                         HttpServletRequest request, HttpServletResponse response) {
        //后台管理员
        String adminName="";
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            Principal principal = (Principal) subject.getPrincipal();
            if (principal != null && principal.getId() != null) {
                adminName=principal.getUsername();
            }
        }

        CouponPayDetail couponPayDetail = couponPayDetailService.find(couponPayDetailId);

        String backurl = Constants.MSG_URL;
        if (couponPayDetailId!=null){

            //订单是否已完成
            if (couponPayDetail.getCouponOrderState()!=40){
                model.addAttribute("referer", request.getHeader("Referer"));
                model.addAttribute("msg", "该订单尚未完成");
                return backurl;
            }

            //是否已全部退款
            if (couponPayDetail.getRefundState()==2){
                model.addAttribute("referer", request.getHeader("Referer"));
                model.addAttribute("msg", "该优惠券已全部退款");
                return backurl;
            }

            //优惠券
            Coupon coupon = couponService.find(couponPayDetail.getCouponId());
            //购买所有优惠券详情
            List<CouponDetail> couponDetailList = couponDetailService.findList("buyOrderId", couponPayDetailId);
            //还未退款优惠券数量
            Integer refundNum = 0;
            for (CouponDetail couponDetail : couponDetailList) {
                if (couponDetail.getUseState()==2 && couponDetail.getRefundState()==1){
                    //未使用且未退款
                    refundNum++;
                }
            }

            if (refundNum==0){
                model.addAttribute("referer", request.getHeader("Referer"));
                model.addAttribute("msg", "该订单已全部退款");
                return backurl;
            }

            //判断是否积分支付
            String paymentCode = "";
            if (couponPayDetail.getPaymentId()==6){
                paymentCode = "pointsPaymentPlugin";
            }else{
                //不是积分支付
                TSystemPluginConfig pluginConfig = tSystemPluginConfigService.find(couponPayDetail.getPaymentId());
                if (pluginConfig!=null){
                    paymentCode = pluginConfig.getPluginId();
                }
            }
            if ("".equals(paymentCode)){
                model.addAttribute("referer", request.getHeader("Referer"));
                model.addAttribute("msg", "该订单支付方式不存在");
                return backurl;
            }

            if (paymentCode.equals("alipayMobilePaymentPlugin")) {//支付宝退款
                String bathno =DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();

                AliPayRefund aliPayRefund = new AliPayRefund();
                //支付宝交易号 ，退款金额，退款理由
                aliPayRefund.setRefundAmountNum(1);//退款数量，目前是单笔退款
                aliPayRefund.setBatchNo(bathno);
                aliPayRefund.setTradeNo(couponPayDetail.getPaySn());
                aliPayRefund.setRefundAmount(coupon.getCouponPrice().multiply(new BigDecimal(refundNum)));
                //aliPayRefund.setRefundAmount(new BigDecimal(0.01));
                aliPayRefund.setRRefundReason("单张优惠券退款");
                aliPayRefund.setDetaildata(couponPayDetail.getTradeSn(),coupon.getCouponPrice(),"单张优惠券退款");

                //跳到支付宝退款接口
                String sHtmlText = alipayRefundService.toRefund(aliPayRefund);//构造提交支付宝的表单
                if ("true".equals(sHtmlText)) {
                    //保存批次号和修改订单数据
                    updateCouponDetailList(couponPayDetail,bathno,coupon,couponDetailList,refundNum);
                    model.addAttribute("msg", "退款成功");
                } else {
                    model.addAttribute("msg", "退款失败:" + sHtmlText);
                }
                //model.addAttribute("referer_url", "/activity/shop_activity/couponbuy_list.jhtml");
                model.addAttribute("referer_url", request.getHeader("Referer"));
                return Constants.MSG_URL;

            } else if (paymentCode.equals("weixinMobilePaymentPlugin")) {//微信开放平台支付

                String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();

                WeiRefund weiRefund = new WeiRefund();
                weiRefund.setOutrefundno(bathno);//微信交易号
                weiRefund.setOuttradeno(couponPayDetail.getPaySn());//订单号
                weiRefund.setTotalfee((int) ((couponPayDetail.getOrderAmount().doubleValue()) * 100));//单位，整数微信里以分为单位
                weiRefund.setRefundfee((int) ((coupon.getCouponPrice().doubleValue())* refundNum * 100));
                //weiRefund.setRefundfee(1);
                //weiRefund.setTotalfee(1);
                //跳到微信退款接口
                //toweichatrefund();

                RequestContext requestContext = new RequestContext(request);
                Map<String, Object> map = wechatMobileRefundService.toRefund(weiRefund);
                String msg = "";
                if (map.size() != 0 && map.get("result_code").equals("SUCCESS")) {
                    //保存批次号和修改订单数据
                    updateCouponDetailList(couponPayDetail,bathno,coupon,couponDetailList,refundNum);
                    msg = requestContext.getMessage("checksuccess");
                    model.addAttribute("msg", msg);
                } else if (map.size() != 0 && map.get("result_code").equals("FAIL")) {
                    model.addAttribute("msg",
                            requestContext.getMessage("tsn.order_no") + "：" + weiRefund.getOuttradeno() + "<br/>" + requestContext
                                    .getMessage("Micro-channel_number") + "：" + weiRefund.getOutrefundno()
                                    + "<br/><span style='color:red;'>" + requestContext.getMessage("Micro-channel_error") + "：" + map
                                    .get("err_code_des") + "</span>");
                    model.addAttribute("noAuto", true);
                }
                //model.addAttribute("referer_url", "/activity/shop_activity/couponbuy_list.jhtml");
                model.addAttribute("referer_url", request.getHeader("Referer"));
                return Constants.MSG_URL;


            } else if (paymentCode.equals("weixinH5PaymentPlugin")) {//微信公共平台支付
                String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();

                WeiRefund weiRefund = new WeiRefund();
                weiRefund.setOutrefundno(bathno);//微信交易号
                weiRefund.setOuttradeno(couponPayDetail.getPaySn());//订单号
                weiRefund.setTotalfee((int) ((couponPayDetail.getOrderAmount().doubleValue()) * 100));//单位，整数微信里以分为单位
                weiRefund.setRefundfee((int) ((coupon.getCouponPrice().doubleValue())* refundNum * 100));
//              weiRefund.setRefundfee((int) (0.01 * 100));
                //跳到微信退款接口
                //backurl = toweichatrefund(weiRefund, id, adminMessage, "mp_weichatpay", model, request);

                RequestContext requestContext = new RequestContext(request);
                Map<String, Object> map = wechatRefundService.toRefund(weiRefund);
                String msg = "";
                if (map.size() != 0 && map.get("result_code").equals("SUCCESS")) {
                    //保存批次号和修改订单数据
                    updateCouponDetailList(couponPayDetail,bathno,coupon,couponDetailList,refundNum);
                    msg = requestContext.getMessage("checksuccess");
                    model.addAttribute("msg", msg);
                } else if (map.size() != 0 && map.get("result_code").equals("FAIL")) {
                    model.addAttribute("msg",
                            requestContext.getMessage("tsn.order_no") + "：" + weiRefund.getOuttradeno() + "<br/>" + requestContext
                                    .getMessage("Micro-channel_number") + "：" + weiRefund.getOutrefundno()
                                    + "<br/><span style='color:red;'>" + requestContext.getMessage("Micro-channel_error") + "：" + map
                                    .get("err_code_des") + "</span>");
                    model.addAttribute("noAuto", true);
                }
                //model.addAttribute("referer_url", "/activity/shop_activity/couponbuy_list.jhtml");
                model.addAttribute("referer_url", request.getHeader("Referer"));
                return Constants.MSG_URL;

            }else if (paymentCode.equals("pointsPaymentPlugin")) {

                String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();
                //把积分退还给用户
                String mCode = couponPayDetail.getReceiveId();
                RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", couponPayDetail.getReceiveId());
                if (rdMmAccountInfo!=null){
                    Date date = new Date();
                    //更新用户购物积分
                    RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
                    rdMmAccountLog.setTransTypeCode("OT");
                    rdMmAccountLog.setAccType("SWB");
                    rdMmAccountLog.setTrSourceType("SWB");
                    rdMmAccountLog.setMmCode(couponPayDetail.getReceiveId());
                    rdMmAccountLog.setMmNickName(couponPayDetail.getReceiveNickName());
                    rdMmAccountLog.setTrMmCode(couponPayDetail.getReceiveId());
                    rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getWalletBlance());
                    rdMmAccountLog.setTransDesc("优惠券退款");
                    //所需积分
                    BigDecimal pricePoint = (couponPayDetail.getUsePointNum().divide(new BigDecimal(couponPayDetail.getCouponNumber()),0,BigDecimal.ROUND_HALF_UP)).multiply(new BigDecimal(refundNum));
                    rdMmAccountLog.setAmount(pricePoint);
                    rdMmAccountLog.setTransDate(date);
                    String period = rdSysPeriodService.getSysPeriodService(date);
                    rdMmAccountLog.setTransPeriod(period);
                    rdMmAccountLog.setTrOrderOid(couponPayDetail.getId());
                    //无需审核直接成功
                    rdMmAccountLog.setStatus(3);
                    rdMmAccountLog.setCreationBy(adminName);
                    rdMmAccountLog.setCreationTime(date);
                    rdMmAccountLog.setAutohrizeBy(adminName);
                    rdMmAccountLog.setAutohrizeTime(date);
                    rdMmAccountInfo.setWalletBlance(rdMmAccountInfo.getWalletBlance().add(pricePoint));
                    rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getWalletBlance());
                    rdMmAccountInfoService.update(rdMmAccountInfo);
                    rdMmAccountLogService.save(rdMmAccountLog);

                    //保存批次号和修改订单数据
                    //updateCouponDetailList(couponDetailId,bathno,coupon,couponPayDetail);
                    for (CouponDetail couponDetail : couponDetailList) {
                        if (couponDetail.getUseState()==2 && couponDetail.getRefundState()==1){
                            //未使用且未退款
                            couponDetail.setUseState(3);
                            couponDetail.setRefundState(2);
                            couponDetail.setRefundSum(couponPayDetail.getUsePointNum().divide(new BigDecimal(couponPayDetail.getCouponNumber()),0,BigDecimal.ROUND_HALF_UP));
                            couponDetail.setBatchNo(bathno); //退款批次号
                            couponDetail.setRefundTime(date);
                            couponDetailService.update(couponDetail);//将批次号存入优惠券表

                            //改rd_coupon_user
                            List<CouponUser> couponUsers = couponUserService.findByMMCodeAndCouponId(couponDetail.getHoldId(), couponDetail.getCouponId());
                            CouponUser couponUser = couponUsers.get(0);
                            couponUser.setOwnNum(couponUser.getOwnNum()-1);
                            couponUserService.update(couponUser);
                            List<CouponUser> couponUserss = couponUserService.findByMMCodeAndCouponId(couponDetail.getReceiveId(), couponDetail.getCouponId());
                            CouponUser couponUser1 = couponUserss.get(0);
                            couponUser1.setHaveCouponNum(couponUser1.getHaveCouponNum()-1);
                            couponUserService.update(couponUser1);


                            if (coupon.getRefundNum()==null){
                                coupon.setRefundNum(1);
                            }else {
                                coupon.setRefundNum(coupon.getRefundNum()+1);
                            }
                            couponService.update(coupon);
                        }
                    }

                    if ((couponPayDetail.getRefundCouponNum()+refundNum)==couponPayDetail.getCouponNumber()){
                        couponPayDetail.setRefundState(2);
                    }else{
                        couponPayDetail.setRefundState(1);
                    }
                    couponPayDetail.setRefundCouponNum(couponPayDetail.getRefundCouponNum()+refundNum);
                    couponPayDetail.setBatchNo(bathno);
                    couponPayDetail.setRefundTime(date);
                    couponPayDetail.setRefundAmount(couponPayDetail.getRefundAmount().add(pricePoint));
                    couponPayDetailService.update(couponPayDetail);

                    //添加退款记录表
                    CouponRefund couponRefund = new CouponRefund();
                    couponRefund.setId(twiterIdService.getTwiterId());
                    couponRefund.setRefundSn("9" + Dateutil.getDateString());
                    couponRefund.setPayDetailId(couponPayDetail.getId());
                    couponRefund.setRefundNum(refundNum);
                    couponRefund.setCouponId(coupon.getId());
                    couponRefund.setRefundAmount(new BigDecimal("0.00"));
                    couponRefund.setRefundPoint(pricePoint);
                    couponRefund.setRefundTime(date);
                    couponRefund.setBuyerId(couponPayDetail.getReceiveId());
                    couponRefund.setBuyerName(couponPayDetail.getReceiveNickName());
                    couponRefund.setOrderPaySn(couponPayDetail.getPaySn());
                    couponRefund.setOrderSn(couponPayDetail.getCouponOrderSn());
                    couponRefundService.save(couponRefund);

                    model.addAttribute("msg", "退款成功");
                    backurl = Constants.MSG_URL;
                }else{
                    model.addAttribute("msg", "退款失败");
                    backurl = Constants.MSG_URL;
                }

            }
        }else {
            model.addAttribute("referer", request.getHeader("Referer"));
            model.addAttribute("msg", "订单id未空");
            return backurl;
        }

        //model.addAttribute("referer", "/activity/shop_activity/couponbuy_list.jhtml");
        model.addAttribute("referer", request.getHeader("Referer"));
        return backurl;///common/common/show_msg
    }

    public void updateCouponDetailList(CouponPayDetail couponPayDetail, String bathno, Coupon coupon, List<CouponDetail> couponDetailList,Integer refundNum) {
        Date date = new Date();
        if ((couponPayDetail.getRefundCouponNum()+refundNum)==couponPayDetail.getCouponNumber()){
            couponPayDetail.setRefundState(2);
        }else{
            couponPayDetail.setRefundState(1);
        }
        couponPayDetail.setRefundCouponNum(couponPayDetail.getRefundCouponNum()+refundNum);
        couponPayDetail.setBatchNo(bathno);
        couponPayDetail.setRefundTime(date);
        couponPayDetail.setRefundAmount(couponPayDetail.getRefundAmount().add(coupon.getCouponPrice().multiply(new BigDecimal(refundNum))));
        couponPayDetailService.update(couponPayDetail);

        for (CouponDetail couponDetail : couponDetailList) {
            if (couponDetail.getUseState()==2 && couponDetail.getRefundState()==1){
                //未使用且未退款
                couponDetail.setUseState(3);
                couponDetail.setRefundState(2);
                couponDetail.setRefundSum(coupon.getCouponPrice());
                couponDetail.setBatchNo(bathno); //退款批次号
                couponDetail.setRefundTime(date);
                couponDetailService.update(couponDetail);//将批次号存入优惠券表

                //改rd_coupon_user
                List<CouponUser> couponUsers = couponUserService.findByMMCodeAndCouponId(couponDetail.getHoldId(), couponDetail.getCouponId());
                CouponUser couponUser = couponUsers.get(0);
                couponUser.setOwnNum(couponUser.getOwnNum()-1);
                couponUserService.update(couponUser);
                List<CouponUser> couponUserss = couponUserService.findByMMCodeAndCouponId(couponDetail.getReceiveId(), couponDetail.getCouponId());
                CouponUser couponUser1 = couponUserss.get(0);
                couponUser1.setHaveCouponNum(couponUser1.getHaveCouponNum()-1);
                couponUserService.update(couponUser1);

                if (coupon.getRefundNum()==null){
                    coupon.setRefundNum(1);
                }else {
                    coupon.setRefundNum(coupon.getRefundNum()+1);
                }
                couponService.update(coupon);
            }
        }

        //添加退款记录表
        CouponRefund couponRefund = new CouponRefund();
        couponRefund.setId(twiterIdService.getTwiterId());
        couponRefund.setRefundSn("9" + Dateutil.getDateString());
        couponRefund.setPayDetailId(couponPayDetail.getId());
        couponRefund.setRefundNum(refundNum);
        couponRefund.setCouponId(coupon.getId());
        couponRefund.setRefundAmount(coupon.getCouponPrice().multiply(new BigDecimal(refundNum)));
        couponRefund.setRefundPoint(new BigDecimal("0.00"));
        couponRefund.setRefundTime(date);
        couponRefund.setBuyerId(couponPayDetail.getReceiveId());
        couponRefund.setBuyerName(couponPayDetail.getReceiveNickName());
        couponRefund.setOrderPaySn(couponPayDetail.getPaySn());
        couponRefund.setOrderSn(couponPayDetail.getCouponOrderSn());
        couponRefundService.save(couponRefund);

    }


    /**
     * 跳到微信退款接口
     */
    public String toweichatrefund(WeiRefund weiRefund, Long id, String adminMessage, String weitype, Model model,
                                  HttpServletRequest request) {
        RequestContext requestContext = new RequestContext(request);
        Map<String, Object> map = null;
        if (weitype.equals("open_weichatpay")) {//微信开放平台退款
            map = wechatMobileRefundService.toRefund(weiRefund);
        } else if (weitype.equals("mp_weichatpay")) {//微信公共平台退款
            map = wechatRefundService.toRefund(weiRefund);
        }
        String msg = "";
        if (map.size() != 0 && map.get("result_code").equals("SUCCESS")) {
            refundReturnService.updateRefundReturnAudiReturn(id, adminMessage,"1", "");
            msg = requestContext.getMessage("checksuccess");
            model.addAttribute("msg", msg);
        } else if (map.size() != 0 && map.get("result_code").equals("FAIL")) {
            model.addAttribute("msg",
                    requestContext.getMessage("tsn.order_no") + "：" + weiRefund.getOuttradeno() + "<br/>" + requestContext
                            .getMessage("Micro-channel_number") + "：" + weiRefund.getOutrefundno()
                            + "<br/><span style='color:red;'>" + requestContext.getMessage("Micro-channel_error") + "：" + map
                            .get("err_code_des") + "</span>");
            model.addAttribute("noAuto", true);
        }
        model.addAttribute("referer_url", "/admin/shop_refund_return/list.jhtml");
        return Constants.MSG_URL;
    }

    /**
     * 跳到微信退款接口
     */
    public String toweichatrefundTL(WeiRefund weiRefund, Long id, String adminMessage, String weitype, Model model,
                                  HttpServletRequest request) {
        RequestContext requestContext = new RequestContext(request);

        ShopRefundReturn refundReturn = refundReturnService.find(id);
        ShopOrder shopOrder = orderService.find(refundReturn.getOrderId());

        Map<String, Object> map = null;
        if (weitype.equals("open_weichatpay")) {//微信开放平台退款
            String backUrl = "";//后台通知地址
            JSONArray refundList = new JSONArray();
            JSONObject refundMember = new JSONObject();
            //refundMember.accumulate("bizUserId",shopOrder.getBuyerId());//商户系统用户标识，商户系统中唯一编号
            //refundMember.accumulate("amount",shopOrder.getOrderAmount().longValue()*100);// 金额，单位：分
            //refundList.add(refundMember);

            String s = TongLianUtils.refundOrder(TongLianUtils.BIZ_USER_ID, shopOrder.getOrderSn(), shopOrder.getBuyerId().toString(), "D0", refundList,
                    backUrl,shopOrder.getOrderAmount().longValue()*100,0l,0l,null);
            if(!"".equals(s)) {
                Map maps = (Map) JSON.parse(s);
                String status = maps.get("status").toString();
                if (status.equals("OK")) {
                    String signedValue = maps.get("signedValue").toString();
                    Map okMap = (Map) JSON.parse(signedValue);
                    String payStatus = okMap.get("payStatus").toString();//仅交易验证方式为“0”时返回成功：success 进行中：pending 失败：fail 订单成功时会发订单结果通知商户。
                    if (payStatus.equals("fail")){
                        String payFailMessage = okMap.get("payFailMessage").toString();//仅交易验证方式为“0”时返回 只有 payStatus 为 fail 时有效
                        map.put("result_code","FAIL");
                        map.put("err_code_des","退款失败"+","+payFailMessage);
                    }
                    String bizUserId = okMap.get("bizUserId").toString();//商户系统用户标识，商户 系统中唯一编号。
                    String bizOrderNo = okMap.get("bizOrderNo").toString();//商户订单号（支付订单）
                    map.put("result_code","SUCCESS");
                }else {
                    map.put("result_code","FAIL");
                    String message = maps.get("message").toString();
                    map.put("err_code_des","退款失败"+","+message);
                }
            }else {
                map.put("result_code","FAIL");
                map.put("err_code_des","退款失败");
            }
            //map = wechatMobileRefundService.toRefund(weiRefund);
        } else if (weitype.equals("mp_weichatpay")) {//微信公共平台退款
            map = wechatRefundService.toRefund(weiRefund);
        }
        String msg = "";
        if (map.size() != 0 && map.get("result_code").equals("SUCCESS")) {
            refundReturnService.updateRefundReturnAudiReturn(id, adminMessage,"1", "");
            msg = requestContext.getMessage("checksuccess");
            model.addAttribute("msg", msg);
        } else if (map.size() != 0 && map.get("result_code").equals("FAIL")) {
            model.addAttribute("msg",
                    requestContext.getMessage("tsn.order_no") + "：" + weiRefund.getOuttradeno() + "<br/>" + requestContext
                            .getMessage("Micro-channel_number") + "：" + weiRefund.getOutrefundno()
                            + "<br/><span style='color:red;'>" + requestContext.getMessage("Micro-channel_error") + "：" + map
                            .get("err_code_des") + "</span>");
            model.addAttribute("noAuto", true);
        }
        model.addAttribute("referer_url", "/admin/shop_refund_return/list.jhtml");
        return Constants.MSG_URL;
    }

    /**
     * 跳到支付宝退款接口
     */
    public String toalirefund(AliPayRefund aliPayRefund, Model modelMap, Long id, String adminMessage) {
        String sHtmlText = alipayRefundService.toRefund(aliPayRefund);//构造提交支付宝的表单
        if ("true".equals(sHtmlText)) {
            refundReturnService.updateRefundReturnAudiReturn(id, adminMessage,"1", "");
            modelMap.addAttribute("msg", "退款成功");
        } else {
            modelMap.addAttribute("msg", "退款失败:" + sHtmlText);
        }
        modelMap.addAttribute("referer_url", "/admin/shop_refund_return/list.jhtml");
        return Constants.MSG_URL;
    }

}
