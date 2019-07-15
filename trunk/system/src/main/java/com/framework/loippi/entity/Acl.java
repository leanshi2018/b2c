package com.framework.loippi.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Entity - 资源
 *
 * @author Loippi Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_SYSTEM_ACL")
public class Acl implements GenericEntity {

    private static final long serialVersionUID = 5660456626612505104L;

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
     * 路径
     */
    @Column(name = "URL")
    private String url;

    /**
     * 名称
     */
    @Column(name = "ACL_NAME")
    private String name;

    /**
     * 类型 0:导航 1：菜单 2：按钮
     */
    @Column(name = "ACL_TYPE")
    private int type;

    /**
     * 权限
     */
    @Column(name = "PERMISSION")
    private String permission;

    /**
     * 图标
     */
    @Column(name = "ICON")
    private String icon;

    /**
     * 上级编号
     */
    @Column(name = "PARENT_ID")
    private Long parentId;


    /**
     * 排序
     */
    @Column(name = "SORTS")
    private Integer sorts;

    /**
     * 平台类型1-平台 2-区域
     */
    @Column(name = "PLAT_TYPE")
    private Integer platType = 1;

    /**
     * 上级菜单
     */
    private Acl parent;

    /**
     * 下级菜单
     */
    private List<Acl> children = new ArrayList<Acl>();

}
