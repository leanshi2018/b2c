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
@Table(name = "SHOP_RETURN_LOG")
public class ShopReturnLog implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 索引ID
     */
    @Column(name = "id")
    private Long id;

    /**  */
    @Column(name = "return_id")
    private Long returnId;

    /**  */
    @Column(name = "return_state")
    private String returnState;

    /**  */
    @Column(name = "change_state")
    private String changeState;

    /**  */
    @Column(name = "state_info")
    private String stateInfo;

    /**  */
    @Column(name = "operator")
    private String operator;

    @Column(name = "create_time")
    private Date createTime;

}
