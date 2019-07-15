package com.framework.loippi.vo.user;

import lombok.Data;

/**
 * 会员excel超类
 * Created by Administrator on 2017/6/30.
 */
@Data
public class MemberExcelVo {
    /**
     * 编号
     */
    private Integer no;
    /**
     * 会员名
     */
    private String memberName;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 姓名
     */
    private String trueName;
    /**
     * 分组名称
     */
    private String groupName;
    /**
     * 登录次数
     */
    private Integer loginNum;
    /**
     * 红包余额
     */
    private String hbBalance;
    /**
     * 账户余额
     */
    private String accountBalance;
    /**
     * 最后登录时间
     */
    private String lastLoginTime;
    /**
     * 来源
     */
    private String sourse;
    /**
     * 认证
     */
    private String authc;
    /**
     * 状态
     */
    private String status;
}
