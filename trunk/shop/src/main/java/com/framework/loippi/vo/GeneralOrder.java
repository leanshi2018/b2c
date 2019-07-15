package com.framework.loippi.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Administrator on 2017/9/2.
 */
@Data
@ToString
public class GeneralOrder {

    private Integer orderState;

    private Long id;

    private String orderSn;

    private BigDecimal orderAmount;

    private String expressCode;

    private Date createTime;

    private BigDecimal freight;

    private String shippingCode;

    private String paySn;

    /**
     * 0未评价 1已评价
     */
    private Integer evaluationStatus;

    private Integer orderType; // 1普通订单 2积分订单

    private List<GeneralOrderGoods> orderGoodsList;

    private Integer isAudit; // 是否已经审核过 0未审核 1已经审核
}
