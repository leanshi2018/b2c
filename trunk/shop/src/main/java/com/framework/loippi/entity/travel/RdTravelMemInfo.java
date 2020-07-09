package com.framework.loippi.entity.travel;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参与旅游活动会员信息
 * @author :zc
 * @date:2020/7/7
 * @description:dubbo com.framework.loippi.entity.travel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_travel_mem_info")
public class RdTravelMemInfo implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;
    /**
     * 主键id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;
    /**
     * 会员编号
     */
    @Column(name = "m_code" )
    private String mmCode;
    /**
     * 旅游活动id
     */
    @Column(name = "activity_id" )
    private Long activityId;
    /**
     * 参与人真实姓名
     */
    @Column(name = "name" )
    private String name;
    /**
     * 身份证号码
     */
    @Column(name = "id_card" )
    private String idCard;
    /**
     * 手机号码
     */
    @Column(name = "mobile" )
    private String mobile;
}
