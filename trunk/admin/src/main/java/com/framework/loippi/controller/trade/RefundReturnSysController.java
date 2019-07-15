package com.framework.loippi.controller.trade;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.RefundReturnState;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.AliPayRefund;
import com.framework.loippi.entity.WeiRefund;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.trade.ShopRefundReturn;
import com.framework.loippi.entity.trade.ShopReturnOrderGoods;

import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.alipay.AlipayRefundService;
import com.framework.loippi.service.trade.ShopReturnOrderGoodsService;

import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.service.user.RdRanksService;
import com.framework.loippi.service.wechat.WechatMobileRefundService;
import com.framework.loippi.service.wechat.WechatRefundService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.trade.ShopRefundReturnService;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.NumberUtils;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.utils.validator.DateUtils;
import com.framework.loippi.vo.refund.ShopRefundReturnVo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContext;

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

    /**
     * 列表查询
     * refundType【退货/退款】
     * sellerState【0为待审核,1审核确认,2为同意,3为不同意, 4为退款完成 默认为01】
     * refundSnKeyWord【服务单号搜索快捷键】
     *
     * @param refundSnKeyWord refundSnKeyWord服务单号关键字
     * @see ShopRefundReturn#sellerState
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
     * @see ShopRefundReturn#sellerState
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
     * @see ShopRefundReturn#sellerState 同意/不同意
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
                totalPpv=totalPpv.add(Optional.ofNullable(item.getPpv()).orElse(BigDecimal.ZERO));
//                totalPpv+=Optional.ofNullable(item.getPpv()).orElse(BigDecimal.ZERO);
            }
            model.addAttribute("shopReturnOrderGoodsList", shopReturnOrderGoodsList);
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
                         @RequestParam(required = false, value = "returntype", defaultValue = "") String returntype,
                         @RequestParam(required = false, value = "orderpaytype", defaultValue = "") String orderpaytype,
                         @RequestParam(required = false, value = "refundAmount", defaultValue = "0") String refundAmount,
                         @RequestParam(required = false, value = "refundPpv", defaultValue = "0") Integer refundPpv,
                         @RequestParam(required = false, value = "refundPoint", defaultValue = "0") Integer refundPoint,
                         HttpServletRequest request, HttpServletResponse response) {

        ShopRefundReturn refundReturn = refundReturnService.find(id);
        if (refundReturn.getRefundType()==3 && "2".equals(returntype)){
            // TODO: 2019/1/5 换单
            List<ShopReturnOrderGoods> shopReturnOrderGoodsList=shopReturnOrderGoodsService.findList("returnOrderId",id);
             orderService.addExchangeOrderReturnOrderId(shopReturnOrderGoodsList,refundReturn.getOrderId());
            refundReturnService.updateRefundReturnAudiReturn(id, adminMessage,"1");
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
            refundReturn.setPpv(BigDecimal.valueOf(refundPpv));
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
                        refundReturnService.updateRefundReturnAudiReturn(id, adminMessage,orderpaytype);
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
                        refundReturnService.updateRefundReturnAudiReturn(id, adminMessage,orderpaytype);
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
                refundReturnService.updateRefundReturnAudiReturn(id, adminMessage,orderpaytype);
                model.addAttribute("msg", "退款成功");
                backurl = Constants.MSG_URL;
            }
            model.addAttribute("referer", "list.jhtml");
            return backurl;
        }


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
            refundReturnService.updateRefundReturnAudiReturn(id, adminMessage,"1");
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
            refundReturnService.updateRefundReturnAudiReturn(id, adminMessage,"1");
            modelMap.addAttribute("msg", "退款成功");
        } else {
            modelMap.addAttribute("msg", "退款失败:" + sHtmlText);
        }
        modelMap.addAttribute("referer_url", "/admin/shop_refund_return/list.jhtml");
        return Constants.MSG_URL;
    }

}
