package com.framework.loippi.consts;

public interface AllInPayConstant {
    /**
     * 分账回调地址
     */
    public static final String CUT_BILL_BACKURL="http://7awtnv.natappfree.cc/admin_war_exploded/admin/allinpayContract/cutBack.jhtml";
    /**
     * 通联网络签约地址 正式测试区分
     */
    public static final String SIGN_URL="http://116.228.64.55:6900/yungateway/member/signContract.html?";
    //public static final String SIGN_URL="https://fintech.allinpay.com/yungateway/member/signContract.html?";

    /**
     * 通联网络签约后台回调地址
     */
    public static final String SIGN_BACK_URL="http://7awtnv.natappfree.cc/admin_war_exploded/admin/allinpayContract/signBack.jhtml";
    //public static final String SIGN_BACK_URL="https://fintech.allinpay.com/yungateway/member/signContract.html?";

    /**
     * 通联网络签约app回调地址
     */
    //public static final String SIGN_BACK_URL_APP="http://www.baidu.com";
    public static final String SIGN_BACK_URL_APP="http://www.rdnmall.com/signing.html";
}
