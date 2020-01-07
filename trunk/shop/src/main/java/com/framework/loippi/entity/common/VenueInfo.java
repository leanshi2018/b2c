package com.framework.loippi.entity.common;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_venue_info")
public class VenueInfo implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;

    /** 主键id */
    @Column(id = true, name = "id")
    private Long id;

    /**
     * 会场编号
     */
    @Column(name = "venue_num")
    private Integer venueNum;

    /**
     * 会场二维码映射url
     */
    @Column(name = "venue_url")
    private String venueUrl;

    /**
     * 会场名称
     */
    @Column(name = "venue_name")
    private String venueName;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 活动开始时间
     */
    @Column(name = "start_time")
    private Date startTime;

    /**
     * 活动结束时间
     */
    @Column(name = "end_time")
    private Date endTime;
}
