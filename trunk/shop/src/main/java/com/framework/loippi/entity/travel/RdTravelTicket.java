package com.framework.loippi.entity.travel;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author :zc
 * @date:2020/7/7
 * @description:dubbo com.framework.loippi.entity.travel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_travel_ticket")
public class RdTravelTicket implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;
    /**
     * 主键id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 旅游券名称
     */
    @Column(name = "travel_name" )
    private String travelName;

    /**
     * 旅游券面值
     */
    @Column(name = "ticket_price" )
    private BigDecimal ticketPrice;

    /**
     * 旅游券图片
     */
    @Column(name = "image" )
    private String image;

    /**
     * 使用说明
     */
    @Column(name = "remark" )
    private String remark;

    /**
     * 使用开始时间
     */
    @Column(name = "use_start_time" )
    private Date useStartTime;

    /**
     * 使用结束时间
     */
    @Column(name = "use_end_time" )
    private Date useEndTime;

    /**
     * 创建人会员编号
     */
    @Column(name = "create_code" )
    private String createCode;

    /**
     * 创建人姓名
     */
    @Column(name = "create_name" )
    private String createName;

    /**
     * 创建时间
     */
    @Column(name = "create_time" )
    private Date createTime;
}
