package com.framework.loippi.vo.order;

import com.framework.loippi.mybatis.ext.annotation.Column;

import java.math.BigDecimal;
import java.util.Date;

import javolution.util.Index;
import lombok.Data;


/**
 * 功能： 后台单个订单项列表返回结果
 * 类名：OrderResult
 * 日期：2017/8/05  11:11
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
@Data
public class OrderGoodsAdminVo {

    /** 订单商品表索引id */
    private Long id;

    /** 订单id */
    private Long orderId;

    private String orderSn;

    /** 商品名称 */
    private String goodsName;

    /** 商品价格 */
    private BigDecimal goodsPrice;

    /** 商品数量 */
    private Integer goodsNum;

    /** 商品实际成交价 */
    private BigDecimal goodsPayPrice;

    /** 订单项余额支付金额 */
    private BigDecimal goodsPreAmount;

    /** 买家ID */
    private Long buyerId;

    /** 是否已经发货 */
    private Integer isShipped;

    /** 配送公司编号 */
    private String shippingExpressCode;

    /** 物流单号 */
    private String shippingCode;

    /** 发货时间 */
    private Date shippingTime;

    /** 配送公司id */
    private Long shippingExpressId;

    /** 会员名*/
    private String accountName;

    /** 收件人*/
    private String receiverName;

    /** 手机号码*/
    private String receiverPhone;

    /** 收货人地址*/
    private String receiverAddress;

    /** 物流公司*/
    private String shippingExpressName;

    /** 物流费用*/
    private BigDecimal shippingFee;


    private String refundSn; //退款单号
    private BigDecimal refundAmount; // 退货金额
    private Date adminTime; // 退款时间
}
