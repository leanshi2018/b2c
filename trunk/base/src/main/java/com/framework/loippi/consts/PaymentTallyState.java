package com.framework.loippi.consts;

public final class PaymentTallyState {

    /**
     * 支付状态:1,成功
     */
    public final static int PAYMENTTALLY_STATE_SUCCESS = 1;

    /**
     * 支付状态:0,未成功
     */
    public final static int PAYMENTTALLY_STATE_NOSUCCESS = 2;

    /**
     * 支付终端类型 1:PC
     */
    public final static int PAYMENTTALLY_TREM_PC = 1;

    /**
     * 支付终端类型 2:mobile
     */
    public final static int PAYMENTTALLY_TREM_MB = 2;

    /**
     * 支付终端类型 3:h5
     */
    public final static int PAYMENTTALLY_TREM_H5 = 3;

    /**
     * 支付终端类型 3:h5
     */
    public final static int PAYMENTTALLY_TREM_WALLET = 4;

    /**
     * 支付终端类型 3:h5
     */
    public final static int PAYMENTTALLY_TREM_CREDIT = 5;

    /**
     * 交易类型:10 订单支付
     */
    public final static int PAYMENTTALLY_ORDER_PAY = 10;

    /**
     * 交易类型:20充值
     */
    public final static int PAYMENTTALLY_RECHARGE_PAY = 20;

    /**
     * 交易类型:30 还贷款
     */
    public final static int PAYMENTTALLY_RECHARGE_REFOUND = 30;

    /**
     * 交易类型:40 取消订单 退款
     */
    public final static int PAYMENTTALLY_RECHARGE_CANCEL_ORDER = 40;


    /**
     * 售后
     */
    public final static int PAYMENTTALLY_RECHARGE_REFUND_RETURN = 50;
}
