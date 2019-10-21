package com.framework.loippi.entity.coupon;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    private Long id;
    /**
     * 关联rd_coupon_user表
     */
    private Long rdCouponUserId;
    /**
     * 优惠券id
     */
    private Long couponId;
    /**
     * 单张优惠券序列号
     */
    private String couponSn;
    /**
     * 优惠券使用密码，没有密码默认为0
     */
    private String couponPwd;
    /**
     * 优惠券名称
     */
    private String couponName;
    /**
     * 领取人会员编号
     */
    private String receiveId;
    /**
     * 领取人昵称
     */
    private String receiveNickName;
    /**
     * 领取时间
     */
    private Date receiveTime;
    /**
     * 持有人id （领取时默认领取人和持有人为同一人）
     */
    private String holdId;
    /**
     * 持有人昵称
     */
    private String holdNickName;
    /**
     * 持有时间
     */
    private Date holdTime;
    /**
     * 优惠券使用开始时间
     */
    private Date useStartTime;
    /**
     * 优惠券使用结束时间
     */
    private Date useEndTime;
    /**
     * 优惠券状态 1：已使用 2：未使用 3：已过期 4：已禁用
     */
    private Integer useState;
    /**
     * 使用优惠券时间
     */
    private Date useTime;
    /**
     * 使用优惠券关联订单id
     */
    private Long useOrderId;
}
