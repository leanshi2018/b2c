package com.framework.loippi.consts;

/**
 * Created by Administrator on 2018/5/8.
 */
public class ShopOrderDiscountTypeConsts {

    /**
     * 零售价
     */
    public static final int DISCOUNT_TYPE_RETAIL = 1;
    /**
     * 会员价
     */
    public static final int DISCOUNT_TYPE_MEMBER= 2;
    /**
     * PV大单价
     */
    public static final int DISCOUNT_TYPE_PPV = 3;
    /**
     * 优惠额度
     */
    public static final int DISCOUNT_TYPE_PREFERENTIAL = 4;
    /**
     * 换购订单
     */
    public static final int DISCOUNT_TYPE_INTEGRATION = 5;
    /**
     * 换货订单 retransmission
     */
    public static final int DISCOUNT_TYPE_RETRANSMISSION = 6;




    public static String convert(int type){
        if (type==DISCOUNT_TYPE_RETAIL){
            return "零售订单";
        }
        if (type==DISCOUNT_TYPE_MEMBER){
            return "会员订单";
        }
        if (type==DISCOUNT_TYPE_PPV){
            return "pv订单";
        }
        if (type==DISCOUNT_TYPE_PREFERENTIAL){
            return "优惠订单";
        }
        if (type==DISCOUNT_TYPE_INTEGRATION){
            return "换购订单";
        }
        if (type==DISCOUNT_TYPE_RETRANSMISSION){
            return "换货订单";
        }
        return "";
    }




}
