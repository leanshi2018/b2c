package com.framework.loippi.vo.user;

import lombok.Data;

/**
 * 会员认证excel超类
 * Created by Administrator on 2017/6/30.
 */
@Data
public class MemberAuthExcelVo {
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
     * 会员身份证号码
     */
    private String idCard;
    /**
     * 申请认证时间
     */
    private String authDate;
    /**
     * 状态
     */
    private String status;
}
