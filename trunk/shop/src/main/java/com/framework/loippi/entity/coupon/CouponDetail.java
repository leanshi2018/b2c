package com.framework.loippi.entity.coupon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * 优惠券明细记录表
 * create by zc on 2019/10/18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_coupon_detail")
public class CouponDetail implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;
    /**
     * 主键id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Column(name = "id")
    private Long id;
    /**
     * 关联rd_coupon_user表
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Column(name = "rd_coupon_user_id")
    private Long rdCouponUserId;
    /**
     * 优惠券id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Column(name = "coupon_id")
    private Long couponId;
    /**
     * 单张优惠券序列号
     */
    @Column(name = "coupon_sn")
    private String couponSn;
    /**
     * 优惠券使用密码，没有密码默认为0
     */
    @Column(name = "coupon_pwd")
    private String couponPwd;
    /**
     * 优惠券名称
     */
    @Column(name = "coupon_name")
    private String couponName;
    /**
     * 领取人会员编号
     */
    @Column(name = "receive_id")
    private String receiveId;
    /**
     * 领取人昵称
     */
    @Column(name = "receive_nick_name")
    private String receiveNickName;
    /**
     * 领取时间
     */
    @Column(name = "receive_time")
    private Date receiveTime;
    /**
     * 持有人id （领取时默认领取人和持有人为同一人）
     */
    @Column(name = "hold_id")
    private String holdId;
    /**
     * 持有人昵称
     */
    @Column(name = "hold_nick_name")
    private String holdNickName;
    /**
     * 持有时间
     */
    @Column(name = "hold_time")
    private Date holdTime;
    /**
     * 优惠券使用开始时间
     */
    @Column(name = "use_start_time")
    private Date useStartTime;
    /**
     * 优惠券使用结束时间
     */
    @Column(name = "use_end_time")
    private Date useEndTime;
    /**
     * 优惠券状态 1：已使用 2：未使用 3：已过期 4：已禁用
     */
    @Column(name = "use_state")
    private Integer useState;
    /**
     * 使用优惠券时间
     */
    @Column(name = "use_time")
    private Date useTime;
    /**
     * 使用优惠券关联订单id
     */
    @Column(name = "use_order_id")
    private Long useOrderId;
    /**
     * 使用优惠券关联订单编号
     */
    @Column(name = "use_order_sn")
    private String useOrderSn;
    /**
     * 使用优惠券关联订单支付状态 0:未支付 1：已支付
     */
    @Column(name = "use_order_pay_status")
    private Integer useOrderPayStatus;
    /**
     * 购买优惠券关联订单id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Column(name = "buy_order_id")
    private Long buyOrderId;
    /**
     * 退款状态 0：无需退款（非交易性优惠券）1：未退款 2：已退款
     */
    @Column(name = "refund_state")
    private Integer refundState;
    /**
     * 退款金额
     */
    @Column(name = "refund_sum")
    private BigDecimal refundSum;
    /**
     * 退款批次号
     */
    @Column(name = "batch_no")
    private String batchNo;
    /**
     * 退款时间
     */
    @Column(name = "refund_time")
    private Date refundTime;
    /**
     * 购买订单获取优惠券关联订单号id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Column(name = "get_order_id")
    private Long getOrderId;

    //查询字段
    private Integer elseState;
}
