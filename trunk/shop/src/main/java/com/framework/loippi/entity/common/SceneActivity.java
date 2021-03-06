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
@Table(name = "rd_scene_activity")
public class SceneActivity implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;

    /** 主键id */
    @Column(id = true, name = "id")
    private Long id;

    /**
     *  会员编号
     */
    @Column(name = "mm_code")
    private String mCode;

    /**
     *  礼品领取状态 0：无资格  1：领取资格 2：资格兑换礼品
     */
    @Column(name = "present_status")
    private Integer presentStatus;

    /**
     *  获取资格时间
     */
    @Column(name = "get_time")
    private Date getime;

    /**
     *  使用资格时间
     */
    @Column(name = "use_time")
    private Date useTime;

    /**
     *  会员昵称
     */
    @Column(name = "mm_nick_name")
    private String mNickName;

    /**
     *  会员头像
     */
    @Column(name = "image")
    private String image;

    /**
     *关联会场信息
     */
    @Column(name = "venue_id")
    private Long venueId;

    /**
     *会场编码
     */
    @Column(name = "venue_num")
    private Integer venueNum;

}
