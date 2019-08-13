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
 * Entity - 零售利润记录表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "retail_profit")
public class RetailProfit implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;

    @Column(name = "id" )
    private long id;//主键id

    @Column(name = "buyer_id" )
    private String buyerId;//零售订单购买人

    @Column(name = "receiptor_id" )
    private String receiptorId;//受益人会员编号

    @Column(name = "create_time" )
    private Date createTime;//产生时间

    @Column(name = "expect_time" )
    private Date expectTime;//预计发放时间

    @Column(name = "actual_time" )
    private Date actualTime;//实际发放时间

    @Column(name = "profits" )
    private BigDecimal profits;//零售订单产生零售利润

    @Column(name = "order_id" )
    private long orderId;//关联订单表id

    @Column(name = "order_sn" )
    private String orderSn;//关联订单表订单编号

    @Column(name = "state" )
    private Integer state;//0:未发放  1：已发放   2：已推迟   -1：已作废

    @Column(name = "remark" )
    private String remark;//备注

    @Column(name = "create_period" )
    private String createPeriod;//零售利润产生周期

    @Column(name = "expect_period" )
    private String expectPeriod;//零售利润预计发放周期

    @Column(name = "actual_period" )
    private String actualPeriod;//零售利润实际发放周期
}
