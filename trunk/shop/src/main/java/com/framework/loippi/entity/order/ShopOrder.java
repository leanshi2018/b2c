package com.framework.loippi.entity.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import com.framework.loippi.utils.validator.Words;

/**
 * Entity - 订单表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_ORDER")
public class ShopOrder implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /** 订单索引id */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /** 订单编号 */
    @Column(name = "order_sn" )
    private String orderSn;

    /** 卖家店铺id */
    @Column(name = "store_id" )
    private Long storeId;

    /** 卖家店铺名称 */
    @Column(name = "store_name" )
    private String storeName;

    /** 买家id */
    @Column(name = "buyer_id" )
    private Long buyerId;

    /** 买家姓名 */
    @Column(name = "buyer_name" )
    private String buyerName;

    /** 买家手机号码 */
    @Column(name = "buyer_phone" )
    private String buyerPhone;

    /** 订单生成时间 */
    @Column(name = "create_time" )
    private java.util.Date createTime;

    /** 订单类型  1.零售价 2.会员价 3.PV大单价 4.优惠额度 5 换购积分 */
    @Column(name = "order_type" )
    private Integer orderType;

    /** 来自什么平台的订单 默认2 1 pc，2 app */
    @Column(name = "order_platform" )
    private Integer orderPlatform;

    /** 支付方式id */
    @Column(name = "payment_id" )
    private Long paymentId;

    /** 支付方式名称 */
    @Column(name = "payment_name" )
    private String paymentName;

    /** 支付方式名称代码 */
    @Column(name = "payment_code" )
    private String paymentCode;

    /** 支付分支 */
    @Column(name = "payment_branch" )
    private String paymentBranch;

    /** 付款状态:0:未付款;1:已付款 */
    @Column(name = "payment_state" )
    private Integer paymentState;

    /** 订单编号，外部支付时使用，有些外部支付系统要求特定的订单编号 */
    @Column(name = "out_sn" )
    private String outSn;

    /** 交易流水号 */
    @Column(name = "trade_sn" )
    private String tradeSn;

    /** 支付(付款)时间 */
    @Column(name = "payment_time" )
    private java.util.Date paymentTime;

    /** 支付留言 */
    @Column(name = "pay_message" )
    private String payMessage;

    /** 配送时间 */
    @Column(name = "shipping_time" )
    private java.util.Date shippingTime;

    /** 配送公司ID */
    @Column(name = "shipping_express_id" )
    private Long shippingExpressId;

    /** 物流单号 */
    @Column(name = "shipping_code" )
    private String shippingCode;

    /** 外部交易平台单独使用的标识字符串 */
    @Column(name = "out_payment_code" )
    private String outPaymentCode;

    /** 订单完成时间 */
    @Column(name = "finnshed_time" )
    private java.util.Date finnshedTime;

    /** 商品总价格 */
    @Column(name = "goods_amount" )
    private java.math.BigDecimal goodsAmount;

    /** 优惠总金额 */
    @Column(name = "discount" )
    private java.math.BigDecimal discount;

    /** 等级优惠总金额 */
    @Column(name = "rank_discount" )
    private java.math.BigDecimal rankDiscount;

    /** 优惠券优惠金额 */
    @Column(name = "coupon_discount" )
    private java.math.BigDecimal couponDiscount;

    /** 订单应付金额(现金支付) */
    @Column(name = "order_amount" )
    private java.math.BigDecimal orderAmount;

    /** 订单总价格 */
    @Column(name = "order_total_price" )
    private java.math.BigDecimal orderTotalPrice;

    /** 运费价格 */
    @Column(name = "shipping_fee" )
    private java.math.BigDecimal shippingFee;

    /** 配送方式 */
    @Column(name = "shipping_name" )
    private String shippingName;

    /** 评价状态 0为评价，1已评价 */
    @Column(name = "evaluation_status" )
    private Integer evaluationStatus;

    /** 评价时间 */
    @Column(name = "evaluation_time" )
    private java.util.Date evaluationTime;

    /** 卖家是否已评价买家 */
    @Column(name = "evalseller_status" )
    private Long evalsellerStatus;

    /** 卖家评价买家的时间 */
    @Column(name = "evalseller_time" )
    private java.util.Date evalsellerTime;

    /** 订单留言 */
    @Column(name = "order_message" )
    private String orderMessage;

    /** 订单状态：0:已取消;5待审核;10:待付款;20:待发货;30:待收货;40:交易完成;50:已提交;60:已确认; */
    @Column(name = "order_state" )
    private Integer orderState;

    /** 订单赠送积分 */
    @Column(name = "order_pointscount" )
    private Integer orderPointscount;

    /** 退款状态:0是无退款,1是部分退款,2是全部退款 */
    @Column(name = "refund_state" )
    private Integer refundState;

    /** 退货状态:0是无退货,1是部分退货,2是全部退货 */
    @Column(name = "return_state" )
    private Integer returnState;

    /** 退款金额 */
    @Column(name = "refund_amount" )
    private java.math.BigDecimal refundAmount;

    /** 退货数量 */
    @Column(name = "return_num" )
    private Integer returnNum;

    /** 限时折扣编号 */
    @Column(name = "xianshi_id" )
    private Long xianshiId;

    /** 限时折扣说明 */
    @Column(name = "xianshi_explain" )
    private String xianshiExplain;

    /** 满就送编号 */
    @Column(name = "mansong_id" )
    private Long mansongId;

    /** 满就送说明 */
    @Column(name = "mansong_explain" )
    private String mansongExplain;

    /** 搭配套餐id */
    @Column(name = "bundling_id" )
    private Long bundlingId;

    /** 搭配套餐说明 */
    @Column(name = "bundling_explain" )
    private String bundlingExplain;

    /**
     * 发货备注
     */
    @Length(max = 50, message = "发货备注0和50之间")
    @Words(field = "会员名称", message = "会员名称包含敏感词")
    @Column(name = "deliver_explain")
    private String deliverExplain;

    /** 发货地址ID */
    @Column(name = "daddress_id" )
    private Long daddressId;

    /** 收货地址ID */
    @Column(name = "address_id" )
    private Long addressId;

    /** 订单支付表id */
    @Column(name = "pay_id" )
    private Long payId;

    /** 订单支付表编号 */
    @Column(name = "pay_sn" )
    private String paySn;

    /** 结算状态:0,未结算,1已结算 */
    @Column(name = "balance_state" )
    private Integer balanceState;

    /** 结算时间 */
    @Column(name = "balance_time" )
    private java.util.Date balanceTime;

    /** 配送公司编号 */
    @Column(name = "shipping_express_code" )
    private String shippingExpressCode;

    /** 余额支付金额 */
    @Column(name = "predeposit_amount" )
    private java.math.BigDecimal predepositAmount;

    /** 订单取消原因 */
    @Column(name = "cancel_cause" )
    private String cancelCause;

    /** 锁定状态:0是正常,大于0是锁定,默认是0 */
    @Column(name = "lock_state" )
    private Integer lockState;

    /** 换货状态:70是无换货,80是部分换货,90是全部换货 */
    @Column(name = "barter_state" )
    private Integer barterState;

    /** 换货数量 */
    @Column(name = "barter_num" )
    private Integer barterNum;

    /** 订单所用积分数量 */
    @Column(name = "use_point_num" )
    private BigDecimal usePointNum;

    /** 积分抵扣金额 */
    @Column(name = "point_rmb_num" )
    private BigDecimal pointRmbNum;

    /** 订单删除 默认未删：0  已删：1 */
    @Column(name = "is_del" )
    private Integer isDel;

    /** 取消订单退款批次号 */
    @Column(name = "batch_no" )
    private String batchNo;

    /** 是否已经提醒发货 */
    @Column(name = "is_remind" )
    private Integer isRemind;

    /** 后台是否修改过订单  0 没修改 1修改过 */
    @Column(name = "is_modify" )
    private Integer isModify;

    /** 品牌id */
    @Column(name = "brand_id" )
    private Long brandId;

    /** 品牌名称 */
    @Column(name = "brand_name" )
    private String brandName;

    /** 订单pv值 */
    @Column(name = "ppv" )
    private BigDecimal ppv;

    /** 运费优惠价格 */
    @Column(name = "shipping_preferential_fee" )
    private java.math.BigDecimal shippingPreferentialFee;

    /** 上次提醒发货时间 */
    @Column(name = "remind_time" )
    private java.util.Date remindTime;

    /** 订单类型id*/
    @Column(name = "shop_order_type_id" )
    private Long shopOrderTypeId;

    /** 录入业务周期:YYYYMM */
    @Column(name = "creation_period" )
    private String creationPeriod;

    /**  1快递 2自提 */
    @Column(name = "logistic_type" )
    private Integer logisticType;

    /** 售后积分 */
    @Column(name = "refund_point" )
    private BigDecimal refundPoint;

    /** 售后pv */
    @Column(name = "refund_ppv" )
    private BigDecimal refundPpv;
    /**
     * 上一订单状态
     */
    private Integer prevOrderState;

    /**
     * 上一锁定状态
     */
    private Integer prevLockState;

    /**
     * 订单总额
     */
    private BigDecimal countOrderPrice;

    /**
     * 订单数
     */
    private Integer orderNum;

    /**
     *订单取消时间，默认为null
     */
    @Column(name = "cancel_time" )
    private java.util.Date cancelTime;

    /**
     *提交状态  未提交：0   已提交：10  提交失败：20  默认：0
     */
    @Column(name = "submit_status" )
    private Integer submitStatus;

    /**
     *提交失败原因
     */
    @Column(name = "fail_info" )
    private String failInfo;

    /**
     *订单关联通联支付分账状态 0:未分账 1:不满足分账条件 2:已经分账  3:分账失败 4：分账进行中
     */
    @Column(name = "cut_status" )
    private Integer cutStatus;

    /**
     * 不满足分账条件或分账失败 原因备注
     */
    @Column(name = "cut_fail_info" )
    private String cutFailInfo;

    /**
     * 订单自动提现分账受益人
     */
    @Column(name = "cut_get_id" )
    private String cutGetId;

    /**
     * 分账金额 注：具体分账金额视分账状态而定，仅当分账状态为2时成立
     */
    @Column(name = "cut_amount" )
    private BigDecimal cutAmount;

    /**
     * 分账扣减积分 注：具体分账金额视分账状态而定，仅当分账状态为2时成立
     */
    @Column(name = "cut_acc" )
    private BigDecimal cutAcc;

    /**
     * 分账时间
     */
    @Column(name = "cut_time" )
    private Date cutTime;

    /**
     * 是否分单 0：不分单 1：分单
     */
    @Column(name = "split_flag" )
    private Integer splitFlag;

    /**
     * 0：购物车结算订单 1：立即购买订单
     */
    @Column(name = "immediately_flag" )
    private Integer immediatelyFlag;

    /**
     * plus vip订单记入零售购买额，用于周期结算
     */
    @Column(name = "retail_amount" )
    private BigDecimal retailAmount;
/*********************添加*********************/
    /**
     * 订单商品
     */
    public List<ShopOrderGoods> shopOrderGoodses;

//******************通联**************************
/*    *//**
     *订单关联通联支付分账状态 0:未分账 1:不满足分账条件 2:已经分账  3:分账失败 4.分账进行中
     *//*
    @Column(name = "cut_status" )
    private Integer cutStatus;
    *//**
     *不满足分账条件或分账失败 原因备注
     *//*
    @Column(name = "cut_fail_info" )
    private String cutFailInfo;
    *//**
     *分账受益人会员编号
     *//*
    @Column(name = "cut_get_id" )
    private String cutGetId;
    *//**
     *分账金额 注：具体分账金额视分账状态而定，仅当分账状态为2时成立
     *//*
    @Column(name = "cut_amount" )
    private BigDecimal cutAmount;
    *//**
     *分账扣减积分 注：具体分账金额视分账状态而定，仅当分账状态为2时成立
     *//*
    @Column(name = "cut_acc" )
    private BigDecimal cutAcc;
    *//**
     *分账时间
     *//*
    @Column(name = "cut_time" )
    private Date cutTime;*/
}
