package com.framework.loippi.entity;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 插件配置
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_SYSTEM_PLUGIN_CONFIG")
public class TSystemPluginConfig implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 编号
     */
    @Column(id = true, name = "ID", updatable = false)
    private Long id;

    /**
     * 插件编号
     */
    @Column(name = "PLUGIN_ID")
    private String pluginId;

    /**
     * 是否启用
     */
    @Column(name = "IS_ENABLED")
    private Integer isEnabled;

    private String pluginName;
    private String pluginNameLike;

}
