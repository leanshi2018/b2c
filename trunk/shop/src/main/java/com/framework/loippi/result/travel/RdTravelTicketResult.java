package com.framework.loippi.result.travel;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 用户旅游券分类信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RdTravelTicketResult implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;
    /**
     * 旅游券id
     */
    private Long travelId;
    /**
     * 旅游券名称
     */
    private String travelName;
    /**
     * 剩余可用数量
     */
    private Integer ownNum;
    /**
     * 旅游券面值
     */
    private BigDecimal ticketPrice;
}
