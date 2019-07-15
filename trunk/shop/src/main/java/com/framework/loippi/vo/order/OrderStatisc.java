package com.framework.loippi.vo.order;

import lombok.Data;
import lombok.ToString;

/**
 * Created by longbh on 2017/8/24.
 */
@Data
@ToString
public class OrderStatisc {

    private Long id;
    private String orderSn;
    private Integer sumGoods;
    private Double sumAmount;

}
