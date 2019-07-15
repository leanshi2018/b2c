package com.framework.loippi.vo.activity;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 推荐返佣
 */
@Data
@ToString
public class ShopOrderRebateVo implements GenericEntity {
    /** id */
    private Long id;

    /** 返佣订单id */
    private Long rebateOrderid;

    /** 被推荐人id 即为购买者id*/
    private Long rebateRecommendedid;

    /** 订单应付金额**/
    private BigDecimal  orderAmount;

    /** 类型 */
    private Integer rebateType;

    /** 返佣时间 */
    private Date rebateDate;
}
