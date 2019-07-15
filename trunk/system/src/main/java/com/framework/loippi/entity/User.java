package com.framework.loippi.entity;

import java.util.Date;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 系统用户
 *
 * @author Loippi Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_SYSTEM_USER")
public class User implements GenericEntity {

    private static final long serialVersionUID = -7404471728192141480L;

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
     * 更新日期
     */
    @Column(name = "UPDATE_DATE")
    private Date updateDate;

    /**
     * 用户名
     */
    @Column(name = "USER_NAME")
    private String username;

    /**
     * 手机
     */
    @Column(name = "PHONE")
    private String phone;

    /**
     * 密码
     */
    @Column(name = "PASSWORD")
    private String password;

    /**
     * 昵称
     */
    @Column(name = "NICKNAME")
    private String nickname;

    /**
     * 头像
     */
    @Column(name = "AVATAR")
    private String avatar;

    /**
     * 是否启用
     */
    @Column(name = "IS_ENABLED")
    private Boolean isEnabled;

    /**
     * 是否锁定
     */
    @Column(name = "IS_LOCKED")
    private Boolean isLocked;

    /**
     * 连续登录失败次数
     */
    @Column(name = "LOGIN_FAILURE_COUNT")
    private Integer loginFailureCount;

    /**
     * 锁定日期
     */
    @Column(name = "LOCKED_DATE")
    private Date lockedDate;

    /**
     * 最后登录日期
     */
    @Column(name = "LOGIN_DATE")
    private Date loginDate;

    /**
     * 最后登录IP
     */
    @Column(name = "LOGIN_IP")
    private String loginIp;

    /**
     * E-mail
     */
    @Column(name = "EMAIL")
    private String email;

    /**
     * 角色编号
     */
    @Column(name = "ROLE_ID")
    private Long roleId;

     /**
     * 主题 默认1经典2
     */
    @Column(name = "THEME")
    private Integer theme;

    /**
     * 1-管理员  2-区域管理员
     */
    @Column(name = "user_type")
    private Integer userType;

    @Column(name = "content")
    private String content;

    /**
     * 角色
     */
    private Role role = new Role();
    //模糊查询
    private String likeUserName;
    private String searchStartTime;

    private String searchEndTime;

}
