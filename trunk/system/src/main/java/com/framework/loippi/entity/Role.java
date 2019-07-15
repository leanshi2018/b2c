package com.framework.loippi.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Entity - 角色
 *
 * @author Loippi Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_SYSTEM_ROLE")
public class Role implements GenericEntity {

    private static final long serialVersionUID = -4449299600117715568L;

    /**
     * ID
     */
    @Column(id = true, name = "ID", updatable = false)
    private Long id;

    /**
     * 创建日期
     */
    @Column(name = "CREATE_DATE")
    private Date createDate;

    /**
     * 创建者
     */
    @Column(name = "CREATOR")
    private Long creator;

    /**
     * 更新日期
     */
    @Column(name = "UPDATE_DATE")
    private Date updateDate;

    /**
     * 更新者
     */
    @Column(name = "UPDATOR")
    private Long updator;

    /**
     * 名称
     */
    @Column(name = "ROLE_NAME")
    private String name;

    /**
     * 是否内置
     */
    @Column(name = "IS_SYSTEM")
    private Boolean isSystem;

    /**
     * 描述
     */
    @Column(name = "DESCRIPTIONS")
    private String description;

    /**
     * 平台类型1-平台 2-区域
     */
    @Column(name = "PLAT_TYPE")
    private Integer platType = 1;

    /**
     * 权限
     */
    private Set<Acl> authorities = new HashSet<Acl>();


    public List<Long> getAclIds() {
        List<Long> ids = new ArrayList<Long>();
        for (Acl acl : authorities) {
            ids.add(acl.getId());
        }
        return ids;
    }
}
