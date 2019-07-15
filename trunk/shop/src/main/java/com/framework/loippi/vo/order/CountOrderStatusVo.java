package com.framework.loippi.vo.order;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 
 * @author ihui
 * 所有订单记录数
 */
@Data
@ToString
public class CountOrderStatusVo implements Serializable {

  /**
   * 订单数量
   */
  private int total;
  /**
   * 订单状态
   */
  private int orderStatus;
  /**
   * 是否评价
   */
  private int evaluationStatusType;
}
