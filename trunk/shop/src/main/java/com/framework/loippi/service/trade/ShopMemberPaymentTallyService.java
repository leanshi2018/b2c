package com.framework.loippi.service.trade;

import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.entity.user.ShopMemberPaymentTally;
import com.framework.loippi.entity.walet.ShopWalletRecharge;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.vo.activity.ActivityStatisticsVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * SERVICE - ShopMemberPaymentTally(支付流水表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopMemberPaymentTallyService extends GenericService<ShopMemberPaymentTally, Long> {

    /**
     * 支付
     *
     * @param paytype
     * @param payname
     * @param pay
     * @param paytrem
     */
    void savePaymentTally(String paytype, String payname, ShopWalletRecharge pay, Integer paytrem);

    /**
     * 保存支付流水记录
     *
     * @param paytype 支付方式
     * @param payname 支付名称
     * @param pay     支付体
     * @param paytrem 支付终端
     */
    void savePaymentTally(String paytype, String payname, ShopOrderPay pay, Integer paytrem,Integer type);

    /**
     * 修改支付流水表
     *
     * @param paymentSn 站内交易单号
     * @param tradeSn   交易流水号
     */
    void updatePaymentTally(String paymentSn, String tradeSn, String plug);

    /**
     * 财务管理-统计总收入
     *
     * @return
     */
    BigDecimal countAmount();

    BigDecimal countAmountByStore(Long storeId);

    Page findByPageStore(Pageable pageable);

    Page findByPageStoreIncome(Pageable pageable);

    /**
     * 统计收入金额  日   周  月
     */
    List<ActivityStatisticsVo> statisticsIncomesBystate(ActivityStatisticsVo param);
}
