package com.framework.loippi.param.travel;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class TravelTicketSubmitParam {
    /**
     * 使用几张旅游券参团
     */
    @Min(1)
    @NotNull
    private Integer useTicketNum;

    /**
     * 旅游券id
     */
    @NotNull
    private Long travelId;
}
