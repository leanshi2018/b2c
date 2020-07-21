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
 * 旅游券单券信息
 * @author :zc
 * @date:2020/7/7
 * @description:dubbo com.framework.loippi.entity.travel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_travel_ticket_detail")
public class RdTravelTicketDetail implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 主键id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 旅游券id
     */
    @Column(name = "travel_id" )
    private Long travelId;

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
     * 旅游券详情编号
     */
    @Column(name = "ticket_sn" )
    private String ticketSn;

    /**
     * 旅游券详情状态 0未使用 1：报名占用 2：已核销 3：已过期
     */
    @Column(name = "status" )
    private Integer status;

    /**
     * 持有人会员编号
     */
    @Column(name = "own_code" )
    private String ownCode;

    /**
     * 持有人昵称
     */
    @Column(name = "own_nick_name" )
    private String ownNickName;

    /**
     * 持有（获取）时间
     */
    @Column(name = "own_time" )
    private Date ownTime;

    /**
     * 使用时间
     */
    @Column(name = "use_time" )
    private Date useTime;

    /**
     * 关联旅游活动id
     */
    @Column(name = "use_activity_id" )
    private Long useActivityId;

    /**
     * 使用于旅游活动编号
     */
    @Column(name = "use_activity_code" )
    private String useActivityCode;

    /**
     * 核销时间
     */
    @Column(name = "confirm_time" )
    private Date confirmTime;

    /**
     * 核销人
     */
    @Column(name = "confirm_code" )
    private String confirmCode;

    /**
     * 旅游券详情图片
     */
    @Column(name = "image" )
    private String image;

    //查询字段
    private Integer elseState;

    //获取时间范围左边限
    private String ownTimeLeft;

    //获取时间范围右边限
    private String ownTimeRight;
}
