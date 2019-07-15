package com.framework.loippi.entity.user;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity - 支付流水表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_MEMBER_PAYMENT_TALLY")
public class ShopMemberPaymentTally implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 支付流水表id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 支付代码名称
     */
    @Column(name = "payment_code")
    private String paymentCode;

    /**
     * 支付名称
     */
    @Column(name = "payment_name")
    private String paymentName;

    /**
     * 支付状态:1,成功;2,失败;
     */
    @Column(name = "payment_state")
    private Integer paymentState;

    /**
     * 交易编号(商城内部交易单号)
     */
    @Column(name = "payment_sn")
    private String paymentSn;

    /**
     * 1:PC;2:APP;3:h5
     */
    @Column(name = "payment_from")
    private Integer paymentFrom;

    /**
     * 交易金额
     */
    @Column(name = "payment_amount")
    private BigDecimal paymentAmount;

    /**
     * 交易类型:10,商城订单;20,充值,30门店订单
     */
    @Column(name = "trade_type")
    private Integer tradeType;

    /**
     * 交易流水号
     */
    @Column(name = "trade_sn")
    private String tradeSn;

    /**
     * 买家id
     */
    @Column(name = "buyer_id")
    private Long buyerId;

    /**
     * 买家名称
     */
    @Column(name = "buyer_name")
    private String buyerName;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    private Date searchStartTime;
    private Date searchEndTime;

}
