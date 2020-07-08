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
 * 旅游活动
 * @author :zc
 * @date:2020/7/7
 * @description:dubbo com.framework.loippi.entity.travel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_travel_activity")
public class RdTravelActivity implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 主键id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 旅游活动名称
     */
    @Column(name = "activity_name" )
    private String activityName;

    /**
     * 旅游活动图片 若多张图片逗号隔开
     */
    @Column(name = "image" )
    private String image;

    /**
     * 报名开始时间
     */
    @Column(name = "start_time" )
    private Date startTime;

    /**
     * 报名结束时间
     */
    @Column(name = "end_time" )
    private Date endTime;

    /**
     * 创建人会员编号
     */
    @Column(name = "create_code" )
    private String createCode;

    /**
     * 创建时间
     */
    @Column(name = "create_time" )
    private Date createTime;

    /**
     * 旅游活动费用
     */
    @Column(name = "activity_cost" )
    private BigDecimal activityCost;

    /**
     * 旅游活动说明
     */
    @Column(name = "remark" )
    private String remark;

    /**
     * 旅游活动详情链接
     */
    @Column(name = "detail_link" )
    private String detailLink;
}
