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
 * Entity - plus订单产生积分奖励
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "plus_profit")
public class PlusProfit implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;

    @Column(name = "id" )
    private long id;//主键id

    @Column(name = "buyer_id" )
    private String buyerId;//plus订单购买人

    @Column(name = "receiptor_id" )
    private String receiptorId;//受益人会员编号

    @Column(name = "create_time" )
    private Date createTime;//产生时间

    @Column(name = "expect_time" )
    private Date expectTime;//预计发放时间

    @Column(name = "actual_time" )
    private Date actualTime;//实际发放时间

    @Column(name = "profits" )
    private BigDecimal profits;//plus订单产生奖励积分

    @Column(name = "order_id" )
    private long orderId;//关联订单表id

    @Column(name = "order_sn" )
    private String orderSn;//关联订单表订单编号

    @Column(name = "state" )
    private Integer state;//0:未发放  1：已发放   2：售后取消  3.已抵扣使用（plus会员购买plus订单，不给推荐人奖励积分，直接抵扣订单金额） -1：订单取消

    @Column(name = "remark" )
    private String remark;//备注

    @Column(name = "create_period" )
    private String createPeriod;//奖励积分产生周期

    @Column(name = "expect_period" )
    private String expectPeriod;//奖励积分预计发放周期

    @Column(name = "actual_period" )
    private String actualPeriod;//奖励积分实际发放周期

    private String expectTimeFlag;
}
