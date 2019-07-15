package com.framework.loippi.entity.trade;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity -
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_RETURN_REASON")
public class ShopReturnReason implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;
    /**  */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**  */
    @Column(name = "reason_info")
    private String reasonInfo;

    /**  */
    @Column(name = "sort")
    private Integer sort;

    /**  */
    @Column(name = "update_time")
    private Date updateTime;

    /**  */
    @Column(name = "is_del")
    private Integer isDel;

    /**
     * 0退货原因  1取消订单原因
     */
    @Column(name = "reason_type")
    private Integer reasonType;

}
