package com.framework.loippi.vo.stats;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;
import lombok.ToString;

/**
 * Created by Administrator on 2018/5/8.
 */
@Data
@ToString
public class StatsCountVo implements Serializable {

    /**
     * 昨日 上一周 上一月...名称
     */
    private String timeLong;


    /**
     * 统计得数量或金额
     */
    private BigDecimal count;
}
