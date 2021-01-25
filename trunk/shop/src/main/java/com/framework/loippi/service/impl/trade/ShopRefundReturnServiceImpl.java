package com.framework.loippi.service.impl.trade;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.consts.OrderState;
import com.framework.loippi.consts.PaymentTallyState;
import com.framework.loippi.consts.RefundReturnState;
import com.framework.loippi.dao.ShopCommonMessageDao;
import com.framework.loippi.dao.ShopMemberMessageDao;
import com.framework.loippi.dao.order.ShopOrderDao;
import com.framework.loippi.dao.order.ShopOrderGoodsDao;
import com.framework.loippi.dao.product.ShopGoodsSpecDao;
import com.framework.loippi.dao.trade.ShopRefundReturnDao;
import com.framework.loippi.dao.trade.ShopReturnLogDao;
import com.framework.loippi.dao.trade.ShopReturnOrderGoodsDao;
import com.framework.loippi.dao.user.RdMmRelationDao;
import com.framework.loippi.dao.user.RetailProfitDao;
import com.framework.loippi.dao.user.ShopMemberPaymentTallyDao;
import com.framework.loippi.entity.AliPayRefund;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.entity.ShopMemberMessage;
import com.framework.loippi.entity.TSystemPluginConfig;
import com.framework.loippi.entity.WeiRefund;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponDetail;
import com.framework.loippi.entity.coupon.CouponPayDetail;
import com.framework.loippi.entity.coupon.CouponUser;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.entity.trade.ShopRefundReturn;
import com.framework.loippi.entity.trade.ShopReturnLog;
import com.framework.loippi.entity.trade.ShopReturnOrderGoods;
import com.framework.loippi.entity.user.PlusProfit;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RetailProfit;
import com.framework.loippi.entity.user.ShopMemberPaymentTally;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.service.TSystemPluginConfigService;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.alipay.AlipayRefundService;
import com.framework.loippi.service.coupon.CouponDetailService;
import com.framework.loippi.service.coupon.CouponPayDetailService;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.service.coupon.CouponUserService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.trade.ShopRefundReturnService;
import com.framework.loippi.service.user.PlusProfitService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmAccountLogService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdSysPeriodService;
import com.framework.loippi.service.wechat.WechatMobileRefundService;
import com.framework.loippi.service.wechat.WechatRefundService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.NumberUtils;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.validator.DateUtils;
import com.framework.loippi.vo.refund.ReturnGoodsVo;
import com.framework.loippi.vo.refund.ShopRefundReturnVo;
import com.google.common.collect.Maps;

/**
 * SERVICE - ShopRefundReturn(退款退货)
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopRefundReturnServiceImpl extends GenericServiceImpl<ShopRefundReturn, Long>
    implements ShopRefundReturnService {
    @Autowired
    private WechatMobileRefundService wechatMobileRefundService;
    @Autowired
    private AlipayRefundService alipayRefundService;
    @Resource
    private CouponPayDetailService couponPayDetailService;
    @Autowired
    private ShopRefundReturnDao shopRefundReturnDao;
    @Resource
    private RdMmAccountInfoService rdMmAccountInfoService;
    @Resource
    private RdMmAccountLogService rdMmAccountLogService;
    @Autowired
    private ShopReturnLogDao returnLogDao;
    @Autowired
    private ShopOrderGoodsDao orderGoodsDao;
    @Autowired
    private TwiterIdService twiterIdService;
    @Resource
    private WechatRefundService wechatRefundService;
    @Autowired
    private ShopOrderDao orderDao;
    @Autowired
    private ShopGoodsSpecDao shopGoodsSpecDao;
    @Autowired
    private RdSysPeriodService rdSysPeriodService;
    @Autowired
    private TSystemPluginConfigService tSystemPluginConfigService;
    @Autowired
    private RetailProfitDao retailProfitDao;
    @Autowired
    private ShopMemberPaymentTallyDao paymentTallyDao;
    @Autowired
    private ShopCommonMessageDao shopCommonMessageDao;
    @Resource
    private ShopMemberMessageDao shopMemberMessageDao;
    @Resource
    private ShopReturnOrderGoodsDao shopReturnOrderGoodsDao;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private RdMmRelationDao rdMmRelationDao;
    @Resource
    private CouponUserService couponUserService;
    @Resource
    private CouponDetailService couponDetailService;
    @Resource
    private CouponService couponService;
    @Resource
    private PlusProfitService plusProfitService;
    @Resource
    private ShopGoodsService shopGoodsService;
    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopRefundReturnDao);
    }

    /********************* 业务方法 *********************/
    @Override
    public void updateAuditConfirm(Long refundId, Long storeId, String sellerMessage, String processInfo) {
        ShopRefundReturn refundReturn = shopRefundReturnDao.find(refundId);
        if (!refundReturn.getStoreId().equals(storeId)) {
            throw new RuntimeException("不能操作其他商家售后单");
        }

        // 审核确认前提, 状态必须要为待审核状态
        if (refundReturn.getSellerState() == RefundReturnState.SELLER_STATE_PENDING_AUDIT) {
            //判断卖家是否同意
            Map<String, Object> msgMap = JacksonUtil.convertMap(refundReturn.getSellerMessage());
            msgMap = msgMap == null ? Maps.newHashMap() : msgMap;
            msgMap.put(RefundReturnState.SELLER_STATE_CONFIRM_AUDIT + "", sellerMessage);
            refundReturn.setSellerMessage(JacksonUtil.toJson(msgMap)); //卖家备注
            refundReturn.setSellerTime(new Date()); //卖家处理时间
            refundReturn.setSellerState(RefundReturnState.SELLER_STATE_CONFIRM_AUDIT);
            shopRefundReturnDao.update(refundReturn);
            // 保存售后日志
            ShopReturnLog returnLog = new ShopReturnLog();
            returnLog.setId(twiterIdService.getTwiterId());
            returnLog.setReturnId(refundReturn.getId()); //退款表id
            returnLog.setReturnState(RefundReturnState.SELLER_STATE_CONFIRM_AUDIT + ""); //
            returnLog.setStateInfo(processInfo); //退款状态描述
            returnLog.setChangeState(
                RefundReturnState.SELLER_STATE_AGREE + "|" + RefundReturnState.SELLER_STATE_DISAGREE); //下一步退款状态信息
            returnLog.setCreateTime(new Date()); //创建时间
            returnLog.setOperator("平台自营"); //操作人
            returnLogDao.insert(returnLog);
        }
    }

    @Override
    public void updateAuditPass(Long refundId, Long storeId, Integer sellerState, String operator, String processInfo,
                                String sellerMessage, Long addId) {
        ShopRefundReturn refundReturn = shopRefundReturnDao.find(refundId);
        if (!refundReturn.getStoreId().equals(storeId)) {
            throw new RuntimeException("不能操作其他商家售后单");
        }

        if (refundReturn.getSellerState() == RefundReturnState.SELLER_STATE_CONFIRM_AUDIT||refundReturn.getSellerState() == RefundReturnState.SELLER_STATE_AGREE) {
            Boolean flag=false;
            if(refundReturn.getSellerState() == RefundReturnState.SELLER_STATE_AGREE){
                flag=true;
            }
            if (refundReturn.getRefundType() == RefundReturnState.TYPE_REFUND) {
                refundReturn.setGoodsState(RefundReturnState.GOODS_STATE_UNSHIP);
            }

            //判断卖家是否同意
            if (sellerState == RefundReturnState.SELLER_STATE_AGREE) { //若同意,修改商品状态为带发货
                refundReturn.setSellerState(RefundReturnState.SELLER_STATE_AGREE); //卖家处理状态
                refundReturn.setRefundState(RefundReturnState.REFUND_STATE_PENDING); //申请状态
            } else {
                refundReturn.setSellerState(RefundReturnState.SELLER_STATE_DISAGREE); //卖家处理状态
            }
            refundReturn.setSellerTime(new Date()); //卖家处理时间
            Map<String, Object> msgMap = JacksonUtil.convertMap(refundReturn.getSellerMessage());
            msgMap = msgMap == null ? Maps.newHashMap() : msgMap;
            msgMap.put(sellerState.toString(), sellerMessage);
            refundReturn.setSellerMessage(JacksonUtil.toJson(msgMap));
            refundReturn.setReasonInfo(sellerMessage);
            if(addId!=null){
                refundReturn.setBackAddId(addId);
            }
            shopRefundReturnDao.update(refundReturn);
            //进行用户订单通知
            ShopCommonMessage message=new ShopCommonMessage();
            message.setSendUid(refundReturn.getBuyerId()+"");
            message.setType(1);
            message.setOnLine(1);
            message.setCreateTime(new Date());
            message.setBizType(3);
            message.setBizId(refundReturn.getOrderId());
            message.setIsTop(1);
            message.setCreateTime(new Date());
            message.setTitle(" 订单编号："+refundReturn.getOrderSn());
            StringBuffer shareUrl = new StringBuffer();
            shareUrl.append("<ol class='list-paddingleft-2' style='list-style-type: decimal;'>");
            ShopReturnLog returnLog = new ShopReturnLog();
            returnLog.setReturnId(refundReturn.getId()); //退款表id
            //判断卖家同意或拒绝
            if(flag){
                if (sellerState == RefundReturnState.SELLER_STATE_DISAGREE) {
                    returnLog.setReturnState(RefundReturnState.SELLER_STATE_DISAGREE + ""); //退款状态信息
                    returnLog.setChangeState(""); //下一步退款状态信息
                    returnLog.setStateInfo("卖家已拒绝"); //退款状态描述
                    shareUrl.append("<li><p>您的售后申请已被拒绝，如有疑问，请联系客服</p></li>");
                    shareUrl.append("<li><p>理由："+sellerMessage+"</p></li>");
                    List<ShopReturnOrderGoods> shopReturnOrderGoodsList=shopReturnOrderGoodsDao.findByParams(Paramap.create().put("returnOrderId",refundId));
                    //换货还是别的
                    for (ShopReturnOrderGoods item:shopReturnOrderGoodsList) {
                        ShopOrderGoods shopOrderGoods=orderGoodsDao.findByParams(Paramap.create().put("orderId",refundReturn.getOrderId()).put("specId",item.getSpecId())).get(0);
                        if (refundReturn.getRefundState()==3){
                            shopOrderGoods.setGoodsBarternum(Optional.ofNullable(shopOrderGoods.getGoodsBarternum()).orElse(0)-item.getGoodsNum());
                        }else{
                            shopOrderGoods.setGoodsReturnnum(Optional.ofNullable(shopOrderGoods.getGoodsReturnnum()).orElse(0)-item.getGoodsNum());
                        }
                        orderGoodsDao.update(shopOrderGoods);
                    }
                } else if (sellerState == RefundReturnState.SELLER_STATE_AGREE) {
                    returnLog.setReturnState(RefundReturnState.SELLER_STATE_AGREE + ""); //退款状态信息
                    returnLog.setChangeState(RefundReturnState.SELLER_STATE_FINISH + ""); //下一步退款状态信息
                    returnLog.setStateInfo("卖家已同意"); //退款状态描述
                    shareUrl.append("<li><p>已同意</p></li>");
                    if (refundReturn.getRefundState()==3){
                        shareUrl.append("<li><p>72小时内会进行换货处理</p></li>");
                    }else{
                        shareUrl.append("<li><p>72小时内会退款到您的账号里</p></li>");
                    }
                    List<ShopReturnOrderGoods> shopReturnOrderGoodsList=shopReturnOrderGoodsDao.findByParams(Paramap.create().put("returnOrderId",refundId));
                    //换货还是别的
                    for (ShopReturnOrderGoods item:shopReturnOrderGoodsList) {
                        ShopOrderGoods shopOrderGoods=orderGoodsDao.findByParams(Paramap.create().put("orderId",refundReturn.getOrderId()).put("specId",item.getSpecId())).get(0);
                        if (refundReturn.getRefundState()==3){
                            shopOrderGoods.setGoodsBarternum(Optional.ofNullable(shopOrderGoods.getGoodsBarternum()).orElse(0)+item.getGoodsNum());
                        }else{
                            shopOrderGoods.setGoodsReturnnum(Optional.ofNullable(shopOrderGoods.getGoodsReturnnum()).orElse(0)+item.getGoodsNum());
                        }
                        orderGoodsDao.update(shopOrderGoods);
                    }
                }
            }else {
                if (sellerState == RefundReturnState.SELLER_STATE_DISAGREE) {
                    returnLog.setReturnState(RefundReturnState.SELLER_STATE_DISAGREE + ""); //退款状态信息
                    returnLog.setChangeState(""); //下一步退款状态信息
                    returnLog.setStateInfo("卖家已拒绝"); //退款状态描述
                    shareUrl.append("<li><p>您的售后申请已被拒绝，如有疑问，请联系客服</p></li>");
                    shareUrl.append("<li><p>理由："+sellerMessage+"</p></li>");
                    List<ShopReturnOrderGoods> shopReturnOrderGoodsList=shopReturnOrderGoodsDao.findByParams(Paramap.create().put("returnOrderId",refundId));
                } else if (sellerState == RefundReturnState.SELLER_STATE_AGREE) {
                    returnLog.setReturnState(RefundReturnState.SELLER_STATE_AGREE + ""); //退款状态信息
                    returnLog.setChangeState(RefundReturnState.SELLER_STATE_FINISH + ""); //下一步退款状态信息
                    returnLog.setStateInfo("卖家已同意"); //退款状态描述
                    shareUrl.append("<li><p>已同意</p></li>");
                    if (refundReturn.getRefundState()==3){
                        shareUrl.append("<li><p>72小时内会进行换货处理</p></li>");
                    }else{
                        shareUrl.append("<li><p>72小时内会退款到您的账号里</p></li>");
                    }
                    List<ShopReturnOrderGoods> shopReturnOrderGoodsList=shopReturnOrderGoodsDao.findByParams(Paramap.create().put("returnOrderId",refundId));
                    //换货还是别的
                    for (ShopReturnOrderGoods item:shopReturnOrderGoodsList) {
                        ShopOrderGoods shopOrderGoods=orderGoodsDao.findByParams(Paramap.create().put("orderId",refundReturn.getOrderId()).put("specId",item.getSpecId())).get(0);
                        if (refundReturn.getRefundState()==3){
                            shopOrderGoods.setGoodsBarternum(Optional.ofNullable(shopOrderGoods.getGoodsBarternum()).orElse(0)+item.getGoodsNum());
                        }else{
                            shopOrderGoods.setGoodsReturnnum(Optional.ofNullable(shopOrderGoods.getGoodsReturnnum()).orElse(0)+item.getGoodsNum());
                        }
                        orderGoodsDao.update(shopOrderGoods);
                    }
                }
            }
            message.setContent(shareUrl.toString());
            Long msgId = twiterIdService.getTwiterId();
            message.setId(msgId);
            shopCommonMessageDao.insert(message);
            ShopMemberMessage shopMemberMessage=new ShopMemberMessage();
            shopMemberMessage.setBizType(3);
            shopMemberMessage.setCreateTime(new Date());
            shopMemberMessage.setId(twiterIdService.getTwiterId());
            shopMemberMessage.setIsRead(0);
            shopMemberMessage.setMsgId(msgId);
            shopMemberMessage.setUid(refundReturn.getBuyerId());
            shopMemberMessageDao.insert(shopMemberMessage);
            returnLog.setStateInfo(processInfo);
            returnLog.setId(twiterIdService.getTwiterId());
            returnLog.setCreateTime(new Date()); //创建时间
            returnLog.setOperator(operator); //操作人
            //保存退货日志
            returnLogDao.insert(returnLog);
        }
    }

    /**
     * 退款退货管理员审核退款
     *  @param refundId 记录ID
     * @param adminMessage 管理员备注
     * @param username
     */
    @Override
    public void updateRefundReturnAudiReturn(Long refundId, String adminMessage, String type, String username) {//pointsPaymentPlugin
        ShopRefundReturn refundReturn = shopRefundReturnDao.find(refundId);
        if (refundReturn.getSellerState() == null
            || refundReturn.getSellerState() != RefundReturnState.SELLER_STATE_AGREE) {
            throw new IllegalStateException("售后状态错误");
        }

        //通过订单id查询订单信息
        ShopOrder order = orderDao.find(refundReturn.getOrderId());
        List<ShopOrderGoods> shopOrderGoodses = orderGoodsDao.findByParams(Paramap.create().put("orderId",order.getId()));
        refundReturn.setAdminMessage(adminMessage); //管理员备注
        refundReturn.setAdminTime(new Date()); //管理员处理时间
        refundReturn.setRefundState(RefundReturnState.REFUND_STATE_FINISH); //申请状态
        refundReturn.setSellerState(RefundReturnState.SELLER_STATE_FINISH); //处理状态


        if (refundReturn.getRefundType()==3){
            refundReturn.setSellerState(RefundReturnState.SELLER_STATE_EXCHANGE);
            shopRefundReturnDao.update(refundReturn);
        }else if ("cashOnDeliveryPlugin".equals(order.getPaymentCode())) {
            /********************* 退还余额 *********************/
            refundReturn.setSellerState(RefundReturnState.SELLER_STATE_FINISH_UNDERLINE);
            shopRefundReturnDao.update(refundReturn);
        }else if ("pointsPaymentPlugin".equals(order.getPaymentCode())) {
            /********************* 退还余额 *********************/
            if ("2".equals(type)){
                refundReturn.setSellerState(RefundReturnState.SELLER_STATE_FINISH_UNDERLINE);
            }else {
                refundReturn.setSellerState(RefundReturnState.SELLER_STATE_FINISH);
            }
            shopRefundReturnDao.update(refundReturn);
        } else {
            //根据支付单号获取订单信息
            shopRefundReturnDao.update(refundReturn);
            ShopMemberPaymentTally paymentTally = new ShopMemberPaymentTally();
            paymentTally.setPaymentCode(order.getPaymentCode());//保存支付类型
            switch (order.getPaymentCode()) {
                case "alipayMobilePaymentPlugin":
                    paymentTally.setPaymentName("支付宝手机支付");//支付名称
                    break;
                case "weixinMobilePaymentPlugin":
                    paymentTally.setPaymentName("微信手机支付");//支付名称
                    break;
                case "weixinAppletsPaymentPlugin":
                    paymentTally.setPaymentName("微信小程序支付");//支付名称
                    break;
                case "weixinInternaPaymentPlugin":
                    paymentTally.setPaymentName("微信国际支付");//支付名称
                    break;
                case "alipayInternaPaymentPlugin":
                    paymentTally.setPaymentName("支付宝国际支付");//支付名称
                    break;
                case "weixinH5PaymentPlugin":
                    paymentTally.setPaymentName("微信公众号");//支付名称
                    break;
                default:
                    throw new RuntimeException("支付类型没有对应的名称");
            }

            paymentTally.setPaymentSn(order.getPaySn());//商城内部交易号
            paymentTally.setTradeSn(refundReturn.getBatchNo());
            paymentTally.setPaymentAmount(order.getOrderAmount());// 订单交易金额
            paymentTally.setTradeType(PaymentTallyState.PAYMENTTALLY_RECHARGE_REFUND_RETURN);
            //支付状态
            paymentTally.setPaymentState(PaymentTallyState.PAYMENTTALLY_STATE_SUCCESS);
            //支付终端类型 1:PC;2:APP;3:h5
            paymentTally.setPaymentFrom(PaymentTallyState.PAYMENTTALLY_TREM_PC);
            //用户id
            paymentTally.setBuyerId(order.getBuyerId());
            //用户名
            paymentTally.setBuyerName(order.getBuyerName());
            //保存生成时间
            paymentTally.setCreateTime(new Date());
            paymentTally.setId(twiterIdService.getTwiterId());
            //保存流水表记录
            paymentTallyDao.insert(paymentTally);
        }

        /******************** 修改订单退款金额和退款状态 **********************/
        // 平台优惠券金额 打赏积分抵扣 由平台自己承担
        //double totalReturnPrice = 0.00;
        //新建一个订单当前全部退款金额(包括本次退款的金额)
        BigDecimal refundedAmount = (Optional.ofNullable(order.getRefundPoint()).orElse(BigDecimal.ZERO)).add((Optional.ofNullable(order.getRefundAmount()).orElse(BigDecimal.ZERO)));
        double refundAmount = refundedAmount.doubleValue();
        /*for (ShopOrderGoods orderGoods1 : shopOrderGoodses) {
            totalReturnPrice += orderGoods1.getGoodsPayPrice().doubleValue();
            totalReturnPrice += Optional.ofNullable(orderGoods1.getRewardPointPrice()).orElse(BigDecimal.ZERO)
                .doubleValue();
        }*/
        BigDecimal totalMoney = Optional.ofNullable(order.getOrderAmount()).orElse(BigDecimal.ZERO).
                add(Optional.ofNullable(order.getPointRmbNum()).orElse(BigDecimal.ZERO)).subtract(Optional.ofNullable(order.getShippingFee()).orElse(BigDecimal.ZERO)).add(Optional.ofNullable(order.getShippingPreferentialFee()).orElse(BigDecimal.ZERO));
        ShopOrder newOrder = new ShopOrder();
        newOrder.setId(refundReturn.getOrderId());
//        if (order.getRefundAmount() != null) {
//            refundedAmount = order.getRefundAmount().doubleValue()
//                + refundReturn.getRefundAmount().doubleValue()
//                + Optional.ofNullable(refundReturn.getRewardPointAmount()).orElse(BigDecimal.ZERO).doubleValue();
////                + Optional.ofNullable(refundReturn.getCouponAmount()).orElse(BigDecimal.ZERO).doubleValue();
//        } else {
//            refundedAmount = refundReturn.getRefundAmount().doubleValue()
//                + Optional.ofNullable(refundReturn.getRewardPointAmount()).orElse(BigDecimal.ZERO).doubleValue();
////                + Optional.ofNullable(refundReturn.getCouponAmount()).orElse(BigDecimal.ZERO).doubleValue();
//        }
//        newOrder.setRefundAmount(BigDecimal.valueOf(refundedAmount));

        //判断订单是否全部退款
        if (totalMoney.doubleValue() > refundAmount) {
            newOrder.setRefundState(OrderState.REFUND_STATE_SOM);
        } else {
            newOrder.setRefundState(OrderState.REFUND_STATE_ALL);
            //如果为全部退款，则查看是否存在优惠券使用情况，如果有优惠券使用，则退还优惠券
            List<CouponDetail> couponDetails = couponDetailService.findList("useOrderId", order.getId());
            if(couponDetails!=null&&couponDetails.size()>0){//查找出使用在当前取消订单中的优惠券，退还
                for (CouponDetail couponDetail : couponDetails) {
                    //修改couponUser
                    List<CouponUser> couponUsers = couponUserService.findList(Paramap.create().put("couponId",couponDetail.getCouponId()).put("mCode",order.getBuyerId()));
                    if(couponUsers==null||couponUsers.size()==0){
                        throw new RuntimeException("优惠券拥有记录异常");
                    }
                    List<CouponUser> couponUsers1 = couponUserService.findList(Paramap.create().put("couponId",couponDetail.getCouponId()).put("mCode",couponDetail.getReceiveId()));
                    if(couponUsers1==null||couponUsers1.size()==0){
                        throw new RuntimeException("优惠券拥有记录异常");
                    }
                    //可能在订单取消时，当前订单选取的优惠券已过期，判断是退换优惠券还是过期优惠券
                    Coupon coupon = couponService.find(couponDetail.getCouponId());
                    if(coupon!=null&&coupon.getStatus()==4){
                        //回收优惠券
                        if(coupon.getUseMoneyFlag()==1){//退款回收 TODO
                            coupon.setRefundNum(Optional.ofNullable(coupon.getRefundNum()).orElse(0)+1);
                            couponService.update(coupon);
                            couponDetail.setUseState(3);
                            couponDetail.setUseTime(null);
                            couponDetail.setUseOrderId(null);
                            couponDetail.setUseOrderSn(null);
                            couponDetail.setUseOrderPayStatus(null);
                            couponDetail.setRefundState(2);
                            couponDetail.setRefundSum(coupon.getCouponPrice());
                            couponDetailService.update(couponDetail);
                            //退款
                            returnCoupon(coupon,couponDetail,username);
                            /*CouponUser couponUser = couponUsers.get(0);
                            couponUser.setUseNum(couponUser.getUseNum()-1);
                            couponUser.setOwnNum(couponUser.getOwnNum()+1);
                            couponUser.setHaveCouponNum(couponUser.getHaveCouponNum()-1);
                            couponUserService.update(couponUser);*/
                        }else {//回收
                            couponDetail.setUseState(3);
                            couponDetail.setUseTime(null);
                            couponDetail.setUseOrderId(null);
                            couponDetail.setUseOrderSn(null);
                            couponDetail.setUseOrderPayStatus(null);
                            couponDetail.setRefundState(0);
                            couponDetail.setRefundSum(BigDecimal.ZERO);
                            couponDetailService.update(couponDetail);
                        }
                    }else if(coupon!=null&&coupon.getStatus()==2){
                        //退还优惠券
                        couponDetail.setUseState(2);//修改为未使用
                        couponDetail.setUseTime(null);
                        couponDetail.setUseOrderId(null);
                        couponDetail.setUseOrderSn(null);
                        couponDetail.setUseOrderPayStatus(null);
                        couponDetailService.update(couponDetail);
                        CouponUser couponUser = couponUsers.get(0);
                        couponUser.setUseNum(couponUser.getUseNum()-1);
                        couponUser.setOwnNum(couponUser.getOwnNum()+1);
                        couponUserService.update(couponUser);
                    }
                }
            }
        }
        //判断售前售后退款
//        if ("0".equals(refundReturn.getOrderGoodsId())) {
//            newOrder.setOrderState(OrderState.ORDER_STATE_CANCLE);
//        }
        //订单解锁
        newOrder.setLockState(OrderState.ORDER_LOCK_STATE_NO);
        //修改订单
        orderDao.update(newOrder);

        ShopReturnLog returnLog = new ShopReturnLog();
        returnLog.setId(twiterIdService.getTwiterId());
        returnLog.setReturnId(refundReturn.getId()); //退货表id
        returnLog.setReturnState(RefundReturnState.SELLER_STATE_AGREE + ""); //退货状态信息
        returnLog.setChangeState(RefundReturnState.SELLER_STATE_AGREE + ""); //下一步退货状态信息
        returnLog.setStateInfo("系统审核退款成功"); //退货状态描述
        returnLog.setCreateTime(new Date()); //创建时间
        returnLog.setOperator("系统"); //操作人
        //保存退货日志
        returnLogDao.insert(returnLog);

        //查询当前订单是否对应有零售利润记录，如果有，根据退款或者退货退款修改零售利润金额  Long refundId, String adminMessage,String type
        if(order.getOrderType()==1){//如果是零售订单 查询出零售利润记录
            List<RetailProfit> profits = retailProfitDao.findByParams(Paramap.create().put("orderId",order.getId()).put("state",0));
            if(profits!=null&&profits.size()>0){
                RetailProfit retailProfit = profits.get(0);
                BigDecimal cut=BigDecimal.ZERO;
                //根据退款记录表查询退款
                List<ShopReturnOrderGoods> shopReturnOrderGoods = shopReturnOrderGoodsDao.findByParams(Paramap.create().put("returnOrderId",refundReturn.getId()));
                if(shopReturnOrderGoods!=null&&shopReturnOrderGoods.size()>0){
                    for (ShopReturnOrderGoods shopReturnOrderGood : shopReturnOrderGoods) {
                        ShopGoodsSpec shopGoodsSpec = shopGoodsSpecDao.find(shopReturnOrderGood.getSpecId());
                        cut=cut.add(shopGoodsSpec.getSpecRetailProfit().multiply(new BigDecimal(shopReturnOrderGood.getGoodsNum())));
                    }
                }
                retailProfit.setProfits(retailProfit.getProfits().subtract(cut));
                retailProfitDao.update(retailProfit);
            }
        }
        if(order.getOrderType()==8){//如果是plus会员订单
            List<PlusProfit> profits = plusProfitService.findList(Paramap.create().put("orderId",order.getId()).put("state",0));
            if(profits!=null&&profits.size()>0){
                PlusProfit plusProfit = profits.get(0);
                //根据退款记录表查询退款
                List<ShopReturnOrderGoods> shopReturnOrderGoods = shopReturnOrderGoodsDao.findByParams(Paramap.create().put("returnOrderId",refundReturn.getId()));
                if(shopReturnOrderGoods!=null&&shopReturnOrderGoods.size()>0){
                    for (ShopReturnOrderGoods shopReturnOrderGood : shopReturnOrderGoods) {
                        ShopGoods goods = shopGoodsService.find(shopReturnOrderGood.getGoodsId());
                        if(goods!=null&&goods.getPlusVipType()!=null&&goods.getPlusVipType()==1){
                            plusProfit.setState(2);
                            plusProfitService.update(plusProfit);
                            Long num=orderDao.getPlusVipOrderNum(order.getBuyerId());
                            if(num<=1){
                                RdMmBasicInfo mmBasicInfo = rdMmBasicInfoService.findByMCode(Long.toString(order.getBuyerId()));
                                if(mmBasicInfo.getPlusVip()==1){
                                    mmBasicInfo.setPlusVip(0);
                                    rdMmBasicInfoService.update(mmBasicInfo);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    /********************* 扩展查询 *********************/
    @Override
    public ShopRefundReturnVo findWithRefundReturnLog(Long refundReturnId, boolean findAllLog) {
        return shopRefundReturnDao.getShopRefundReturnVoWithLog(refundReturnId, findAllLog);
    }

    @Override
    public Page listWithGoods(Pageable pageable) {
        PageList<ReturnGoodsVo> result = shopRefundReturnDao
                .listRefundReturnVoWithGoods(pageable.getParameter(), pageable.getPageBounds());
        return new Page<>(result, result.getPaginator().getTotalCount(), pageable);
    }

    @Override
    public Page<ReturnGoodsVo> listWithGoods1(Pageable pageable) {
        PageList<ReturnGoodsVo> result = shopRefundReturnDao
                .listRefundReturnVoWithGoods1(pageable.getParameter(), pageable.getPageBounds());
        return new Page<>(result, result.getPaginator().getTotalCount(), pageable);
    }

    @Override
    public Integer findAfterSaleYesterday(HashMap<String, Object> map) {
        return shopRefundReturnDao.findAfterSaleYesterday(map);
    }

    @Override
    public BigDecimal getSumRefundPoint(HashMap<String, Object> map) {
        return shopRefundReturnDao.getSumRefundPoint(map);
    }

    @Override
    public BigDecimal getSumRefundAmount(HashMap<String, Object> map) {
        return shopRefundReturnDao.getSumRefundAmount(map);
    }

    @Override
    public List<ShopRefundReturn> findByOrderId(long orderId) {
        return shopRefundReturnDao.findByOrderId(orderId);
    }

    @Override
    public void updateTlStatusById(String refundSn, Integer tlRefundStatus, String msg) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("id",Long.valueOf(refundSn));
        map.put("tlRefundStatus",tlRefundStatus);
        map.put("adminMessage",msg);
        shopRefundReturnDao.updateTlStatusById(map);
    }


//    @Override
//    public Page findOrderGoodsAdminVoList(Pageable pageable) {
////        PageList<OrderAdminVo> result = shopRefundReturnDao.findOrderGoodsAdminVoList(pageable.getParameter(), pageable.getPageBounds());
////        return new Page(result, result.getPaginator().getTotalCount(), pageable);
//        return null;
//    }

//    @Override
//    public Page<ShopRefundReturn> findWithExtendsByPage(Pageable pager) {
//        PageList<ShopRefundReturn> result = shopRefundReturnDao.findByPage(pager.getParameter(), pager.getPageBounds());
//        result.forEach(item -> {
//            ShopOrderGoods orderGoods = orderGoodsDao.find(item.getOrderGoodsId());
//            if (orderGoods != null) {
//                item.setSpecInfo(orderGoods.getSpecInfo());
//                item.setSpecId(orderGoods.getSpecId());
//                item.setGoodsPrice(orderGoods.getGoodsPrice());
//            }
//        });
//        return new Page(result, result.getPaginator().getTotalCount(), pager);
//    }
public void returnCoupon(Coupon coupon,CouponDetail couponDetail,String opName){
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
        return;
    }
    if (paymentCode.equals("alipayMobilePaymentPlugin")) {//支付宝退款
        String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();

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
        }
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
        Map<String, Object> map = wechatMobileRefundService.toRefund(weiRefund);
        String msg = "";
        if (map.size() != 0 && map.get("result_code").equals("SUCCESS")) {
            //保存批次号和修改订单数据
            updateCoupon(couponDetail,bathno,coupon,couponPayDetail);
        }
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
        Map<String, Object> map = wechatRefundService.toRefund(weiRefund);
        String msg = "";
        if (map.size() != 0 && map.get("result_code").equals("SUCCESS")) {
            //保存批次号和修改订单数据
            updateCoupon(couponDetail,bathno,coupon,couponPayDetail);
        }
    }else if (paymentCode.equals("pointsPaymentPlugin")) {

        String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();
        //把积分退还给用户
        String mCode = couponDetail.getReceiveId();
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", couponDetail.getReceiveId());
        if (rdMmAccountInfo!=null){
            //更新用户购物积分
            RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
            rdMmAccountLog.setTransTypeCode("OT");
            rdMmAccountLog.setAccType("SWB");
            rdMmAccountLog.setTrSourceType("SWB");
            rdMmAccountLog.setMmCode(couponDetail.getReceiveId());
            rdMmAccountLog.setMmNickName(couponDetail.getReceiveNickName());
            rdMmAccountLog.setTrMmCode(couponDetail.getReceiveId());
            rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getWalletBlance());
            //单张所需积分
            BigDecimal pricePoint = couponPayDetail.getUsePointNum().divide(new BigDecimal(couponPayDetail.getCouponNumber()),0,BigDecimal.ROUND_HALF_UP);
            rdMmAccountLog.setAmount(pricePoint);
            rdMmAccountLog.setTransDate(new Date());
            String period = rdSysPeriodService.getSysPeriodService(new Date());
            rdMmAccountLog.setTransPeriod(period);
            rdMmAccountLog.setTrOrderOid(couponDetail.getBuyOrderId());
            //无需审核直接成功
            rdMmAccountLog.setStatus(3);
            rdMmAccountLog.setCreationBy(opName);
            rdMmAccountLog.setCreationTime(new Date());
            rdMmAccountLog.setAutohrizeBy(opName);
            rdMmAccountLog.setAutohrizeTime(new Date());
            rdMmAccountInfo.setWalletBlance(rdMmAccountInfo.getWalletBlance().add(pricePoint));
            rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getWalletBlance());
            rdMmAccountInfoService.update(rdMmAccountInfo);
            rdMmAccountLogService.save(rdMmAccountLog);

            //保存批次号和修改订单数据
            //updateCoupon(couponDetailId,bathno,coupon,couponPayDetail);
            CouponDetail couponDetail1 = new CouponDetail();
            couponDetail1.setId(couponDetail.getId()); //记录ID
            couponDetail1.setRefundState(2);//0：无需退款（非交易性优惠券）1：未退款 2：已退款
            couponDetail1.setRefundSum(pricePoint);
            couponDetail1.setBatchNo(bathno); //退款批次号
            couponDetail1.setRefundTime(new Date());
            couponDetailService.update(couponDetail1);//将批次号存入优惠券表

            if (couponPayDetail.getRefundCouponNum()+1==couponPayDetail.getCouponNumber()){
                couponPayDetail.setRefundState(2);
            }else{
                couponPayDetail.setRefundState(1);
            }
            couponPayDetail.setRefundCouponNum(couponPayDetail.getRefundCouponNum()+1);
            couponPayDetail.setBatchNo(bathno);
            couponPayDetail.setRefundTime(new Date());
            couponPayDetail.setRefundAmount(couponPayDetail.getRefundAmount().add(pricePoint));
            couponPayDetailService.update(couponPayDetail);

            //改rd_coupon_user
            /*List<CouponUser> couponUsers = couponUserService.findByMMCodeAndCouponId(couponDetail.getHoldId(), couponDetail.getCouponId());
            CouponUser couponUser = couponUsers.get(0);
            couponUser.setOwnNum(couponUser.getOwnNum()-1);
            couponUserService.update(couponUser);*/
            //改rd_coupon_user
            List<CouponUser> couponUsers1 = couponUserService.findByMMCodeAndCouponId(couponDetail.getReceiveId(), couponDetail.getCouponId());
            CouponUser couponUser1 = couponUsers1.get(0);
            couponUser1.setHaveCouponNum(couponUser1.getHaveCouponNum()-1);
            couponUserService.update(couponUser1);
        }
    }
}

    public void updateCoupon(CouponDetail couponDetail, String bathno, Coupon coupon, CouponPayDetail couponPayDetail) {
        couponDetail.setRefundState(2);//0：无需退款（非交易性优惠券）1：未退款 2：已退款
        couponDetail.setRefundSum(coupon.getCouponPrice());
        couponDetail.setBatchNo(bathno); //退款批次号
        couponDetail.setRefundTime(new Date());
        couponDetailService.update(couponDetail);//将批次号存入优惠券表

        if (couponPayDetail.getRefundCouponNum()+1==couponPayDetail.getCouponNumber()){
            couponPayDetail.setRefundState(2);
        }else{
            couponPayDetail.setRefundState(1);
        }
        couponPayDetail.setRefundCouponNum(couponPayDetail.getRefundCouponNum()+1);
        couponPayDetail.setBatchNo(bathno);
        couponPayDetail.setRefundTime(new Date());
        couponPayDetail.setRefundAmount(couponPayDetail.getRefundAmount().add(coupon.getCouponPrice()));
        couponPayDetailService.update(couponPayDetail);

        //改rd_coupon_user
        /*List<CouponUser> couponUsers = couponUserService.findByMMCodeAndCouponId(couponDetail.getHoldId(), couponDetail.getCouponId());
        CouponUser couponUser = couponUsers.get(0);
        couponUser.setOwnNum(couponUser.getOwnNum()-1);
        couponUserService.update(couponUser);*/
        //改rd_coupon_user
        List<CouponUser> couponUsers1 = couponUserService.findByMMCodeAndCouponId(couponDetail.getReceiveId(), couponDetail.getCouponId());
        CouponUser couponUser1 = couponUsers1.get(0);
        couponUser1.setHaveCouponNum(couponUser1.getHaveCouponNum()-1);
        couponUserService.update(couponUser1);
    }

}
