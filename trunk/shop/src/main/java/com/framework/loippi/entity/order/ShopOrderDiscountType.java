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
 * Entity - 优惠订单类型
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_ORDER_DISCOUNT_TYPE")
public class ShopOrderDiscountType implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 订单类型id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 订单类型名称
     */
    @Column(name = "order_name")
    private String orderName;

    /**
     * 优惠金额
     */
    @Column(name = "preferential")
    private BigDecimal preferential;

    /**
     * 优惠类型 1.零售价 2.会员价 3.PV大单价 4.优惠额度
     */
    @Column(name = "preferential_type")
    private Integer preferentialType;

    /**
     * 订单pv额度
     */
    @Column(name = "ppv")
    private BigDecimal ppv;

    /**
     * 排序
     */
    @Column(name = "sort")
    private Integer sort;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 备注
     */
    @Column(name = "remarks")
    private String remarks;

}
