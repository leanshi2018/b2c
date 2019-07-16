package com.framework.loippi.service.impl.common;

import com.framework.loippi.consts.PaymentTallyState;
import com.framework.loippi.dao.user.ShopMemberPaymentTallyDao;
import com.framework.loippi.entity.AliPayRefund;
import com.framework.loippi.entity.PayCommon;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.user.RdMmAccountInfo;

import com.framework.loippi.entity.user.ShopMemberPaymentTally;
import com.framework.loippi.service.PaymentService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.trade.ShopMemberPaymentTallyService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.wallet.ShopWalletRechargeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 支付实现
 * Created by longbh on 2017/8/1.
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    @Resource
    private ShopOrderService orderService;
    @Resource
    private ShopMemberPaymentTallyService paymentTallyService;
    @Autowired
    private ShopMemberPaymentTallyDao shopMemberPaymentTallyDao;
    @Resource
    private ShopWalletRechargeService walletRechargeService;

    @Resource
    private RdMmAccountInfoService rdMmAccountInfoService;


    @Override
    public AliPayRefund updateFrontBack(Long bizId) {
        return null;
    }

    @Override
    public PayCommon prePay(String no) {
        return null;
    }

    @Override
    public void updateReturnBack(Integer status, String message, String batchNo) {

    }

    @Override
    public void updatePayBack(String sn, String batchNo, String plug, String totalFee) {
        System.out.print("ok" + sn + "---" + batchNo);
        /**
         * 充值支付回调逻辑处理
         * YMQ 20180203
         */
        Map<String, Object> qyMap = new HashMap<>();
        qyMap.put("paymentSn", sn);
        List<ShopMemberPaymentTally> paymentTallys = shopMemberPaymentTallyDao.findByParams(qyMap);
        ShopMemberPaymentTally paymentTally = null;
        if (paymentTallys != null && paymentTallys.size() > 0) {
            paymentTally = paymentTallys.get(0);
        }
        if (paymentTally != null && paymentTally.getTradeType() == PaymentTallyState.PAYMENTTALLY_RECHARGE_PAY) {
            walletRechargeService.payRechargeOrderCallback(sn);
            return;
        }
        /**
         * 其他
         */
        ShopOrder order = orderService.find("paySn", sn);
        // 回调会有多次
        if (order.getPaymentState() == null || order.getPaymentState() == 0) {//TODO
            orderService.updateOrderStatePayFinish(sn, batchNo, plug);
            //更改支付流水表状态
            paymentTallyService.updatePaymentTally(sn, batchNo, plug);
        }
    }

    @Override
    public void updatePayfailBack(String sn) {
        System.out.print("file" + sn + "---" );
        List<ShopOrder> shopOrderList=orderService.findList("orderSn",sn);
        int totalPointNum=0;
        if (shopOrderList!=null && shopOrderList.size()>0){
            Long memberId=shopOrderList.get(0).getBuyerId();
            for (ShopOrder item:shopOrderList) {
                if (Optional.ofNullable(item.getUsePointNum()).orElse(0)!=0){
                    totalPointNum+=item.getUsePointNum();
                    item.setUsePointNum(0);
                    item.setOrderAmount(item.getOrderAmount().add(item.getPointRmbNum()));
                    item.setPointRmbNum(new BigDecimal("0"));
                    orderService.update(item);
                }
            }
            RdMmAccountInfo rdMmAccountInfo=rdMmAccountInfoService.find("mmCode",memberId);
            rdMmAccountInfo.setWalletBlance(rdMmAccountInfo.getWalletBlance().add(BigDecimal.valueOf(totalPointNum)));
            rdMmAccountInfoService.update(rdMmAccountInfo);
        }

    }

}
