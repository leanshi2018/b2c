package com.framework.loippi.vo.order;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * 结算管理总账单excel超类
 *
 * @author liukai
 */
@Data
public class OrderExcelVo {
    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 会员号
     */
    private String buyerName;

    /**
     * 商品数量
     */
    private Integer goodsCount;

    /**
     * 订单金额
     */
    private BigDecimal orderTotalPrice;

    /**
     * 实付金额
     */
    private BigDecimal orderAmount;

    /**
     * 收件人
     */
    private String receiverName;

    /**
     * 手机号码
     */
    private String receiverPhone;

    /**
     * 订单状态
     */
    private String orderState;

    /**
     * 订单类型
     */
    private Integer orderType;

    /**
     * 订单来源
     */
    private String orderPlatform; //app或微信端

    /**
     * 下单时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 备注
     */
    private String deliverExplain; //备注
}
