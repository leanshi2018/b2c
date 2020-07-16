package com.framework.loippi.param.travel;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author zc
 * 旅游活动参团会员个人信息提交参数
 */
@Data
public class TravelMemSubmitParam {
    /**
     * 旅游活动id
     */
    private Long activityId;

    /**
     * 参团人姓名
     */
    private String name;

    /**
     * 参团人身份证号码
     */
    private String idCard;

    /**
     * 参团人手机号码
     */
    private String mobile;

    /**
     * 使用几张旅游券参团
     */
    private Integer useTicketNum;

    /**
     * 旅游券id
     */
    private Long travelId;
}
