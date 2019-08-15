package com.framework.loippi.service.impl.trade;

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
import com.framework.loippi.dao.walet.ShopWalletLogDao;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.entity.ShopMemberMessage;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.entity.trade.ShopRefundReturn;
import com.framework.loippi.entity.trade.ShopReturnLog;
import com.framework.loippi.entity.trade.ShopReturnOrderGoods;

import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RetailProfit;
import com.framework.loippi.entity.user.ShopMemberPaymentTally;
import com.framework.loippi.entity.walet.LgTypeEnum;
import com.framework.loippi.entity.walet.ShopWalletLog;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.order.ShopOrderGoodsService;
import com.framework.loippi.service.trade.ShopRefundReturnService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.vo.order.ShopOrderVo;
import com.framework.loippi.vo.refund.ReturnGoodsVo;
import com.framework.loippi.vo.refund.ShopRefundReturnVo;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private ShopRefundReturnDao shopRefundReturnDao;
    @Autowired
    private ShopReturnLogDao returnLogDao;
    @Autowired
    private ShopOrderGoodsDao orderGoodsDao;
    @Autowired
    private TwiterIdService twiterIdService;

    @Autowired
    private ShopOrderDao orderDao;
    @Autowired
    private ShopGoodsSpecDao shopGoodsSpecDao;

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
    private RdMmRelationDao rdMmRelationDao;

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
        String sellerMessage) {
        ShopRefundReturn refundReturn = shopRefundReturnDao.find(refundId);
        if (!refundReturn.getStoreId().equals(storeId)) {
            throw new RuntimeException("不能操作其他商家售后单");
        }

        if (refundReturn.getSellerState() == RefundReturnState.SELLER_STATE_CONFIRM_AUDIT) {
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
            shopRefundReturnDao.update(refundReturn);
            //进行用户订单通知
            ShopCommonMessage message=new ShopCommonMessage();
            message.setSendUid(refundReturn.getBuyerId()+"");
            message.setType(1);
            message.setOnLine(1);
            message.setCreateTime(new Date());
            message.setBizType(3);
            message.setIsTop(1);
            message.setCreateTime(new Date());
            message.setTitle(" 订单编号："+refundReturn.getOrderSn());
            StringBuffer shareUrl = new StringBuffer();
            shareUrl.append("<ol class='list-paddingleft-2' style='list-style-type: decimal;'>");
            ShopReturnLog returnLog = new ShopReturnLog();
            returnLog.setReturnId(refundReturn.getId()); //退款表id
            //判断卖家同意或拒绝
            if (sellerState == RefundReturnState.SELLER_STATE_DISAGREE) {
                returnLog.setReturnState(RefundReturnState.SELLER_STATE_DISAGREE + ""); //退款状态信息
                returnLog.setChangeState(""); //下一步退款状态信息
                 returnLog.setStateInfo("卖家已拒绝"); //退款状态描述
                shareUrl.append("<li><p>已拒绝</p></li>");
                shareUrl.append("<li><p>理由："+sellerMessage+"</p></li>");

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
            message.setContent(shareUrl.toString());
            Long msgId = twiterIdService.getTwiterId();
            message.setId(msgId);
            shopCommonMessageDao.insert(message);
            message.setId(msgId);
            ShopMemberMessage shopMemberMessage=new ShopMemberMessage();
            shopMemberMessage.setBizType(3);
            shopMemberMessage.setCreateTime(new Date());
            shopMemberMessage.setId(twiterIdService.getTwiterId());
            shopMemberMessage.setIsRead(1);
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
     *
     * @param refundId 记录ID
     * @param adminMessage 管理员备注
     */
    @Override
    public void updateRefundReturnAudiReturn(Long refundId, String adminMessage,String type) {//pointsPaymentPlugin
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
        double totalReturnPrice = 0.00;
        //新建一个订单当前全部退款金额(包括本次退款的金额)
        double refundedAmount = 0.00;
        for (ShopOrderGoods orderGoods1 : shopOrderGoodses) {
            totalReturnPrice += orderGoods1.getGoodsPayPrice().doubleValue();
            totalReturnPrice += Optional.ofNullable(orderGoods1.getRewardPointPrice()).orElse(BigDecimal.ZERO)
                .doubleValue();
        }

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
        if (totalReturnPrice > refundedAmount) {
            newOrder.setRefundState(OrderState.REFUND_STATE_SOM);
        } else {
            newOrder.setRefundState(OrderState.REFUND_STATE_ALL);
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
    public List<ShopRefundReturn> findByOrderId(long orderId) {
        return shopRefundReturnDao.findByOrderId(orderId);
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


}
