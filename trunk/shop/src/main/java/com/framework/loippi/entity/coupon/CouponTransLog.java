package com.framework.loippi.entity.coupon;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠券转账记录表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_coupon_trans_log")
public class CouponTransLog implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;
    /**
     * 主键id
     */
    @Column(name = "id")
    private Long id;
    /**
     * 转赠人会员编号
     */
    @Column(name = "turn_id")
    private String turnId;
    /**
     * 转赠人会员昵称
     */
    @Column(name = "turn_nick_name")
    private String turnNickName;
    /**
     * 接收人会员编号
     */
    @Column(name = "accept_id")
    private String acceptId;
    /**
     * 接收人会员昵称
     */
    @Column(name = "accept_nick_name")
    private String acceptNickName;
    /**
     * 交易备注
     */
    @Column(name = "remark")
    private String remark;
    /**
     * 交易时间
     */
    @Column(name = "trans_time")
    private Date transTime;
    /**
     * 交易业务周期
     */
    @Column(name = "trans_period")
    private String transPeriod;
    /**
     * 优惠券id
     */
    @Column(name = "coupon_id")
    private Long couponId;
    /**
     * 优惠券名称
     */
    @Column(name = "coupon_name")
    private String couponName;
    /**
     * 优惠券编号
     */
    @Column(name = "coupon_sn")
    private String couponSn;
}
