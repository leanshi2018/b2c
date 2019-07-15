package com.framework.loippi.vo.order;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 
 * @author ihui
 * 订单销售量导出
 */
@Data
@ToString
public class OrderCountVo implements Serializable {
  /**
   * 时间
   */
  private String orderDate;
  /**
   * 销量
   */
  private String num;
  /**
   * 订单价格
   */
  private String orderPrice;

  /**
   * 订单状态
   */
  private int orderState;

  /**
   * 订单类型
   */
  private int orderType;
}
