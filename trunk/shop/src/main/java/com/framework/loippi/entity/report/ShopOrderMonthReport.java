package com.framework.loippi.entity.report;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单月报表（按平台订单支付情况统计）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shop_order_month_report")
public class ShopOrderMonthReport implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     *主键id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 统计日期
     */
    @Column(name = "report_code")
    private String reportCode;

    /**
     * 统计日期
     */
    @Column(name = "report_time")
    private Date reportTime;

    /**
     * 记录生成日期
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 支付订单数
     */
    @Column(name = "pay_num")
    private Integer payNum;

    /**
     * 售后订单数
     */
    @Column(name = "refund_num")
    private Integer refundNum;

    /**
     * 微信实际收入 收入-退款
     */
    @Column(name = "wechat_income")
    private BigDecimal wechatIncome;

    /**
     * 支付宝实际收入 收入-退款
     */
    @Column(name = "alipay_income")
    private BigDecimal alipayIncome;

    /**
     * 通联支付实际收入 收入-退款
     */
    @Column(name = "allinpay_income")
    private BigDecimal allinpayIncome;

    /**
     * 积分支付合计 支付-退款
     */
    @Column(name = "point_income")
    private BigDecimal pointIncome;

    /**
     * 第三方现金合计
     */
    @Column(name = "cash_total")
    private BigDecimal cashTotal;
    /**
     * 合计
     */
    @Column(name = "total")
    private BigDecimal total;




    /**
     * 查询时间左 yyyy-MM
     */
    private String searchTimeLeft;

    /**
     * 查询时间右 yyyy-MM
     */
    private String searchTimeRight;

}
