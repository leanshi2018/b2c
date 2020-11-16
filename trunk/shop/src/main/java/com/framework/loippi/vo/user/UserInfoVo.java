package com.framework.loippi.vo.user;

import lombok.Data;

/**
 * plus订单分单会员信息
 */
@Data
public class UserInfoVo {
    /**
     * 会员编号
     */
    private String mmCode;
    /**
     * 会员昵称
     */
    private String mmNickName;
    /**
     * 会员级别
     */
    private Integer rank;
    /**
     * 会员级别名称
     */
    private String rankName;
}
