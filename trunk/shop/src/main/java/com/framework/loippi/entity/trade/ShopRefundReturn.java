package com.framework.loippi.entity.trade;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 退款退货表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_REFUND_RETURN")
public class ShopRefundReturn implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 记录ID
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 订单ID
     */
    @Column(name = "order_id")
    private Long orderId;

    /**
     * 订单编号
     */
    @Column(name = "order_sn")
    private String orderSn;

    /**
     * 申请编号
     */
    @Column(name = "refund_sn")
    private String refundSn;

    /**
     * 店铺ID
     */
    @Column(name = "store_id")
    private Long storeId;

    /**
     * 店铺名称
     */
    @Column(name = "store_name")
    private String storeName;

    /**
     * 买家ID
     */
    @Column(name = "buyer_id")
    private Long buyerId;

    /**
     * 买家会员名
     */
    @Column(name = "buyer_name")
    private String buyerName;

    /**
     * 买家手机号
     */
    @Column(name = "buyer_mobile")
    private String buyerMobile;

    /** 售后商品数量 */
    @Column(name = "goods_num" )
    private Integer goodsNum;



    /**
     * 返回积分
     */
    @Column(name = "reward_point_amount")
    private BigDecimal rewardPointAmount;

    /**
     * 退款金额
     */
    @Column(name = "refund_amount")
    private java.math.BigDecimal refundAmount;


    /**
     * 订单商品类型:1默认2团购商品3限时折扣商品4组合套装
     */
    @Column(name = "order_goods_type")
    private Integer orderGoodsType;

    /**
     * 申请类型:1为退款,2为退货退款,3为换货默认为1
     */
    @Column(name = "refund_type")
    private Integer refundType;

    /**
     * 卖家处理状态:0为待审核,1审核确认,2为同意,3为不同意, 4为完成 默认为01
     */
    @Column(name = "seller_state")
    private Integer sellerState;

    /**
     * 申请状态:1为处理中,2为待管理员处理,3为已完成,默认为1
     */
    @Column(name = "refund_state")
    private Integer refundState;

    /**
     * 退货类型:1为不用退货,2为需要退货,默认为1
     */
    @Column(name = "return_type")
    private Integer returnType;

    /**
     * 订单锁定类型:1为不用锁定,2为需要锁定,默认为1
     */
    @Column(name = "order_lock")
    private Integer orderLock;

    /**
     * 物流状态:1为待发货,2为待收货,3为未收到,4为已收货,默认为1
     */
    @Column(name = "goods_state")
    private Integer goodsState;

    /**
     * 添加时间
     */
    @Column(name = "create_time")
    private java.util.Date createTime;

    /**
     * 卖家处理时间
     */
    @Column(name = "seller_time")
    private java.util.Date sellerTime;

    /**
     * 管理员处理时间
     */
    @Column(name = "admin_time")
    private java.util.Date adminTime;

    /**
     * 原因ID:0为其它
     */
    @Column(name = "reason_id")
    private Long reasonId;

    /**
     * 原因内容
     */
    @Column(name = "reason_info")
    private String reasonInfo;

    /**
     * 图片
     */
    @Column(name = "pic_info")
    private String picInfo;

    /**
     * 申请原因
     */
    @Column(name = "buyer_message")
    private String buyerMessage;

    /**
     * 卖家备注
     */
    @Column(name = "seller_message")
    private String sellerMessage;

    /**
     * 管理员备注
     */
    @Column(name = "admin_message")
    private String adminMessage;

    /**
     * 物流公司编号
     */
    @Column(name = "express_id")
    private Long expressId;

    /**
     * 物流单号
     */
    @Column(name = "invoice_no")
    private String invoiceNo;

    /**
     * 发货时间
     */
    @Column(name = "ship_time")
    private java.util.Date shipTime;

    /**
     * 收货延迟时间
     */
    @Column(name = "delay_time")
    private java.util.Date delayTime;

    /**
     * 收货时间
     */
    @Column(name = "receive_time")
    private java.util.Date receiveTime;

    /**
     * 收货备注
     */
    @Column(name = "receive_message")
    private String receiveMessage;

    /**
     * 佣金比例
     */
    @Column(name = "commis_rate")
    private Integer commisRate;

    /**
     * 物流公司名称
     */
    @Column(name = "express_name")
    private String expressName;

    /**
     * 退款批次号
     */
    @Column(name = "batch_no")
    private String batchNo;
    /**
     * 退款批次号
     */
    @Column(name = "brand_name")
    private String brandName;
    /**
     *  物流公司编号
     */
    @Column(name = "express_code")
    private String expressCode;

    /**
     *  商品pv值
     */
    @Column(name = "ppv")
    private BigDecimal ppv;

    /*********************
     * 添加
     *********************/

    private String specName;

    /**
     * 搜索
     */
    private String refundSnKeyWord;
    //关键字手机号
    private String keyWordMobile;
    //关键字会员编号
    private String keyWordMemberId;
    //关键字订单号
    private String keyWordOrderId;
    //查询条件开始时间
    private String startTime;
    //查询条件结束时间
    private String endTime;
    private String notApproved;

}
