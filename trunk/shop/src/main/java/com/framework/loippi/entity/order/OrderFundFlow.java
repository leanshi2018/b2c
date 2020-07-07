package com.framework.loippi.entity.order;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity - 订单金额及关联积分流向记录
 * @author zc
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_fund_flow")
public class OrderFundFlow implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 主键id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     *关联订单id
     */
    @Column(name = "order_id" )
    private Long orderId;

    /**
     *订单类型 0：商品订单 1：优惠券订单 2：旅游订单
     */
    @Column(name = "order_type" )
    private Integer orderType;

    /**
     *购买人会员编号
     */
    @Column(name = "buyer_id" )
    private String buyerId;

    /**
     *支付时间
     */
    @Column(name = "pay_time" )
    private Date payTime;

    /**
     *订单状态 0：取消 1：正常
     */
    @Column(name = "state" )
    private Integer state;

    /**
     *支付现金金额
     */
    @Column(name = "cash_amount" )
    private BigDecimal cashAmount;

    /**
     *现金支付方式 0：无现金支付 1：微信支付 2：支付宝支付 3：微信小程序支付
     */
    @Column(name = "cash_pay_type" )
    private Integer cashPayType;

    /**
     *购物积分支付金额
     */
    @Column(name = "point_amount" )
    private BigDecimal pointAmount;

    /**
     *现金退款
     */
    @Column(name = "cash_refund" )
    private BigDecimal cashRefund;

    /**
     *购物积分退款
     */
    @Column(name = "point_refund" )
    private BigDecimal pointRefund;

    /**
     *是否为零售订单 0：否 1：是（发放零售利润时填充）
     */
    @Column(name = "retail_flag" )
    private Integer retailFlag;

    /**
     *零售利润（最终发放时写入）
     */
    @Column(name = "retail_profit" )
    private BigDecimal retailProfit;

    /**
     *零售利润受益人
     */
    @Column(name = "retail_get_id" )
    private String retailGetId;

    /**
     *零售利润发放时间
     */
    @Column(name = "retail_time" )
    private Date retailTime;

    /**
     *是否分账标识 0：否 1：是（分账时填充）
     */
    @Column(name = "cut_flag" )
    private Integer cutFlag;

    /**
     *分账提现积分数（分账时记录）
     */
    @Column(name = "cut_point" )
    private BigDecimal cutPoint;

    /**
     *分账奖励积分自动提现受益人
     */
    @Column(name = "cut_get_id" )
    private String cutGetId;

    /**
     *分账时间
     */
    @Column(name = "cut_time" )
    private Date cutTime;
}
