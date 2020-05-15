package com.framework.loippi.entity.common;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 日会员订单订单统计记录
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_daily_order_census")
public class DailyOrderCensus implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 主键自增
     */
    @Column(id = true, name = "id", updatable = false)

    private Long id;
    /**
     * 日期 格式YYYY-MM-DD
     */
    @Column(name = "report_code" )
    private String reportCode;

    /**
     * 数据生成时间
     */
    @Column(name = "statistical_time" )
    private Date statisticalTime;

    /**
     * 总订单数量
     */
    @Column(name = "order_num" )
    private Integer orderNum;
    /**
     * 有效订单数
     */
    @Column(name = "effective_order_num" )
    private Integer effectiveOrderNum;
    /**
     * 无效订单数
     */
    @Column(name = "invalid_order_num" )
    private Integer invalidOrderNum;
    /**
     * app订单数
     */
    @Column(name = "order_num_app" )
    private Integer orderNumApp;
    /**
     * 微信小程序订单数
     */
    @Column(name = "order_num_wechat" )
    private Integer orderNumWechat;
    /**
     * 订单总收入
     */
    @Column(name = "order_income_total" )
    private BigDecimal orderIncomeTotal;
    /**
     * 积分占比
     */
    @Column(name = "point_proportion" )
    private BigDecimal pointProportion;
    /**
     * 平均客单价
     */
    @Column(name = "unit_price" )
    private BigDecimal unitPrice;
    /**
     * 零售订单数
     */
    @Column(name = "retail_order_num" )
    private Integer retailOrderNum;
    /**
     * 零售订单总额
     */
    @Column(name = "retail_income_total" )
    private BigDecimal retailIncomeTotal;
    /**
     * 零售订单客单价
     */
    @Column(name = "retail_unit_price" )
    private BigDecimal retailUnitPrice;
    /**
     * 会员订单数
     */
    @Column(name = "vip_order_num" )
    private Integer vipOrderNum;
    /**
     * 会员订单总额
     */
    @Column(name = "vip_income_total" )
    private BigDecimal vipIncomeTotal;
    /**
     * 会员订单客单价
     */
    @Column(name = "vip_unit_price" )
    private BigDecimal vipUnitPrice;
    /**
     * 大单订单数
     */
    @Column(name = "big_order_num" )
    private Integer bigOrderNum;
    /**
     * 大单订单总额
     */
    @Column(name = "big_income_total" )
    private BigDecimal bigIncomeTotal;
    /**
     * 大单订单客单价
     */
    @Column(name = "big_unit_price" )
    private BigDecimal bigUnitPrice;
}
