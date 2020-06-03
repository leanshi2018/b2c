package com.framework.loippi.pojo.common;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 统计各个级别会员人数
 */
@Data
public class RankNumVo {
    /**
     * 会员数
     */
    private Long num;
    /**
     * 级别
     */
    private Integer rank;
}
