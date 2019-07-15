package com.framework.loippi.entity.user;

import lombok.Data;

import java.util.Date;

/**
 * 会员注册数bean
 * Created by Administrator on 2017/9/16.
 */
@Data
public class ShopMemberRegisterNum {
    /** 注册日期 */
    private Date registerDate;
    /** 注册数 */
    private Integer registerNum;
    /** 排序类型（1-统计每天，2-统计每周，3-统计每月） */
    private Integer orderType;

    private Date afterDate;
    private Date beforeDate;
}
