package com.framework.loippi.entity.activity;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动指南
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_activity_guide")
public class ActivityGuide implements GenericEntity, Cloneable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;
    /**
     * 图片
     */
    private String image;
    /**
     * 跳转路径
     */
    private String url;
    /**
     * 是否使用
     */
    private Integer isUse;
}
