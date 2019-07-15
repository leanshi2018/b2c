package com.framework.loippi.entity.walet;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity - 预存款充值表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_WALLET_RECHARGE")
public class ShopWalletRecharge implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 自增编号
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 记录唯一标示
     */
    @Column(name = "pdr_sn")
    private String pdrSn;

    /**
     * 会员编号
     */
    @Column(name = "pdr_member_id")
    private Long pdrMemberId;

    /**
     * 会员名称
     */
    @Column(name = "pdr_member_name")
    private String pdrMemberName;

    /**
     * 充值金额
     */
    @Column(name = "pdr_amount")
    private BigDecimal pdrAmount;

    /**
     * 支付方式
     */
    @Column(name = "pdr_payment_code")
    private String pdrPaymentCode;

    /**
     * 支付方式
     */
    @Column(name = "pdr_payment_name")
    private String pdrPaymentName;

    /**
     * 第三方支付接口交易号
     */
    @Column(name = "pdr_trade_sn")
    private String pdrTradeSn;

    /**
     * 添加时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 支付状态 0未支付1支付
     */
    @Column(name = "pdr_payment_state")
    private String pdrPaymentState;

    /**
     * 支付时间
     */
    @Column(name = "pdr_payment_time")
    private Date pdrPaymentTime;

    /**
     * 管理员名
     */
    @Column(name = "pdr_admin")
    private String pdrAdmin;

}
