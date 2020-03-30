package com.framework.loippi.result.sys;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * 功能： 后台单个订单列表返回结果
 * 类名：OrderAdminVo
 * 日期：2017/7/31  17:40
 * 作者：czl
 * 详细说明： 后台订单列表封装vo
 * 修改备注:
 */
@Data
public class OrderView {

    /**
     * 订单id
     */
    private Long id;

    /**
     * 商品数量
     */
    private Integer goodsCount;

    /**
     * 订单金额
     */
    private BigDecimal orderTotalPrice;

    /**
     * 订单实付金额
     */
    private BigDecimal orderAmount;

    /** 买家手机号码 */
    private String buyerPhone;

    /** 商品总价格 */
    private java.math.BigDecimal goodsAmount;

    /** 优惠总金额 */
    private java.math.BigDecimal discount;

    /** 运费价格 */
    private java.math.BigDecimal shippingFee;

    /** 积分抵扣金额 */
    private java.math.BigDecimal pointRmbNum;

    /** 录入业务周期:YYYYMM */
    private String creationPeriod;
    /**
     * 订单类型  1 零售订单 2 会员订单 3 pv订单 4 优惠订单 5 换购订单
     */
    private Integer orderType;

    /**
     * 微信  app  PC
     */
    private Integer orderPlatform;

    /**
     * 发货备注
     */
    private String deliverExplain;

    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 商品编号
     */
    private String goodsSn;

    /**
     * 收货人名称
     */
    private String receiverName;

    /**
     * 收货人手机
     */
    private String receiverPhone;

    /**
     * 支付方式编码
     */
    private String paymentCode;

    /**
     * 订单状态 - 待付款 待发货 待收货 交易完成
     */
    private String orderState;

    /**
     * 支付单编号
     */
    private String paySn;

    /**
     * 支付方式名称
     */
    private String paymentName;

    /**
     * 0未付款 1已付款
     */
    private Integer paymentState;

    /**
     * 支付时间
     */
    private Date paymentTime;

    /**
     * 会员名
     */
    private String accountName;
    /**  1快递 2自提 */
    private Integer logisticType;
    /** 物流单号 */
    private String shippingCode;

    /** 配送公司编号 */
    private String shippingExpressCode;
    /**
     * 商家id
     */
    private Long storeId;
    /**
     * 订单pv值
     */
    private BigDecimal ppv;
    /**
     * 购买者id
     */
    private Long buyerId;
    /**
     * 商家名称
     */
    private String storeName;
    /**
     * 评论状态
     */
    private Integer evalsellerStatus;
    /**
     * 订单备注
     */
    private String orderMessage;

    private Date cancelTime;

    private String keyWordsBuyerId;

    private Date createTime;

    private Date updateTime;

    private String searchStartTime;

    private String searchEndTime;

    private String payStartTime;

    private String payEndTime;

    private String paymentType;
    private List<Long> ids;

    private Long unequalStoreId;

    private Integer submitStatus;
    private String failInfo;

    /**
     *订单关联通联支付分账状态 0:未分账 1:不满足分账条件 2:已经分账  3:分账失败 4.分账进行中
     */
    private Integer cutStatus;



}
