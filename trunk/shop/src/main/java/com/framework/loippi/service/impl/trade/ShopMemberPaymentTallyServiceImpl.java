package com.framework.loippi.service.impl.trade;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.consts.PaymentTallyState;
import com.framework.loippi.dao.coupon.CouponPayDetailDao;
import com.framework.loippi.dao.order.ShopOrderDao;
import com.framework.loippi.dao.user.ShopMemberPaymentTallyDao;
import com.framework.loippi.entity.coupon.CouponPayDetail;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.entity.user.ShopMemberPaymentTally;
import com.framework.loippi.entity.walet.ShopWalletRecharge;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.trade.ShopMemberPaymentTallyService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.vo.activity.ActivityStatisticsVo;
import com.framework.loippi.vo.user.ShopMemberPaymentTallyVo;

/**
 * SERVICE - ShopMemberPaymentTally(支付流水表)
 *
 * @author zijing
 * @version 2.0
 */
@Slf4j
@Service
public class ShopMemberPaymentTallyServiceImpl extends GenericServiceImpl<ShopMemberPaymentTally, Long>
        implements ShopMemberPaymentTallyService {


    @Autowired
    private ShopMemberPaymentTallyDao shopMemberPaymentTallyDao;

    @Autowired
    private ShopOrderDao orderDao;

    @Autowired
    private ShopMemberPaymentTallyDao paymentTallyDao;

    @Autowired
    private TwiterIdService twiterIdService;

    @Autowired
    private CouponPayDetailDao couponPayDetailDao;

    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopMemberPaymentTallyDao);
    }

    @Override
    public void savePaymentTally(String paytype, String payname, ShopOrderPay pay, Integer paytrem,Integer type) {
        //根据支付单号获取订单信息
        List<ShopOrder> orderList = orderDao.findByParams(Paramap.create().put("paySn", pay.getPaySn()));
        if (CollectionUtils.isEmpty(orderList)) {
            return;
        }

        ShopOrder order = orderList.get(0);
        if (order != null) {
            ShopMemberPaymentTally paymentTally = new ShopMemberPaymentTally();
            paymentTally.setPaymentCode(paytype);//保存支付类型
            paymentTally.setPaymentName(payname);//支付名称
            paymentTally.setPaymentSn(pay.getPaySn());//商城内部交易号
            paymentTally.setPaymentAmount(pay.getPayAmount());// 订单交易金额
            if (pay.getPaySn().contains("R")) {//充值
                paymentTally.setTradeType(PaymentTallyState.PAYMENTTALLY_RECHARGE_PAY);
            } else {//订单支付
                paymentTally.setTradeType(PaymentTallyState.PAYMENTTALLY_ORDER_PAY);
            }
            //支付状态
            if (type==2){
                paymentTally.setPaymentState(PaymentTallyState.PAYMENTTALLY_STATE_SUCCESS);
            }else{
                paymentTally.setPaymentState(PaymentTallyState.PAYMENTTALLY_STATE_NOSUCCESS);
            }
            //支付终端类型 1:PC;2:APP;3:h5
            if (paytrem == 1) {
                paymentTally.setPaymentFrom(PaymentTallyState.PAYMENTTALLY_TREM_PC);
            } else if (paytrem == 2) {
                paymentTally.setPaymentFrom(PaymentTallyState.PAYMENTTALLY_TREM_MB);
            } else {
                paymentTally.setPaymentFrom(PaymentTallyState.PAYMENTTALLY_TREM_H5);
            }
            //用户id
            paymentTally.setBuyerId(order.getBuyerId());
            //用户名
            paymentTally.setBuyerName(order.getBuyerName());
            //保存生成时间
            paymentTally.setCreateTime(new Date());
            paymentTally.setId(twiterIdService.getTwiterId());
            //保存流水表记录
            paymentTallyDao.insert(paymentTally);
            //释放资源
            paymentTally = null;
        }
    }

    @Override
    public void savePaymentTallyCoupon(String paytype, String payname, ShopOrderPay pay, Integer paytrem, Integer type) {
        //根据支付单号获取订单信息
        List<CouponPayDetail> couponPayList = couponPayDetailDao.findByParams(Paramap.create().put("paySn", pay.getPaySn()));
        if (CollectionUtils.isEmpty(couponPayList)) {
            return;
        }

        CouponPayDetail couponPay = couponPayList.get(0);
        if (couponPay != null) {
            ShopMemberPaymentTally paymentTally = new ShopMemberPaymentTally();
            paymentTally.setPaymentCode(paytype);//保存支付类型
            paymentTally.setPaymentName(payname);//支付名称
            paymentTally.setPaymentSn(pay.getPaySn());//商城内部交易号
            paymentTally.setPaymentAmount(pay.getPayAmount());// 订单交易金额
            if (pay.getPaySn().contains("R")) {//充值
                paymentTally.setTradeType(PaymentTallyState.PAYMENTTALLY_RECHARGE_PAY);
            } else {//订单支付
                paymentTally.setTradeType(PaymentTallyState.PAYMENTTALLY_ORDER_PAY);
            }
            //支付状态
            if (type==2){
                paymentTally.setPaymentState(PaymentTallyState.PAYMENTTALLY_STATE_SUCCESS);
            }else{
                paymentTally.setPaymentState(PaymentTallyState.PAYMENTTALLY_STATE_NOSUCCESS);
            }
            //支付终端类型 1:PC;2:APP;3:h5
            if (paytrem == 1) {
                paymentTally.setPaymentFrom(PaymentTallyState.PAYMENTTALLY_TREM_PC);
            } else if (paytrem == 2) {
                paymentTally.setPaymentFrom(PaymentTallyState.PAYMENTTALLY_TREM_MB);
            } else {
                paymentTally.setPaymentFrom(PaymentTallyState.PAYMENTTALLY_TREM_H5);
            }
            //用户id
            paymentTally.setBuyerId(new Long(couponPay.getReceiveId()));
            //用户名
            paymentTally.setBuyerName(couponPay.getReceiveNickName());
            //保存生成时间
            paymentTally.setCreateTime(new Date());
            paymentTally.setId(twiterIdService.getTwiterId());
            //保存流水表记录
            paymentTallyDao.insert(paymentTally);
            //释放资源
            paymentTally = null;
        }
    }

    @Override
    public void savePaymentTally(String paytype, String payname, ShopWalletRecharge pay, Integer paytrem) {
        //根据支付单号获取订单信息
        ShopMemberPaymentTally paymentTally = new ShopMemberPaymentTally();
        paymentTally.setPaymentCode(paytype);//保存支付类型
        paymentTally.setPaymentName(payname);//支付名称
        paymentTally.setPaymentSn(pay.getPdrSn());//商城内部交易号
        paymentTally.setPaymentAmount(pay.getPdrAmount());// 订单交易金额
        paymentTally.setTradeType(PaymentTallyState.PAYMENTTALLY_RECHARGE_PAY);
        //支付状态
        paymentTally.setPaymentState(PaymentTallyState.PAYMENTTALLY_STATE_NOSUCCESS);
        //支付终端类型 1:PC;2:APP;3:h5
        if (paytrem == 1) {
            paymentTally.setPaymentFrom(PaymentTallyState.PAYMENTTALLY_TREM_PC);
        } else if (paytrem == 2) {
            paymentTally.setPaymentFrom(PaymentTallyState.PAYMENTTALLY_TREM_MB);
        } else {
            paymentTally.setPaymentFrom(PaymentTallyState.PAYMENTTALLY_TREM_H5);
        }
        //用户id
        paymentTally.setBuyerId(pay.getPdrMemberId());
        //用户名
        paymentTally.setBuyerName(pay.getPdrPaymentName());
        //保存生成时间
        paymentTally.setCreateTime(new Date());
        paymentTally.setId(twiterIdService.getTwiterId());
        //保存流水表记录
        paymentTallyDao.insert(paymentTally);
        //释放资源
        paymentTally = null;
    }

    @Override
    public void updatePaymentTally(String paymentSn, String tradeSn, String paymentBranch) {
        try {
            Paramap paramMap = Paramap.create().put("paymentSn", paymentSn);
            switch (paymentBranch) {
                case "mp_weichatpay":
                    paramMap.put("paymentCode", "weixinH5PaymentPlugin");
                    break;
                case "open_weichatpay":
                    paramMap.put("paymentCode", "weixinMobilePaymentPlugin");
                    break;
                case "alipay":
                    paramMap.put("paymentCode", "alipayMobilePaymentPlugin");
                    break;
                case "waletPaymentPlugin":
                    paramMap.put("paymentCode", "waletPaymentPlugin");
                    break;
                case "scan_weichatpay":
                    paramMap.put("paymentCode", "weiscan");
                    break;
                default:
                    log.error(paymentBranch + "找不到对应的paymentName");
            }
            ShopMemberPaymentTally paymentTallys = this.findList(paramMap).get(0);
            //判断流水状态是否修改
            if (paymentTallys != null
                    && paymentTallys.getPaymentState() == PaymentTallyState.PAYMENTTALLY_STATE_NOSUCCESS) {
                ShopMemberPaymentTally paymentTally = new ShopMemberPaymentTally();
                paymentTally.setId(paymentTallys.getId());
                paymentTally.setPaymentState(PaymentTallyState.PAYMENTTALLY_STATE_SUCCESS);//支付成功
                paymentTally.setPaymentSn(paymentSn);
                paymentTally.setTradeSn(tradeSn);
                paymentTallyDao.update(paymentTally);
            }
        } catch (Exception e) {
            log.error("更改shop_member_payment_tally交易流水日志表状态出错", e);
        }
    }

    /**
     * 财务管理-统计总收入
     */
    public BigDecimal countAmountByStore(Long storeId) {
        return paymentTallyDao.countAmountByStore(storeId);
    }

    public BigDecimal countAmount() {
        return paymentTallyDao.countAmount();
    }

    @Override
    public Page findByPageStore(Pageable pageable) {
        PageList<ShopMemberPaymentTallyVo> shopMemberPaymentTallies = paymentTallyDao
                .findByPageStore(pageable.getParameter(), pageable.getPageBounds());
        shopMemberPaymentTallies.getPaginator().getTotalCount();
        return new Page(shopMemberPaymentTallies, shopMemberPaymentTallies.getPaginator().getTotalCount(), pageable);
    }

    @Override
    public Page findByPageStoreIncome(Pageable pageable) {
        PageList<ShopMemberPaymentTallyVo> shopMemberPaymentTallies = paymentTallyDao
                .findByPageStoreIncome(pageable.getParameter(), pageable.getPageBounds());
        shopMemberPaymentTallies.getPaginator().getTotalCount();
        return new Page(shopMemberPaymentTallies, shopMemberPaymentTallies.getPaginator().getTotalCount(), pageable);
    }

    @Override
    public List<ActivityStatisticsVo> statisticsIncomesBystate(ActivityStatisticsVo param) {
        return paymentTallyDao.statisticsIncomesBystate(param);
    }

}
