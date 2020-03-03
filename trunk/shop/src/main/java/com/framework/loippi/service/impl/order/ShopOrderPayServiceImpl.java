package com.framework.loippi.service.impl.order;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.order.ShopOrderPayDao;
import com.framework.loippi.entity.coupon.CouponPayDetail;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.service.coupon.CouponPayDetailService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.order.ShopOrderPayService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.utils.StringUtil;

/**
 * SERVICE - ShopOrderPay(订单支付表)
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopOrderPayServiceImpl extends GenericServiceImpl<ShopOrderPay, Long> implements ShopOrderPayService {

    @Autowired
    private ShopOrderPayDao shopOrderPayDao;

    @Autowired
    private ShopOrderService shopOrderService;

    @Autowired
    private CouponPayDetailService couponPayDetailService;


    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopOrderPayDao);
    }

    @Override
    public ShopOrderPay findBySn(String paySn) {
        if (StringUtil.isEmpty(paySn)) {
            return null;
        }
        ShopOrderPay pay = new ShopOrderPay();
        //新建一个付款状态字段，查询支付单下所有订单支付状态都为已支付后，设置为已支付
        int payState = 1; //默认已支付
        //通过支付单号查询订单集合
        List<ShopOrder> orderList = shopOrderService.findList("paySn", paySn);
        //支付总额
        double amount = 0.00;
        //1在线支付 2货到付款
        int paymentType=1;
        String orderSn="";
        Boolean usePointFlag=false;
        for (ShopOrder order : orderList) {
            orderSn=order.getOrderSn()+",";
            amount += order.getOrderAmount().doubleValue();
            if (order.getPaymentState() == 0) {
                payState = 0;
                if (order.getPaymentName().equals("货到付款")){
                    paymentType=2;
                }
            }
            if(order.getUsePointNum()!=null&&order.getUsePointNum()>0){
                usePointFlag=true;
            }
        }
        //将支付单号存入paySn
        pay.setPaySn(paySn);
        pay.setPayAmount(BigDecimal.valueOf(amount));
        pay.setApiPayState(payState + "");
        pay.setOrderCreateTime(orderList.get(0).getCreateTime());
        pay.setPaymentType(paymentType);
        pay.setOrderSn(orderSn);
        pay.setUsePointFlag(usePointFlag);
        return pay;
    }

    @Override
    public ShopOrderPay findCouponBySn(String paySn) {
        if (StringUtil.isEmpty(paySn)) {
            return null;
        }
        ShopOrderPay pay = new ShopOrderPay();
        //新建一个付款状态字段，查询支付单下所有订单支付状态都为已支付后，设置为已支付
        int payState = 1; //默认已支付
        //通过支付单号查询订单集合
        List<CouponPayDetail> couponPayDetailList = couponPayDetailService.findList("paySn", paySn);
        //支付总额
        double amount = 0.00;
        //1在线支付 2货到付款
        int paymentType=1;
        String couponOrderSn="";

        for (CouponPayDetail couponPayDetail : couponPayDetailList) {
            couponOrderSn=couponPayDetail.getCouponOrderSn()+",";
            amount += couponPayDetail.getOrderAmount().doubleValue();
            if (couponPayDetail.getPaymentState() == 0) {
                payState = 0;
            }
        }
        //将支付单号存入paySn
        pay.setPaySn(paySn);
        pay.setPayAmount(BigDecimal.valueOf(amount));
        pay.setApiPayState(payState + "");
        pay.setOrderCreateTime(couponPayDetailList.get(0).getCreateTime());
        pay.setPaymentType(paymentType);
        pay.setOrderSn(couponOrderSn);
        return pay;
    }
}
