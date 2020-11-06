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
 * Entity - 订单分单表
 *
 * @author zc
 * @date 2020/11/05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shop_order_split")
public class ShopOrderSplit implements GenericEntity {
    /** 订单索引id */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /** 关联订单id */
    @Column(id = true, name = "order_id")
    private Long orderId;

    /** 关联订单编号 */
    @Column(name = "order_sn" )
    private String orderSn;

    /** 会员编号 */
    @Column(name = "mm_code" )
    private String mmCode;

    /** 会员昵称 */
    @Column(name = "mm_nick_name" )
    private String mmNickName;

    /** 购买人及分单人标识 1：购买人 2：分单人 */
    @Column(name = "buy_flag" )
    private Integer buyFlag;

    /** 分单获得mi值 */
    @Column(name = "pv" )
    private BigDecimal pv;

    /** 购买时间 */
    @Column(name = "payment_time" )
    private Date paymentTime;

    /** 产生业务周期 */
    @Column(name = "period_code" )
    private String periodCode;

    /** 状态  1：正常 2：取消  3：未支付 */
    @Column(name = "status" )
    private Integer status;
}
