package com.framework.loippi.entity;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import com.framework.loippi.utils.JacksonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    @Column(name = "logo")
    private String logo;

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

    /**  */
    @Column(name = "PLUGIN_NAME")
    private String pluginName;

    /**  */
    @Column(name = "create_time")
    private Date createTime;

    /**  */
    @Column(name = "attrs")
    private String attrs;

    /**
     * 插件类型 1-文件  2-支付
     */
    @Column(name = "type")
    private Integer type;

    private Map<String, Object> data;

    public Map<String, String> getData() {
        if(attrs == null){
            return new HashMap<>();
        }
        Map<String,String> data = JacksonUtil.convertStrMap(attrs);
        return data;
    }

}
