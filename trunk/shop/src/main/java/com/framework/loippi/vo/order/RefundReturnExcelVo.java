package com.framework.loippi.vo.order;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * 结算管理总账单excel超类
 *
 * @author liukai
 */
@Data
public class RefundReturnExcelVo {

    /**
     * 退货单号
     */
    private String refundSn;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 会员名
     */
    private String buyerName;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 退货金额
     */
    private BigDecimal refundAmount;

    /**
     * 收件人
     */
    private String receiverName;

    /**
     * 手机号码
     */
    private String receiverPhone;


    /**
     * 收货人地址
     */
    private String receiverAddr;

    /**
     * 创建时间
     */
    private Date createTime;
}
